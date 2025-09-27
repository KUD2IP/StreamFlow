import { useState, useEffect, useCallback, useRef } from "react";
import { TrendsLayout } from "~/components/layout/TrendsLayout";
import { VideoCard } from "~/components/content/VideoCard";
import { recommendationsApi } from "~/services/recommendationsApi";
import type { Video } from "~/types/video";

/**
 * Пропсы для компонента TrendsContent
 */
interface TrendsContentProps {
  onVideoClick?: (video: Video) => void; // Обработчик клика по видео
}

/**
 * Компонент контента страницы трендов
 */
function TrendsContent({ onVideoClick }: TrendsContentProps) {
  // Состояние списка видео
  const [videos, setVideos] = useState<Video[]>([]);
  
  // Состояние загрузки
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  
  // Состояние пагинации
  const [hasNextPage, setHasNextPage] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  
  // Состояние ошибок
  const [error, setError] = useState<string | null>(null);

  // Рефы для Intersection Observer
  const observerRef = useRef<IntersectionObserver | null>(null);
  const loadingRef = useRef<HTMLDivElement | null>(null);

  /**
   * Загружает трендовые видео
   * @param page - номер страницы
   * @param reset - сбросить ли текущий список
   */
  const loadTrends = useCallback(async (page: number, reset = false) => {
    try {
      if (reset) {
        setLoading(true);
        setError(null);
      } else {
        setLoadingMore(true);
      }

      // Загружаем трендовые видео (используем API рекомендаций, но с префиксом "Тренды")
      const params = {
        page,
        limit: 24,
        search: ''
      };

      const response = await recommendationsApi.getRecommendations(params);
      
      // Добавляем префикс "Тренды:" к заголовкам для отличия
      const trendsVideos = response.videos.map(video => ({
        ...video,
        id: `trend-${video.id}`,
        title: `Тренды: ${video.title}`
      }));

      if (reset) {
        setVideos(trendsVideos);
      } else {
        setVideos(prev => [...prev, ...trendsVideos]);
      }

      setHasNextPage(response.hasNextPage);
      setCurrentPage(page);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка загрузки трендов');
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  }, []);

  // Загрузка начальных видео при монтировании компонента
  useEffect(() => {
    loadTrends(1, true);
  }, [loadTrends]);

  // Intersection Observer для бесконечной прокрутки
  useEffect(() => {
    if (!hasNextPage || loadingMore || loading) return;

    // Удаляем старый observer
    if (observerRef.current) {
      observerRef.current.disconnect();
    }

    observerRef.current = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasNextPage && !loadingMore && !loading) {
          loadTrends(currentPage + 1);
        }
      },
      {
        threshold: 0.1,
        rootMargin: '50px',
      }
    );

    if (loadingRef.current) {
      observerRef.current.observe(loadingRef.current);
    }

    return () => {
      if (observerRef.current) {
        observerRef.current.disconnect();
      }
    };
  }, [hasNextPage, loadingMore, loading, currentPage, loadTrends]);

  // Альтернативный способ бесконечной прокрутки через window scroll
  // Используется как fallback если Intersection Observer не работает
  useEffect(() => {
    const handleScroll = () => {
      if (!hasNextPage || loadingMore || loading) return;
      
      const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
      const isNearBottom = scrollTop + clientHeight >= scrollHeight - 200;
      
      if (isNearBottom) {
        loadTrends(currentPage + 1);
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [hasNextPage, loadingMore, loading, currentPage, loadTrends]);

  // Загрузка дополнительных видео при достижении конца (для кнопки)
  const handleLoadMore = () => {
    if (!loadingMore && hasNextPage) {
      loadTrends(currentPage + 1);
    }
  };

  if (loading) {
    return (
      <div className="flex-1 flex flex-col bg-background overflow-hidden">
        <div className="flex-1 p-6 overflow-hidden">
          <div className="flex justify-center items-center min-h-[400px]">
            <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary border-t-transparent"></div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex-1 flex flex-col bg-background overflow-hidden">
        <div className="flex-1 p-6 overflow-hidden">
          <div className="text-center text-red-500">
            <h2 className="text-2xl font-bold mb-4">Ошибка загрузки</h2>
            <p>{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col bg-background overflow-hidden">
      <div className="flex-1 p-6 overflow-hidden">
        {/* Заголовок страницы */}
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-primary mb-4">
            Тренды
          </h1>
          <p className="text-sm text-secondary">
            Самые популярные видео прямо сейчас
          </p>
        </div>

        {/* Сетка трендовых видео */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-8">
          {videos.map((video) => (
            <VideoCard
              key={video.id}
              video={video}
              onClick={onVideoClick}
            />
          ))}
        </div>

        {/* Индикатор загрузки для Intersection Observer */}
        {hasNextPage && (
          <div ref={loadingRef} className="text-center py-8">
            {loadingMore && (
              <div className="flex justify-center items-center gap-2">
                <div className="animate-spin rounded-full h-6 w-6 border-2 border-primary border-t-transparent"></div>
                <span className="text-secondary">Загрузка трендов...</span>
              </div>
            )}
          </div>
        )}

        {/* Сообщение, если больше нет видео */}
        {!hasNextPage && videos.length > 0 && (
          <div className="text-center text-secondary py-8">
            <p>Вы просмотрели все трендовые видео!</p>
          </div>
        )}
      </div>
    </div>
  );
}

/**
 * Страница с трендовыми видео
 * Показывает все трендовые видео с пагинацией
 */
export default function TrendsPage() {
  // Обработчик клика по видео
  const handleVideoClick = (video: Video) => {
    // TODO: Добавить навигацию к странице видео
    console.log('Клик по видео:', video);
  };

  return (
    <TrendsLayout onVideoClick={handleVideoClick}>
      <TrendsContent onVideoClick={handleVideoClick} />
    </TrendsLayout>
  );
}

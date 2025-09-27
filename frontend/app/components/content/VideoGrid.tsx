import { useState, useEffect, useCallback, useRef } from "react";
import { VideoCard } from "./VideoCard";
import { recommendationsApi } from "~/services/recommendationsApi";
import type { Video, VideoListParams } from "~/types/video";

/**
 * Пропсы для компонента VideoGrid
 */
interface VideoGridProps {
  onVideoClick?: (video: Video) => void; // Обработчик клика по видео
}

/**
 * Компонент сетки видео с поддержкой бесконечной прокрутки
 * Автоматически загружает новые видео при достижении конца списка
 */
export function VideoGrid({ onVideoClick }: VideoGridProps) {
  // Состояние списка видео
  const [videos, setVideos] = useState<Video[]>([]);
  
  // Состояние загрузки
  const [loading, setLoading] = useState(true); // Первоначальная загрузка
  const [loadingMore, setLoadingMore] = useState(false); // Загрузка дополнительных видео
  
  // Состояние пагинации
  const [hasNextPage, setHasNextPage] = useState(true); // Есть ли еще страницы
  const [currentPage, setCurrentPage] = useState(3); // Текущая страница (начинаем с 3-й, так как первые 32 уже показаны в других блоках)
  
  // Состояние ошибок
  const [error, setError] = useState<string | null>(null);

  // Рефы для Intersection Observer
  const observerRef = useRef<IntersectionObserver | null>(null);
  const loadingRef = useRef<HTMLDivElement | null>(null);

  /**
   * Загружает видео с сервера
   * @param page - номер страницы
   * @param reset - сбросить ли текущий список
   */
  const loadVideos = useCallback(async (page: number, reset = false) => {
    try {
      if (reset) {
        setLoading(true);
        setError(null);
      } else {
        setLoadingMore(true);
      }

          const params: VideoListParams = {
            page,
            limit: 24,
          };

      const response = await recommendationsApi.getRecommendations(params);
      
      if (reset) {
        setVideos(response.videos);
      } else {
        setVideos(prev => [...prev, ...response.videos]);
      }
      
      setHasNextPage(response.hasNextPage);
      setCurrentPage(response.currentPage);
      
      
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка загрузки видео');
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  }, []);

  // Загрузка начальных видео при монтировании компонента
  useEffect(() => {
    loadVideos(3, true);
  }, [loadVideos]);

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
          loadVideos(currentPage + 1);
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
  }, [hasNextPage, loadingMore, loading, currentPage, loadVideos]);

  // Альтернативный способ бесконечной прокрутки через window scroll
  // Используется как fallback если Intersection Observer не работает
  useEffect(() => {
    const handleScroll = () => {
      if (!hasNextPage || loadingMore || loading) return;
      
      const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
      const isNearBottom = scrollTop + clientHeight >= scrollHeight - 200;
      
      if (isNearBottom) {
        loadVideos(currentPage + 1);
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [hasNextPage, loadingMore, loading, currentPage, loadVideos]);

  // Состояние загрузки - показываем спиннер
  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="flex flex-col items-center gap-4">
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary border-t-transparent"></div>
          <p className="text-secondary">Загрузка видео...</p>
        </div>
      </div>
    );
  }

  // Состояние ошибки - показываем сообщение и кнопку повтора
  if (error) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="text-center">
          <p className="text-red-500 mb-4">{error}</p>
          <button
            onClick={() => loadVideos(1, true)}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-hover transition-colors"
          >
            Попробовать снова
          </button>
        </div>
      </div>
    );
  }

  // Пустое состояние - нет видео
  if (videos.length === 0) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="text-center">
          <p className="text-secondary text-lg mb-2">Видео не найдены</p>
          <p className="text-muted">
            Попробуйте обновить страницу
          </p>
        </div>
      </div>
    );
  }

  // Основной рендер - сетка видео
  return (
    <div className="space-y-6">
      {/* Сетка видео с адаптивными колонками */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {videos.map((video) => (
          <VideoCard
            key={video.id}
            video={video}
            onClick={onVideoClick}
          />
        ))}
      </div>

      {/* Индикатор загрузки дополнительных видео */}
      {loadingMore && (
        <div className="flex justify-center py-8">
          <div className="flex items-center gap-3">
            <div className="animate-spin rounded-full h-6 w-6 border-2 border-primary border-t-transparent"></div>
            <span className="text-secondary">Загрузка...</span>
          </div>
        </div>
      )}

      {/* Невидимый элемент для Intersection Observer */}
      <div ref={loadingRef} className="h-1" />

      {/* Индикатор окончания контента */}
      {!hasNextPage && videos.length > 0 && (
        <div className="flex justify-center py-8">
          <p className="text-muted">Все видео загружены</p>
        </div>
      )}
    </div>
  );
}

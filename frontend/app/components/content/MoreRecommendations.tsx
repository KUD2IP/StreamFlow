import { useState, useEffect } from "react";
import { useAuth } from '~/contexts/AuthContext';
import { VideoCard } from './VideoCard';
import { recommendationsApi } from '~/services/recommendationsApi';
import type { Video } from '~/types/video';

/**
 * Пропсы для компонента MoreRecommendations
 */
interface MoreRecommendationsProps {
  onVideoClick?: (video: Video) => void; // Обработчик клика по видео
}

/**
 * Компонент дополнительных рекомендаций (16 видео)
 * Показывается только авторизованным пользователям
 */
export function MoreRecommendations({ onVideoClick }: MoreRecommendationsProps) {
  const { isAuthenticated, user } = useAuth();
  const [videos, setVideos] = useState<Video[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Загружаем дополнительные рекомендации (с 9 по 24)
  useEffect(() => {
    // Если пользователь не авторизован - не загружаем
    if (!isAuthenticated || !user) {
      setLoading(false);
      return;
    }

    const loadMoreRecommendations = async () => {
      try {
        setLoading(true);
        // Загружаем с 9 по 24 видео (16 видео)
        const response = await recommendationsApi.getRecommendations({ page: 2, limit: 16 });
        setVideos(response.videos);
        setError(null);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Ошибка загрузки рекомендаций');
      } finally {
        setLoading(false);
      }
    };

    loadMoreRecommendations();
  }, [isAuthenticated, user]);

  // Если пользователь не авторизован - не показываем блок
  if (!isAuthenticated || !user) {
    return null;
  }

  if (loading) {
    return (
      <div className="mb-8">
        <div className="flex justify-center items-center min-h-[400px]">
          <div className="flex flex-col items-center gap-4">
            <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary border-t-transparent"></div>
            <p className="text-secondary">Загрузка дополнительных рекомендаций...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="mb-8">
        <div className="flex justify-center items-center min-h-[400px]">
          <div className="text-center">
            <p className="text-red-500 mb-4">{error}</p>
            <button
              onClick={() => window.location.reload()}
              className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-hover transition-colors"
            >
              Попробовать снова
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="mb-8">
      {/* Дополнительные 16 рекомендаций */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {videos.map((video) => (
          <VideoCard
            key={video.id}
            video={video}
            onClick={onVideoClick}
          />
        ))}
      </div>

      {/* Разделительная линия */}
      <div className="mt-8 border-t border-custom"></div>
    </div>
  );
}

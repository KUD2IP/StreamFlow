import { useRef } from 'react';
import { useAuth } from '~/contexts/AuthContext';
import { VideoCard } from './VideoCard';
import type { Video } from '~/types/video';

/**
 * Пропсы для компонента ContinueWatchingCarousel
 */
interface ContinueWatchingCarouselProps {
  onVideoClick?: (video: Video) => void; // Обработчик клика по видео
}

/**
 * Компонент карусели "Продолжить просмотр"
 * Показывается только авторизованным пользователям
 * Адаптируется к ширине контейнера и показывает 4 видео из 8
 */
export function ContinueWatchingCarousel({ onVideoClick }: ContinueWatchingCarouselProps) {
  const { isAuthenticated, user } = useAuth();
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  // Если пользователь не авторизован - не показываем блок
  if (!isAuthenticated || !user) {
    return null;
  }

  // Моковые данные для "Продолжить просмотр" (8 видео)
  const continueWatchingVideos: Video[] = [
    {
      id: 'continue-1',
      title: 'Большое шоу 13 сезон. Третья серия.',
      description: 'Продолжайте смотреть с момента остановки',
      thumbnailUrl: '🎬',
      videoUrl: '#',
      duration: '45:20',
      viewCount: 1250000,
      likeCount: 89000,
      createdAt: '2024-01-15T10:00:00Z',
      channel: {
        id: 'channel-continue-1',
        name: 'Азамат Мусагалиев',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 2500000
      },
      isLive: false
    },
    {
      id: 'continue-2',
      title: 'Зенит - Дубай. Прямая трансляция',
      description: 'Футбольный матч в прямом эфире',
      thumbnailUrl: '⚽',
      videoUrl: '#',
      duration: '2:30:15',
      viewCount: 890000,
      likeCount: 56000,
      createdAt: '2024-01-10T14:30:00Z',
      channel: {
        id: 'channel-continue-2',
        name: 'Спорт ТВ',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 1800000
      },
      isLive: false
    },
    {
      id: 'continue-3',
      title: 'Как приготовить идеальную пасту',
      description: 'Кулинарный мастер-класс от шеф-повара',
      thumbnailUrl: '🍝',
      videoUrl: '#',
      duration: '32:45',
      viewCount: 567000,
      likeCount: 34000,
      createdAt: '2024-01-08T09:15:00Z',
      channel: {
        id: 'channel-continue-3',
        name: 'Кулинарные Шедевры',
        avatarUrl: '👤',
        isVerified: false,
        subscriberCount: 450000
      },
      isLive: false
    },
    {
      id: 'continue-4',
      title: 'Новые технологии в программировании',
      description: 'Обзор современных фреймворков и инструментов',
      thumbnailUrl: '💻',
      videoUrl: '#',
      duration: '1:15:30',
      viewCount: 234000,
      likeCount: 18000,
      createdAt: '2024-01-05T16:20:00Z',
      channel: {
        id: 'channel-continue-4',
        name: 'Tech Channel',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 890000
      },
      isLive: false
    },
    {
      id: 'continue-5',
      title: 'Путешествие по Японии. Часть 1',
      description: 'Документальный фильм о культуре Японии',
      thumbnailUrl: '🗾',
      videoUrl: '#',
      duration: '1:45:20',
      viewCount: 456000,
      likeCount: 32000,
      createdAt: '2024-01-03T12:00:00Z',
      channel: {
        id: 'channel-continue-5',
        name: 'Путешественник',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 1200000
      },
      isLive: false
    },
    {
      id: 'continue-6',
      title: 'Мотивационная речь для успеха',
      description: 'Вдохновляющие слова от известного спикера',
      thumbnailUrl: '💡',
      videoUrl: '#',
      duration: '25:30',
      viewCount: 789000,
      likeCount: 67000,
      createdAt: '2024-01-01T18:30:00Z',
      channel: {
        id: 'channel-continue-6',
        name: 'Мотивация +',
        avatarUrl: '👤',
        isVerified: false,
        subscriberCount: 650000
      },
      isLive: false
    },
    {
      id: 'continue-7',
      title: 'Обзор нового iPhone 15',
      description: 'Подробный разбор всех нововведений',
      thumbnailUrl: '📱',
      videoUrl: '#',
      duration: '38:15',
      viewCount: 1230000,
      likeCount: 89000,
      createdAt: '2023-12-28T15:45:00Z',
      channel: {
        id: 'channel-continue-7',
        name: 'Гаджеты Про',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 2100000
      },
      isLive: false
    },
    {
      id: 'continue-8',
      title: 'Стрим: Играем в новую RPG',
      description: 'Геймплей новой ролевой игры',
      thumbnailUrl: '🎮',
      videoUrl: '#',
      duration: '3:20:45',
      viewCount: 567000,
      likeCount: 45000,
      createdAt: '2023-12-25T20:00:00Z',
      channel: {
        id: 'channel-continue-8',
        name: 'Геймер Про',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 1800000
      },
      isLive: false
    }
  ];

  // Функции навигации карусели
  const scrollLeft = () => {
    if (scrollContainerRef.current) {
      const container = scrollContainerRef.current;
      // Прокручиваем на ширину одного видео + отступ = 300px + 24px = 324px
      const scrollAmount = 324;
      container.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    }
  };

  const scrollRight = () => {
    if (scrollContainerRef.current) {
      const container = scrollContainerRef.current;
      // Прокручиваем на ширину одного видео + отступ = 300px + 24px = 324px
      const scrollAmount = 324;
      container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  };

  return (
    <div className="mb-8">
      {/* Заголовок блока "Продолжить просмотр" */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-xl font-bold text-primary mb-2">
            Продолжить просмотр
          </h2>
        </div>
        
        {/* Кнопка "Смотреть все" */}
        <button className="px-4 py-2 text-primary hover:text-primary-hover transition-colors font-medium">
          Смотреть все
        </button>
      </div>

      {/* Карусель с кнопками навигации */}
      <div className="relative overflow-hidden">
        {/* Кнопка "Назад" */}
        <button
          onClick={scrollLeft}
          className="absolute left-0 top-8 translate-y-8 z-10 bg-surface/90 hover:bg-surface border border-custom rounded-full p-2 shadow-lg transition-colors"
          aria-label="Предыдущие видео"
        >
          <svg className="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>

        {/* Кнопка "Вперед" */}
        <button
          onClick={scrollRight}
          className="absolute right-0 top-8 translate-y-8 z-10 bg-surface/90 hover:bg-surface border border-custom rounded-full p-2 shadow-lg transition-colors"
          aria-label="Следующие видео"
        >
          <svg className="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
          </svg>
        </button>

        {/* Контейнер карусели */}
        <div 
          ref={scrollContainerRef}
          className="overflow-x-auto scrollbar-hide scroll-smooth"
          style={{ 
            scrollbarWidth: 'none', 
            msOverflowStyle: 'none'
          }}
        >
          {/* Внутренний контейнер с видео - показываем все 8 в ряд */}
          <div className="flex gap-6 pb-4" style={{ width: 'max-content' }}>
            {continueWatchingVideos.map((video) => (
              <div key={video.id} className="flex-shrink-0" style={{ width: '300px' }}>
                <VideoCard
                  video={video}
                  onClick={onVideoClick}
                />
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Разделительная линия */}
      <div className="mt-8 border-t border-custom"></div>
    </div>
  );
}

import { Link } from 'react-router';
import { VideoCard } from './VideoCard';
import type { Video } from '~/types/video';

/**
 * Пропсы для компонента Trends
 */
interface TrendsProps {
  onVideoClick?: (video: Video) => void; // Обработчик клика по видео
}

/**
 * Компонент блока трендов
 * Показывает 8 трендовых видео в сетке
 */
export function Trends({ onVideoClick }: TrendsProps) {
  // Моковые данные для трендов (8 видео)
  const trendsVideos: Video[] = [
    {
      id: 'trend-1',
      title: 'ТРЕНД: Самые популярные видео недели',
      description: 'Топовые видео, которые набирают миллионы просмотров',
      thumbnailUrl: '🔥',
      videoUrl: '#',
      duration: '12:34',
      viewCount: 5000000,
      likeCount: 250000,
      createdAt: '2024-01-20T10:00:00Z',
      channel: {
        id: 'channel-trend-1',
        name: 'Trending Now',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 5000000
      },
      isLive: false
    },
    {
      id: 'trend-2',
      title: 'ВИРУС: Новый челлендж захватил интернет',
      description: 'Смотрите, как этот челлендж стал популярным',
      thumbnailUrl: '⚡',
      videoUrl: '#',
      duration: '8:45',
      viewCount: 3200000,
      likeCount: 180000,
      createdAt: '2024-01-19T15:30:00Z',
      channel: {
        id: 'channel-trend-2',
        name: 'Viral Content',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 3200000
      },
      isLive: false
    },
    {
      id: 'trend-3',
      title: 'ТОП: Лучшие моменты спорта',
      description: 'Невероятные спортивные достижения',
      thumbnailUrl: '🏆',
      videoUrl: '#',
      duration: '15:20',
      viewCount: 2800000,
      likeCount: 150000,
      createdAt: '2024-01-18T12:00:00Z',
      channel: {
        id: 'channel-trend-3',
        name: 'Sports Highlights',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 2800000
      },
      isLive: false
    },
    {
      id: 'trend-4',
      title: 'ХИТ: Новый трек взорвал чарты',
      description: 'Музыкальный хит, который все слушают',
      thumbnailUrl: '🎵',
      videoUrl: '#',
      duration: '3:45',
      viewCount: 4500000,
      likeCount: 320000,
      createdAt: '2024-01-17T18:15:00Z',
      channel: {
        id: 'channel-trend-4',
        name: 'Music Hits',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 4500000
      },
      isLive: false
    },
    {
      id: 'trend-5',
      title: 'НОВИНКА: Обзор последних технологий',
      description: 'Что нового в мире технологий',
      thumbnailUrl: '💻',
      videoUrl: '#',
      duration: '20:10',
      viewCount: 1600000,
      likeCount: 89000,
      createdAt: '2024-01-16T14:20:00Z',
      channel: {
        id: 'channel-trend-5',
        name: 'Tech Review',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 1600000
      },
      isLive: false
    },
    {
      id: 'trend-6',
      title: 'КОМЕДИЯ: Самые смешные моменты',
      description: 'Видео, от которых невозможно не смеяться',
      thumbnailUrl: '😂',
      videoUrl: '#',
      duration: '6:30',
      viewCount: 3800000,
      likeCount: 280000,
      createdAt: '2024-01-15T16:45:00Z',
      channel: {
        id: 'channel-trend-6',
        name: 'Funny Moments',
        avatarUrl: '👤',
        isVerified: false,
        subscriberCount: 3800000
      },
      isLive: false
    },
    {
      id: 'trend-7',
      title: 'КРАСОТА: Тренды макияжа 2024',
      description: 'Новые тренды в мире красоты',
      thumbnailUrl: '💄',
      videoUrl: '#',
      duration: '10:25',
      viewCount: 2200000,
      likeCount: 140000,
      createdAt: '2024-01-14T11:30:00Z',
      channel: {
        id: 'channel-trend-7',
        name: 'Beauty Trends',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 2200000
      },
      isLive: false
    },
    {
      id: 'trend-8',
      title: 'ПУТЕШЕСТВИЯ: Самые красивые места',
      description: 'Невероятные локации со всего мира',
      thumbnailUrl: '🌍',
      videoUrl: '#',
      duration: '14:50',
      viewCount: 1900000,
      likeCount: 120000,
      createdAt: '2024-01-13T09:15:00Z',
      channel: {
        id: 'channel-trend-8',
        name: 'Travel World',
        avatarUrl: '👤',
        isVerified: true,
        subscriberCount: 1900000
      },
      isLive: false
    }
  ];

  return (
    <div className="mb-8">
      {/* Заголовок блока трендов */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-xl font-bold text-primary mb-2">
            Тренды
          </h2>
          <p className="text-secondary text-sm">
            Самые популярные видео прямо сейчас
          </p>
        </div>
        
        {/* Кнопка "Показать все" */}
        <Link 
          to="/trends"
          className="px-4 py-2 text-primary hover:text-primary-hover transition-colors font-medium"
        >
          Показать все
        </Link>
      </div>

      {/* Сетка трендовых видео */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {trendsVideos.map((video) => (
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

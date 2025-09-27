import { VideoGrid } from "./VideoGrid";
import { FirstRecommendations } from "./FirstRecommendations";
import { ContinueWatchingCarousel } from "./ContinueWatchingCarousel";
import { MoreRecommendations } from "./MoreRecommendations";
import { Trends } from "./Trends";
import type { Video } from "~/types/video";

/**
 * Пропсы для компонента MainContent
 */
interface MainContentProps {
  onVideoClick?: (video: Video) => void; // Обработчик клика по видео
}

/**
 * Компонент основного контента приложения
 * Содержит независимые блоки: рекомендации, карусель, тренды и бесконечную загрузку
 */
export function MainContent({ onVideoClick }: MainContentProps) {

return (
  <div className="flex-1 flex flex-col bg-background overflow-hidden">
    {/* Основной контент с отступами */}
    <div className="flex-1 p-6 overflow-hidden">
      {/* 1. Блок из 8 видео рекомендаций */}
      <FirstRecommendations onVideoClick={onVideoClick} />
      
      {/* 2. Блок с продолжить просмотр (карусель) */}
      <ContinueWatchingCarousel onVideoClick={onVideoClick} />
      
      {/* 3. Блок 16 видео с рекомендаций */}
      <MoreRecommendations onVideoClick={onVideoClick} />
      
      {/* 4. Блок из 8 видео с трендов */}
      <Trends onVideoClick={onVideoClick} />
      
      {/* 5. Дальше бесконечная автопрогрузка видео из рекомендаций (по 24) */}
      <div className="mb-8">
        <div className="mb-6">
          <h1 className="text-xl font-bold text-primary mb-2">
            Больше рекомендаций
          </h1>
          <p className="text-secondary text-sm">
            Продолжайте открывать для себя новый контент
          </p>
        </div>
        
        {/* Сетка видео с бесконечной загрузкой */}
        <VideoGrid 
          onVideoClick={onVideoClick} 
        />
      </div>
    </div>
  </div>
  );
}

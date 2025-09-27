import type { Video, VideoListResponse, VideoListParams } from "~/types/video";

/**
 * Сервис для работы с рекомендациями
 * Получает персонализированные рекомендации для пользователя
 */
export class RecommendationsApi {
  /**
   * Получает список рекомендуемых видео
   * @param params - параметры запроса (пагинация)
   * @returns Promise с ответом API
   */
  async getRecommendations(params: VideoListParams = {}): Promise<VideoListResponse> {
    // Имитируем задержку сети
    await new Promise(resolve => setTimeout(resolve, 300));
    
    const { page = 1, limit = 24 } = params;
    
    // Генерируем рекомендации на основе страницы
    const recommendations = this.generateRecommendations(page, limit);
    
    // Вычисляем пагинацию
    const totalCount = 1000; // Общее количество рекомендаций (бесконечно много)
    const totalPages = Math.ceil(totalCount / limit);
    
    return {
      videos: recommendations,
      totalCount,
      currentPage: page,
      totalPages,
      hasNextPage: page < totalPages
    };
  }

  /**
   * Генерирует моковые рекомендации
   * @param page - номер страницы
   * @param limit - количество видео на странице
   * @returns массив рекомендуемых видео
   */
  private generateRecommendations(page: number, limit: number): Video[] {
    const recommendations: Video[] = [];
    const startId = (page - 1) * limit + 1;
    
    const recommendationTitles = [
      "Рекомендуем: Лучшие фильмы этого года",
      "Персональная подборка: Технологии 2024",
      "Для вас: Кулинарные шедевры",
      "Рекомендуем: Путешествия по миру",
      "Ваша подборка: Образовательный контент",
      "Рекомендуем: Музыкальные клипы",
      "Для вас: Спортивные трансляции",
      "Рекомендуем: Игровые стримы",
      "Ваша подборка: Научные открытия",
      "Рекомендуем: Модные тренды",
      "Для вас: Здоровый образ жизни",
      "Рекомендуем: Исторические документалки",
      "Ваша подборка: Комедийные шоу",
      "Рекомендуем: Приключенческие фильмы",
      "Для вас: Бизнес и финансы",
      "Рекомендуем: Искусство и культура",
      "Ваша подборка: Природа и животные",
      "Рекомендуем: Технические обзоры",
      "Для вас: Мотивационные речи",
      "Рекомендуем: Новости и аналитика"
    ];

    const channelNames = [
      "Рекомендации AI",
      "Персональный выбор",
      "Ваш контент",
      "Умные рекомендации",
      "Индивидуальная подборка",
      "Алгоритм для вас",
      "Персональный ассистент",
      "Ваши интересы",
      "Рекомендательная система",
      "Индивидуальный контент"
    ];

    const thumbnails = [
      "🎬", "💻", "🍳", "✈️", "📚", "🎵", "⚽", "🎮", 
      "🔬", "👗", "💪", "📜", "😂", "🗺️", "💰", "🎨", 
      "🌿", "⚙️", "💡", "📰"
    ];

    for (let i = 0; i < limit; i++) {
      const videoId = startId + i;
      const randomTitle = recommendationTitles[Math.floor(Math.random() * recommendationTitles.length)];
      const randomChannel = channelNames[Math.floor(Math.random() * channelNames.length)];
      const randomThumbnail = thumbnails[Math.floor(Math.random() * thumbnails.length)];
      const isVerified = Math.random() < 0.8; // 80% шанс быть верифицированным для рекомендаций
      
      recommendations.push({
        id: `rec-${videoId}`,
        title: `${randomTitle} #${videoId}`,
        description: `Персонализированная рекомендация на основе ваших предпочтений и истории просмотров. Видео ${videoId} из коллекции рекомендаций.`,
        thumbnailUrl: randomThumbnail,
        videoUrl: `https://example.com/recommendation/${videoId}`,
        duration: `${Math.floor(Math.random() * 45) + 5}:${Math.floor(Math.random() * 60).toString().padStart(2, '0')}`,
        viewCount: Math.floor(Math.random() * 500000) + 5000,
        likeCount: Math.floor(Math.random() * 25000) + 500,
        createdAt: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(), // За последние 30 дней
        isLive: false,
        channel: {
          id: `rec-channel-${videoId}`,
          name: randomChannel,
          avatarUrl: "🤖",
          isVerified,
          subscriberCount: Math.floor(Math.random() * 1000000) + 50000
        }
      });
    }
    
    return recommendations;
  }
}

// Экспортируем экземпляр сервиса
export const recommendationsApi = new RecommendationsApi();

import type { Video, CreateVideoRequest, CreateVideoResponse, VideoUploadResponse } from '~/types/video';

// Базовый URL для API (можно настроить через переменные окружения)
const API_BASE_URL = process.env.NODE_ENV === 'production' 
  ? 'https://api.streamflow.com' 
  : 'http://localhost:8081/api/v1';

// Функция для создания заголовков с авторизацией
const createHeaders = (token?: string): HeadersInit => {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  return headers;
};

// Функция для создания заголовков для загрузки файлов
const createFileHeaders = (token?: string): HeadersInit => {
  const headers: HeadersInit = {};
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  return headers;
};

// Обработка ошибок API
const handleApiError = async (response: Response): Promise<never> => {
  let errorMessage = 'Произошла ошибка при выполнении запроса';
  
  console.error('API Error - Status:', response.status, response.statusText);
  
  try {
    const errorData = await response.json();
    console.error('API Error - Body:', errorData);
    errorMessage = errorData.message || errorData.error || errorMessage;
  } catch {
    // Если не удалось распарсить JSON, используем стандартное сообщение
    console.error('API Error - Could not parse JSON response');
    errorMessage = `HTTP ${response.status}: ${response.statusText}`;
  }
  
  throw new Error(errorMessage);
};

export const videoApi = {
  // Создание нового видео (только метаданные)
  async createVideo(data: CreateVideoRequest, token?: string): Promise<CreateVideoResponse> {
    const response = await fetch(`${API_BASE_URL}/videos`, {
      method: 'POST',
      headers: createHeaders(token),
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      await handleApiError(response);
    }

    return response.json();
  },

  // Обновление данных видео после загрузки файла
  async updateVideoMetadata(videoId: string, data: CreateVideoRequest, token?: string): Promise<CreateVideoResponse> {
    const response = await fetch(`${API_BASE_URL}/videos/${videoId}`, {
      method: 'POST',
      headers: createHeaders(token),
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      await handleApiError(response);
    }

    return response.json();
  },

  // Загрузка видео файла (тестовый эндпоинт /uploads)
  async testUploadVideo(file: File, token?: string): Promise<VideoUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_BASE_URL}/videos/uploads`, {
      method: 'POST',
      headers: createFileHeaders(token),
      body: formData,
    });

    if (!response.ok) {
      await handleApiError(response);
    }

    return response.json();
  },

  // Загрузка видео файла
  async uploadVideo(videoId: string, file: File, token?: string): Promise<void> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_BASE_URL}/videos/upload/${videoId}`, {
      method: 'POST',
      headers: createFileHeaders(token),
      body: formData,
    });

    if (!response.ok) {
      await handleApiError(response);
    }
  },

  // Загрузка превью (thumbnail)
  async uploadThumbnail(videoId: string, file: File, token?: string): Promise<void> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_BASE_URL}/videos/${videoId}/thumbnail`, {
      method: 'POST',
      headers: createFileHeaders(token),
      body: formData,
    });

    if (!response.ok) {
      await handleApiError(response);
    }
  },

  // Получение списка видео
  async getVideos(page: number = 0, size: number = 24, search?: string): Promise<Video[]> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });

    if (search) {
      params.append('search', search);
    }

    const response = await fetch(`${API_BASE_URL}/videos?${params}`, {
      method: 'GET',
      headers: createHeaders(),
    });

    if (!response.ok) {
      await handleApiError(response);
    }

    const data = await response.json();
    return data.content || data; // Поддержка разных форматов ответа
  },

  // Получение видео по ID
  async getVideoById(id: string): Promise<Video> {
    const response = await fetch(`${API_BASE_URL}/videos/${id}`, {
      method: 'GET',
      headers: createHeaders(),
    });

    if (!response.ok) {
      await handleApiError(response);
    }

    return response.json();
  },

  // Получение трендовых видео
  async getTrendingVideos(page: number = 0, size: number = 24): Promise<Video[]> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });

    const response = await fetch(`${API_BASE_URL}/videos/trending?${params}`, {
      method: 'GET',
      headers: createHeaders(),
    });

    if (!response.ok) {
      await handleApiError(response);
    }

    const data = await response.json();
    return data.content || data;
  },

  // Лайк видео
  async likeVideo(videoId: string, token?: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/videos/${videoId}/like`, {
      method: 'POST',
      headers: createHeaders(token),
    });

    if (!response.ok) {
      await handleApiError(response);
    }
  },

  // Дизлайк видео
  async dislikeVideo(videoId: string, token?: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/videos/${videoId}/dislike`, {
      method: 'POST',
      headers: createHeaders(token),
    });

    if (!response.ok) {
      await handleApiError(response);
    }
  },

  // Удаление лайка/дизлайка
  async removeReaction(videoId: string, token?: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/videos/${videoId}/reaction`, {
      method: 'DELETE',
      headers: createHeaders(token),
    });

    if (!response.ok) {
      await handleApiError(response);
    }
  },

  // Получение тегов
  async getTags(): Promise<{ id: string; name: string }[]> {
    const response = await fetch(`${API_BASE_URL}/tags`, {
      method: 'GET',
      headers: createHeaders(),
    });

    if (!response.ok) {
      await handleApiError(response);
    }

    return response.json();
  },
};
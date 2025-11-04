import { useState, useRef } from "react";
import { AuthButton } from "./AuthButton";
import { useCreateVideoModal } from "~/contexts/CreateVideoModalContext";
import { useAuth } from "~/contexts/AuthContext";
import { videoApi } from "~/services/videoApi";

/**
 * Пропсы для компонента Header
 */
interface HeaderProps {
  isSidebarCompact?: boolean; // Состояние сайдбара (свернут/развернут)
  onToggleSidebar?: () => void; // Обработчик переключения сайдбара
}

/**
 * Компонент шапки приложения
 * Содержит: логотип, поиск, кнопки управления
 */
export function Header({ isSidebarCompact, onToggleSidebar }: HeaderProps) {
  const { openModal } = useCreateVideoModal(); // Используется для открытия модала после загрузки
  const { isAuthenticated, token } = useAuth();
  const [isUploading, setIsUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleTestUploadClick = () => {
    if (!isAuthenticated) {
      alert('Необходимо войти в систему для загрузки видео');
      return;
    }
    fileInputRef.current?.click();
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!token) {
      alert('Токен не найден');
      return;
    }

    setIsUploading(true);
    try {
      console.log('Загрузка видео файла:', file.name, 'Размер:', file.size);
      const response = await videoApi.testUploadVideo(file, token);
      console.log('Ответ сервера:', response);
      
      // Открываем модал для заполнения данных видео с полученным videoId
      openModal(response.videoId);
    } catch (error) {
      console.error('Ошибка загрузки:', error);
      console.error('Детали ошибки:', error instanceof Error ? error.stack : error);
      alert(`Ошибка загрузки: ${error instanceof Error ? error.message : 'Неизвестная ошибка'}`);
    } finally {
      setIsUploading(false);
      // Очищаем input для возможности повторной загрузки того же файла
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-surface/60 backdrop-blur-md">
      <div className="flex items-center justify-between h-16 px-2 sm:px-4">
        {/* Левая часть - кнопка меню и логотип */}
        <div className="flex items-center gap-2 sm:gap-4">
          {/* Кнопка переключения сайдбара */}
          <button 
            onClick={onToggleSidebar}
            className="p-2 rounded-lg hover:bg-surface-hover transition-colors"
          >
            <svg className="h-5 w-5 sm:h-6 sm:w-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          
          {/* Логотип и название */}
          <div className="flex items-center gap-1 sm:gap-2">
            <img 
              src="/logo.png" 
              alt="StreamFlow Logo" 
              className="h-8 w-8 sm:h-10 sm:w-10"
            />
            {/* Название скрыто на мобильных */}
            <span className="hidden sm:block text-primary text-xl font-bold">StreamFlow</span>
          </div>
        </div>

        {/* Центральная часть - поиск */}
        <div className="flex-1 max-w-md sm:max-w-xl mx-2 sm:mx-1">
          <div className="relative">
            <div className="relative">
              {/* Иконка поиска */}
              <div className="absolute inset-y-0 left-0 pl-2 sm:pl-3 flex items-center pointer-events-none">
                <svg className="h-4 w-4 sm:h-5 sm:w-5 text-muted" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              {/* Поле ввода поиска */}
              <input
                type="text"
                placeholder="Поиск видео..."
                className="block w-full pl-8 sm:pl-10 pr-2 sm:pr-3 py-1.5 sm:py-2 text-sm sm:text-base border border-custom rounded-2xl sm:rounded-3xl bg-surface text-primary placeholder-muted focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all duration-300 ease-in-out focus:shadow-lg focus:shadow-primary/20"
              />
            </div>
          </div>
        </div>

        {/* Правая часть - действия пользователя */}
        <div className="flex items-center gap-1 sm:gap-4">
          {/* Скрытый input для выбора файла */}
          <input
            ref={fileInputRef}
            type="file"
            accept="video/*"
            className="hidden"
            onChange={handleFileChange}
            disabled={isUploading}
          />
          
          {/* Кнопка загрузки видео */}
          <button 
            onClick={handleTestUploadClick}
            className="p-2 rounded-lg hover:bg-surface-hover transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            title="Загрузить видео"
            disabled={isUploading}
          >
            {isUploading ? (
              <svg className="h-4 w-4 sm:h-5 sm:w-5 text-primary animate-spin" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            ) : (
              <svg className="h-4 w-4 sm:h-5 sm:w-5 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
            )}
          </button>
          
          {/* Кнопка уведомлений с бейджем */}
          <button className="relative p-1.5 sm:p-2 rounded-lg hover:bg-surface-hover transition-colors">
            <svg className="h-5 w-5 sm:h-6 sm:w-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            {/* Бейдж с количеством уведомлений */}
            <span className="absolute -top-0.5 -right-0.5 sm:-top-1 sm:-right-1 h-3 w-3 sm:h-4 sm:w-4 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
              3
            </span>
          </button>
          
          {/* Кнопка аутентификации */}
          <AuthButton />
        </div>
      </div>
    </header>
  );
}

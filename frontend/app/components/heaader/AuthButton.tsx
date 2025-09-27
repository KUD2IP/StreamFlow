import { useAuth } from '~/contexts/AuthContext';

/**
 * Компонент кнопки аутентификации
 * Показывает кнопку входа или информацию о пользователе с кнопкой выхода
 */
export function AuthButton() {
  const { isAuthenticated, isLoading, user, login, logout } = useAuth();

  // Показываем спиннер во время загрузки
  if (isLoading) {
    return (
      <div className="flex items-center gap-2">
        <div className="animate-spin rounded-full h-4 w-4 border-2 border-primary border-t-transparent"></div>
        <span className="text-sm text-secondary">Загрузка...</span>
      </div>
    );
  }

  // Если пользователь не аутентифицирован - показываем кнопку входа
  if (!isAuthenticated) {
    return (
      <button 
        onClick={login}
        className="px-2 py-1.5 sm:px-4 sm:py-2 text-sm sm:text-base bg-primary text-white rounded-2xl sm:rounded-3xl hover:bg-primary-hover transition-colors font-medium"
      >
        <span>Войти</span>
      </button>
    );
  }

  // Если пользователь аутентифицирован - показываем информацию о пользователе и кнопку выхода
  return (
    <div className="flex items-center gap-2">
      {/* Аватар пользователя */}
      <div className="flex items-center gap-2">
        <div className="w-8 h-8 bg-primary rounded-full flex items-center justify-center text-white text-sm font-medium">
          {user?.firstName?.charAt(0) || user?.username?.charAt(0) || '👤'}
        </div>
        {/* Имя пользователя (скрыто на мобильных) */}
        <span className="hidden sm:inline text-sm text-primary font-medium">
          {user?.firstName || user?.username}
        </span>
      </div>
      
      {/* Кнопка выхода */}
      <button 
        onClick={logout}
        className="p-1.5 sm:p-2 rounded-lg hover:bg-surface-hover transition-colors"
        title="Выйти"
      >
        <svg className="h-4 w-4 sm:h-5 sm:w-5 text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
        </svg>
      </button>
    </div>
  );
}

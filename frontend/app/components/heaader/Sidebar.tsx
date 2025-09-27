import {Link, useLocation} from "react-router";

/**
 * Пропсы для компонента Sidebar
 */
interface SidebarProps {
  isCompact?: boolean; // Состояние свернутого/развернутого меню (для десктопа)
  isOpen?: boolean; // Состояние открытия/закрытия меню (для мобильных)
  onToggleCompact?: () => void; // Обработчик переключения состояния
}

/**
 * Компонент бокового меню навигации
 * Содержит основные разделы и категории контента
 */
export function Sidebar({ isCompact = false, isOpen = false, onToggleCompact }: SidebarProps) {
  // Получаем текущий путь для подсветки активного элемента
  const location = useLocation();
  
  // Массив элементов меню с иконками и названиями
  const menuItems = [
      // Основные разделы
      { 
        id: "home", 
        label: "Главная",
        path: "/",
        icon: (
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
          </svg>
        ) 
      },
      { 
        id: "trending", 
        label: "Тренды",
        path: "/trends",
        icon: (
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
          </svg>
        ) 
      },
      // Разделитель перед историей
      { 
        id: "history", 
        label: "История просмотра",
        path: "#", // TODO: Добавить страницу истории
        icon: (
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        ) 
      },
      { 
        id: "watch-later", 
        label: "Смотреть позже",
        path: "#", // TODO: Добавить страницу "смотреть позже"
        icon: (
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        ) 
      },
      { 
        id: "liked", 
        label: "Мне понравилось",
        path: "#", // TODO: Добавить страницу понравившихся
        icon: (
          <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
        ) 
      },
      { 
        id: "my-videos", 
        label: "Мои видео",
        path: "#", // TODO: Добавить страницу моих видео
        icon: (
          <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M8 5v14l11-7z"/>
          </svg>
        ) 
      },
      { 
        id: "subscriptions", 
        label: "Подписки",
        path: "#", // TODO: Добавить страницу подписок
        icon: (
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 10h16M4 14h16M4 18h16" />
          </svg>
        ) 
      },
    ];

      return (
        <div className={`
          bg-surface transition-all duration-300 flex flex-col h-full 
          w-64
          ${isCompact ? 'sm:w-16' : 'sm:w-64'}
        `}>
          {/* Навигационное меню */}
          <nav className="flex-1 p-2 overflow-y-auto sidebar-scroll">
        {menuItems.map((item, index) => (
          <div key={item.id}>
            {/* Горизонтальная полоса перед "История просмотра" */}
            {item.id === 'history' && (
              <div className="mx-3 my-2 border-t border-custom"></div>
            )}
            
            {/* Кнопка элемента меню */}
            {item.path ? (
              <Link
                to={item.path}
                className={`
                  w-full flex items-center justify-between gap-3 px-3 py-2 rounded-lg transition-colors mb-1
                  ${location.pathname === item.path
                    ? 'bg-surface-hover text-primary' 
                    : 'text-secondary hover:bg-surface-hover hover:text-primary'
                  }
                `}
              >
                <div className="flex items-center gap-3">
                  {/* Иконка элемента */}
                  <div className="flex-shrink-0">{item.icon}</div>
                  {/* Название элемента (скрыто в компактном режиме на десктопе) */}
                  <span className={`text-sm font-medium truncate ${isCompact ? 'sm:hidden' : ''}`}>
                    {item.label}
                  </span>
                </div>
              </Link>
            ) : (
              <button
                className="w-full flex items-center justify-between gap-3 px-3 py-2 rounded-lg transition-colors mb-1 text-secondary hover:bg-surface-hover hover:text-primary"
              >
                <div className="flex items-center gap-3">
                  {/* Иконка элемента */}
                  <div className="flex-shrink-0">{item.icon}</div>
                  {/* Название элемента (скрыто в компактном режиме на десктопе) */}
                  <span className={`text-sm font-medium truncate ${isCompact ? 'sm:hidden' : ''}`}>
                    {item.label}
                  </span>
                </div>
              </button>
            )}
          </div>
        ))}
      </nav>

    </div>
  );
}

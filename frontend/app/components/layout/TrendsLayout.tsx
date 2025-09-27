import { type ReactNode } from "react";
import { Sidebar } from "../heaader/Sidebar";
import { Header } from "../heaader/Header";
import { useSidebar } from "~/contexts/SidebarContext";
import type { Video } from "~/types/video";

/**
 * Пропсы для компонента TrendsLayout
 */
interface TrendsLayoutProps {
  children: ReactNode; // Контент страницы
  onVideoClick?: (video: Video) => void; // Обработчик клика по видео
}

/**
 * Layout для страницы трендов
 * Управляет общей структурой: Header, Sidebar и контентом страницы
 */
export function TrendsLayout({ children, onVideoClick }: TrendsLayoutProps) {
  // Используем контекст сайдбара
  const { 
    isSidebarCompact, 
    isSidebarOpen, 
    toggleSidebar 
  } = useSidebar();

  return (
    <div className="min-h-screen bg-background">
      {/* Фиксированный Header сверху */}
      <Header 
        isSidebarCompact={isSidebarCompact}
        onToggleSidebar={toggleSidebar}
      />
      
      <div className="flex relative">
        {/* Overlay для мобильных устройств */}
        {isSidebarOpen && (
          <div 
            className="fixed inset-0 bg-black/50 z-40 sm:hidden"
            onClick={toggleSidebar}
          />
        )}
        
        {/* Sidebar - адаптивное поведение */}
        <div className={`
          fixed left-0 top-16 bottom-0 z-50 transition-transform duration-300
          sm:transform-none
          ${isSidebarOpen ? 'translate-x-0' : '-translate-x-full sm:translate-x-0'}
        `}>
          <Sidebar 
            isCompact={isSidebarCompact}
            isOpen={isSidebarOpen}
            onToggleCompact={toggleSidebar}
          />
        </div>
        
        {/* Основной контент с отступами */}
        <div className={`
          bg-surface flex-1 transition-all duration-300 pt-16 overflow-hidden
          ${isSidebarCompact ? 'sm:ml-16' : 'sm:ml-64'}
        `}>
          {children}
        </div>
      </div>
    </div>
  );
}

import { Sidebar } from "../heaader/Sidebar";
import { MainContent } from "../content/MainContent";
import { Header } from "../heaader/Header";
import { CreateVideoModal } from "../content/CreateVideoModal";
import { useSidebar } from "~/contexts/SidebarContext";
import { useCreateVideoModal } from "~/contexts/CreateVideoModalContext";
import type { Video } from "~/types/video";

/**
 * Пропсы для компонента Layout
 */
interface LayoutProps {
  onVideoClick?: (video: Video) => void; // Обработчик клика по видео
}

/**
 * Главный компонент макета приложения
 * Управляет общей структурой: Header, Sidebar и MainContent
 */
export function Layout({ onVideoClick }: LayoutProps) {
  // Используем контекст сайдбара
  const { 
    isSidebarCompact, 
    isSidebarOpen, 
    toggleSidebar 
  } = useSidebar();

  // Используем контекст модального окна
  const { isOpen: isCreateVideoModalOpen, closeModal: closeCreateVideoModal } = useCreateVideoModal();

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
          <MainContent 
            onVideoClick={onVideoClick} 
          />
        </div>
      </div>

      {/* Модальное окно создания видео */}
      <CreateVideoModal 
        isOpen={isCreateVideoModalOpen}
        onClose={closeCreateVideoModal}
      />
    </div>
  );
}

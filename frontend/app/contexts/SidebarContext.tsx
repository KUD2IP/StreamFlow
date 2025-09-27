import { createContext, useContext, useState, type ReactNode } from 'react';

/**
 * Интерфейс контекста сайдбара
 */
interface SidebarContextType {
  isSidebarCompact: boolean;
  isSidebarOpen: boolean;
  setIsSidebarCompact: (compact: boolean) => void;
  setIsSidebarOpen: (open: boolean) => void;
  toggleSidebar: () => void;
}

/**
 * Контекст для управления состоянием сайдбара
 */
const SidebarContext = createContext<SidebarContextType | undefined>(undefined);

/**
 * Пропсы для провайдера контекста сайдбара
 */
interface SidebarProviderProps {
  children: ReactNode;
}

/**
 * Провайдер контекста сайдбара
 * Управляет состоянием сайдбара глобально
 */
export function SidebarProvider({ children }: SidebarProviderProps) {
  // Состояние свернутого/развернутого сайдбара
  const [isSidebarCompact, setIsSidebarCompact] = useState(false);
  // Состояние открытия/закрытия сайдбара на мобильных
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  
  /**
   * Переключает состояние сайдбара
   * На мобильных: открывает/закрывает overlay
   * На десктопе: переключает компактный/полный режим
   */
  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
    setIsSidebarCompact(!isSidebarCompact);
  };

  const value: SidebarContextType = {
    isSidebarCompact,
    isSidebarOpen,
    setIsSidebarCompact,
    setIsSidebarOpen,
    toggleSidebar,
  };

  return (
    <SidebarContext.Provider value={value}>
      {children}
    </SidebarContext.Provider>
  );
}

/**
 * Хук для использования контекста сайдбара
 */
export function useSidebar() {
  const context = useContext(SidebarContext);
  if (context === undefined) {
    throw new Error('useSidebar must be used within a SidebarProvider');
  }
  return context;
}

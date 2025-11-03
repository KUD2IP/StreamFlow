import { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';

interface CreateVideoModalContextType {
  isOpen: boolean;
  openModal: () => void;
  closeModal: () => void;
}

const CreateVideoModalContext = createContext<CreateVideoModalContextType | undefined>(undefined);

interface CreateVideoModalProviderProps {
  children: ReactNode;
}

export function CreateVideoModalProvider({ children }: CreateVideoModalProviderProps) {
  const [isOpen, setIsOpen] = useState(false);

  const openModal = () => setIsOpen(true);
  const closeModal = () => setIsOpen(false);

  return (
    <CreateVideoModalContext.Provider value={{ isOpen, openModal, closeModal }}>
      {children}
    </CreateVideoModalContext.Provider>
  );
}

export function useCreateVideoModal() {
  const context = useContext(CreateVideoModalContext);
  if (context === undefined) {
    throw new Error('useCreateVideoModal must be used within a CreateVideoModalProvider');
  }
  return context;
}

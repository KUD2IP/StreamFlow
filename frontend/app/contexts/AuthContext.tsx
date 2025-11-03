import React, { createContext, useContext, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import Keycloak from 'keycloak-js';
import { keycloakConfig } from '~/config/keycloak';
import { syncUser, type SyncResponse } from '~/services/userApi';

/**
 * Интерфейс пользователя
 */
interface User {
  id: string;
  username: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  roles: string[];
  synced?: boolean; // Флаг синхронизации с сервером
}

/**
 * Интерфейс контекста аутентификации
 */
interface AuthContextType {
  // Состояние аутентификации
  isAuthenticated: boolean;
  isLoading: boolean;
  user: User | null;
  
  // Методы аутентификации
  login: () => void;
  logout: () => void;
  register: () => void;
  
  // Синхронизация пользователя
  syncUser: () => Promise<boolean>;
  
  // Информация о токене
  token: string | null;
  refreshToken: () => Promise<boolean>;
  
  // Keycloak instance
  keycloak: Keycloak | null;
}

/**
 * Контекст аутентификации
 */
const AuthContext = createContext<AuthContextType | undefined>(undefined);

/**
 * Пропсы для провайдера аутентификации
 */
interface AuthProviderProps {
  children: ReactNode;
}

/**
 * Провайдер аутентификации
 * Управляет состоянием аутентификации через Keycloak
 */
export function AuthProvider({ children }: AuthProviderProps) {
  // Состояние аутентификации
  const [keycloak, setKeycloak] = useState<Keycloak | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);

  /**
   * Инициализация Keycloak (только в браузере)
   */
  useEffect(() => {
    // Проверяем, что мы в браузере
    if (typeof window === 'undefined') {
      setIsLoading(false);
      return;
    }

    const initKeycloak = async () => {
      try {
        // Создаем экземпляр Keycloak
        const keycloakInstance = new Keycloak({
          url: keycloakConfig.url,
          realm: keycloakConfig.realm,
          clientId: keycloakConfig.clientId,
        });

        // Инициализируем Keycloak
        const authenticated = await keycloakInstance.init(keycloakConfig.initOptions);

        setKeycloak(keycloakInstance);
        setIsAuthenticated(authenticated);

        if (authenticated && keycloakInstance.token) {
          setToken(keycloakInstance.token);
          
          // Получаем информацию о пользователе
          await keycloakInstance.loadUserProfile();
          const profile = keycloakInstance.profile;
          
          if (profile) {
            const userData = {
              id: keycloakInstance.subject || '',
              username: profile.username || '',
              email: profile.email,
              firstName: profile.firstName,
              lastName: profile.lastName,
              roles: keycloakInstance.realmAccess?.roles || [],
              synced: false, // Пока не синхронизирован
            };
            
            setUser(userData);
            
            // Автоматическая синхронизация с сервером
            try {
              const syncedUser = await syncUser(keycloakInstance.token);
              
              if (syncedUser) {
                setUser({
                  ...userData,
                  id: syncedUser.id,
                  synced: true,
                });
              }
            } catch (error) {
              // Не прерываем аутентификацию из-за ошибки синхронизации
            }
          }
        }

        // Настраиваем автоматическое обновление токена
        if (authenticated) {
          setInterval(() => {
            keycloakInstance.updateToken(70).then((refreshed) => {
              if (refreshed && keycloakInstance.token) {
                setToken(keycloakInstance.token);
              }
            }).catch(() => {
              setIsAuthenticated(false);
            });
          }, 60000); // Проверка каждую минуту
        }

        setIsLoading(false);
      } catch (error) {
        setIsLoading(false);
      }
    };

    initKeycloak();
  }, []);

  /**
   * Вход в систему
   */
  const login = () => {
    keycloak?.login(keycloakConfig.loginOptions);
  };

  /**
   * Регистрация
   */
  const register = () => {
    keycloak?.register();
  };

  /**
   * Выход из системы
   */
  const logout = () => {
    keycloak?.logout();
  };

  /**
   * Обновление токена
   */
  const refreshToken = async (): Promise<boolean> => {
    if (!keycloak) return false;
    
    try {
      const refreshed = await keycloak.updateToken(30);
      if (refreshed && keycloak.token) {
        setToken(keycloak.token);
      }
      return refreshed;
    } catch (error) {
      return false;
    }
  };

  /**
   * Синхронизация пользователя с сервером
   */
  const syncUserWithServer = async (): Promise<boolean> => {
    if (!keycloak || !keycloak.token) {
      return false;
    }

    try {
      const syncedUser = await syncUser(keycloak.token);
      
      if (syncedUser) {
        setUser(prevUser => {
          if (!prevUser) return null;
          return {
            ...prevUser,
            id: syncedUser.id,
            synced: true,
          };
        });
        
        return true;
      }
      
      return false;
    } catch (error) {
      return false;
    }
  };

  /**
   * Значение контекста
   */
  const contextValue: AuthContextType = {
    isAuthenticated,
    isLoading,
    user,
    login,
    logout,
    register,
    syncUser: syncUserWithServer,
    token,
    refreshToken,
    keycloak,
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Хук для использования контекста аутентификации
 */
export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth должен использоваться внутри AuthProvider');
  }
  return context;
}

import React, { createContext, useContext, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import Keycloak from 'keycloak-js';
import { keycloakConfig } from '~/config/keycloak';

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
  login: () => Promise<void>;
  logout: () => Promise<void>;
  
  // Информация о токене
  token: string | null;
  refreshToken: () => Promise<boolean>;
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
        const kc = new Keycloak(keycloakConfig);
        
        // Инициализация Keycloak
        const authenticated = await kc.init(keycloakConfig.initOptions);
        
        setKeycloak(kc);
        setIsAuthenticated(authenticated);
        
        if (authenticated) {
          // Пользователь аутентифицирован
          const userInfo = await kc.loadUserInfo();
          setUser({
            id: kc.subject || '',
            username: kc.tokenParsed?.preferred_username || '',
            email: kc.tokenParsed?.email || '',
            firstName: kc.tokenParsed?.given_name || '',
            lastName: kc.tokenParsed?.family_name || '',
            roles: kc.tokenParsed?.realm_access?.roles || [],
          });
          setToken(kc.token || null);
        }
        
        setIsLoading(false);
      } catch (error) {
        console.error('Ошибка инициализации Keycloak:', error);
        setIsLoading(false);
      }
    };

    initKeycloak();
  }, []);

  /**
   * Вход в систему
   */
  const login = async (): Promise<void> => {
    if (keycloak) {
      try {
        await keycloak.login();
      } catch (error) {
        console.error('Ошибка входа:', error);
      }
    }
  };

  /**
   * Выход из системы
   */
  const logout = async (): Promise<void> => {
    if (keycloak) {
      try {
        await keycloak.logout();
      } catch (error) {
        console.error('Ошибка выхода:', error);
      }
    }
  };

  /**
   * Обновление токена
   */
  const refreshToken = async (): Promise<boolean> => {
    if (keycloak) {
      try {
        const refreshed = await keycloak.updateToken(30);
        if (refreshed) {
          setToken(keycloak.token || null);
        }
        return refreshed;
      } catch (error) {
        console.error('Ошибка обновления токена:', error);
        return false;
      }
    }
    return false;
  };

  /**
   * Автоматическое обновление токена
   */
  useEffect(() => {
    if (keycloak && isAuthenticated) {
      const interval = setInterval(async () => {
        try {
          await keycloak.updateToken(15);
          setToken(keycloak.token || null);
        } catch (error) {
          console.error('Ошибка автоматического обновления токена:', error);
        }
      }, 30000); // Обновление каждые 30 секунд

      return () => clearInterval(interval);
    }
  }, [keycloak, isAuthenticated]);

  /**
   * Значение контекста
   */
  const contextValue: AuthContextType = {
    isAuthenticated,
    isLoading,
    user,
    login,
    logout,
    token,
    refreshToken,
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

/**
 * Конфигурация Keycloak
 * Настройки для подключения к серверу аутентификации
 */
export const keycloakConfig = {
  // URL сервера Keycloak
  url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8080',
  
  // Идентификатор клиента в Keycloak
  realm: import.meta.env.VITE_KEYCLOAK_REALM || 'StreamFlow',
  
  // Идентификатор клиента
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'streamflow-frontend',
  
  // Настройки инициализации
  initOptions: {
    // Режим проверки токена
    onLoad: 'check-sso' as const,
    
    // Включить silent check-sso для автоматического обновления токенов
    silentCheckSsoRedirectUri: typeof window !== 'undefined' 
      ? window.location.origin + '/silent-check-sso.html'
      : '/silent-check-sso.html',
    
    // Включить проверку токена при каждой загрузке страницы
    checkLoginIframe: true,
    
    // Интервал проверки токена (в секундах)
    checkLoginIframeInterval: 5,
    
    // Включить автоматическое обновление токенов
    enableLogging: process.env.NODE_ENV === 'development',
  },
  
  // Настройки токена
  tokenOptions: {
    // Включить автоматическое обновление токенов
    enableAutoRefresh: true,
    
    // Интервал автоматического обновления (в секундах)
    autoRefreshInterval: 30,
  },
};

/**
 * Типы для конфигурации Keycloak
 */
export type KeycloakConfig = typeof keycloakConfig;

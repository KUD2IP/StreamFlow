/**
 * API сервис для синхронизации пользователей
 */

const API_BASE_URL = 'http://localhost:8081/api/v1';

/**
 * Интерфейс ответа синхронизации
 */
export interface SyncResponse {
  id: string;
  username: string;
}

/**
 * Синхронизирует текущего пользователя с сервером
 * Вызывается автоматически после успешного входа в Keycloak
 */
export async function syncUser(token: string): Promise<SyncResponse | null> {
  try {
    const response = await fetch(`${API_BASE_URL}/users/sync`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      return null;
    }

    const userData = await response.json();
    return userData;
  } catch (error) {
    return null;
  }
}

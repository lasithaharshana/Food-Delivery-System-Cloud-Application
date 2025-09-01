export const AUTH_URL = 'http://localhost:8081';
export const API_BASE_URL = 'http://localhost:8080'; // Use relative URL for proxy

// Auth
export const API_AUTH_REGISTER = `${AUTH_URL}/api/auth/register`;
export const API_AUTH_LOGIN = `${AUTH_URL}/api/auth/login`;

// Food - Use relative URL to go through proxy
export const API_FOODS = `${API_BASE_URL}/foods`;
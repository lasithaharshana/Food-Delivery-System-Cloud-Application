export const API_BASE_URL = 'http://localhost:8080'; 

// Auth endpoints through API Gateway
export const API_AUTH_REGISTER = `${API_BASE_URL}/api/auth/register`;
export const API_AUTH_LOGIN = `${API_BASE_URL}/api/auth/login`;

// Food - Use relative URL to go through proxy
export const API_FOODS = `${API_BASE_URL}/foods`;

// Food endpoints through API Gateway
export const API_FOOD_LIST = `${API_BASE_URL}/api/foods`;
export const API_FOOD_CREATE = `${API_BASE_URL}/api/foods`;
export const API_FOOD_UPDATE = (id: string) => `${API_BASE_URL}/api/foods/${id}`;
export const API_FOOD_DELETE = (id: string) => `${API_BASE_URL}/api/foods/${id}`;

// Order endpoints through API Gateway
export const API_ORDER_LIST = `${API_BASE_URL}/api/order`;
export const API_ORDER_CREATE = `${API_BASE_URL}/api/order`;
export const API_ORDER_UPDATE = (id: string) => `${API_BASE_URL}/api/order/${id}`;
export const API_ORDER_DELETE = (id: string) => `${API_BASE_URL}/api/order/${id}`;

// User endpoints through API Gateway
export const API_USER_PROFILE = `${API_BASE_URL}/api/user/profile`;
export const API_USER_UPDATE = `${API_BASE_URL}/api/user/profile`;
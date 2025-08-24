export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  role: 'CUSTOMER' | 'RESTAURANT';
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  address: string;
  restaurantName?: string;
}

export interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  accessToken: string | null;
}

export interface LoginCredentials {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterData {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  role: 'CUSTOMER' | 'RESTAURANT';
  restaurantName?: string;
  address: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  user: User;
}
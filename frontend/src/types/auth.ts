export interface User {
  id: string;
  email: string;
  name: string;
  role: 'user' | 'restaurant' | 'admin';
  phone?: string;
  address?: string;
  restaurantId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  email: string;
  password: string;
  name: string;
  phone?: string;
  role: 'user' | 'restaurant';
  restaurantName?: string;
  address?: string;
}
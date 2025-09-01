import axios from 'axios';
import { getStoredToken, getStoredUser } from './utils';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: '/api', // Use relative path to go through Vite proxy
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = getStoredToken();
    console.log('🔑 Token check:', token ? 'Token found' : 'No token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('✅ Authorization header added:', config.headers.Authorization);
    } else {
      console.log('❌ No token available for request');
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.log('🚨 API Error:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      url: error.config?.url,
      method: error.config?.method,
      headers: error.config?.headers
    });
    
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
      window.location.href = '/auth';
    }
    return Promise.reject(error);
  }
);

// Types
export interface Restaurant {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string | null;
  role: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  restaurantName: string;
  address: string;
  imageUrl: string | null;
}

export interface Food {
  id: number;
  restaurantId: number;
  name: string;
  description: string;
  price: number;
  quantity: number;
  category: string;
  imageUrl: string;
  status: string;
  popular: boolean;
}

export interface CreateFoodRequest {
  name: string;
  description: string;
  price: number;
  quantity: number;
  category: string;
  imageUrl: string;
  status: string; // 'available' | 'unavailable'
  popular: boolean;
}

export interface OrderItem {
  foodId: number;
  quantity: number;
}

export interface CreateOrderRequest {
  note: string;
  cost: number;
  orderItems: OrderItem[];
}

export interface OrderItemResponse {
  id: number;
  foodId: number;
  quantity: number;
}

export interface Order {
  id: number;
  customerId: number;
  note: string;
  status: string;
  cost: number;
  createdAt: string;
  updatedAt: string;
  orderItems: OrderItemResponse[];
}

// API functions
export const restaurantApi = {
  // Get all restaurants
  getAllRestaurants: async (): Promise<Restaurant[]> => {
    const response = await api.get('/users/role/RESTAURANT');
    return response.data;
  },
};

export const foodApi = {
  // Get all foods
  getAllFoods: async (): Promise<Food[]> => {
    // Explicitly include Authorization in case interceptor is bypassed
    const response = await api.get('/foods', {
      headers: {
        Authorization: `Bearer ${getStoredToken()}`,
      },
    });
    return response.data;
  },
  
  // Get foods by restaurant ID
  getFoodsByRestaurant: async (restaurantId: number): Promise<Food[]> => {
    const response = await api.get('/foods', {
      headers: {
        Authorization: `Bearer ${getStoredToken()}`,
      },
    });
    const allFoods = response.data as Food[];
    return allFoods.filter((food: Food) => food.restaurantId === restaurantId);
  },

  // Create a new food item for current restaurant (uses user.id)
  createFood: async (foodData: CreateFoodRequest): Promise<Food> => {
    const token = getStoredToken();
    const user = getStoredUser();
    if (!token) {
      throw new Error('No authentication token found');
    }
    if (!user || !user.id) {
      throw new Error('No user information found');
    }

    const payload = {
      ...foodData,
      restaurantId: user.id,
    };

    const response = await api.post('/foods', payload, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data as Food;
  },
};

export const orderApi = {
  // Create new order
  createOrder: async (orderData: CreateOrderRequest): Promise<Order> => {
    const response = await api.post('/order', orderData, {
      headers: {
        Authorization: `Bearer ${getStoredToken()}`,
      },
    });
    return response.data;
  },
  
  // Get all orders for current user
  getUserOrders: async (): Promise<Order[]> => {
    const response = await api.get('/order', {
      headers: {
        Authorization: `Bearer ${getStoredToken()}`,
      },
    });
    return response.data;
  },

  // Get all orders (for restaurant to filter their own)
  getAllOrders: async (): Promise<Order[]> => {
    const response = await api.get('/order', {
      headers: {
        Authorization: `Bearer ${getStoredToken()}`,
      },
    });
    return response.data;
  },

  // Update order status
  updateOrderStatus: async (orderId: number, updateData: {
    note?: string;
    status?: string;
    cost?: number;
    orderItems?: { id: number; foodId: number; quantity: number; }[];
  }): Promise<Order> => {
    const response = await api.put(`/order/${orderId}`, updateData, {
      headers: {
        Authorization: `Bearer ${getStoredToken()}`,
      },
    });
    return response.data;
  },
};

// User API for getting customer details
export const userApi = {
  // Get user by ID
  getUserById: async (userId: number): Promise<Restaurant> => {
    const response = await api.get(`/users/${userId}`, {
      headers: {
        Authorization: `Bearer ${getStoredToken()}`,
      },
    });
    return response.data;
  },
};

export default api;

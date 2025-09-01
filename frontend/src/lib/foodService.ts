import { API_FOODS } from './api-endpoints';
import { getStoredToken, getStoredUser } from './utils';

export interface AddFoodRequest {
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

export interface AddFoodResponse {
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

export interface FoodItem {
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

// Test function to check API connectivity
export const testApiConnection = async (): Promise<boolean> => {
  try {
    console.log('Testing API connection to:', API_FOODS);
    console.log('Actual URL being used:', window.location.origin + API_FOODS);
    const response = await fetch(API_FOODS, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    console.log('API test response status:', response.status);
    return response.ok;
  } catch (error) {
    console.error('API connection test failed:', error);
    return false;
  }
};

// Test function to check POST request without auth
export const testPostRequest = async (): Promise<boolean> => {
  try {
    console.log('Testing POST request to:', API_FOODS);
    const testData = {
      restaurantId: 8,
      name: 'Test Item',
      description: 'Test Description',
      price: 10.99,
      quantity: 5,
      category: 'Test',
      imageUrl: 'http://example.com/test.jpg',
      status: 'available',
      popular: false,
    };
    
    const response = await fetch(API_FOODS, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(testData),
    });
    console.log('POST test response status:', response.status);
    return response.ok;
  } catch (error) {
    console.error('POST test failed:', error);
    return false;
  }
};

export const addFood = async (foodData: Omit<AddFoodRequest, 'restaurantId'>): Promise<AddFoodResponse> => {
  const token = getStoredToken();
  const user = getStoredUser();
  
  console.log('addFood - Token:', token);
  console.log('addFood - User:', user);
  console.log('addFood - API URL:', API_FOODS);
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  if (!user || !user.id) {
    throw new Error('No user information found');
  }

  const requestData: AddFoodRequest = {
    ...foodData,
    restaurantId: user.id,
    quantity: foodData.quantity || 20, // Default quantity
    status: foodData.status || 'available', // Default status
  };

  console.log('addFood - Request Data:', requestData);

  try {
    const response = await fetch(API_FOODS, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(requestData),
    });

    console.log('addFood - Response status:', response.status);
    console.log('addFood - Response headers:', Object.fromEntries(response.headers.entries()));

    if (!response.ok) {
      const errorText = await response.text();
      console.error('addFood - Error response text:', errorText);
      
      let errorData;
      try {
        errorData = JSON.parse(errorText);
      } catch (e) {
        errorData = { message: errorText };
      }
      
      console.error('addFood - Error response:', errorData);
      throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
    }

    const result = await response.json();
    console.log('addFood - Success response:', result);
    return result;
  } catch (error) {
    console.error('addFood - Fetch error:', error);
    throw error;
  }
};

export const getFoodsByRestaurant = async (): Promise<FoodItem[]> => {
  const token = getStoredToken();
  const user = getStoredUser();
  
  console.log('getFoodsByRestaurant - Token:', token);
  console.log('getFoodsByRestaurant - User:', user);
  console.log('getFoodsByRestaurant - API URL:', API_FOODS);
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  if (!user || !user.id) {
    throw new Error('No user information found');
  }

  try {
    const response = await fetch(API_FOODS, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    console.log('getFoodsByRestaurant - Response status:', response.status);
    console.log('getFoodsByRestaurant - Response headers:', Object.fromEntries(response.headers.entries()));

    if (!response.ok) {
      const errorText = await response.text();
      console.error('getFoodsByRestaurant - Error response text:', errorText);
      
      let errorData;
      try {
        errorData = JSON.parse(errorText);
      } catch (e) {
        errorData = { message: errorText };
      }
      
      console.error('getFoodsByRestaurant - Error response:', errorData);
      throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
    }

    const allFoods: FoodItem[] = await response.json();
    console.log('getFoodsByRestaurant - All foods:', allFoods);
    
    // Filter foods by current restaurant ID
    const filteredFoods = allFoods.filter(food => food.restaurantId === user.id);
    console.log('getFoodsByRestaurant - Filtered foods for restaurant', user.id, ':', filteredFoods);
    
    return filteredFoods;
  } catch (error) {
    console.error('getFoodsByRestaurant - Fetch error:', error);
    throw error;
  }
};

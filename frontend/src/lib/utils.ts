import { type ClassValue, clsx } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

// Token management utilities
export const getStoredToken = (): string | null => {
  return localStorage.getItem('accessToken');
};

export const setStoredToken = (token: string): void => {
  localStorage.setItem('accessToken', token);
};

export const removeStoredToken = (): void => {
  localStorage.removeItem('accessToken');
};

export const getStoredUser = (): any => {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

export const setStoredUser = (user: any): void => {
  localStorage.setItem('user', JSON.stringify(user));
};

export const removeStoredUser = (): void => {
  localStorage.removeItem('user');
};

// API call utility with authentication
export const apiCall = async (url: string, options: RequestInit = {}): Promise<Response> => {
  const token = getStoredToken();
  
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` }),
    ...options.headers,
  };

  return fetch(url, {
    ...options,
    headers,
  });
};

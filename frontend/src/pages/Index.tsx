import React from 'react';
import { useLocation } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import Auth from './Auth';
import Dashboard from './Dashboard';
import MyOrders from './MyOrders';
import Profile from './Profile';
import RestaurantDashboard from './RestaurantDashboard';
import RestaurantMenu from './RestaurantMenu';
import RestaurantOrders from './RestaurantOrders';
import AdminDashboard from './AdminDashboard';

const Index = () => {
  const { user, isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="text-center">
          <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Auth />;
  }

  // Route based on user role and current path
  if (user?.role === 'admin') {
    return <AdminDashboard />;
  }
  
  if (user?.role === 'restaurant') {
    // Handle restaurant routes
    switch (location.pathname) {
      case '/restaurant/menu':
        return <RestaurantMenu />;
      case '/restaurant/orders':
        return <RestaurantOrders />;
      case '/restaurant':
      default:
        return <RestaurantDashboard />;
    }
  }
  
  // Customer routes
  switch (location.pathname) {
    case '/orders':
      return <MyOrders />;
    case '/profile':
      return <Profile />;
    case '/dashboard':
    case '/':
    default:
      return <Dashboard />;
  }
};

export default Index;

import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/contexts/AuthContext';
import { NotificationBell } from '@/components/notifications/NotificationBell';
import { 
  Home, 
  ShoppingBag, 
  Store, 
  Settings, 
  LogOut, 
  User,
  UtensilsCrossed
} from 'lucide-react';

const Navigation = () => {
  const { user, logout } = useAuth();
  const location = useLocation();

  const getRoleIcon = (role: string) => {
    switch (role) {
      case 'RESTAURANT':
        return <Store className="w-4 h-4" />;
      default:
        return <User className="w-4 h-4" />;
    }
  };

  const getRoleLinks = () => {
    switch (user?.role) {
      case 'RESTAURANT':
        return [
          { to: '/restaurant', label: 'Dashboard', icon: <Home className="w-4 h-4" /> },
          { to: '/restaurant/menu', label: 'Menu', icon: <UtensilsCrossed className="w-4 h-4" /> },
// ...existing code...
        ];
      default:
        return [
          { to: '/dashboard', label: 'Browse', icon: <Home className="w-4 h-4" /> },
          { to: '/my-orders', label: 'My Orders', icon: <ShoppingBag className="w-4 h-4" /> },
          { to: '/profile', label: 'Profile', icon: <User className="w-4 h-4" /> },
// ...existing code...
        ];
    }
  };

  // Get user's display name
  const getUserDisplayName = () => {
    if (!user) return 'Guest';
    return `${user.firstName} ${user.lastName}`;
  };

  // Get user's role display name
  const getUserRoleDisplay = () => {
    if (!user) return '';
    return user.role === 'CUSTOMER' ? 'Customer' : 'Restaurant Owner';
  };

  if (!user) return null;

  const isActiveLink = (path: string) => location.pathname === path;

  return (
    <nav className="bg-card border-b border-border shadow-card">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-gradient-primary rounded-lg flex items-center justify-center">
              <UtensilsCrossed className="w-5 h-5 text-primary-foreground" />
            </div>
            <span className="text-xl font-bold bg-gradient-primary bg-clip-text text-transparent">
              QuickEats
            </span>
          </Link>

          {/* Navigation Links */}
          <div className="hidden md:flex items-center space-x-4">
            {getRoleLinks().map((link) => (
              <Link key={link.to} to={link.to}>
                <Button
                  variant={isActiveLink(link.to) ? "default" : "ghost"}
                  size="sm"
                  className="flex items-center space-x-2"
                >
                  {link.icon}
                  <span>{link.label}</span>
                </Button>
              </Link>
            ))}
          </div>

          {/* User Menu */}
          <div className="flex items-center space-x-4">
            <NotificationBell userRole={user.role === 'RESTAURANT' ? 'restaurant' : 'customer'} unreadCount={3} />
            <div className="flex items-center space-x-2 text-sm">
              {getRoleIcon(user.role)}
              <span className="hidden sm:block text-muted-foreground">
                {getUserDisplayName()}
              </span>
              <span className="px-2 py-1 bg-primary/10 text-primary rounded-full text-xs font-medium capitalize">
                {getUserRoleDisplay()}
              </span>
            </div>
            
            <Button
              variant="ghost"
              size="sm"
              onClick={() => {
                localStorage.clear();
                logout();
                window.location.href = window.location.origin + '/';
              }}
              className="flex items-center space-x-2"
            >
              <LogOut className="w-4 h-4" />
              <span className="hidden sm:block">Logout</span>
            </Button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navigation;
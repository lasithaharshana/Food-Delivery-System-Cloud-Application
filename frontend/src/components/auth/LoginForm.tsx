import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useAuth } from '@/contexts/AuthContext';
import { toast } from 'sonner';

interface LoginFormProps {
  onToggle: () => void;
}

const LoginForm: React.FC<LoginFormProps> = ({ onToggle }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login, isLoading } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await login({ email, password });
      toast.success('Welcome back!');
    } catch (error) {
      toast.error('Invalid email or password');
    }
  };

  const handleDemoLogin = (role: 'user' | 'restaurant' | 'admin') => {
    const demoCredentials = {
      user: { email: 'user@demo.com', password: 'demo123' },
      restaurant: { email: 'restaurant@demo.com', password: 'demo123' },
      admin: { email: 'admin@demo.com', password: 'demo123' },
    };
    
    setEmail(demoCredentials[role].email);
    setPassword(demoCredentials[role].password);
  };

  return (
    <Card className="w-full max-w-md mx-auto shadow-card">
      <CardHeader className="space-y-1">
        <CardTitle className="text-2xl font-bold text-center">Welcome Back</CardTitle>
        <p className="text-muted-foreground text-center">
          Sign in to your QuickEats account
        </p>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          
          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <Button 
            type="submit" 
            className="w-full" 
            variant="hero"
            disabled={isLoading}
          >
            {isLoading ? 'Signing In...' : 'Sign In'}
          </Button>
        </form>

        <div className="mt-6">
          <div className="text-center text-sm text-muted-foreground mb-3">
            Try demo accounts:
          </div>
          <div className="space-y-2">
            <Button
              variant="outline"
              size="sm"
              className="w-full"
              onClick={() => handleDemoLogin('user')}
            >
              Demo Customer
            </Button>
            <Button
              variant="outline"
              size="sm"
              className="w-full"
              onClick={() => handleDemoLogin('restaurant')}
            >
              Demo Restaurant
            </Button>
            <Button
              variant="outline"
              size="sm"
              className="w-full"
              onClick={() => handleDemoLogin('admin')}
            >
              Demo Admin
            </Button>
          </div>
        </div>

        <div className="mt-6 text-center">
          <button
            type="button"
            onClick={onToggle}
            className="text-sm text-primary hover:text-primary/80 underline"
          >
            Don't have an account? Sign up
          </button>
        </div>
      </CardContent>
    </Card>
  );
};

export default LoginForm;
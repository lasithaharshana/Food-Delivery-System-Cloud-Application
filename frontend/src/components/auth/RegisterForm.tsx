import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { useAuth } from '@/contexts/AuthContext';
import { toast } from 'sonner';

interface RegisterFormProps {
  onToggle: () => void;
}

const RegisterForm: React.FC<RegisterFormProps> = ({ onToggle }) => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    name: '',
    phone: '',
    role: 'user' as 'user' | 'restaurant',
    restaurantName: '',
    address: '',
  });
  
  const { register, isLoading } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await register(formData);
      toast.success('Account created successfully!');
    } catch (error) {
      toast.error('Failed to create account');
    }
  };

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  return (
    <Card className="w-full max-w-md mx-auto shadow-card">
      <CardHeader className="space-y-1">
        <CardTitle className="text-2xl font-bold text-center">Join QuickEats</CardTitle>
        <p className="text-muted-foreground text-center">
          Create your account and start ordering
        </p>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="name">Full Name</Label>
            <Input
              id="name"
              placeholder="Enter your full name"
              value={formData.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              placeholder="Enter your email"
              value={formData.email}
              onChange={(e) => handleInputChange('email', e.target.value)}
              required
            />
          </div>
          
          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              type="password"
              placeholder="Create a password"
              value={formData.password}
              onChange={(e) => handleInputChange('password', e.target.value)}
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="phone">Phone Number</Label>
            <Input
              id="phone"
              type="tel"
              placeholder="Enter your phone number"
              value={formData.phone}
              onChange={(e) => handleInputChange('phone', e.target.value)}
            />
          </div>

          <div className="space-y-3">
            <Label>Account Type</Label>
            <RadioGroup
              value={formData.role}
              onValueChange={(value) => handleInputChange('role', value)}
            >
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="user" id="user" />
                <Label htmlFor="user">Customer - Order food from restaurants</Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="restaurant" id="restaurant" />
                <Label htmlFor="restaurant">Restaurant - Manage your restaurant</Label>
              </div>
            </RadioGroup>
          </div>

          {formData.role === 'restaurant' && (
            <div className="space-y-2">
              <Label htmlFor="restaurantName">Restaurant Name</Label>
              <Input
                id="restaurantName"
                placeholder="Enter your restaurant name"
                value={formData.restaurantName}
                onChange={(e) => handleInputChange('restaurantName', e.target.value)}
                required
              />
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="address">Address</Label>
            <Input
              id="address"
              placeholder="Enter your address"
              value={formData.address}
              onChange={(e) => handleInputChange('address', e.target.value)}
            />
          </div>

          <Button 
            type="submit" 
            className="w-full" 
            variant="hero"
            disabled={isLoading}
          >
            {isLoading ? 'Creating Account...' : 'Create Account'}
          </Button>
        </form>

        <div className="mt-6 text-center">
          <button
            type="button"
            onClick={onToggle}
            className="text-sm text-primary hover:text-primary/80 underline"
          >
            Already have an account? Sign in
          </button>
        </div>
      </CardContent>
    </Card>
  );
};

export default RegisterForm;
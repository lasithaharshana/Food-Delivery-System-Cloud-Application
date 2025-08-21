import React, { useState } from 'react';
import LoginForm from '@/components/auth/LoginForm';
import RegisterForm from '@/components/auth/RegisterForm';
import heroFood from '@/assets/hero-food.jpg';

const Auth = () => {
  const [isLogin, setIsLogin] = useState(true);

  return (
    <div className="min-h-screen flex">
      {/* Left side - Auth Form */}
      <div className="flex-1 flex items-center justify-center p-8 bg-background">
        <div className="w-full max-w-md">
          {isLogin ? (
            <LoginForm onToggle={() => setIsLogin(false)} />
          ) : (
            <RegisterForm onToggle={() => setIsLogin(true)} />
          )}
        </div>
      </div>

      {/* Right side - Hero Image */}
      <div className="hidden lg:flex flex-1 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-primary opacity-90"></div>
        <img
          src={heroFood}
          alt="Delicious food delivery"
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 flex items-center justify-center p-12">
          <div className="text-center text-primary-foreground">
            <h1 className="text-4xl font-bold mb-4 animate-slide-up">
              Welcome to QuickEats
            </h1>
            <p className="text-xl opacity-90 mb-8 animate-slide-up animation-delay-200">
              Your favorite restaurants, delivered fast
            </p>
            <div className="flex items-center justify-center space-x-8 text-sm opacity-80">
              <div className="text-center">
                <div className="text-2xl font-bold">500+</div>
                <div>Restaurants</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold">30min</div>
                <div>Avg Delivery</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold">4.8★</div>
                <div>Rating</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Auth;
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { useAuth } from '@/contexts/AuthContext';
import Navigation from '@/components/layout/Navigation';
import { restaurantApi, foodApi, orderApi, Restaurant, Food, OrderItem } from '@/lib/api-service';
import { getStoredToken } from '@/lib/utils';
import { 
  ArrowLeft,
  Star,
  MapPin,
  Clock,
  Phone,
  ShoppingCart,
  Plus,
  Minus,
  Loader2,
  CheckCircle
} from 'lucide-react';

interface CartItem extends Food {
  quantity: number;
}

const RestaurantDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  const [restaurant, setRestaurant] = useState<Restaurant | null>(null);
  const [foods, setFoods] = useState<Food[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [orderNote, setOrderNote] = useState('');
  const [isPlacingOrder, setIsPlacingOrder] = useState(false);
  const [orderSuccess, setOrderSuccess] = useState(false);

  useEffect(() => {
    if (id) {
      const token = getStoredToken();
      if (!token) {
        setError('You need to sign in to view this restaurant.');
        navigate('/auth');
        return;
      }
      fetchRestaurantData();
    }
  }, [id]);

  const fetchRestaurantData = async () => {
    try {
      setLoading(true);
      const restaurantsData = await restaurantApi.getAllRestaurants();
      const selectedRestaurant = restaurantsData.find(r => r.id === parseInt(id!));
      if (!selectedRestaurant) {
        setError('Restaurant not found');
        return;
      }
      setRestaurant(selectedRestaurant);
      // Fetch foods securely and filter server response by restaurantId
      const restaurantFoods = await foodApi.getFoodsByRestaurant(parseInt(id!));
      setFoods(restaurantFoods);
    } catch (err: any) {
      console.error('Error fetching restaurant data:', err);
      if (err?.response?.status === 403) {
        setError('Access denied. Please sign in again.');
      } else {
        const message = err instanceof Error ? err.message : 'Failed to load restaurant data';
        setError(message);
      }
    } finally {
      setLoading(false);
    }
  };

  const addToCart = (food: Food) => {
    setCart(prevCart => {
      const existingItem = prevCart.find(item => item.id === food.id);
      if (existingItem) {
        return prevCart.map(item =>
          item.id === food.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      } else {
        return [...prevCart, { ...food, quantity: 1 }];
      }
    });
  };

  const removeFromCart = (foodId: number) => {
    setCart(prevCart => {
      const existingItem = prevCart.find(item => item.id === foodId);
      if (existingItem && existingItem.quantity > 1) {
        return prevCart.map(item =>
          item.id === foodId
            ? { ...item, quantity: item.quantity - 1 }
            : item
        );
      } else {
        return prevCart.filter(item => item.id !== foodId);
      }
    });
  };

  const getCartTotal = () => {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const getCartItemCount = () => {
    return cart.reduce((total, item) => total + item.quantity, 0);
  };

  const placeOrder = async () => {
    if (cart.length === 0) return;

    try {
      setIsPlacingOrder(true);
      
      const orderItems: OrderItem[] = cart.map(item => ({
        foodId: item.id,
        quantity: item.quantity
      }));

      const orderData = {
        note: orderNote,
        cost: getCartTotal(),
        orderItems
      };

      await orderApi.createOrder(orderData);
      
      setOrderSuccess(true);
      setCart([]);
      setOrderNote('');
      
      // Redirect to orders page after 2 seconds
      setTimeout(() => {
        navigate('/my-orders');
      }, 2000);
      
    } catch (err) {
      console.error('Error placing order:', err);
      setError('Failed to place order');
    } finally {
      setIsPlacingOrder(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-background">
        <Navigation />
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="flex items-center space-x-2">
            <Loader2 className="w-6 h-6 animate-spin" />
            <span>Loading restaurant...</span>
          </div>
        </div>
      </div>
    );
  }

  if (error || !restaurant) {
    return (
      <div className="min-h-screen bg-background">
        <Navigation />
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="text-center">
            <p className="text-red-500 mb-4">{error || 'Restaurant not found'}</p>
            <Button onClick={() => navigate('/')}>Back to Dashboard</Button>
          </div>
        </div>
      </div>
    );
  }

  if (orderSuccess) {
    return (
      <div className="min-h-screen bg-background">
        <Navigation />
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="text-center">
            <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
            <h2 className="text-2xl font-bold mb-2">Order Placed Successfully!</h2>
            <p className="text-muted-foreground">Redirecting to your orders...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Back Button */}
        <Button
          variant="ghost"
          onClick={() => navigate('/')}
          className="mb-6 flex items-center space-x-2"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Restaurants</span>
        </Button>

        {/* Restaurant Header */}
        <Card className="mb-8">
          <CardContent className="p-6">
            <div className="flex flex-col md:flex-row gap-6">
              <div className="flex-shrink-0">
                <img
                  src={restaurant.imageUrl || 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400'}
                  alt={restaurant.restaurantName}
                  className="w-32 h-32 object-cover rounded-lg"
                  onError={(e) => {
                    const target = e.target as HTMLImageElement;
                    target.src = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400';
                  }}
                />
              </div>
              <div className="flex-1">
                <h1 className="text-3xl font-bold mb-2">{restaurant.restaurantName}</h1>
                <p className="text-muted-foreground mb-4">
                  {restaurant.firstName} {restaurant.lastName}
                </p>
                <div className="flex flex-wrap gap-4 text-sm text-muted-foreground">
                  <div className="flex items-center space-x-1">
                    <MapPin className="w-4 h-4" />
                    <span>{restaurant.address}</span>
                  </div>
                  {restaurant.phoneNumber && (
                    <div className="flex items-center space-x-1">
                      <Phone className="w-4 h-4" />
                      <span>{restaurant.phoneNumber}</span>
                    </div>
                  )}
                  <div className="flex items-center space-x-1">
                    <Clock className="w-4 h-4" />
                    <span>25-35 min delivery</span>
                  </div>
                </div>
                {restaurant.isActive && (
                  <Badge className="mt-2 bg-green-500 text-white">
                    Open for Orders
                  </Badge>
                )}
              </div>
            </div>
          </CardContent>
        </Card>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Menu Section */}
          <div className="lg:col-span-2">
            <h2 className="text-2xl font-bold mb-6">Menu</h2>
            {foods.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-muted-foreground">No menu items available.</p>
              </div>
            ) : (
              <div className="space-y-4">
                {foods.map((food) => (
                  <Card key={food.id} className="overflow-hidden">
                    <CardContent className="p-4">
                      <div className="flex gap-4">
                        <img
                          src={food.imageUrl || 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400'}
                          alt={food.name}
                          className="w-20 h-20 object-cover rounded-lg flex-shrink-0"
                          onError={(e) => {
                            const target = e.target as HTMLImageElement;
                            target.src = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400';
                          }}
                        />
                        <div className="flex-1">
                          <div className="flex justify-between items-start mb-2">
                            <h3 className="font-semibold">{food.name}</h3>
                            <span className="font-bold text-primary">${food.price}</span>
                          </div>
                          <p className="text-sm text-muted-foreground mb-2">{food.description}</p>
                          <div className="flex items-center justify-between">
                            <Badge variant="secondary">{food.category}</Badge>
                            <Button
                              size="sm"
                              onClick={() => addToCart(food)}
                              disabled={food.status !== 'available'}
                            >
                              <Plus className="w-4 h-4" />
                            </Button>
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>

          {/* Cart Section */}
          <div className="lg:col-span-1">
            <Card className="sticky top-8">
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <ShoppingCart className="w-5 h-5" />
                  <span>Your Order ({getCartItemCount()})</span>
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                {cart.length === 0 ? (
                  <p className="text-muted-foreground text-center py-8">
                    Your cart is empty
                  </p>
                ) : (
                  <>
                    <div className="space-y-3 max-h-64 overflow-y-auto">
                      {cart.map((item) => (
                        <div key={item.id} className="flex items-center justify-between p-3 bg-muted rounded-lg">
                          <div className="flex-1">
                            <h4 className="font-medium">{item.name}</h4>
                            <p className="text-sm text-muted-foreground">${item.price} x {item.quantity}</p>
                          </div>
                          <div className="flex items-center space-x-2">
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => removeFromCart(item.id)}
                            >
                              <Minus className="w-3 h-3" />
                            </Button>
                            <span className="w-8 text-center">{item.quantity}</span>
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => addToCart(item)}
                            >
                              <Plus className="w-3 h-3" />
                            </Button>
                          </div>
                        </div>
                      ))}
                    </div>
                    
                    <div className="border-t pt-4">
                      <div className="flex justify-between items-center mb-4">
                        <span className="font-semibold">Total:</span>
                        <span className="font-bold text-lg">${getCartTotal().toFixed(2)}</span>
                      </div>
                      
                      <Textarea
                        placeholder="Add a note to your order (optional)"
                        value={orderNote}
                        onChange={(e) => setOrderNote(e.target.value)}
                        className="mb-4"
                        rows={3}
                      />
                      
                      <Button
                        onClick={placeOrder}
                        disabled={cart.length === 0 || isPlacingOrder}
                        className="w-full"
                      >
                        {isPlacingOrder ? (
                          <>
                            <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                            Placing Order...
                          </>
                        ) : (
                          <>
                            <ShoppingCart className="w-4 h-4 mr-2" />
                            Place Order
                          </>
                        )}
                      </Button>
                    </div>
                  </>
                )}
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  );
};

export default RestaurantDetail;
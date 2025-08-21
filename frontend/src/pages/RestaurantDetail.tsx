import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useAuth } from '@/contexts/AuthContext';
import Navigation from '@/components/layout/Navigation';
import { useToast } from '@/hooks/use-toast';
import { 
  ArrowLeft,
  Clock, 
  Star, 
  MapPin, 
  Truck,
  Plus,
  Minus,
  ShoppingCart,
  Heart
} from 'lucide-react';

// Mock restaurants data
const restaurants = [
  {
    id: '1',
    name: 'Pizza Palace',
    cuisine: 'Italian',
    rating: 4.8,
    deliveryTime: '25-35 min',
    deliveryFee: 2.99,
    image: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400',
    featured: true,
    distance: '1.2 km',
    description: 'Authentic Italian pizzas made with fresh ingredients and traditional recipes.',
    address: '123 Main Street, Downtown',
    phone: '+1 (555) 123-4567'
  },
  {
    id: '2',
    name: 'Burger Kingdom',
    cuisine: 'American',
    rating: 4.6,
    deliveryTime: '20-30 min',
    deliveryFee: 1.99,
    image: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400',
    featured: false,
    distance: '0.8 km',
    description: 'Juicy burgers and crispy fries made fresh to order.',
    address: '456 Oak Avenue, Midtown',
    phone: '+1 (555) 987-6543'
  },
  {
    id: '3',
    name: 'Sushi Zen',
    cuisine: 'Japanese',
    rating: 4.9,
    deliveryTime: '35-45 min',
    deliveryFee: 3.99,
    image: 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=400',
    featured: true,
    distance: '2.1 km',
    description: 'Fresh sushi and sashimi prepared by expert chefs.',
    address: '789 Pine Road, Eastside',
    phone: '+1 (555) 456-7890'
  },
  {
    id: '4',
    name: 'Taco Fiesta',
    cuisine: 'Mexican',
    rating: 4.7,
    deliveryTime: '15-25 min',
    deliveryFee: 1.49,
    image: 'https://images.unsplash.com/photo-1565299507177-b0ac66763828?w=400',
    featured: false,
    distance: '1.5 km',
    description: 'Authentic Mexican tacos with traditional flavors.',
    address: '321 Elm Street, Westside',
    phone: '+1 (555) 321-0987'
  },
];

// Mock food items data
const foodItems = {
  '1': [ // Pizza Palace
    {
      id: 'f1',
      name: 'Margherita Pizza',
      description: 'Fresh tomatoes, mozzarella cheese, fresh basil',
      price: 18.99,
      image: 'https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=300',
      category: 'Pizza',
      popular: true
    },
    {
      id: 'f2',
      name: 'Pepperoni Pizza',
      description: 'Pepperoni, mozzarella cheese, tomato sauce',
      price: 21.99,
      image: 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=300',
      category: 'Pizza',
      popular: true
    },
    {
      id: 'f3',
      name: 'Caesar Salad',
      description: 'Crisp romaine lettuce, parmesan cheese, croutons',
      price: 12.99,
      image: 'https://images.unsplash.com/photo-1546793665-c74683f339c1?w=300',
      category: 'Salad',
      popular: false
    },
    {
      id: 'f4',
      name: 'Garlic Bread',
      description: 'Fresh baked bread with garlic butter and herbs',
      price: 6.99,
      image: 'https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?w=300',
      category: 'Appetizer',
      popular: false
    }
  ],
  '2': [ // Burger Kingdom
    {
      id: 'f5',
      name: 'Classic Cheeseburger',
      description: 'Beef patty, cheese, lettuce, tomato, onion',
      price: 14.99,
      image: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300',
      category: 'Burger',
      popular: true
    },
    {
      id: 'f6',
      name: 'Bacon Burger',
      description: 'Beef patty, bacon, cheese, lettuce, tomato',
      price: 17.99,
      image: 'https://images.unsplash.com/photo-1551782450-17144efb9c50?w=300',
      category: 'Burger',
      popular: true
    },
    {
      id: 'f7',
      name: 'French Fries',
      description: 'Crispy golden fries with sea salt',
      price: 4.99,
      image: 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=300',
      category: 'Side',
      popular: false
    }
  ],
  '3': [ // Sushi Zen
    {
      id: 'f8',
      name: 'California Roll',
      description: 'Crab, avocado, cucumber, sesame seeds',
      price: 12.99,
      image: 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=300',
      category: 'Roll',
      popular: true
    },
    {
      id: 'f9',
      name: 'Salmon Sashimi',
      description: 'Fresh salmon slices, 6 pieces',
      price: 16.99,
      image: 'https://images.unsplash.com/photo-1617196034796-73dfa7b1fd56?w=300',
      category: 'Sashimi',
      popular: true
    },
    {
      id: 'f10',
      name: 'Miso Soup',
      description: 'Traditional soybean paste soup with tofu',
      price: 3.99,
      image: 'https://images.unsplash.com/photo-1543826173-1a8ffe9e6638?w=300',
      category: 'Soup',
      popular: false
    }
  ],
  '4': [ // Taco Fiesta
    {
      id: 'f11',
      name: 'Beef Tacos',
      description: 'Seasoned ground beef, lettuce, cheese, tomato',
      price: 8.99,
      image: 'https://images.unsplash.com/photo-1565299507177-b0ac66763828?w=300',
      category: 'Taco',
      popular: true
    },
    {
      id: 'f12',
      name: 'Chicken Quesadilla',
      description: 'Grilled chicken, cheese, peppers, onions',
      price: 11.99,
      image: 'https://images.unsplash.com/photo-1618040996337-56904b7850b9?w=300',
      category: 'Quesadilla',
      popular: true
    },
    {
      id: 'f13',
      name: 'Guacamole & Chips',
      description: 'Fresh guacamole with tortilla chips',
      price: 6.99,
      image: 'https://images.unsplash.com/photo-1605309516293-2784bc4d4024?w=300',
      category: 'Appetizer',
      popular: false
    }
  ]
};

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  image: string;
}

const RestaurantDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { toast } = useToast();
  const [cart, setCart] = useState<CartItem[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('All');

  const restaurant = restaurants.find(r => r.id === id);
  const foods = id ? foodItems[id as keyof typeof foodItems] || [] : [];

  if (!restaurant) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-foreground mb-4">Restaurant not found</h1>
          <Button onClick={() => navigate('/')}>Go Back</Button>
        </div>
      </div>
    );
  }

  const categories = ['All', ...Array.from(new Set(foods.map(food => food.category)))];
  const filteredFoods = selectedCategory === 'All' 
    ? foods 
    : foods.filter(food => food.category === selectedCategory);

  const addToCart = (food: any) => {
    setCart(prevCart => {
      const existingItem = prevCart.find(item => item.id === food.id);
      if (existingItem) {
        return prevCart.map(item =>
          item.id === food.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      } else {
        return [...prevCart, {
          id: food.id,
          name: food.name,
          price: food.price,
          quantity: 1,
          image: food.image
        }];
      }
    });
    
    toast({
      title: "Added to cart",
      description: `${food.name} has been added to your cart.`,
    });
  };

  const updateQuantity = (id: string, change: number) => {
    setCart(prevCart => {
      return prevCart.map(item => {
        if (item.id === id) {
          const newQuantity = item.quantity + change;
          return newQuantity > 0 ? { ...item, quantity: newQuantity } : item;
        }
        return item;
      }).filter(item => item.quantity > 0);
    });
  };

  const getTotalPrice = () => {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const getTotalItems = () => {
    return cart.reduce((total, item) => total + item.quantity, 0);
  };

  const placeOrder = () => {
    if (cart.length === 0) {
      toast({
        title: "Cart is empty",
        description: "Please add items to your cart before placing an order.",
        variant: "destructive"
      });
      return;
    }

    toast({
      title: "Order placed successfully!",
      description: `Your order of ${getTotalItems()} items for $${getTotalPrice().toFixed(2)} has been placed.`,
    });
    
    setCart([]);
  };

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Back Button */}
        <Button 
          variant="outline" 
          onClick={() => navigate('/')}
          className="mb-6 flex items-center space-x-2"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Restaurants</span>
        </Button>

        {/* Restaurant Header */}
        <Card className="mb-8 overflow-hidden shadow-card">
          <div className="relative h-64 md:h-80">
            <img
              src={restaurant.image}
              alt={restaurant.name}
              className="w-full h-full object-cover"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
            <div className="absolute bottom-6 left-6 text-primary-foreground">
              <h1 className="text-3xl md:text-4xl font-bold mb-2">{restaurant.name}</h1>
              <p className="text-lg opacity-90">{restaurant.description}</p>
            </div>
          </div>
          <CardContent className="p-6">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="flex items-center space-x-2">
                <Star className="w-5 h-5 text-yellow-400 fill-current" />
                <span className="font-semibold">{restaurant.rating}</span>
                <span className="text-muted-foreground">Rating</span>
              </div>
              <div className="flex items-center space-x-2">
                <Clock className="w-5 h-5 text-muted-foreground" />
                <span>{restaurant.deliveryTime}</span>
              </div>
              <div className="flex items-center space-x-2">
                <Truck className="w-5 h-5 text-muted-foreground" />
                <span>${restaurant.deliveryFee} Delivery</span>
              </div>
              <div className="flex items-center space-x-2">
                <MapPin className="w-5 h-5 text-muted-foreground" />
                <span>{restaurant.distance}</span>
              </div>
            </div>
          </CardContent>
        </Card>

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
          {/* Menu Section */}
          <div className="lg:col-span-3">
            {/* Category Filter */}
            <div className="mb-6">
              <h2 className="text-2xl font-bold mb-4">Menu</h2>
              <div className="flex flex-wrap gap-2">
                {categories.map(category => (
                  <Button
                    key={category}
                    variant={selectedCategory === category ? "default" : "outline"}
                    onClick={() => setSelectedCategory(category)}
                    className="mb-2"
                  >
                    {category}
                  </Button>
                ))}
              </div>
            </div>

            {/* Food Items Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {filteredFoods.map((food) => (
                <Card key={food.id} className="overflow-hidden shadow-card hover:shadow-glow transition-all duration-300 group">
                  <div className="relative">
                    <img
                      src={food.image}
                      alt={food.name}
                      className="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                    {food.popular && (
                      <Badge className="absolute top-3 left-3 bg-primary text-primary-foreground">
                        Popular
                      </Badge>
                    )}
                    <button className="absolute top-3 right-3 p-2 bg-card/90 backdrop-blur-sm rounded-full hover:bg-card transition-colors">
                      <Heart className="w-4 h-4 text-muted-foreground hover:text-red-500" />
                    </button>
                  </div>
                  <CardContent className="p-4">
                    <div className="flex justify-between items-start mb-2">
                      <h3 className="text-lg font-semibold">{food.name}</h3>
                      <span className="text-lg font-bold text-primary">${food.price}</span>
                    </div>
                    <p className="text-muted-foreground text-sm mb-4">{food.description}</p>
                    <div className="flex items-center justify-between">
                      <Badge variant="secondary">{food.category}</Badge>
                      <Button 
                        onClick={() => addToCart(food)}
                        className="flex items-center space-x-1"
                      >
                        <Plus className="w-4 h-4" />
                        <span>Add</span>
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>

          {/* Cart Sidebar */}
          <div className="lg:col-span-1">
            <Card className="sticky top-8 shadow-card">
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <ShoppingCart className="w-5 h-5" />
                  <span>Your Order</span>
                </CardTitle>
              </CardHeader>
              <CardContent>
                {cart.length === 0 ? (
                  <p className="text-muted-foreground text-center py-8">Your cart is empty</p>
                ) : (
                  <div className="space-y-4">
                    {cart.map((item) => (
                      <div key={item.id} className="flex items-center space-x-3">
                        <img
                          src={item.image}
                          alt={item.name}
                          className="w-12 h-12 object-cover rounded"
                        />
                        <div className="flex-1">
                          <h4 className="text-sm font-medium">{item.name}</h4>
                          <p className="text-sm text-muted-foreground">${item.price}</p>
                        </div>
                        <div className="flex items-center space-x-2">
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => updateQuantity(item.id, -1)}
                            className="w-8 h-8 p-0"
                          >
                            <Minus className="w-3 h-3" />
                          </Button>
                          <span className="w-8 text-center">{item.quantity}</span>
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => updateQuantity(item.id, 1)}
                            className="w-8 h-8 p-0"
                          >
                            <Plus className="w-3 h-3" />
                          </Button>
                        </div>
                      </div>
                    ))}
                    
                    <div className="border-t pt-4">
                      <div className="flex justify-between items-center mb-2">
                        <span>Subtotal:</span>
                        <span>${getTotalPrice().toFixed(2)}</span>
                      </div>
                      <div className="flex justify-between items-center mb-2">
                        <span>Delivery Fee:</span>
                        <span>${restaurant.deliveryFee}</span>
                      </div>
                      <div className="flex justify-between items-center font-bold text-lg border-t pt-2">
                        <span>Total:</span>
                        <span>${(getTotalPrice() + restaurant.deliveryFee).toFixed(2)}</span>
                      </div>
                    </div>
                    
                    <Button 
                      onClick={placeOrder}
                      className="w-full mt-4"
                      size="lg"
                    >
                      Place Order ({getTotalItems()} items)
                    </Button>
                  </div>
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
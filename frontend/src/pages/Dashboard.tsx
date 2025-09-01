import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useAuth } from '@/contexts/AuthContext';
import Navigation from '@/components/layout/Navigation';
import { restaurantApi, Restaurant } from '@/lib/api-service';
import { 
  Clock, 
  Star, 
  MapPin, 
  Truck,
  UtensilsCrossed,
  Heart,
  Search,
  Filter,
  Loader2
} from 'lucide-react';

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchRestaurants();
  }, []);

  const fetchRestaurants = async () => {
    try {
      setLoading(true);
      const data = await restaurantApi.getAllRestaurants();
      setRestaurants(data);
    } catch (err) {
      console.error('Error fetching restaurants:', err);
      setError('Failed to load restaurants');
    } finally {
      setLoading(false);
    }
  };

  // Filter restaurants based on search term
  const filteredRestaurants = restaurants.filter(restaurant =>
    restaurant.restaurantName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    restaurant.address.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Get user's full name
  const getUserDisplayName = () => {
    if (!user) return 'Guest';
    return `${user.firstName} ${user.lastName}`;
  };

  // Get user's role display name
  const getUserRoleDisplay = () => {
    if (!user) return '';
    return user.role === 'CUSTOMER' ? 'Customer' : 'Restaurant Owner';
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-background">
        <Navigation />
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="flex items-center space-x-2">
            <Loader2 className="w-6 h-6 animate-spin" />
            <span>Loading restaurants...</span>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-background">
        <Navigation />
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="text-center">
            <p className="text-red-500 mb-4">{error}</p>
            <Button onClick={fetchRestaurants}>Try Again</Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Section */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground mb-2">
            Hey {getUserDisplayName()}! 👋
          </h1>
          <p className="text-muted-foreground">
            What would you like to eat today?
          </p>
        </div>

        {/* Search and Filters */}
        <Card className="mb-8 shadow-card">
          <CardContent className="p-6">
            <div className="flex flex-col md:flex-row gap-4">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-muted-foreground" />
                <input
                  type="text"
                  placeholder="Search restaurants, cuisines, or dishes..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 border border-input rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
                />
              </div>
              <Button variant="outline" className="flex items-center space-x-2">
                <Filter className="w-4 h-4" />
                <span>Filters</span>
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* All Restaurants */}
        <section>
          <h2 className="text-2xl font-bold mb-4 flex items-center space-x-2">
            <UtensilsCrossed className="w-6 h-6 text-primary" />
            <span>All Restaurants ({filteredRestaurants.length})</span>
          </h2>
          
          {filteredRestaurants.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-muted-foreground">No restaurants found.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredRestaurants.map((restaurant) => (
                <Card 
                  key={restaurant.id} 
                  className="overflow-hidden shadow-card hover:shadow-primary transition-all duration-300 group cursor-pointer"
                  onClick={() => navigate(`/restaurant/${restaurant.id}`)}
                >
                  <div className="relative">
                    <img
                      src={restaurant.imageUrl || 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400'}
                      alt={restaurant.restaurantName}
                      className="w-full h-40 object-cover group-hover:scale-105 transition-transform duration-300"
                      onError={(e) => {
                        const target = e.target as HTMLImageElement;
                        target.src = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400';
                      }}
                    />
                    <button className="absolute top-3 right-3 p-2 bg-white/90 rounded-full hover:bg-white transition-colors">
                      <Heart className="w-4 h-4 text-gray-600 hover:text-red-500" />
                    </button>
                    {restaurant.isActive && (
                      <Badge className="absolute top-3 left-3 bg-green-500 text-white">
                        Open
                      </Badge>
                    )}
                  </div>
                  <CardContent className="p-4">
                    <div className="flex justify-between items-start mb-2">
                      <h3 className="text-lg font-semibold">{restaurant.restaurantName}</h3>
                      <div className="flex items-center space-x-1">
                        <Star className="w-4 h-4 text-yellow-400 fill-current" />
                        <span className="text-sm font-medium">4.5</span>
                      </div>
                    </div>
                    <p className="text-muted-foreground mb-3">
                      {restaurant.firstName} {restaurant.lastName}
                    </p>
                    <div className="flex items-center justify-between text-sm text-muted-foreground">
                      <div className="flex items-center space-x-1">
                        <MapPin className="w-4 h-4" />
                        <span className="truncate">{restaurant.address}</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <Clock className="w-4 h-4" />
                        <span>25-35 min</span>
                      </div>
                    </div>
                    {restaurant.phoneNumber && (
                      <div className="mt-2 text-sm text-muted-foreground">
                        📞 {restaurant.phoneNumber}
                      </div>
                    )}
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
};

export default Dashboard;
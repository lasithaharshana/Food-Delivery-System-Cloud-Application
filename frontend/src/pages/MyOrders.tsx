import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useAuth } from '@/contexts/AuthContext';
import Navigation from '@/components/layout/Navigation';
import { 
  Clock, 
  MapPin, 
  Package,
  CheckCircle,
  XCircle,
  Truck,
  Star,
  RotateCcw,
  Phone
} from 'lucide-react';

// Mock orders data
const mockOrders = [
  {
    id: '1',
    restaurantName: 'Pizza Palace',
    restaurantImage: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=100',
    items: [
      { name: 'Margherita Pizza', quantity: 1, price: 18.99 },
      { name: 'Garlic Bread', quantity: 2, price: 6.99 }
    ],
    total: 32.97,
    status: 'delivered',
    orderDate: '2024-01-10T14:30:00Z',
    deliveryTime: '35 min',
    address: '123 Main St, City, State',
    rating: 5
  },
  {
    id: '2',
    restaurantName: 'Sushi Zen',
    restaurantImage: 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=100',
    items: [
      { name: 'California Roll', quantity: 2, price: 14.99 },
      { name: 'Miso Soup', quantity: 1, price: 4.99 }
    ],
    total: 34.97,
    status: 'preparing',
    orderDate: '2024-01-11T12:15:00Z',
    deliveryTime: '40 min',
    address: '123 Main St, City, State',
    estimatedDelivery: '2024-01-11T13:00:00Z'
  },
  {
    id: '3',
    restaurantName: 'Burger Kingdom',
    restaurantImage: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=100',
    items: [
      { name: 'Classic Burger', quantity: 1, price: 12.99 },
      { name: 'French Fries', quantity: 1, price: 4.99 }
    ],
    total: 19.97,
    status: 'cancelled',
    orderDate: '2024-01-09T18:45:00Z',
    deliveryTime: '25 min',
    address: '123 Main St, City, State'
  }
];

const getStatusColor = (status: string) => {
  switch (status) {
    case 'delivered':
      return 'bg-green-100 text-green-800 border-green-200';
    case 'preparing':
      return 'bg-blue-100 text-blue-800 border-blue-200';
    case 'on-the-way':
      return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    case 'cancelled':
      return 'bg-red-100 text-red-800 border-red-200';
    default:
      return 'bg-gray-100 text-gray-800 border-gray-200';
  }
};

const getStatusIcon = (status: string) => {
  switch (status) {
    case 'delivered':
      return <CheckCircle className="w-4 h-4" />;
    case 'preparing':
      return <Package className="w-4 h-4" />;
    case 'on-the-way':
      return <Truck className="w-4 h-4" />;
    case 'cancelled':
      return <XCircle className="w-4 h-4" />;
    default:
      return <Clock className="w-4 h-4" />;
  }
};

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const MyOrders = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('all');

  const filterOrders = (status?: string) => {
    if (!status || status === 'all') return mockOrders;
    return mockOrders.filter(order => order.status === status);
  };

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground mb-2">My Orders</h1>
          <p className="text-muted-foreground">
            Track your current and previous orders
          </p>
        </div>

        {/* Order Tabs */}
        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="all">All Orders</TabsTrigger>
            <TabsTrigger value="preparing">Active</TabsTrigger>
            <TabsTrigger value="delivered">Delivered</TabsTrigger>
            <TabsTrigger value="cancelled">Cancelled</TabsTrigger>
          </TabsList>

          <TabsContent value="all" className="mt-6">
            <OrdersList orders={filterOrders('all')} />
          </TabsContent>

          <TabsContent value="preparing" className="mt-6">
            <OrdersList orders={filterOrders('preparing')} />
          </TabsContent>

          <TabsContent value="delivered" className="mt-6">
            <OrdersList orders={filterOrders('delivered')} />
          </TabsContent>

          <TabsContent value="cancelled" className="mt-6">
            <OrdersList orders={filterOrders('cancelled')} />
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
};

const OrdersList = ({ orders }: { orders: typeof mockOrders }) => {
  if (orders.length === 0) {
    return (
      <Card className="p-8 text-center">
        <Package className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
        <h3 className="text-lg font-semibold mb-2">No orders found</h3>
        <p className="text-muted-foreground">You don't have any orders in this category yet.</p>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      {orders.map((order) => (
        <Card key={order.id} className="shadow-card hover:shadow-glow transition-all duration-300">
          <CardContent className="p-6">
            <div className="flex flex-col lg:flex-row gap-4">
              {/* Restaurant Info */}
              <div className="flex items-start space-x-4 flex-1">
                <img
                  src={order.restaurantImage}
                  alt={order.restaurantName}
                  className="w-16 h-16 rounded-lg object-cover"
                />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between mb-2">
                    <h3 className="text-lg font-semibold">{order.restaurantName}</h3>
                    <Badge className={`${getStatusColor(order.status)} flex items-center space-x-1`}>
                      {getStatusIcon(order.status)}
                      <span className="capitalize">{order.status.replace('-', ' ')}</span>
                    </Badge>
                  </div>
                  
                  {/* Order Items */}
                  <div className="space-y-1 mb-3">
                    {order.items.map((item, index) => (
                      <div key={index} className="flex justify-between text-sm">
                        <span>{item.quantity}x {item.name}</span>
                        <span>${item.price}</span>
                      </div>
                    ))}
                  </div>
                  
                  {/* Order Details */}
                  <div className="text-sm text-muted-foreground space-y-1">
                    <div className="flex items-center space-x-1">
                      <Clock className="w-4 h-4" />
                      <span>Ordered on {new Date(order.orderDate).toLocaleDateString('en-US', {
                        month: 'short',
                        day: 'numeric',
                        year: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                      })}</span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <MapPin className="w-4 h-4" />
                      <span>{order.address}</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Order Actions */}
              <div className="flex flex-col items-end justify-between lg:min-w-[200px]">
                <div className="text-right mb-4">
                  <div className="text-2xl font-bold">${order.total}</div>
                  <div className="text-sm text-muted-foreground">Total Amount</div>
                </div>

                <div className="flex flex-col space-y-2 w-full lg:w-auto">
                  {order.status === 'delivered' && (
                    <>
                      <Button variant="outline" size="sm" className="flex items-center space-x-2">
                        <RotateCcw className="w-4 h-4" />
                        <span>Reorder</span>
                      </Button>
                      {!order.rating && (
                        <Button variant="outline" size="sm" className="flex items-center space-x-2">
                          <Star className="w-4 h-4" />
                          <span>Rate Order</span>
                        </Button>
                      )}
                    </>
                  )}
                  
                  {order.status === 'preparing' && (
                    <Button variant="outline" size="sm" className="flex items-center space-x-2">
                      <Phone className="w-4 h-4" />
                      <span>Contact Restaurant</span>
                    </Button>
                  )}
                  
                  <Button variant="ghost" size="sm">
                    View Details
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
};

export default MyOrders;
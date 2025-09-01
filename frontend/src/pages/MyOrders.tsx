import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useAuth } from '@/contexts/AuthContext';
import Navigation from '@/components/layout/Navigation';
import { orderApi, Order, foodApi, Food } from '@/lib/api-service';
import { getStoredUserId } from '@/lib/utils';
import { 
  Clock, 
  MapPin, 
  Package,
  CheckCircle,
  XCircle,
  Truck,
  Star,
  RotateCcw,
  Phone,
  Loader2
} from 'lucide-react';

const getStatusColor = (status: string) => {
  switch (status) {
    case 'DELIVERED':
      return 'bg-green-100 text-green-800 border-green-200';
    case 'PREPARING':
      return 'bg-blue-100 text-blue-800 border-blue-200';
    case 'ON_THE_WAY':
      return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    case 'CANCELLED':
      return 'bg-red-100 text-red-800 border-red-200';
    case 'PENDING':
      return 'bg-orange-100 text-orange-800 border-orange-200';
    default:
      return 'bg-gray-100 text-gray-800 border-gray-200';
  }
};

const getStatusIcon = (status: string) => {
  switch (status) {
    case 'DELIVERED':
      return <CheckCircle className="w-4 h-4" />;
    case 'PREPARING':
      return <Package className="w-4 h-4" />;
    case 'ON_THE_WAY':
      return <Truck className="w-4 h-4" />;
    case 'CANCELLED':
      return <XCircle className="w-4 h-4" />;
    case 'PENDING':
      return <Clock className="w-4 h-4" />;
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

interface OrderWithFoods extends Order {
  foodDetails?: Food[];
}

const MyOrders = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('all');
  const [orders, setOrders] = useState<OrderWithFoods[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      // Fetch orders first; handle auth errors clearly
      const ordersData = await orderApi.getUserOrders();
      const userId = getStoredUserId();
      const userOrders = ordersData.filter(order => order.customerId === userId);

      // Try to fetch foods; if forbidden, continue without food details
      let foodsData: Food[] = [];
      try {
        foodsData = await foodApi.getAllFoods();
      } catch (foodErr: any) {
        console.warn('Failed to fetch foods for order details (continuing without names):', foodErr);
      }

      const ordersWithFoods = userOrders.map(order => ({
        ...order,
        foodDetails: foodsData.length
          ? order.orderItems.map(item => foodsData.find(food => food.id === item.foodId)).filter(Boolean) as Food[]
          : undefined,
      }));

      setOrders(ordersWithFoods);
    } catch (err: any) {
      console.error('Error fetching orders:', err);
      if (err?.response?.status === 403) {
        setError('Access denied. Please sign in again.');
      } else {
        setError('Failed to load orders');
      }
    } finally {
      setLoading(false);
    }
  };

  const filterOrders = (status?: string) => {
    if (!status || status === 'all') return orders;
    return orders.filter(order => order.status === status);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-background">
        <Navigation />
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="flex items-center space-x-2">
            <Loader2 className="w-6 h-6 animate-spin" />
            <span>Loading orders...</span>
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
            <Button onClick={fetchOrders}>Try Again</Button>
          </div>
        </div>
      </div>
    );
  }

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
          <TabsList className="grid w-full grid-cols-5">
            <TabsTrigger value="all">All Orders</TabsTrigger>
            <TabsTrigger value="PENDING">Pending</TabsTrigger>
            <TabsTrigger value="PREPARING">Preparing</TabsTrigger>
            <TabsTrigger value="DELIVERED">Delivered</TabsTrigger>
            <TabsTrigger value="CANCELLED">Cancelled</TabsTrigger>
          </TabsList>

          <TabsContent value="all" className="mt-6">
            <OrdersList orders={filterOrders('all')} />
          </TabsContent>

          <TabsContent value="PENDING" className="mt-6">
            <OrdersList orders={filterOrders('PENDING')} />
          </TabsContent>

          <TabsContent value="PREPARING" className="mt-6">
            <OrdersList orders={filterOrders('PREPARING')} />
          </TabsContent>

          <TabsContent value="DELIVERED" className="mt-6">
            <OrdersList orders={filterOrders('DELIVERED')} />
          </TabsContent>

          <TabsContent value="CANCELLED" className="mt-6">
            <OrdersList orders={filterOrders('CANCELLED')} />
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
};

const OrdersList = ({ orders }: { orders: OrderWithFoods[] }) => {
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
              {/* Order Info */}
              <div className="flex items-start space-x-4 flex-1">
                <div className="w-16 h-16 bg-muted rounded-lg flex items-center justify-center">
                  <Package className="w-8 h-8 text-muted-foreground" />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between mb-2">
                    <h3 className="text-lg font-semibold">Order #{order.id}</h3>
                    <Badge className={`${getStatusColor(order.status)} flex items-center space-x-1`}>
                      {getStatusIcon(order.status)}
                      <span className="capitalize">{order.status.replace('_', ' ').toLowerCase()}</span>
                    </Badge>
                  </div>
                  
                  {/* Order Items */}
                  <div className="space-y-1 mb-3">
                    {order.foodDetails?.map((food, index) => {
                      const orderItem = order.orderItems.find(item => item.foodId === food.id);
                      return (
                        <div key={index} className="flex justify-between text-sm">
                          <span>{orderItem?.quantity}x {food.name}</span>
                          <span>${food.price}</span>
                        </div>
                      );
                    })}
                  </div>
                  
                  {/* Order Details */}
                  <div className="text-sm text-muted-foreground space-y-1">
                    <div className="flex items-center space-x-1">
                      <Clock className="w-4 h-4" />
                      <span>Ordered on {formatDate(order.createdAt)}</span>
                    </div>
                    {order.note && (
                      <div className="flex items-center space-x-1">
                        <span>📝 Note: {order.note}</span>
                      </div>
                    )}
                  </div>
                </div>
              </div>

              {/* Order Actions */}
              <div className="flex flex-col items-end justify-between lg:min-w-[200px]">
                <div className="text-right mb-4">
                  <div className="text-2xl font-bold">${order.cost}</div>
                  <div className="text-sm text-muted-foreground">Total Amount</div>
                </div>

                <div className="flex flex-col space-y-2 w-full lg:w-auto">
                  {order.status === 'DELIVERED' && (
                    <>
                      <Button variant="outline" size="sm" className="flex items-center space-x-2">
                        <RotateCcw className="w-4 h-4" />
                        <span>Reorder</span>
                      </Button>
                      <Button variant="outline" size="sm" className="flex items-center space-x-2">
                        <Star className="w-4 h-4" />
                        <span>Rate Order</span>
                      </Button>
                    </>
                  )}
                  
                  {(order.status === 'PENDING' || order.status === 'PREPARING') && (
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
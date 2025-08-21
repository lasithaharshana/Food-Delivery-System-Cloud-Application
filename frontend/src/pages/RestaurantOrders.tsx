import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useAuth } from '@/contexts/AuthContext';
import Navigation from '@/components/layout/Navigation';
import OrderManagement, { Order } from '@/components/restaurant/OrderManagement';
import { 
  Clock, 
  DollarSign,
  Package,
  TrendingUp
} from 'lucide-react';

// Mock orders data
const mockOrders: Order[] = [
  {
    id: 'ORD-001',
    customer: 'John Smith',
    customerPhone: '+1 (555) 123-4567',
    items: [
      { name: 'Margherita Pizza', quantity: 2, price: 18.99 },
      { name: 'Caesar Salad', quantity: 1, price: 12.99 }
    ],
    total: 50.97,
    status: 'preparing',
    orderTime: '12:30 PM',
    estimatedTime: '1:15 PM',
    paymentMethod: 'card',
    deliveryAddress: '123 Main St, Downtown, NY 10001',
    notes: 'Extra cheese on pizza, no croutons on salad'
  },
  {
    id: 'ORD-002',
    customer: 'Sarah Johnson',
    customerPhone: '+1 (555) 987-6543',
    items: [
      { name: 'Beef Burger', quantity: 1, price: 15.99 },
      { name: 'French Fries', quantity: 1, price: 6.99 }
    ],
    total: 22.98,
    status: 'pending',
    orderTime: '12:45 PM',
    paymentMethod: 'cash',
    deliveryAddress: '456 Oak Ave, Midtown, NY 10002'
  },
  {
    id: 'ORD-003',
    customer: 'Mike Davis',
    items: [
      { name: 'Chicken Tikka', quantity: 1, price: 22.99 }
    ],
    total: 22.99,
    status: 'ready',
    orderTime: '11:15 AM',
    estimatedTime: '12:00 PM',
    paymentMethod: 'online',
    deliveryAddress: '789 Pine St, Uptown, NY 10003'
  },
  {
    id: 'ORD-004',
    customer: 'Emily Wilson',
    customerPhone: '+1 (555) 456-7890',
    items: [
      { name: 'Caesar Salad', quantity: 2, price: 12.99 },
      { name: 'Margherita Pizza', quantity: 1, price: 18.99 }
    ],
    total: 44.97,
    status: 'out_for_delivery',
    orderTime: '11:00 AM',
    estimatedTime: '12:15 PM',
    paymentMethod: 'card',
    deliveryAddress: '321 Elm St, Downtown, NY 10001'
  },
  {
    id: 'ORD-005',
    customer: 'David Brown',
    items: [
      { name: 'Beef Burger', quantity: 2, price: 15.99 }
    ],
    total: 31.98,
    status: 'delivered',
    orderTime: '10:30 AM',
    paymentMethod: 'online',
    deliveryAddress: '654 Maple Ave, Midtown, NY 10002'
  }
];

const RestaurantOrders = () => {
  const { user } = useAuth();
  const [orders, setOrders] = useState(mockOrders);
  const [activeTab, setActiveTab] = useState('all');

  const handleUpdateOrderStatus = (orderId: string, newStatus: Order['status']) => {
    setOrders(currentOrders =>
      currentOrders.map(order =>
        order.id === orderId ? { ...order, status: newStatus } : order
      )
    );
  };

  const filterOrdersByStatus = (status?: string) => {
    if (!status || status === 'all') return orders;
    if (status === 'active') {
      return orders.filter(order => 
        ['pending', 'confirmed', 'preparing', 'ready', 'out_for_delivery'].includes(order.status)
      );
    }
    return orders.filter(order => order.status === status);
  };

  // Calculate statistics
  const todayRevenue = orders
    .filter(order => order.status === 'delivered')
    .reduce((sum, order) => sum + order.total, 0);

  const activeOrders = orders.filter(order => 
    ['pending', 'confirmed', 'preparing', 'ready', 'out_for_delivery'].includes(order.status)
  ).length;

  const avgOrderTime = '25 min'; // This would be calculated from real data

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground mb-2">Order Management</h1>
          <p className="text-muted-foreground">
            Monitor and manage incoming orders in real-time
          </p>
        </div>

        {/* Statistics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card className="shadow-card">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Today's Revenue</p>
                  <p className="text-2xl font-bold">${todayRevenue.toFixed(2)}</p>
                </div>
                <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
                  <DollarSign className="w-6 h-6 text-primary" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="shadow-card">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Active Orders</p>
                  <p className="text-2xl font-bold">{activeOrders}</p>
                </div>
                <div className="w-12 h-12 bg-warning/10 rounded-lg flex items-center justify-center">
                  <Package className="w-6 h-6 text-warning" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="shadow-card">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Total Orders</p>
                  <p className="text-2xl font-bold">{orders.length}</p>
                </div>
                <div className="w-12 h-12 bg-success/10 rounded-lg flex items-center justify-center">
                  <TrendingUp className="w-6 h-6 text-success" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="shadow-card">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Avg Prep Time</p>
                  <p className="text-2xl font-bold">{avgOrderTime}</p>
                </div>
                <div className="w-12 h-12 bg-secondary/10 rounded-lg flex items-center justify-center">
                  <Clock className="w-6 h-6 text-secondary" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Order Tabs */}
        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="grid w-full grid-cols-6">
            <TabsTrigger value="all">All Orders</TabsTrigger>
            <TabsTrigger value="active">Active</TabsTrigger>
            <TabsTrigger value="pending">Pending</TabsTrigger>
            <TabsTrigger value="preparing">Preparing</TabsTrigger>
            <TabsTrigger value="ready">Ready</TabsTrigger>
            <TabsTrigger value="delivered">Delivered</TabsTrigger>
          </TabsList>

          <TabsContent value="all" className="mt-6">
            <OrderManagement
              orders={filterOrdersByStatus('all')}
              onUpdateOrderStatus={handleUpdateOrderStatus}
            />
          </TabsContent>

          <TabsContent value="active" className="mt-6">
            <OrderManagement
              orders={filterOrdersByStatus('active')}
              onUpdateOrderStatus={handleUpdateOrderStatus}
            />
          </TabsContent>

          <TabsContent value="pending" className="mt-6">
            <OrderManagement
              orders={filterOrdersByStatus('pending')}
              onUpdateOrderStatus={handleUpdateOrderStatus}
            />
          </TabsContent>

          <TabsContent value="preparing" className="mt-6">
            <OrderManagement
              orders={filterOrdersByStatus('preparing')}
              onUpdateOrderStatus={handleUpdateOrderStatus}
            />
          </TabsContent>

          <TabsContent value="ready" className="mt-6">
            <OrderManagement
              orders={filterOrdersByStatus('ready')}
              onUpdateOrderStatus={handleUpdateOrderStatus}
            />
          </TabsContent>

          <TabsContent value="delivered" className="mt-6">
            <OrderManagement
              orders={filterOrdersByStatus('delivered')}
              onUpdateOrderStatus={handleUpdateOrderStatus}
            />
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
};

export default RestaurantOrders;
import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useAuth } from '@/contexts/AuthContext';
import Navigation from '@/components/layout/Navigation';
import AddItemModal from '@/components/restaurant/AddItemModal';
import MenuManagement from '@/components/restaurant/MenuManagement';
import OrderManagement, { Order } from '@/components/restaurant/OrderManagement';
import { 
  DollarSign, 
  ShoppingBag, 
  Clock, 
  TrendingUp,
  Plus,
  ChefHat,
  ListOrdered
} from 'lucide-react';

// Mock data for restaurant dashboard
const stats = [
  {
    title: 'Today\'s Revenue',
    value: '$1,234',
    change: '+12%',
    icon: DollarSign,
    variant: 'success' as const,
  },
  {
    title: 'Orders Today',
    value: '28',
    change: '+8%',
    icon: ShoppingBag,
    variant: 'success' as const,
  },
  {
    title: 'Avg Prep Time',
    value: '18 min',
    change: '-2 min',
    icon: Clock,
    variant: 'success' as const,
  },
  {
    title: 'Weekly Growth',
    value: '+23%',
    change: '+5%',
    icon: TrendingUp,
    variant: 'success' as const,
  },
];

// Mock data for restaurant dashboard
const initialMenuItems = [
  {
    id: '1',
    name: 'Margherita Pizza',
    description: 'Fresh tomatoes, mozzarella cheese, fresh basil, olive oil',
    price: 18.99,
    category: 'Pizza',
    image: 'https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=300',
    status: 'active' as const,
    popular: true,
    orders: 45,
  },
  {
    id: '2',
    name: 'Pepperoni Pizza',
    description: 'Pepperoni, mozzarella cheese, tomato sauce',
    price: 21.99,
    category: 'Pizza',
    image: 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=300',
    status: 'active' as const,
    popular: true,
    orders: 67,
  },
  {
    id: '3',
    name: 'Caesar Salad',
    description: 'Crisp romaine lettuce, parmesan cheese, croutons, caesar dressing',
    price: 12.99,
    category: 'Salad',
    image: 'https://images.unsplash.com/photo-1546793665-c74683f339c1?w=300',
    status: 'active' as const,
    popular: false,
    orders: 23,
  },
  {
    id: '4',
    name: 'Garlic Bread',
    description: 'Fresh baked bread with garlic butter and herbs',
    price: 6.99,
    category: 'Appetizer',
    image: 'https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?w=300',
    status: 'inactive' as const,
    popular: false,
    orders: 15,
  },
];

const mockOrders: Order[] = [
  {
    id: 'ORD-001',
    customer: 'John Doe',
    customerPhone: '+1 (555) 123-4567',
    items: [
      { name: 'Margherita Pizza', quantity: 1, price: 18.99 },
      { name: 'Caesar Salad', quantity: 1, price: 12.99 }
    ],
    total: 31.98,
    status: 'preparing',
    orderTime: '2:15 PM',
    estimatedTime: '2:35 PM',
    paymentMethod: 'card',
    deliveryAddress: '123 Main St, Apt 4B',
    notes: 'Extra cheese on pizza please'
  },
  {
    id: 'ORD-002',
    customer: 'Jane Smith',
    customerPhone: '+1 (555) 987-6543',
    items: [
      { name: 'Pepperoni Pizza', quantity: 2, price: 21.99 },
      { name: 'Garlic Bread', quantity: 1, price: 6.99 }
    ],
    total: 50.97,
    status: 'ready',
    orderTime: '2:05 PM',
    estimatedTime: '2:25 PM',
    paymentMethod: 'cash',
    deliveryAddress: '456 Oak Ave, Unit 2'
  },
  {
    id: 'ORD-003',
    customer: 'Mike Johnson',
    items: [
      { name: 'Caesar Salad', quantity: 2, price: 12.99 }
    ],
    total: 25.98,
    status: 'delivered',
    orderTime: '1:45 PM',
    paymentMethod: 'online',
    deliveryAddress: '789 Pine Rd'
  },
  {
    id: 'ORD-004',
    customer: 'Sarah Wilson',
    customerPhone: '+1 (555) 456-7890',
    items: [
      { name: 'Margherita Pizza', quantity: 1, price: 18.99 }
    ],
    total: 18.99,
    status: 'pending',
    orderTime: '2:20 PM',
    paymentMethod: 'card',
    deliveryAddress: '321 Elm St',
    notes: 'Ring doorbell twice'
  }
];

const getStatusColor = (status: string) => {
  switch (status) {
    case 'preparing':
      return 'bg-warning text-warning-foreground';
    case 'ready':
      return 'bg-success text-success-foreground';
    case 'delivered':
      return 'bg-muted text-muted-foreground';
    default:
      return 'bg-muted text-muted-foreground';
  }
};

const RestaurantDashboard = () => {
  const { user } = useAuth();
  const [menuItems, setMenuItems] = useState(initialMenuItems);
  const [orders, setOrders] = useState(mockOrders);

  const handleAddItem = (newItem: Omit<typeof initialMenuItems[0], 'id' | 'orders'>) => {
    const item = {
      ...newItem,
      id: Date.now().toString(),
      orders: 0,
    };
    setMenuItems(prev => [...prev, item]);
  };

  const handleUpdateItem = (id: string, updates: Partial<typeof initialMenuItems[0]>) => {
    setMenuItems(prev => prev.map(item => 
      item.id === id ? { ...item, ...updates } : item
    ));
  };

  const handleDeleteItem = (id: string) => {
    setMenuItems(prev => prev.filter(item => item.id !== id));
  };

  const handleUpdateOrderStatus = (orderId: string, status: Order['status']) => {
    setOrders(prev => prev.map(order => 
      order.id === orderId ? { ...order, status } : order
    ));
  };

  const activeOrders = orders.filter(order => 
    ['pending', 'confirmed', 'preparing', 'ready', 'out_for_delivery'].includes(order.status)
  );

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Section */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground mb-2">
            Welcome back, {user?.firstName} {user?.lastName}! 👨‍🍳
          </h1>
          <p className="text-muted-foreground">
            Here's how your restaurant is performing today
          </p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {stats.map((stat) => (
            <Card key={stat.title} className="shadow-card">
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">
                      {stat.title}
                    </p>
                    <p className="text-2xl font-bold">{stat.value}</p>
                    <p className={`text-sm ${stat.variant === 'success' ? 'text-success' : 'text-muted-foreground'}`}>
                      {stat.change} from yesterday
                    </p>
                  </div>
                  <div className={`p-3 rounded-lg bg-primary/10`}>
                    <stat.icon className="w-6 h-6 text-primary" />
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Management Tabs */}
        <Tabs defaultValue="overview" className="space-y-6">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="overview" className="flex items-center space-x-2">
              <TrendingUp className="w-4 h-4" />
              <span>Overview</span>
            </TabsTrigger>
            <TabsTrigger value="menu" className="flex items-center space-x-2">
              <ChefHat className="w-4 h-4" />
              <span>Menu</span>
            </TabsTrigger>
            <TabsTrigger value="orders" className="flex items-center space-x-2">
              <ListOrdered className="w-4 h-4" />
              <span>Orders</span>
            </TabsTrigger>
          </TabsList>

          {/* Overview Tab */}
          <TabsContent value="overview" className="space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              {/* Recent Orders */}
              <Card className="shadow-card">
                <CardHeader>
                  <CardTitle className="flex items-center justify-between">
                    Active Orders
                    <Badge variant="secondary">{activeOrders.length}</Badge>
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {activeOrders.slice(0, 5).map((order) => (
                      <div key={order.id} className="flex items-center justify-between p-4 border border-border rounded-lg">
                        <div className="flex-1">
                          <div className="flex items-center justify-between mb-2">
                            <span className="font-medium">{order.id}</span>
                            <Badge className={getStatusColor(order.status)}>
                              {order.status.replace('_', ' ')}
                            </Badge>
                          </div>
                          <p className="text-sm text-muted-foreground mb-1">
                            {order.customer}
                          </p>
                          <p className="text-sm text-muted-foreground">
                            {order.items.map(item => `${item.quantity}x ${item.name}`).join(', ')}
                          </p>
                        </div>
                        <div className="text-right ml-4">
                          <p className="font-semibold">${order.total.toFixed(2)}</p>
                          <p className="text-sm text-muted-foreground">{order.orderTime}</p>
                        </div>
                      </div>
                    ))}
                    {activeOrders.length === 0 && (
                      <p className="text-center text-muted-foreground py-8">No active orders</p>
                    )}
                  </div>
                </CardContent>
              </Card>

              {/* Quick Menu Actions */}
              <Card className="shadow-card">
                <CardHeader>
                  <CardTitle className="flex items-center justify-between">
                    Quick Actions
                    <AddItemModal onAddItem={handleAddItem} />
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    {menuItems.slice(0, 4).map((item) => (
                      <div key={item.id} className="flex items-center justify-between p-3 border border-border rounded-lg">
                        <div className="flex-1">
                          <div className="flex items-center space-x-3">
                            <div>
                              <h4 className="font-medium">{item.name}</h4>
                              <p className="text-sm text-muted-foreground">{item.category}</p>
                            </div>
                            <Badge variant={item.status === 'active' ? 'default' : 'secondary'}>
                              {item.status}
                            </Badge>
                          </div>
                        </div>
                        <div className="text-right">
                          <p className="font-semibold">${item.price.toFixed(2)}</p>
                          <p className="text-sm text-muted-foreground">{item.orders} orders</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          {/* Menu Management Tab */}
          <TabsContent value="menu" className="space-y-6">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">Menu Management</h2>
              <AddItemModal onAddItem={handleAddItem} />
            </div>
            <MenuManagement 
              items={menuItems}
              onUpdateItem={handleUpdateItem}
              onDeleteItem={handleDeleteItem}
            />
          </TabsContent>

          {/* Orders Management Tab */}
          <TabsContent value="orders" className="space-y-6">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">Order Management</h2>
              <Badge variant="secondary">{orders.length} total orders</Badge>
            </div>
            <OrderManagement 
              orders={orders}
              onUpdateOrderStatus={handleUpdateOrderStatus}
            />
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
};

export default RestaurantDashboard;
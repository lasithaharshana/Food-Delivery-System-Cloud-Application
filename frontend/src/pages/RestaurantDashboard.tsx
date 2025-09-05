import React, { useState, useEffect } from 'react';
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
import { foodApi, orderApi, userApi, Food, Order as ApiOrder } from '@/lib/api-service';
import { useToast } from '@/hooks/use-toast';

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

// Interface for menu items with orders count
interface MenuItemWithOrders {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  category: string;
  image: string;
  status: 'active' | 'inactive';
  popular: boolean;
  orders: number;
}

// Extended order interface with customer details
interface RestaurantOrder extends ApiOrder {
  customerName?: string;
  customerAddress?: string;
  customerPhone?: string;
}

// const mockOrders: Order[] = [
//   {
//     id: 'ORD-001',
//     customer: 'John Doe',
//     customerPhone: '+1 (555) 123-4567',
//     items: [
//       { name: 'Margherita Pizza', quantity: 1, price: 18.99 },
//       { name: 'Caesar Salad', quantity: 1, price: 12.99 }
//     ],
//     total: 31.98,
//     status: 'preparing',
//     orderTime: '2:15 PM',
//     estimatedTime: '2:35 PM',
//     paymentMethod: 'card',
//     deliveryAddress: '123 Main St, Apt 4B',
//     notes: 'Extra cheese on pizza please'
//   },
//   {
//     id: 'ORD-002',
//     customer: 'Jane Smith',
//     customerPhone: '+1 (555) 987-6543',
//     items: [
//       { name: 'Pepperoni Pizza', quantity: 2, price: 21.99 },
//       { name: 'Garlic Bread', quantity: 1, price: 6.99 }
//     ],
//     total: 50.97,
//     status: 'ready',
//     orderTime: '2:05 PM',
//     estimatedTime: '2:25 PM',
//     paymentMethod: 'cash',
//     deliveryAddress: '456 Oak Ave, Unit 2'
//   },
//   {
//     id: 'ORD-003',
//     customer: 'Mike Johnson',
//     items: [
//       { name: 'Caesar Salad', quantity: 2, price: 12.99 }
//     ],
//     total: 25.98,
//     status: 'delivered',
//     orderTime: '1:45 PM',
//     paymentMethod: 'online',
//     deliveryAddress: '789 Pine Rd'
//   },
//   {
//     id: 'ORD-004',
//     customer: 'Sarah Wilson',
//     customerPhone: '+1 (555) 456-7890',
//     items: [
//       { name: 'Margherita Pizza', quantity: 1, price: 18.99 }
//     ],
//     total: 18.99,
//     status: 'pending',
//     orderTime: '2:20 PM',
//     paymentMethod: 'card',
//     deliveryAddress: '321 Elm St',
//     notes: 'Ring doorbell twice'
//   }
// ];

const getStatusColor = (status: string) => {
  switch (status.toUpperCase()) {
    case 'PENDING':
      return 'bg-orange-100 text-orange-800 border-orange-200';
    case 'PREPARING':
      return 'bg-blue-100 text-blue-800 border-blue-200';
    case 'READY':
      return 'bg-green-100 text-green-800 border-green-200';
    case 'DELIVERED':
      return 'bg-gray-100 text-gray-800 border-gray-200';
    case 'CANCELLED':
      return 'bg-red-100 text-red-800 border-red-200';
    default:
      return 'bg-gray-100 text-gray-800 border-gray-200';
  }
};

const RestaurantDashboard = () => {
  const { user } = useAuth();
  const [menuItems, setMenuItems] = useState<MenuItemWithOrders[]>([]);
  const [orders, setOrders] = useState<RestaurantOrder[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [ordersLoading, setOrdersLoading] = useState(true);
  const { toast } = useToast();

  // Fetch menu items from API
  useEffect(() => {
    const fetchMenuItems = async () => {
      try {
        console.log('RestaurantDashboard - Starting to fetch menu items');
        console.log('RestaurantDashboard - User from context:', user);
        console.log('RestaurantDashboard - localStorage accessToken:', localStorage.getItem('accessToken'));
        console.log('RestaurantDashboard - localStorage user:', localStorage.getItem('user'));
        
        if (!user) return;
        const foods: Food[] = await foodApi.getFoodsByRestaurant(user.id);
        console.log('RestaurantDashboard - Foods received from API:', foods);
        
        // Convert API data to local format and add orders count
        const itemsWithOrders: MenuItemWithOrders[] = foods.map(food => ({
          id: food.id.toString(),
          name: food.name,
          description: food.description,
          price: food.price,
          quantity: food.quantity,
          category: food.category,
          image: food.imageUrl,
          status: food.status === 'available' ? 'active' : 'inactive',
          popular: food.popular,
          orders: Math.floor(Math.random() * 100), // Mock orders count for now
        }));
        
        console.log('RestaurantDashboard - Converted items:', itemsWithOrders);
        setMenuItems(itemsWithOrders);
      } catch (error) {
        console.error('RestaurantDashboard - Failed to fetch menu items:', error);
        toast({
          title: "Error",
          description: "Failed to load menu items",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    if (user) {
      fetchMenuItems();
      fetchRestaurantOrders();
    }
  }, [user, toast]);

  // Fetch restaurant orders
  const fetchRestaurantOrders = async () => {
    try {
      setOrdersLoading(true);
      const allOrders = await orderApi.getAllOrders();
      const allFoods = await foodApi.getAllFoods();
      
      // Filter orders that have at least one item from current restaurant
      const restaurantOrders: RestaurantOrder[] = [];
      
      for (const order of allOrders) {
        const hasRestaurantItem = order.orderItems.some(orderItem => {
          const food = allFoods.find(f => f.id === orderItem.foodId);
          return food && food.restaurantId === user?.id;
        });
        
        if (hasRestaurantItem) {
          try {
            // Get customer details
            const customer = await userApi.getUserById(order.customerId);
            restaurantOrders.push({
              ...order,
              customerName: `${customer.firstName} ${customer.lastName}`,
              customerAddress: customer.address,
              customerPhone: customer.phoneNumber,
            });
          } catch (customerError) {
            console.warn(`Failed to fetch customer ${order.customerId}:`, customerError);
            // Add order without customer details
            restaurantOrders.push({
              ...order,
              customerName: `Customer #${order.customerId}`,
              customerAddress: 'Unknown',
              customerPhone: 'Unknown',
            });
          }
        }
      }
      
      setOrders(restaurantOrders);
    } catch (error) {
      console.error('Failed to fetch restaurant orders:', error);
      toast({
        title: "Error",
        description: "Failed to load orders",
        variant: "destructive",
      });
    } finally {
      setOrdersLoading(false);
    }
  };

  const handleAddItem = async (newItem: Omit<MenuItemWithOrders, 'id' | 'orders'>) => {
    try {
      const created = await foodApi.createFood({
        name: newItem.name,
        description: newItem.description,
        price: newItem.price,
        quantity: newItem.quantity,
        category: newItem.category,
        imageUrl: newItem.image || 'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400',
        status: newItem.status === 'active' ? 'available' : 'unavailable',
        popular: newItem.popular,
      });

      // Add the new item to local state with the API response data
      const item: MenuItemWithOrders = {
        id: created.id.toString(),
        name: created.name,
        description: created.description,
        price: created.price,
        quantity: created.quantity,
        category: created.category,
        image: created.imageUrl,
        status: created.status === 'available' ? 'active' : 'inactive',
        popular: created.popular,
        orders: 0,
      };
      
      setMenuItems(prev => [...prev, item]);
      
      toast({
        title: "Success",
        color: "green",
        description: `${newItem.name} has been added to your menu successfully`,
      });
    } catch (error) {
      console.error('Failed to add food item:', error);
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : 'Failed to add food item',
        variant: "destructive",
      });
    }
  };

  const handleUpdateItem = (id: string, updates: Partial<MenuItemWithOrders>) => {
    setMenuItems(prev => prev.map(item => 
      item.id === id ? { ...item, ...updates } : item
    ));
  };

  const handleDeleteItem = (id: string) => {
    setMenuItems(prev => prev.filter(item => item.id !== id));
  };

  const handleUpdateOrderStatus = (orderId: string | number, status: string) => {
    setOrders(prev => prev.map(order => 
      order.id === orderId ? { ...order, status } : order
    ));
  };

  const activeOrders = orders.filter(order => 
    ['PENDING', 'PREPARING', 'READY'].includes(order.status)
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
                            <span className="font-medium">Order #{order.id}</span>
                            <Badge className={getStatusColor(order.status)}>
                              {order.status.replace('_', ' ')}
                            </Badge>
                          </div>
                          <p className="text-sm text-muted-foreground mb-1">
                            {order.customerName}
                          </p>
                          <p className="text-sm text-muted-foreground">
                            {order.orderItems.length} item(s)
                          </p>
                          {order.note && (
                            <p className="text-xs text-muted-foreground italic">
                              Note: {order.note}
                            </p>
                          )}
                        </div>
                        <div className="text-right ml-4">
                          <p className="font-semibold">${order.cost.toFixed(2)}</p>
                          <p className="text-sm text-muted-foreground">
                            {new Date(order.createdAt).toLocaleString()}
                          </p>
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
            {isLoading ? (
              <div className="text-center py-8">
                <p className="text-muted-foreground">Loading menu items...</p>
              </div>
            ) : (
              <MenuManagement 
                items={menuItems}
                onUpdateItem={handleUpdateItem}
                onDeleteItem={handleDeleteItem}
              />
            )}
          </TabsContent>

          {/* Orders Management Tab */}
          <TabsContent value="orders" className="space-y-6">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">Order Management</h2>
              <Badge variant="secondary">{orders.length} total orders</Badge>
            </div>
            <OrderManagement 
              orders={orders as any}
              onUpdateOrderStatus={handleUpdateOrderStatus}
            />
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
};

export default RestaurantDashboard;

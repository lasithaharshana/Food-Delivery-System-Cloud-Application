import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useAuth } from '@/contexts/AuthContext';
import Navigation from '@/components/layout/Navigation';
import { 
  Users, 
  Store, 
  ShoppingBag, 
  DollarSign,
  TrendingUp,
  Activity,
  AlertTriangle,
  CheckCircle
} from 'lucide-react';

// Mock data for admin dashboard
const stats = [
  {
    title: 'Total Users',
    value: '12,456',
    change: '+5.2%',
    icon: Users,
    variant: 'success' as const,
  },
  {
    title: 'Active Restaurants',
    value: '342',
    change: '+12%',
    icon: Store,
    variant: 'success' as const,
  },
  {
    title: 'Orders Today',
    value: '1,834',
    change: '+8.1%',
    icon: ShoppingBag,
    variant: 'success' as const,
  },
  {
    title: 'Platform Revenue',
    value: '$45,231',
    change: '+15.3%',
    icon: DollarSign,
    variant: 'success' as const,
  },
];

const recentActivities = [
  {
    id: '1',
    type: 'restaurant_approved',
    message: 'New restaurant "Sushi Express" approved',
    time: '5 min ago',
    status: 'success',
  },
  {
    id: '2',
    type: 'user_complaint',
    message: 'Customer complaint about delivery delay',
    time: '12 min ago',
    status: 'warning',
  },
  {
    id: '3',
    type: 'payment_processed',
    message: 'Weekly payout of $12,450 processed',
    time: '1 hour ago',
    status: 'success',
  },
  {
    id: '4',
    type: 'restaurant_suspended',
    message: 'Restaurant "Fast Bites" temporarily suspended',
    time: '2 hours ago',
    status: 'error',
  },
];

const pendingApprovals = [
  {
    id: '1',
    name: 'Mediterranean Grill',
    type: 'Restaurant Application',
    submittedBy: 'Alex Thompson',
    time: '2 days ago',
    status: 'pending',
  },
  {
    id: '2',
    name: 'Thai Palace',
    type: 'Menu Update',
    submittedBy: 'Sarah Chen',
    time: '1 day ago',
    status: 'pending',
  },
  {
    id: '3',
    name: 'Burger Corner',
    type: 'Profile Update',
    submittedBy: 'Mike Wilson',
    time: '3 hours ago',
    status: 'pending',
  },
];

const topRestaurants = [
  {
    id: '1',
    name: 'Pizza Palace',
    orders: 245,
    revenue: '$4,890',
    rating: 4.8,
    status: 'active',
  },
  {
    id: '2',
    name: 'Sushi Zen',
    orders: 189,
    revenue: '$5,670',
    rating: 4.9,
    status: 'active',
  },
  {
    id: '3',
    name: 'Burger Kingdom',
    orders: 198,
    revenue: '$3,240',
    rating: 4.6,
    status: 'active',
  },
];

const getStatusIcon = (status: string) => {
  switch (status) {
    case 'success':
      return <CheckCircle className="w-4 h-4 text-success" />;
    case 'warning':
      return <AlertTriangle className="w-4 h-4 text-warning" />;
    case 'error':
      return <AlertTriangle className="w-4 h-4 text-destructive" />;
    default:
      return <Activity className="w-4 h-4 text-muted-foreground" />;
  }
};

const AdminDashboard = () => {
  const { user } = useAuth();

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Section */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground mb-2">
            Admin Dashboard 👑
          </h1>
          <p className="text-muted-foreground">
            Monitor and manage the QuickEats platform
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
                      {stat.change} from last week
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

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Recent Activities */}
          <Card className="lg:col-span-2 shadow-card">
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                Recent Activities
                <Button size="sm" variant="outline">
                  View All
                </Button>
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {recentActivities.map((activity) => (
                  <div key={activity.id} className="flex items-center space-x-4 p-3 border border-border rounded-lg">
                    {getStatusIcon(activity.status)}
                    <div className="flex-1">
                      <p className="text-sm font-medium">{activity.message}</p>
                      <p className="text-sm text-muted-foreground">{activity.time}</p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* Pending Approvals */}
          <Card className="shadow-card">
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                Pending Approvals
                <Badge variant="warning">3</Badge>
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {pendingApprovals.map((item) => (
                  <div key={item.id} className="p-3 border border-border rounded-lg">
                    <h4 className="font-medium text-sm mb-1">{item.name}</h4>
                    <p className="text-xs text-muted-foreground mb-2">{item.type}</p>
                    <p className="text-xs text-muted-foreground mb-3">by {item.submittedBy} • {item.time}</p>
                    <div className="flex space-x-2">
                      <Button size="sm" variant="success" className="flex-1">
                        Approve
                      </Button>
                      <Button size="sm" variant="outline" className="flex-1">
                        Review
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Top Performing Restaurants */}
        <Card className="mt-8 shadow-card">
          <CardHeader>
            <CardTitle className="flex items-center justify-between">
              Top Performing Restaurants
              <Button size="sm" variant="outline">
                View All
              </Button>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-border">
                    <th className="text-left py-3 px-4 font-medium text-muted-foreground">Restaurant</th>
                    <th className="text-left py-3 px-4 font-medium text-muted-foreground">Orders</th>
                    <th className="text-left py-3 px-4 font-medium text-muted-foreground">Revenue</th>
                    <th className="text-left py-3 px-4 font-medium text-muted-foreground">Rating</th>
                    <th className="text-left py-3 px-4 font-medium text-muted-foreground">Status</th>
                    <th className="text-left py-3 px-4 font-medium text-muted-foreground">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {topRestaurants.map((restaurant) => (
                    <tr key={restaurant.id} className="border-b border-border">
                      <td className="py-3 px-4 font-medium">{restaurant.name}</td>
                      <td className="py-3 px-4">{restaurant.orders}</td>
                      <td className="py-3 px-4 font-semibold">{restaurant.revenue}</td>
                      <td className="py-3 px-4">
                        <div className="flex items-center space-x-1">
                          <span>{restaurant.rating}</span>
                          <TrendingUp className="w-4 h-4 text-success" />
                        </div>
                      </td>
                      <td className="py-3 px-4">
                        <Badge variant="default">Active</Badge>
                      </td>
                      <td className="py-3 px-4">
                        <Button size="sm" variant="ghost">
                          View Details
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  );
};

export default AdminDashboard;
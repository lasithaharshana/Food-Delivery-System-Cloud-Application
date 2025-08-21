import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { useNotifications } from '@/hooks/use-notifications';
import { 
  Clock, 
  User, 
  DollarSign,
  Search,
  CheckCircle,
  XCircle,
  Package,
  Truck
} from 'lucide-react';

export interface Order {
  id: string;
  customer: string;
  customerPhone?: string;
  items: {
    name: string;
    quantity: number;
    price: number;
  }[];
  total: number;
  status: 'pending' | 'confirmed' | 'preparing' | 'ready' | 'out_for_delivery' | 'delivered' | 'cancelled';
  orderTime: string;
  estimatedTime?: string;
  paymentMethod: 'cash' | 'card' | 'online';
  deliveryAddress?: string;
  notes?: string;
}

interface OrderManagementProps {
  orders: Order[];
  onUpdateOrderStatus: (orderId: string, status: Order['status']) => void;
}

const OrderManagement: React.FC<OrderManagementProps> = ({ orders, onUpdateOrderStatus }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('All');
  const notifications = useNotifications();

  const statusOptions = [
    'All', 'pending', 'confirmed', 'preparing', 'ready', 'out_for_delivery', 'delivered', 'cancelled'
  ];

  const filteredOrders = orders.filter(order => {
    const matchesSearch = order.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         order.customer.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = filterStatus === 'All' || order.status === filterStatus;
    
    return matchesSearch && matchesStatus;
  });

  const getStatusColor = (status: Order['status']) => {
    switch (status) {
      case 'pending':
        return 'bg-warning text-warning-foreground';
      case 'confirmed':
        return 'bg-blue-500 text-white';
      case 'preparing':
        return 'bg-orange-500 text-white';
      case 'ready':
        return 'bg-success text-success-foreground';
      case 'out_for_delivery':
        return 'bg-purple-500 text-white';
      case 'delivered':
        return 'bg-green-600 text-white';
      case 'cancelled':
        return 'bg-destructive text-destructive-foreground';
      default:
        return 'bg-muted text-muted-foreground';
    }
  };

  const getStatusIcon = (status: Order['status']) => {
    switch (status) {
      case 'pending':
        return <Clock className="w-4 h-4" />;
      case 'confirmed':
        return <CheckCircle className="w-4 h-4" />;
      case 'preparing':
        return <Package className="w-4 h-4" />;
      case 'ready':
        return <CheckCircle className="w-4 h-4" />;
      case 'out_for_delivery':
        return <Truck className="w-4 h-4" />;
      case 'delivered':
        return <CheckCircle className="w-4 h-4" />;
      case 'cancelled':
        return <XCircle className="w-4 h-4" />;
      default:
        return <Clock className="w-4 h-4" />;
    }
  };

  const handleStatusUpdate = (orderId: string, newStatus: Order['status']) => {
    onUpdateOrderStatus(orderId, newStatus);
    
    // Show appropriate notification based on status
    const orderNumber = `#${orderId}`;
    switch (newStatus) {
      case 'confirmed':
        notifications.orderReceived(`Order ${orderNumber} confirmed and being prepared`);
        break;
      case 'preparing':
        notifications.orderPreparing(`Order ${orderNumber} is now in the kitchen`);
        break;
      case 'ready':
        notifications.orderReady(`Order ${orderNumber} is ready for pickup/delivery`);
        break;
      case 'out_for_delivery':
        notifications.info(`Order ${orderNumber} is out for delivery`);
        break;
      case 'delivered':
        notifications.orderDelivered(`Order ${orderNumber} has been delivered successfully`);
        break;
      case 'cancelled':
        notifications.warning(`Order ${orderNumber} has been cancelled`);
        break;
      default:
        notifications.success(`Order ${orderNumber} status updated to ${formatStatus(newStatus)}`);
    }
  };

  const getNextStatusOptions = (currentStatus: Order['status']): Order['status'][] => {
    switch (currentStatus) {
      case 'pending':
        return ['confirmed', 'cancelled'];
      case 'confirmed':
        return ['preparing', 'cancelled'];
      case 'preparing':
        return ['ready', 'cancelled'];
      case 'ready':
        return ['out_for_delivery', 'delivered'];
      case 'out_for_delivery':
        return ['delivered'];
      case 'delivered':
        return [];
      case 'cancelled':
        return [];
      default:
        return [];
    }
  };

  const formatStatus = (status: string) => {
    return status.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase());
  };

  return (
    <Card className="shadow-card">
      <CardHeader>
        <CardTitle>Order Management</CardTitle>
        
        {/* Search and Filters */}
        <div className="flex flex-col sm:flex-row gap-4 mt-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Search orders or customers..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10"
            />
          </div>
          
          <Select value={filterStatus} onValueChange={setFilterStatus}>
            <SelectTrigger className="w-full sm:w-48">
              <SelectValue placeholder="Filter by status" />
            </SelectTrigger>
            <SelectContent>
              {statusOptions.map(status => (
                <SelectItem key={status} value={status}>
                  {formatStatus(status)}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </CardHeader>
      
      <CardContent>
        {filteredOrders.length === 0 ? (
          <div className="text-center py-8">
            <p className="text-muted-foreground">No orders found matching your criteria.</p>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredOrders.map((order) => (
              <div key={order.id} className="border border-border rounded-lg p-4 hover:bg-muted/50 transition-colors">
                {/* Order Header */}
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center space-x-4">
                    <h4 className="font-semibold text-lg">{order.id}</h4>
                    <Badge className={`${getStatusColor(order.status)} flex items-center space-x-1`}>
                      {getStatusIcon(order.status)}
                      <span>{formatStatus(order.status)}</span>
                    </Badge>
                  </div>
                  <div className="text-right">
                    <p className="text-xl font-bold text-primary">${order.total.toFixed(2)}</p>
                    <p className="text-sm text-muted-foreground">{order.paymentMethod}</p>
                  </div>
                </div>

                {/* Customer Info */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                  <div className="flex items-center space-x-2">
                    <User className="w-4 h-4 text-muted-foreground" />
                    <span className="font-medium">{order.customer}</span>
                    {order.customerPhone && (
                      <span className="text-sm text-muted-foreground">• {order.customerPhone}</span>
                    )}
                  </div>
                  <div className="flex items-center space-x-2">
                    <Clock className="w-4 h-4 text-muted-foreground" />
                    <span className="text-sm">{order.orderTime}</span>
                    {order.estimatedTime && (
                      <span className="text-sm text-muted-foreground">• ETA: {order.estimatedTime}</span>
                    )}
                  </div>
                </div>

                {/* Order Items */}
                <div className="mb-4">
                  <h5 className="font-medium mb-2">Items:</h5>
                  <div className="space-y-1">
                    {order.items.map((item, index) => (
                      <div key={index} className="flex justify-between text-sm">
                        <span>{item.quantity}x {item.name}</span>
                        <span>${(item.price * item.quantity).toFixed(2)}</span>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Delivery Address */}
                {order.deliveryAddress && (
                  <div className="mb-4">
                    <h5 className="font-medium mb-1">Delivery Address:</h5>
                    <p className="text-sm text-muted-foreground">{order.deliveryAddress}</p>
                  </div>
                )}

                {/* Notes */}
                {order.notes && (
                  <div className="mb-4">
                    <h5 className="font-medium mb-1">Notes:</h5>
                    <p className="text-sm text-muted-foreground">{order.notes}</p>
                  </div>
                )}

                {/* Status Actions */}
                {getNextStatusOptions(order.status).length > 0 && (
                  <div className="flex flex-wrap gap-2 pt-4 border-t">
                    {getNextStatusOptions(order.status).map(status => (
                      <Button
                        key={status}
                        size="sm"
                        variant={status === 'cancelled' ? 'destructive' : 'default'}
                        onClick={() => handleStatusUpdate(order.id, status)}
                        className="flex items-center space-x-1"
                      >
                        {getStatusIcon(status)}
                        <span>{formatStatus(status)}</span>
                      </Button>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default OrderManagement;
import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { useNotifications } from '@/hooks/use-notifications';
import { orderApi } from '@/lib/api-service';
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
  id: string | number;
  customer?: string;
  customerName?: string;
  customerPhone?: string;
  customerAddress?: string;
  customerId?: number;
  items?: {
    name: string;
    quantity: number;
    price: number;
  }[];
  orderItems?: {
    id: number;
    foodId: number;
    quantity: number;
  }[];
  total?: number;
  cost?: number;
  status: string;
  orderTime?: string;
  createdAt?: string;
  updatedAt?: string;
  estimatedTime?: string;
  paymentMethod?: 'cash' | 'card' | 'online';
  deliveryAddress?: string;
  notes?: string;
  note?: string;
}

interface OrderManagementProps {
  orders: Order[];
  onUpdateOrderStatus: (orderId: string | number, status: string) => void;
}

const OrderManagement: React.FC<OrderManagementProps> = ({ orders, onUpdateOrderStatus }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('All');
  const notifications = useNotifications();

  const statusOptions = [
    'All', 'PENDING', 'READY', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'
  ];

  const filteredOrders = orders.filter(order => {
    const customerName = order.customerName || order.customer || `Customer #${order.customerId}`;
    const matchesSearch = order.id.toString().toLowerCase().includes(searchTerm.toLowerCase()) ||
                         customerName.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = filterStatus === 'All' || order.status.toUpperCase() === filterStatus;
    
    return matchesSearch && matchesStatus;
  });

  const getStatusColor = (status: string) => {
    switch (status.toUpperCase()) {
      case 'PENDING':
        return 'bg-orange-100 text-orange-800';
      case 'READY':
        return 'bg-green-100 text-green-800';
      case 'OUT_FOR_DELIVERY':
        return 'bg-blue-100 text-blue-800';
      case 'DELIVERED':
        return 'bg-gray-100 text-gray-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status.toUpperCase()) {
      case 'PENDING':
        return <Clock className="w-4 h-4" />;
      case 'READY':
        return <CheckCircle className="w-4 h-4" />;
      case 'OUT_FOR_DELIVERY':
        return <Truck className="w-4 h-4" />;
      case 'DELIVERED':
        return <CheckCircle className="w-4 h-4" />;
      case 'CANCELLED':
        return <XCircle className="w-4 h-4" />;
      default:
        return <Clock className="w-4 h-4" />;
    }
  };

  const handleStatusUpdate = async (orderId: string | number, newStatus: string) => {
    try {
      // Find the full order object
      const order = orders.find(o => o.id === orderId || o.id === Number(orderId));
      if (!order) throw new Error('Order not found');

      // Prepare the payload for backend (ensure all required fields are present)
      const payload = {
        id: typeof order.id === 'string' ? Number(order.id) : order.id,
        customerId: order.customerId || 1, // fallback if missing, ideally should be set
        note: order.note || '',
        status: newStatus,
        cost: order.cost || order.total || 0,
        orderItems: order.orderItems || [],
      };

      await orderApi.updateOrderStatus(payload.id, payload);

      // Update local state
      onUpdateOrderStatus(orderId, newStatus);

      // Show appropriate notification based on status
      const orderNumber = `#${orderId}`;
      switch (newStatus.toUpperCase()) {
        case 'READY':
          notifications.orderReady(`Order ${orderNumber} is ready for pickup/delivery`);
          break;
        case 'OUT_FOR_DELIVERY':
          notifications.info(`Order ${orderNumber} is out for delivery`);
          break;
        case 'DELIVERED':
          notifications.orderDelivered(`Order ${orderNumber} has been delivered successfully`);
          break;
        case 'CANCELLED':
          notifications.warning(`Order ${orderNumber} has been cancelled`);
          break;
        default:
          notifications.success(`Order ${orderNumber} status updated to ${formatStatus(newStatus)}`);
      }
    } catch (error) {
      console.error('Failed to update order status:', error);
      notifications.error('Failed to update order status. Please try again.');
    }
  };

  const getNextStatusOptions = (currentStatus: string): string[] => {
    switch (currentStatus.toUpperCase()) {
      case 'PENDING':
        return ['READY', 'CANCELLED'];
      case 'READY':
        return ['OUT_FOR_DELIVERY', 'DELIVERED'];
      case 'OUT_FOR_DELIVERY':
        return ['DELIVERED'];
      case 'DELIVERED':
        return [];
      case 'CANCELLED':
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
                    <h4 className="font-semibold text-lg">Order #{order.id}</h4>
                    <Badge className={`${getStatusColor(order.status)} flex items-center space-x-1`}>
                      {getStatusIcon(order.status)}
                      <span>{formatStatus(order.status)}</span>
                    </Badge>
                  </div>
                  <div className="text-right">
                    <p className="text-xl font-bold text-primary">${(order.cost || order.total || 0).toFixed(2)}</p>
                    <p className="text-sm text-muted-foreground">
                      {new Date(order.createdAt || order.orderTime || '').toLocaleString()}
                    </p>
                  </div>
                </div>

                {/* Customer Info */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                  <div className="flex items-center space-x-2">
                    <User className="w-4 h-4 text-muted-foreground" />
                    <span className="font-medium">{order.customerName || order.customer || `Customer #${order.customerId}`}</span>
                    {order.customerPhone && (
                      <span className="text-sm text-muted-foreground">• {order.customerPhone}</span>
                    )}
                  </div>
                  <div className="flex items-center space-x-2">
                    <Clock className="w-4 h-4 text-muted-foreground" />
                    <span className="text-sm">
                      {new Date(order.createdAt || order.orderTime || '').toLocaleString()}
                    </span>
                  </div>
                </div>

                {/* Order Items */}
                <div className="mb-4">
                  <h5 className="font-medium mb-2">Items:</h5>
                  <div className="space-y-1">
                    {order.orderItems ? (
                      order.orderItems.map((item, index) => (
                        <div key={index} className="flex justify-between text-sm">
                          <span>{item.quantity}x Item #{item.foodId}</span>
                          <span className="text-muted-foreground">Food ID: {item.foodId}</span>
                        </div>
                      ))
                    ) : order.items ? (
                      order.items.map((item, index) => (
                        <div key={index} className="flex justify-between text-sm">
                          <span>{item.quantity}x {item.name}</span>
                          <span>${(item.price * item.quantity).toFixed(2)}</span>
                        </div>
                      ))
                    ) : (
                      <p className="text-sm text-muted-foreground">No items found</p>
                    )}
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
                {(order.note || order.notes) && (
                  <div className="mb-4">
                    <h5 className="font-medium mb-1">Notes:</h5>
                    <p className="text-sm text-muted-foreground">{order.note || order.notes}</p>
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
import React from "react";
import { useToast } from "@/hooks/use-toast";
import { CheckCircle, AlertCircle, Info, AlertTriangle, Clock, Truck, ChefHat, CreditCard } from "lucide-react";

export type NotificationType = 
  | 'success' 
  | 'error' 
  | 'warning' 
  | 'info'
  | 'order-received'
  | 'order-preparing'
  | 'order-ready'
  | 'order-delivered'
  | 'payment-success'
  | 'payment-failed';

export interface NotificationConfig {
  title: string;
  description?: string;
  duration?: number;
  variant?: 'default' | 'destructive';
  icon?: React.ReactNode;
}

const getNotificationConfig = (type: NotificationType, customMessage?: string): NotificationConfig => {
  const configs: Record<NotificationType, NotificationConfig> = {
    success: {
      title: "Success",
      description: customMessage || "Operation completed successfully",
      variant: "default",
      icon: React.createElement(CheckCircle, { className: "h-4 w-4 text-success" }),
      duration: 3000,
    },
    error: {
      title: "Error",
      description: customMessage || "Something went wrong",
      variant: "destructive",
      icon: React.createElement(AlertCircle, { className: "h-4 w-4" }),
      duration: 5000,
    },
    warning: {
      title: "Warning",
      description: customMessage || "Please check your input",
      variant: "default",
      icon: React.createElement(AlertTriangle, { className: "h-4 w-4 text-warning" }),
      duration: 4000,
    },
    info: {
      title: "Information",
      description: customMessage || "Here's some useful information",
      variant: "default",
      icon: React.createElement(Info, { className: "h-4 w-4 text-primary" }),
      duration: 3000,
    },
    'order-received': {
      title: "New Order Received!",
      description: customMessage || "A new order has been placed",
      variant: "default",
      icon: React.createElement(ChefHat, { className: "h-4 w-4 text-success" }),
      duration: 5000,
    },
    'order-preparing': {
      title: "Order in Kitchen",
      description: customMessage || "Your order is being prepared",
      variant: "default",
      icon: React.createElement(Clock, { className: "h-4 w-4 text-warning" }),
      duration: 4000,
    },
    'order-ready': {
      title: "Order Ready!",
      description: customMessage || "Your order is ready for pickup/delivery",
      variant: "default",
      icon: React.createElement(CheckCircle, { className: "h-4 w-4 text-success" }),
      duration: 6000,
    },
    'order-delivered': {
      title: "Order Delivered!",
      description: customMessage || "Your order has been delivered successfully",
      variant: "default",
      icon: React.createElement(Truck, { className: "h-4 w-4 text-success" }),
      duration: 5000,
    },
    'payment-success': {
      title: "Payment Successful!",
      description: customMessage || "Your payment has been processed",
      variant: "default",
      icon: React.createElement(CreditCard, { className: "h-4 w-4 text-success" }),
      duration: 4000,
    },
    'payment-failed': {
      title: "Payment Failed",
      description: customMessage || "Payment could not be processed",
      variant: "destructive",
      icon: React.createElement(CreditCard, { className: "h-4 w-4" }),
      duration: 6000,
    },
  };

  return configs[type];
};

export const useNotifications = () => {
  const { toast } = useToast();

  const showNotification = (type: NotificationType, customMessage?: string) => {
    const config = getNotificationConfig(type, customMessage);
    
    toast({
      title: config.title,
      description: config.description,
      variant: config.variant,
      duration: config.duration,
    });
  };

  // Convenience methods for common use cases
  const notifications = {
    success: (message?: string) => showNotification('success', message),
    error: (message?: string) => showNotification('error', message),
    warning: (message?: string) => showNotification('warning', message),
    info: (message?: string) => showNotification('info', message),
    
    // Order-specific notifications
    orderReceived: (orderDetails?: string) => showNotification('order-received', orderDetails),
    orderPreparing: (orderDetails?: string) => showNotification('order-preparing', orderDetails),
    orderReady: (orderDetails?: string) => showNotification('order-ready', orderDetails),
    orderDelivered: (orderDetails?: string) => showNotification('order-delivered', orderDetails),
    
    // Payment notifications
    paymentSuccess: (amount?: string) => showNotification('payment-success', amount ? `Payment of ${amount} processed successfully` : undefined),
    paymentFailed: (reason?: string) => showNotification('payment-failed', reason),
  };

  return {
    showNotification,
    ...notifications,
  };
};
import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/contexts/AuthContext';
import Navigation from '@/components/layout/Navigation';
import MenuManagement from '@/components/restaurant/MenuManagement';
import AddItemModal from '@/components/restaurant/AddItemModal';
import { foodApi, Food } from '@/lib/api-service';
import { useToast } from '@/hooks/use-toast';
import { Plus } from 'lucide-react';

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

const RestaurantMenu = () => {
  const { user } = useAuth();
  const [menuItems, setMenuItems] = useState<MenuItemWithOrders[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const { toast } = useToast();

  // Fetch menu items from API
  useEffect(() => {
    const fetchMenuItems = async () => {
      try {
        if (!user) return;
        const foods: Food[] = await foodApi.getFoodsByRestaurant(user.id);
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
          orders: Math.floor(Math.random() * 100),
        }));
        setMenuItems(itemsWithOrders);
      } catch (error) {
        console.error('Failed to fetch menu items:', error);
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
    }
  }, [user, toast]);
  

  const handleUpdateItem = (id: string, updates: any) => {
    setMenuItems(items => 
      items.map(item => 
        item.id === id ? { ...item, ...updates } : item
      )
    );
  };

  const handleDeleteItem = (id: string) => {
    setMenuItems(items => items.filter(item => item.id !== id));
  };

  const handleAddItem = async (newItem: any) => {
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

      setMenuItems(items => [...items, item]);
      
      toast({
        title: "Success",
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

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-foreground mb-2">Menu Management</h1>
            <p className="text-muted-foreground">
              Manage your restaurant's menu items and pricing
            </p>
          </div>
          <AddItemModal onAddItem={handleAddItem} />
        </div>

        {/* Menu Statistics */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card className="shadow-card">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Total Items</p>
                  <p className="text-2xl font-bold">{menuItems.length}</p>
                </div>
                <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
                  <Plus className="w-6 h-6 text-primary" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="shadow-card">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Active Items</p>
                  <p className="text-2xl font-bold">{menuItems.filter(item => item.status === 'active').length}</p>
                </div>
                <div className="w-12 h-12 bg-success/10 rounded-lg flex items-center justify-center">
                  <Plus className="w-6 h-6 text-success" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="shadow-card">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Popular Items</p>
                  <p className="text-2xl font-bold">{menuItems.filter(item => item.popular).length}</p>
                </div>
                <div className="w-12 h-12 bg-warning/10 rounded-lg flex items-center justify-center">
                  <Plus className="w-6 h-6 text-warning" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="shadow-card">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Categories</p>
                  <p className="text-2xl font-bold">{new Set(menuItems.map(item => item.category)).size}</p>
                </div>
                <div className="w-12 h-12 bg-secondary/10 rounded-lg flex items-center justify-center">
                  <Plus className="w-6 h-6 text-secondary" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Menu Management Component */}
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

        {/* Add Item Modal */}
        <AddItemModal
          onAddItem={handleAddItem}
        />
      </main>
    </div>
  );
};

export default RestaurantMenu;
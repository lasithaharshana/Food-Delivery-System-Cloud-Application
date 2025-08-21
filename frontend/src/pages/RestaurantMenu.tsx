import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/contexts/AuthContext';
import Navigation from '@/components/layout/Navigation';
import MenuManagement from '@/components/restaurant/MenuManagement';
import AddItemModal from '@/components/restaurant/AddItemModal';
import { Plus } from 'lucide-react';

// Mock menu items data
const mockMenuItems = [
  {
    id: '1',
    name: 'Margherita Pizza',
    description: 'Fresh tomato sauce, mozzarella, and basil on a crispy crust',
    price: 18.99,
    category: 'Pizza',
    image: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=300',
    status: 'active' as const,
    popular: true,
    orders: 156
  },
  {
    id: '2',
    name: 'Caesar Salad',
    description: 'Crisp romaine lettuce with parmesan cheese and croutons',
    price: 12.99,
    category: 'Salads',
    image: 'https://images.unsplash.com/photo-1551248429-40975aa4de74?w=300',
    status: 'active' as const,
    popular: false,
    orders: 89
  },
  {
    id: '3',
    name: 'Beef Burger',
    description: 'Juicy beef patty with lettuce, tomato, and our special sauce',
    price: 15.99,
    category: 'Burgers',
    image: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300',
    status: 'inactive' as const,
    popular: false,
    orders: 67
  },
  {
    id: '4',
    name: 'Chicken Tikka',
    description: 'Tender marinated chicken with aromatic spices',
    price: 22.99,
    category: 'Main Course',
    image: 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=300',
    status: 'active' as const,
    popular: true,
    orders: 134
  }
];

const RestaurantMenu = () => {
  const { user } = useAuth();
  const [menuItems, setMenuItems] = useState(mockMenuItems);
  

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

  const handleAddItem = (newItem: any) => {
    const item = {
      ...newItem,
      id: Date.now().toString(),
      orders: 0
    };
    setMenuItems(items => [...items, item]);
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
        <MenuManagement
          items={menuItems}
          onUpdateItem={handleUpdateItem}
          onDeleteItem={handleDeleteItem}
        />

        {/* Add Item Modal */}
        <AddItemModal
          onAddItem={handleAddItem}
        />
      </main>
    </div>
  );
};

export default RestaurantMenu;
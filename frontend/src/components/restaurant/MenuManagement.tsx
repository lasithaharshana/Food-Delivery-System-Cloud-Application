import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { useToast } from '@/hooks/use-toast';
import { updateFood, deleteFood } from '@/lib/foodService';
import { useAuth } from '@/contexts/AuthContext';
import {
  Eye,
  Edit,
  Trash2,
  Search,
  Filter,
  Star,
  MoreVertical,
  Image as ImageIcon
} from 'lucide-react';
import EditItemModal from './EditItemModal';
import AddItemModal from './AddItemModal';

interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  category: string;
  imageUrl: string;
  status: 'active' | 'inactive';
  popular: boolean;
  orders?: number;
}

interface MenuManagementProps {
  items: MenuItem[];
  onUpdateItem: (id: string, updates: Partial<MenuItem>) => void;
  onDeleteItem: (id: string) => void;
}

const MenuManagement: React.FC<MenuManagementProps> = ({ items, onUpdateItem, onDeleteItem }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterCategory, setFilterCategory] = useState('All');
  const [filterStatus, setFilterStatus] = useState('All');
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<MenuItem | null>(null);
  const { toast } = useToast();
  const { user } = useAuth();

  const categories = ['All', ...Array.from(new Set(items.map(item => item.category)))];

  const filteredItems = items.filter(item => {
    const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = filterCategory === 'All' || item.category === filterCategory;
    const matchesStatus = filterStatus === 'All' || item.status === filterStatus;

    return matchesSearch && matchesCategory && matchesStatus;
  });

  // Update status API
  const handleStatusToggle = async (id: string, currentStatus: 'active' | 'inactive') => {
    const item = items.find(i => i.id === id);
    if (!item || !user) return;
    const newStatus = currentStatus === 'active' ? 'inactive' : 'active';
    try {
      await updateFood(Number(id), {
        restaurantId: user.id,
        name: item.name,
        description: item.description,
        price: item.price,
        quantity: item.quantity,
        category: item.category,
        imageUrl: item.imageUrl,
        status: newStatus === 'active' ? 'available' : 'unavailable',
        popular: item.popular,
      });
      onUpdateItem(id, { status: newStatus });
      toast({
        title: "Status updated",
        description: `Item status changed to ${newStatus}.`,
      });
    } catch (error) {
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to update status",
        variant: "destructive",
      });
    }
  };

  // Update popular API
  const handlePopularToggle = async (id: string, currentPopular: boolean) => {
    const item = items.find(i => i.id === id);
    if (!item || !user) return;
    try {
      await updateFood(Number(id), {
        restaurantId: user.id,
        name: item.name,
        description: item.description,
        price: item.price,
        quantity: item.quantity,
        category: item.category,
        imageUrl: item.imageUrl,
        status: item.status === 'active' ? 'available' : 'unavailable',
        popular: !currentPopular,
      });
      onUpdateItem(id, { popular: !currentPopular });
      toast({
        title: "Popular status updated",
        description: `Item ${!currentPopular ? 'marked as' : 'removed from'} popular.`,
      });
    } catch (error) {
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to update popular status",
        variant: "destructive",
      });
    }
  };

  // Delete API
  const handleDelete = async (id: string, itemName: string) => {
    if (window.confirm(`Are you sure you want to delete "${itemName}"?`)) {
      try {
        await deleteFood(Number(id));
        onDeleteItem(id);
        toast({
          title: "Item deleted",
          color: "green",
          description: `${itemName} has been removed from your menu.`,
        });
      } catch (error) {
        toast({
          title: "Error",
          description: error instanceof Error ? error.message : "Failed to delete item",
          variant: "destructive",
        });
      }
    }
  };

  // Edit handler
  const handleEditClick = (item: MenuItem) => {
    setEditingItem(item);
    setEditModalOpen(true);
  };

  const handleEditSave = async (updated: Omit<MenuItem, 'id'>) => {
    if (!editingItem || !user) return;
    try {
      await updateFood(Number(editingItem.id), {
        restaurantId: user.id,
        name: updated.name,
        description: updated.description,
        price: updated.price,
        quantity: updated.quantity,
        category: updated.category,
        imageUrl: updated.imageUrl,
        status: updated.status === 'active' ? 'available' : 'unavailable',
        popular: updated.popular,
      });
      onUpdateItem(editingItem.id, { ...updated });
      toast({
        title: "Menu updated",
        description: `${updated.name} has been updated.`,
      });
      setEditModalOpen(false);
      setEditingItem(null);
    } catch (error) {
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to update menu",
        variant: "destructive",
      });
    }
  };

  return (
    <>
      <Card className="shadow-card">
        <CardHeader>
          <CardTitle>Menu Management</CardTitle>

          {/* Search and Filters */}
          <div className="flex flex-col sm:flex-row gap-4 mt-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Search menu items..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>

            <Select value={filterCategory} onValueChange={setFilterCategory}>
              <SelectTrigger className="w-full sm:w-48">
                <SelectValue placeholder="Category" />
              </SelectTrigger>
              <SelectContent>
                {categories.map(category => (
                  <SelectItem key={category} value={category}>
                    {category}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>

            <Select value={filterStatus} onValueChange={setFilterStatus}>
              <SelectTrigger className="w-full sm:w-32">
                <SelectValue placeholder="Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="All">All</SelectItem>
                <SelectItem value="active">Active</SelectItem>
                <SelectItem value="inactive">Inactive</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardHeader>

        <CardContent>
          {filteredItems.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-muted-foreground">No items found matching your criteria.</p>
            </div>
          ) : (
            <div className="space-y-4">
              {filteredItems.map((item) => (
                <div
                  key={item.id}
                  className="flex items-center space-x-4 p-4 border border-border rounded-lg hover:bg-muted/50 transition-colors"
                >
                  {/* Item Image */}
                  <div className="w-16 h-16 rounded-lg overflow-hidden bg-muted flex items-center justify-center">
                    {item.imageUrl ? (
                      <img
                        src={`http://localhost:8080/api/foods/uploads/${item.imageUrl}`}
                        alt={item.name}
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <ImageIcon className="w-6 h-6 text-muted-foreground" />
                    )}
                  </div>

                  {/* Item Info */}
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center space-x-2 mb-1">
                      <h4 className="font-medium truncate">{item.name}</h4>
                      {item.popular && (
                        <Star className="w-4 h-4 text-yellow-400 fill-current" />
                      )}
                    </div>
                    <p className="text-sm text-muted-foreground line-clamp-2 mb-1">
                      {item.description}
                    </p>
                    <div className="flex items-center space-x-2">
                      <Badge variant="secondary" className="text-xs">
                        {item.category}
                      </Badge>
                      <Badge
                        variant={item.status === "active" ? "default" : "secondary"}
                        className="text-xs"
                      >
                        {item.status}
                      </Badge>
                      {item.orders !== undefined && (
                        <span className="text-xs text-muted-foreground">
                          {item.orders} orders
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Price and Actions */}
                  <div className="text-right">
                    <p className="text-lg font-semibold text-primary mb-2">
                      ${item.price.toFixed(2)}
                    </p>

                    <div className="flex items-center space-x-1">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handlePopularToggle(item.id, item.popular)}
                        title={item.popular ? "Remove from popular" : "Mark as popular"}
                      >
                        <Star
                          className={`w-4 h-4 ${item.popular
                              ? "text-yellow-400 fill-current"
                              : "text-muted-foreground"
                            }`}
                        />
                      </Button>

                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleStatusToggle(item.id, item.status)}
                        title={`${item.status === "active" ? "Deactivate" : "Activate"
                          } item`}
                      >
                        <Eye
                          className={`w-4 h-4 ${item.status === "active"
                              ? "text-success"
                              : "text-muted-foreground"
                            }`}
                        />
                      </Button>

                      <Button
                        size="sm"
                        variant="outline"
                        title="Edit item"
                        onClick={() => handleEditClick(item)}
                      >
                        <Edit className="w-4 h-4" />
                      </Button>

                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleDelete(item.id, item.name)}
                        title="Delete item"
                      >
                        <Trash2 className="w-4 h-4 text-destructive" />
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>

      </Card>

      {/* Edit Modal */}
      {editingItem && (
        <AddItemModal
          open={editModalOpen}
          onClose={() => setEditModalOpen(false)}
          onAddItem={handleEditSave}
          initialValues={editingItem}
          isEdit={true}
        />
      )}
    </>
  );
};

export default MenuManagement;
import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogTrigger } from '@/components/ui/dialog';
import { useNotifications } from '@/hooks/use-notifications';
import { Plus, X, Upload } from 'lucide-react';

interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  category: string;
  image: string;
  status: 'active' | 'inactive';
  popular: boolean;
}

interface AddItemModalProps {
  onAddItem: (item: Omit<MenuItem, 'id'>) => void;
  initialValues?: Partial<MenuItem>;
  isEdit?: boolean;
  open?: boolean;
  onClose?: () => void;
}

const AddItemModal: React.FC<AddItemModalProps> = ({
  onAddItem,
  initialValues,
  isEdit = false,
  open: controlledOpen,
  onClose,
}) => {
  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState<{
    name: string;
    description: string;
    price: string;
    quantity: string;
    category: string;
    image: string;
    status: 'active' | 'inactive';
    popular: boolean;
  }>({
    name: '',
    description: '',
    price: '',
    quantity: '20',
    category: '',
    image: '',
    status: 'active',
    popular: false
  });

  React.useEffect(() => {
    if (initialValues) {
      setFormData({
        name: initialValues.name || '',
        description: initialValues.description || '',
        price: initialValues.price?.toString() || '',
        quantity: initialValues.quantity?.toString() || '20',
        category: initialValues.category || '',
        image: initialValues.image || '',
        status: initialValues.status || 'active',
        popular: initialValues.popular || false
      });
    }
  }, [initialValues]);

  const notifications = useNotifications();
  const categories = ['Pizza', 'Burger', 'Salad', 'Appetizer', 'Side', 'Dessert', 'Beverage'];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.description || !formData.price || !formData.category) {
      notifications.error("Please fill in all required fields.");
      return;
    }
    const newItem: Omit<MenuItem, 'id'> = {
      name: formData.name,
      description: formData.description,
      price: parseFloat(formData.price),
      quantity: parseInt(formData.quantity),
      category: formData.category,
      image: formData.image || 'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400',
      status: formData.status,
      popular: formData.popular
    };
    onAddItem(newItem);
    if (!isEdit) {
      setFormData({
        name: '',
        description: '',
        price: '',
        quantity: '20',
        category: '',
        image: '',
        status: 'active',
        popular: false
      });
    }
    if (onClose) onClose();
    else setOpen(false);
  };

  const handleInputChange = (field: string, value: string | boolean) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  return (
    <Dialog open={controlledOpen ?? open} onOpenChange={controlledOpen ? onClose : setOpen}>
      <DialogTrigger asChild>
        {!isEdit && (
          <Button className="flex items-center space-x-2">
            <Plus className="w-4 h-4" />
            <span>Add Item</span>
          </Button>
        )}
      </DialogTrigger>
      <DialogContent className="max-w-md max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>{isEdit ? 'Edit Menu Item' : 'Add New Menu Item'}</DialogTitle>
          <DialogDescription>
            {isEdit
              ? 'Update the details below to edit this menu item.'
              : 'Fill in the details below to add a new item to your restaurant menu.'}
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label htmlFor="name">Item Name *</Label>
            <Input
              id="name"
              value={formData.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              placeholder="e.g., Margherita Pizza"
              required
            />
          </div>

          <div>
            <Label htmlFor="description">Description *</Label>
            <Textarea
              id="description"
              value={formData.description}
              onChange={(e) => handleInputChange('description', e.target.value)}
              placeholder="Describe your item..."
              rows={3}
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <Label htmlFor="price">Price ($) *</Label>
              <Input
                id="price"
                type="number"
                step="0.01"
                min="0"
                value={formData.price}
                onChange={(e) => handleInputChange('price', e.target.value)}
                placeholder="0.00"
                required
              />
            </div>

            <div>
              <Label htmlFor="quantity">Quantity *</Label>
              <Input
                id="quantity"
                type="number"
                min="1"
                value={formData.quantity}
                onChange={(e) => handleInputChange('quantity', e.target.value)}
                placeholder="20"
                required
              />
            </div>
          </div>

          <div>
            <Label htmlFor="category">Category *</Label>
            <Select value={formData.category} onValueChange={(value) => handleInputChange('category', value)}>
              <SelectTrigger>
                <SelectValue placeholder="Select category" />
              </SelectTrigger>
              <SelectContent>
                {categories.map(category => (
                  <SelectItem key={category} value={category}>
                    {category}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {/* <div>
            <Label htmlFor="image">Image URL</Label>
            <div className="flex space-x-2">
              <Input
                id="image"
                value={formData.image}
                onChange={(e) => handleInputChange('image', e.target.value)}
                placeholder="https://example.com/image.jpg"
              />
              <Button type="button" variant="outline" size="icon">
                <Upload className="w-4 h-4" />
              </Button>
            </div>
          </div>  */}

          <div>
            <Label htmlFor="imageFile">Upload Image</Label>
            <div className="flex space-x-2 items-center">
              <Input
                id="imageFile"
                type="file"
                accept="image/*"
                onChange={async (e) => {
                  if (e.target.files?.[0]) {
                    const formData = new FormData();
                    formData.append("file", e.target.files[0]);

                    try {
                      const res = await fetch('http://localhost:8080/api/files/upload', {
                        method: "POST",
                        body: formData,
                      });
                      const data = await res.json();

                      if (data.path) {
                        // Save backend path to state
                        handleInputChange("image", data.path);
                      }
                    } catch (err) {
                      console.error("Upload failed:", err);
                    }
                  }
                }}
              />

              {formData.image && (
                <img
                  src={`http://localhost:8080/api/files/upload${formData.image}`}
                  alt="preview"
                  className="w-16 h-16 object-cover rounded"
                />
              )}
            </div>
          </div>




          <div className="grid grid-cols-2 gap-4">
            <div>
              <Label htmlFor="status">Status</Label>
              <Select value={formData.status} onValueChange={(value: 'active' | 'inactive') => handleInputChange('status', value)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="active">Active</SelectItem>
                  <SelectItem value="inactive">Inactive</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="flex items-center space-x-2 pt-6">
              <input
                type="checkbox"
                id="popular"
                checked={formData.popular}
                onChange={(e) => handleInputChange('popular', e.target.checked)}
                className="rounded border-border"
              />
              <Label htmlFor="popular">Mark as Popular</Label>
            </div>
          </div>

          <div className="flex space-x-2 pt-4">
            <Button type="submit" className="flex-1">{isEdit ? 'Save Changes' : 'Add Item'}</Button>
            <Button type="button" variant="outline" onClick={onClose ?? (() => setOpen(false))}>
              Cancel
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default AddItemModal;
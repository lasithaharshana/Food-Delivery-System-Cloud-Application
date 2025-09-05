import React, { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

interface EditItemModalProps {
  item: {
    id: string;
    name: string;
    description: string;
    price: number;
    quantity: number;
    category: string;
    image: string;
    status: 'active' | 'inactive';
    popular: boolean;
  };
  open: boolean;
  onClose: () => void;
  onSave: (updated: any) => void;
}

const EditItemModal: React.FC<EditItemModalProps> = ({ item, open, onClose, onSave }) => {
  const [form, setForm] = useState({ ...item });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(form);
  };

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Edit Menu Item</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input name="name" value={form.name} onChange={handleChange} placeholder="Name" />
          <Input name="description" value={form.description} onChange={handleChange} placeholder="Description" />
          <Input name="price" type="number" value={form.price} onChange={handleChange} placeholder="Price" />
          <Input name="quantity" type="number" value={form.quantity} onChange={handleChange} placeholder="Quantity" />
          <Input name="category" value={form.category} onChange={handleChange} placeholder="Category" />
          <Input name="image" value={form.image} onChange={handleChange} placeholder="Image URL" />
          {/* You can add status and popular toggles if needed */}
          <div className="flex justify-end">
            <Button type="submit">Save Changes</Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default EditItemModal;
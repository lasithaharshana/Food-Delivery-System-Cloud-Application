import React from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { getStoredToken, getStoredUser } from '@/lib/utils';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import Navigation from '@/components/layout/Navigation';

const Debug = () => {
  const { user, isAuthenticated, isLoading, accessToken } = useAuth();
  const storedToken = getStoredToken();
  const storedUser = getStoredUser();

  const testApiCall = async () => {
    try {
      const response = await fetch('/api/foods', {
        headers: {
          'Authorization': `Bearer ${storedToken}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        console.log('✅ API call successful:', data);
        alert('API call successful! Check console for data.');
      } else {
        console.log('❌ API call failed:', response.status, response.statusText);
        alert(`API call failed: ${response.status} ${response.statusText}`);
      }
    } catch (error) {
      console.error('❌ API call error:', error);
      alert('API call error: ' + error);
    }
  };

  const clearStorage = () => {
    localStorage.clear();
    window.location.reload();
  };

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold mb-8">Debug Information</h1>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Auth Context State */}
          <Card>
            <CardHeader>
              <CardTitle>Auth Context State</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                <div><strong>isLoading:</strong> {isLoading ? 'Yes' : 'No'}</div>
                <div><strong>isAuthenticated:</strong> {isAuthenticated ? 'Yes' : 'No'}</div>
                <div><strong>accessToken:</strong> {accessToken ? 'Present' : 'None'}</div>
                <div><strong>User:</strong> {user ? `${user.firstName} ${user.lastName}` : 'None'}</div>
                <div><strong>Role:</strong> {user?.role || 'None'}</div>
                <div><strong>User ID:</strong> {user?.id || 'None'}</div>
          </div>
            </CardContent>
          </Card>

          {/* Local Storage State */}
          <Card>
            <CardHeader>
              <CardTitle>Local Storage State</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                <div><strong>Stored Token:</strong> {storedToken ? 'Present' : 'None'}</div>
                <div><strong>Stored User:</strong> {storedUser ? 'Present' : 'None'}</div>
                <div><strong>Token Length:</strong> {storedToken?.length || 0}</div>
                <div><strong>Token Preview:</strong> {storedToken ? `${storedToken.substring(0, 20)}...` : 'None'}</div>
          </div>
            </CardContent>
          </Card>

          {/* Actions */}
          <Card>
            <CardHeader>
              <CardTitle>Actions</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <Button onClick={testApiCall} className="w-full">
                Test API Call to /api/foods
              </Button>
              <Button onClick={clearStorage} variant="destructive" className="w-full">
                Clear Local Storage
              </Button>
            </CardContent>
          </Card>

          {/* Debug Info */}
          <Card>
            <CardHeader>
              <CardTitle>Debug Info</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-2 text-sm">
                <div><strong>Current URL:</strong> {window.location.href}</div>
                <div><strong>User Agent:</strong> {navigator.userAgent}</div>
                <div><strong>Local Storage Keys:</strong></div>
                <ul className="ml-4">
                  {Object.keys(localStorage).map(key => (
                    <li key={key}>• {key}</li>
                  ))}
                </ul>
          </div>
            </CardContent>
          </Card>
      </div>
      </main>
    </div>
  );
};

export default Debug;

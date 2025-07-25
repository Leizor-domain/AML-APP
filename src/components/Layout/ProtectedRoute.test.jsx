import React from 'react';
import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import configureStore from 'redux-mock-store';
import ProtectedRoute from './ProtectedRoute';

const mockStore = configureStore([]);

const DummyComponent = () => <div>Protected Content</div>;

// Mock react-router-dom to capture navigation
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  Navigate: ({ to }) => {
    mockNavigate(to);
    return <div data-testid="navigate" data-to={to}>Redirecting to {to}</div>;
  }
}));

// Mock jwtDecode to return valid token
jest.mock('jwt-decode', () => ({
  __esModule: true,
  default: jest.fn((token) => {
    if (token === 'valid-token') {
      return { exp: Date.now() / 1000 + 3600 }; // Valid for 1 hour
    }
    if (token === 'expired-token') {
      return { exp: Date.now() / 1000 - 3600 }; // Expired 1 hour ago
    }
    throw new Error('Invalid token');
  })
}));

describe('ProtectedRoute', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
  });

  it('redirects unauthenticated users to /login', () => {
    const store = mockStore({ 
      auth: { 
        isAuthenticated: false, 
        user: null, 
        loading: false, 
        token: null 
      } 
    });
    
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/protected']}>
          <ProtectedRoute>
            <DummyComponent />
          </ProtectedRoute>
        </MemoryRouter>
      </Provider>
    );
    
    // Should not render protected content
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
    
    // Should show redirect component
    expect(screen.getByTestId('navigate')).toBeInTheDocument();
    expect(screen.getByText('Redirecting to /login')).toBeInTheDocument();
  });

  it('redirects users with expired token to /login', () => {
    const store = mockStore({ 
      auth: { 
        isAuthenticated: true, 
        user: { role: 'ROLE_ADMIN' }, 
        loading: false, 
        token: 'expired-token' 
      } 
    });
    
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/protected']}>
          <ProtectedRoute>
            <DummyComponent />
          </ProtectedRoute>
        </MemoryRouter>
      </Provider>
    );
    
    // Should not render protected content
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
    
    // Should show redirect component
    expect(screen.getByTestId('navigate')).toBeInTheDocument();
    expect(screen.getByText('Redirecting to /login')).toBeInTheDocument();
  });

  it('renders children for authenticated users with correct role', () => {
    const store = mockStore({ 
      auth: { 
        isAuthenticated: true, 
        user: { role: 'ROLE_ADMIN' }, 
        loading: false, 
        token: 'valid-token' 
      } 
    });
    
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/admin/dashboard']}>
          <ProtectedRoute requiredRole="ADMIN">
            <DummyComponent />
          </ProtectedRoute>
        </MemoryRouter>
      </Provider>
    );
    
    // Should render protected content
    expect(screen.getByText('Protected Content')).toBeInTheDocument();
    
    // Should not show redirect
    expect(screen.queryByTestId('navigate')).not.toBeInTheDocument();
  });

  it('redirects users with wrong role', () => {
    const store = mockStore({ 
      auth: { 
        isAuthenticated: true, 
        user: { role: 'ROLE_VIEWER' }, 
        loading: false, 
        token: 'valid-token' 
      } 
    });
    
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/admin/dashboard']}>
          <ProtectedRoute requiredRole="ADMIN">
            <DummyComponent />
          </ProtectedRoute>
        </MemoryRouter>
      </Provider>
    );
    
    // Should not render protected content
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
    
    // Should show redirect to user's dashboard
    expect(screen.getByTestId('navigate')).toBeInTheDocument();
    expect(screen.getByText('Redirecting to /viewer/dashboard')).toBeInTheDocument();
  });

  it('shows loading state when auth is loading', () => {
    const store = mockStore({ 
      auth: { 
        isAuthenticated: false, 
        user: null, 
        loading: true, 
        token: null 
      } 
    });
    
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/protected']}>
          <ProtectedRoute>
            <DummyComponent />
          </ProtectedRoute>
        </MemoryRouter>
      </Provider>
    );
    
    // Should show loading spinner
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
    
    // Should not render protected content or redirect
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
    expect(screen.queryByTestId('navigate')).not.toBeInTheDocument();
  });

  it('renders children for authenticated users without role requirement', () => {
    const store = mockStore({ 
      auth: { 
        isAuthenticated: true, 
        user: { role: 'ROLE_ADMIN' }, 
        loading: false, 
        token: 'valid-token' 
      } 
    });
    
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/dashboard']}>
          <ProtectedRoute>
            <DummyComponent />
          </ProtectedRoute>
        </MemoryRouter>
      </Provider>
    );
    
    // Should render protected content
    expect(screen.getByText('Protected Content')).toBeInTheDocument();
    
    // Should not show redirect
    expect(screen.queryByTestId('navigate')).not.toBeInTheDocument();
  });
}); 
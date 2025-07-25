import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import configureStore from 'redux-mock-store';
import LoginForm from './LoginForm';

const mockStore = configureStore([]);

// Mock the auth service
const mockAuthService = {
  login: jest.fn()
};

jest.mock('../../services/auth.js', () => ({
  authService: mockAuthService
}));

// Mock react-router-dom
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

describe('LoginForm', () => {
  let store;

  beforeEach(() => {
    store = mockStore({
      auth: {
        isAuthenticated: false,
        loading: false,
        error: null,
        token: null,
        user: null
      }
    });
    store.dispatch = jest.fn();
    mockAuthService.login.mockClear();
    mockNavigate.mockClear();
  });

  it('renders login form correctly', () => {
    render(
      <Provider store={store}>
        <BrowserRouter>
          <LoginForm />
        </BrowserRouter>
      </Provider>
    );

    expect(screen.getByText('AML Engine Login')).toBeInTheDocument();
    expect(screen.getByTestId('username-input')).toBeInTheDocument();
    expect(screen.getByTestId('password-input')).toBeInTheDocument();
    expect(screen.getByTestId('login-button')).toBeInTheDocument();
  });

  it('shows error when starting with error state', () => {
    // Start with an error state
    store = mockStore({
      auth: {
        isAuthenticated: false,
        loading: false,
        error: 'Previous error message',
        token: null,
        user: null
      }
    });

    render(
      <Provider store={store}>
        <BrowserRouter>
          <LoginForm />
        </BrowserRouter>
      </Provider>
    );

    // Error should be visible
    expect(screen.getByTestId('login-error')).toBeInTheDocument();
    expect(screen.getByText('Previous error message')).toBeInTheDocument();
  });

  it('handles form input changes', () => {
    render(
      <Provider store={store}>
        <BrowserRouter>
          <LoginForm />
        </BrowserRouter>
      </Provider>
    );

    const usernameInput = screen.getByTestId('username-input');
    const passwordInput = screen.getByTestId('password-input');

    // Test input changes
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'testpass' } });

    expect(usernameInput.value).toBe('testuser');
    expect(passwordInput.value).toBe('testpass');
  });

  it('dispatches loginStart action when form is submitted', () => {
    render(
      <Provider store={store}>
        <BrowserRouter>
          <LoginForm />
        </BrowserRouter>
      </Provider>
    );

    // Fill form
    fireEvent.change(screen.getByTestId('username-input'), { 
      target: { value: 'admin' } 
    });
    fireEvent.change(screen.getByTestId('password-input'), { 
      target: { value: 'password' } 
    });

    // Submit form
    fireEvent.click(screen.getByTestId('login-button'));

    // Verify loginStart action was dispatched
    expect(store.dispatch).toHaveBeenCalledWith(
      expect.objectContaining({ type: expect.stringContaining('loginStart') })
    );
  });

  it('shows loading state when auth is loading', () => {
    store = mockStore({
      auth: {
        isAuthenticated: false,
        loading: true,
        error: null,
        token: null,
        user: null
      }
    });

    render(
      <Provider store={store}>
        <BrowserRouter>
          <LoginForm />
        </BrowserRouter>
      </Provider>
    );

    // Button should be disabled and show loading
    const button = screen.getByTestId('login-button');
    expect(button).toBeDisabled();
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });
}); 
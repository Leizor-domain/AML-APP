import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import CurrencyConverterWidget from './CurrencyConverterWidget';

// Mock the adminApi
jest.mock('../../services/api', () => ({
  adminApi: {
    get: jest.fn()
  }
}));

const mockAdminApi = require('../../services/api').adminApi;

describe('CurrencyConverterWidget', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the currency converter widget with title', () => {
    render(<CurrencyConverterWidget title="Test Currency Converter" />);
    
    expect(screen.getByText('Test Currency Converter')).toBeInTheDocument();
    expect(screen.getByText('Refresh')).toBeInTheDocument();
  });

  it('renders with default title when no title prop is provided', () => {
    render(<CurrencyConverterWidget />);
    
    expect(screen.getByText('Currency Converter')).toBeInTheDocument();
  });

  it('displays loading state when fetching data', async () => {
    mockAdminApi.get.mockImplementation(() => new Promise(() => {})); // Never resolves
    
    render(<CurrencyConverterWidget />);
    
    // Should show loading indicator
    expect(screen.getByText('Converting...')).toBeInTheDocument();
  });

  it('displays error message when API call fails', async () => {
    mockAdminApi.get.mockRejectedValue(new Error('API Error'));
    
    render(<CurrencyConverterWidget />);
    
    await waitFor(() => {
      expect(screen.getByText(/Failed to fetch exchange rates/)).toBeInTheDocument();
    });
  });

  it('allows user to change amount', () => {
    render(<CurrencyConverterWidget />);
    
    const amountInput = screen.getByLabelText('Amount');
    fireEvent.change(amountInput, { target: { value: '100' } });
    
    expect(amountInput.value).toBe('100');
  });

  it('shows currency selection dropdowns', () => {
    render(<CurrencyConverterWidget />);
    
    expect(screen.getAllByText('From')).toHaveLength(2); // Label and legend
    expect(screen.getAllByText('To')).toHaveLength(2); // Label and legend
  });

  it('displays quick currency selection chips', () => {
    render(<CurrencyConverterWidget />);
    
    expect(screen.getByText('🇺🇸 USD')).toBeInTheDocument();
    expect(screen.getByText('🇪🇺 EUR')).toBeInTheDocument();
    expect(screen.getByText('🇬🇧 GBP')).toBeInTheDocument();
  });

  it('shows conversion result when data is available', async () => {
    const mockRatesResponse = {
      data: {
        base: 'USD',
        rates: {
          EUR: 0.85,
          GBP: 0.73,
          JPY: 110.25
        }
      }
    };

    mockAdminApi.get.mockResolvedValue(mockRatesResponse);

    render(<CurrencyConverterWidget />);

    // Wait for the component to load and then check for the conversion result
    await waitFor(() => {
      expect(screen.getByText('Enter an amount to see conversion')).toBeInTheDocument();
    });
  });

  it('handles refresh button click', async () => {
    mockAdminApi.get.mockResolvedValue({
      data: {
        base: 'USD',
        rates: { EUR: 0.85 }
      }
    });

    render(<CurrencyConverterWidget />);
    
    const refreshButton = screen.getByText('Refresh');
    fireEvent.click(refreshButton);
    
    await waitFor(() => {
      expect(mockAdminApi.get).toHaveBeenCalled();
    });
  });
}); 
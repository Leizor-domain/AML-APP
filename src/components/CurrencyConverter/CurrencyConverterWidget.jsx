import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  Grid,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
  Alert,
  Button,
  Chip,
} from '@mui/material';
import { CurrencyExchange, Refresh, TrendingUp } from '@mui/icons-material';
import { adminApi } from '../../services/api';

const CurrencyConverterWidget = ({ title = "Currency Converter" }) => {
  const [currencies, setCurrencies] = useState([]);
  const [fromCurrency, setFromCurrency] = useState('USD');
  const [toCurrency, setToCurrency] = useState('EUR');
  const [amount, setAmount] = useState(1);
  const [conversionResult, setConversionResult] = useState(null);
  const [rates, setRates] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [lastUpdated, setLastUpdated] = useState(null);

  // Popular currencies for quick selection
  const popularCurrencies = [
    { code: 'USD', name: 'US Dollar', flag: '🇺🇸' },
    { code: 'EUR', name: 'Euro', flag: '🇪🇺' },
    { code: 'GBP', name: 'British Pound', flag: '🇬🇧' },
    { code: 'JPY', name: 'Japanese Yen', flag: '🇯🇵' },
    { code: 'CAD', name: 'Canadian Dollar', flag: '🇨🇦' },
    { code: 'AUD', name: 'Australian Dollar', flag: '🇦🇺' },
    { code: 'CHF', name: 'Swiss Franc', flag: '🇨🇭' },
    { code: 'CNY', name: 'Chinese Yuan', flag: '🇨🇳' },
    { code: 'INR', name: 'Indian Rupee', flag: '🇮🇳' },
    { code: 'BRL', name: 'Brazilian Real', flag: '🇧🇷' },
  ];

  // Fetch available currencies
  useEffect(() => {
    const fetchCurrencies = async () => {
      try {
        const response = await adminApi.get('/api/currency?base=USD');
        if (response.data && response.data.rates) {
          const currencyCodes = Object.keys(response.data.rates);
          setCurrencies(currencyCodes);
          setError(null);
        }
      } catch (err) {
        console.error('Failed to fetch currencies:', err);
        // Fallback to popular currencies
        setCurrencies(popularCurrencies.map(c => c.code));
        setError('Failed to load currency list - using fallback currencies');
      }
    };

    fetchCurrencies();
  }, []);

  // Fetch exchange rates
  const fetchRates = async (base = fromCurrency) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await adminApi.get(`/api/currency?base=${base}&symbols=${currencies.join(',')}`);
      
      if (response.data.error) {
        setError(response.data.error);
        setRates(null);
      } else {
        setRates(response.data);
        setLastUpdated(new Date());
        setError(null);
      }
    } catch (err) {
      console.error('Failed to fetch rates:', err);
      setError(err.response?.data?.error || 'Failed to fetch exchange rates');
      setRates(null);
    } finally {
      setLoading(false);
    }
  };

  // Convert currency
  const convertCurrency = async () => {
    if (!amount || amount <= 0) {
      setConversionResult(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await adminApi.get('/api/currency/convert', {
        params: {
          from: fromCurrency,
          to: toCurrency,
          amount: amount
        }
      });

      if (response.data.error) {
        setError(response.data.error);
        setConversionResult(null);
      } else {
        setConversionResult(response.data);
        setError(null);
      }
    } catch (err) {
      console.error('Failed to convert currency:', err);
      setError(err.response?.data?.error || 'Failed to convert currency');
      setConversionResult(null);
    } finally {
      setLoading(false);
    }
  };

  // Auto-convert when amount or currencies change
  useEffect(() => {
    if (amount > 0 && fromCurrency && toCurrency) {
      convertCurrency();
    }
  }, [amount, fromCurrency, toCurrency]);

  // Auto-refresh rates every 15 minutes
  useEffect(() => {
    fetchRates();
    
    const interval = setInterval(() => {
      fetchRates();
    }, 15 * 60 * 1000); // 15 minutes

    return () => clearInterval(interval);
  }, []);

  const handleCurrencyChange = (type, value) => {
    if (type === 'from') {
      setFromCurrency(value);
      fetchRates(value);
    } else {
      setToCurrency(value);
    }
  };

  const handleRefresh = () => {
    fetchRates();
  };

  const formatCurrency = (value, currency) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency,
      minimumFractionDigits: 2,
      maximumFractionDigits: 4,
    }).format(value);
  };

  const getCurrencyInfo = (code) => {
    return popularCurrencies.find(c => c.code === code) || { code, name: code, flag: '💱' };
  };

  return (
    <Card sx={{ p: 3, mb: 4 }}>
      <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
        <Box display="flex" alignItems="center" gap={1}>
          <CurrencyExchange color="primary" />
          <Typography variant="h6" sx={{ fontWeight: 600 }}>
            {title}
          </Typography>
        </Box>
        <Button
          startIcon={<Refresh />}
          onClick={handleRefresh}
          disabled={loading}
          size="small"
          variant="outlined"
        >
          Refresh
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={2} alignItems="center">
        <Grid item xs={12} sm={3}>
          <FormControl fullWidth size="small">
            <InputLabel>From</InputLabel>
            <Select
              value={fromCurrency}
              label="From"
              onChange={(e) => handleCurrencyChange('from', e.target.value)}
            >
              {currencies.map((currency) => {
                const info = getCurrencyInfo(currency);
                return (
                  <MenuItem key={currency} value={currency}>
                    <Box display="flex" alignItems="center" gap={1}>
                      <span>{info.flag}</span>
                      <span>{currency}</span>
                    </Box>
                  </MenuItem>
                );
              })}
            </Select>
          </FormControl>
        </Grid>

        <Grid item xs={12} sm={3}>
          <FormControl fullWidth size="small">
            <InputLabel>To</InputLabel>
            <Select
              value={toCurrency}
              label="To"
              onChange={(e) => handleCurrencyChange('to', e.target.value)}
            >
              {currencies.map((currency) => {
                const info = getCurrencyInfo(currency);
                return (
                  <MenuItem key={currency} value={currency}>
                    <Box display="flex" alignItems="center" gap={1}>
                      <span>{info.flag}</span>
                      <span>{currency}</span>
                    </Box>
                  </MenuItem>
                );
              })}
            </Select>
          </FormControl>
        </Grid>

        <Grid item xs={12} sm={3}>
          <TextField
            label="Amount"
            type="number"
            value={amount}
            onChange={(e) => setAmount(Number(e.target.value))}
            fullWidth
            size="small"
            inputProps={{ min: 0, step: 0.01 }}
          />
        </Grid>

        <Grid item xs={12} sm={3}>
          <Box display="flex" alignItems="center" gap={1}>
            {loading ? (
              <CircularProgress size={20} />
            ) : (
              <TrendingUp color="primary" />
            )}
            <Typography variant="body2" color="text.secondary">
              {loading ? 'Converting...' : 'Live Rates'}
            </Typography>
          </Box>
        </Grid>
      </Grid>

      {/* Conversion Result */}
      <Box sx={{ mt: 3, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
        {conversionResult ? (
          <Box>
            <Typography variant="h5" component="div" sx={{ fontWeight: 'bold', mb: 1 }}>
              {formatCurrency(conversionResult.convertedAmount, toCurrency)}
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
              Rate: 1 {fromCurrency} = {conversionResult.rate ? conversionResult.rate.toFixed(6) : 'N/A'} {toCurrency}
            </Typography>
            {lastUpdated && (
              <Typography variant="caption" color="text.secondary">
                Last updated: {lastUpdated.toLocaleTimeString()}
              </Typography>
            )}
          </Box>
        ) : (
          <Typography color="text.secondary">
            Enter an amount to see conversion
          </Typography>
        )}
      </Box>

      {/* Quick Currency Selection */}
      <Box sx={{ mt: 2 }}>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
          Quick Select:
        </Typography>
        <Box display="flex" flexWrap="wrap" gap={1}>
          {popularCurrencies.slice(0, 6).map((currency) => (
            <Chip
              key={currency.code}
              label={`${currency.flag} ${currency.code}`}
              size="small"
              variant={fromCurrency === currency.code ? "filled" : "outlined"}
              onClick={() => handleCurrencyChange('from', currency.code)}
              sx={{ cursor: 'pointer' }}
            />
          ))}
        </Box>
      </Box>
    </Card>
  );
};

export default CurrencyConverterWidget; 
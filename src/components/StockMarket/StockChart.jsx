import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
} from '@mui/material';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import { TrendingUp } from '@mui/icons-material';
import { adminApi } from '../../services/api';

const StockChart = ({ title = "Live Stock Market Data" }) => {
  const [stockData, setStockData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedSymbol, setSelectedSymbol] = useState('AAPL');

  const availableSymbols = [
    { value: 'AAPL', label: 'Apple Inc. (AAPL)' },
    { value: 'TSLA', label: 'Tesla Inc. (TSLA)' },
    { value: 'MSFT', label: 'Microsoft Corp. (MSFT)' },
    { value: 'GOOGL', label: 'Alphabet Inc. (GOOGL)' },
    { value: 'AMZN', label: 'Amazon.com Inc. (AMZN)' },
    { value: 'BTC/USD', label: 'Bitcoin (BTC/USD)' },
    { value: 'ETH/USD', label: 'Ethereum (ETH/USD)' },
  ];

  const fetchStockData = async (symbol) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await adminApi.get(`/api/stocks/${symbol}`);
      
      if (response.data.error) {
        setError(response.data.error);
        setStockData(null);
      } else {
        setStockData(response.data);
        setError(null);
      }
    } catch (err) {
      console.error('Failed to fetch stock data:', err);
      setError(err.response?.data?.error || 'Failed to fetch stock data');
      setStockData(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStockData(selectedSymbol);
  }, [selectedSymbol]);

  const formatChartData = (data) => {
    if (!data || !data.data) return [];
    
    return data.data
      .slice(-50) // Get last 50 data points for better chart display
      .map(point => ({
        datetime: new Date(point.datetime).toLocaleTimeString(),
        close: parseFloat(point.close),
        timestamp: new Date(point.datetime).getTime(),
      }))
      .sort((a, b) => a.timestamp - b.timestamp);
  };

  const getLatestPrice = () => {
    if (!stockData || !stockData.data || stockData.data.length === 0) return null;
    const latest = stockData.data[stockData.data.length - 1];
    return parseFloat(latest.close);
  };

  const getPriceChange = () => {
    if (!stockData || !stockData.data || stockData.data.length < 2) return null;
    const data = stockData.data;
    const latest = parseFloat(data[data.length - 1].close);
    const previous = parseFloat(data[data.length - 2].close);
    return latest - previous;
  };

  const chartData = formatChartData(stockData);
  const latestPrice = getLatestPrice();
  const priceChange = getPriceChange();

  return (
    <Card sx={{ p: 3, mb: 4 }}>
      <Box display="flex" alignItems="center" gap={1} mb={2}>
        <TrendingUp color="primary" />
        <Typography variant="h6" sx={{ fontWeight: 600 }}>
          {title}
        </Typography>
      </Box>

      <Box sx={{ mb: 3 }}>
        <FormControl fullWidth size="small">
          <InputLabel>Select Symbol</InputLabel>
          <Select
            value={selectedSymbol}
            label="Select Symbol"
            onChange={(e) => setSelectedSymbol(e.target.value)}
          >
            {availableSymbols.map((symbol) => (
              <MenuItem key={symbol.value} value={symbol.value}>
                {symbol.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

      {loading ? (
        <Box display="flex" justifyContent="center" alignItems="center" minHeight={300}>
          <CircularProgress />
        </Box>
      ) : error ? (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      ) : stockData && chartData.length > 0 ? (
        <>
          <Box sx={{ mb: 2, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
            <Typography variant="h5" component="div" sx={{ fontWeight: 'bold' }}>
              ${latestPrice?.toFixed(2)}
            </Typography>
            {priceChange !== null && (
              <Typography
                variant="body2"
                color={priceChange >= 0 ? 'success.main' : 'error.main'}
                sx={{ fontWeight: 'medium' }}
              >
                {priceChange >= 0 ? '+' : ''}{priceChange.toFixed(2)} (
                {((priceChange / (latestPrice - priceChange)) * 100).toFixed(2)}%)
              </Typography>
            )}
            <Typography variant="caption" color="text.secondary">
              {stockData.data[stockData.data.length - 1]?.datetime}
            </Typography>
          </Box>

          <Box sx={{ height: 300 }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis
                  dataKey="datetime"
                  tick={{ fontSize: 12 }}
                  interval="preserveStartEnd"
                />
                <YAxis
                  domain={['dataMin - 1', 'dataMax + 1']}
                  tick={{ fontSize: 12 }}
                  tickFormatter={(value) => `$${value.toFixed(2)}`}
                />
                <Tooltip
                  formatter={(value) => [`$${value.toFixed(2)}`, 'Price']}
                  labelFormatter={(label) => `Time: ${label}`}
                />
                <Line
                  type="monotone"
                  dataKey="close"
                  stroke="#1976d2"
                  strokeWidth={2}
                  dot={false}
                  activeDot={{ r: 4 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </Box>
        </>
      ) : (
        <Box display="flex" justifyContent="center" alignItems="center" minHeight={300}>
          <Typography color="text.secondary">
            No data available for {selectedSymbol}
          </Typography>
        </Box>
      )}
    </Card>
  );
};

export default StockChart; 
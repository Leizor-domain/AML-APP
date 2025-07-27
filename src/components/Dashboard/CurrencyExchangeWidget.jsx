import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Grid,
  TextField,
  Autocomplete,
  CircularProgress,
  Box,
} from '@mui/material';
import { CurrencyExchange } from '@mui/icons-material';

const CurrencyExchangeWidget = ({ title = "Live Currency Exchange" }) => {
  const [currencies, setCurrencies] = useState([]);
  const [from, setFrom] = useState('USD');
  const [to, setTo] = useState('EUR');
  const [amount, setAmount] = useState(1);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Fetch available currencies
    fetch('https://api.exchangerate.host/symbols')
      .then(res => res.json())
      .then(data => {
        const symbols = data?.symbols || {};
        setCurrencies(Object.keys(symbols));
        setError(null);
      })
      .catch(err => {
        console.error('Failed to fetch currencies:', err);
        setCurrencies(['USD', 'EUR', 'GBP', 'JPY', 'CAD', 'AUD', 'CHF', 'CNY']); // Fallback currencies
        setError('Failed to load currency list');
      });
  }, []);

  useEffect(() => {
    if (!from || !to || !amount || amount <= 0) {
      setResult(null);
      return;
    }
    
    setLoading(true);
    setError(null);
    
    fetch(`https://api.exchangerate.host/convert?from=${from}&to=${to}&amount=${amount}`)
      .then(res => res.json())
      .then(data => {
        if (data.success) {
          setResult(data.result);
        } else {
          setError('Failed to convert currency');
        }
        setLoading(false);
      })
      .catch(err => {
        console.error('Failed to convert currency:', err);
        setError('Failed to convert currency');
        setLoading(false);
      });
  }, [from, to, amount]);

  return (
    <Card sx={{ p: 3, mb: 4 }}>
      <Box display="flex" alignItems="center" gap={1} mb={2}>
        <CurrencyExchange color="primary" />
        <Typography variant="h6" sx={{ fontWeight: 600 }}>
          {title}
        </Typography>
      </Box>
      
      <Grid container spacing={2} alignItems="center">
        <Grid item xs={12} sm={4}>
          <Autocomplete
            options={currencies}
            value={from}
            onChange={(_, v) => setFrom(v)}
            renderInput={(params) => <TextField {...params} label="From" variant="outlined" />}
            disableClearable
            size="small"
          />
        </Grid>
        <Grid item xs={12} sm={4}>
          <Autocomplete
            options={currencies}
            value={to}
            onChange={(_, v) => setTo(v)}
            renderInput={(params) => <TextField {...params} label="To" variant="outlined" />}
            disableClearable
            size="small"
          />
        </Grid>
        <Grid item xs={12} sm={4}>
          <TextField
            label="Amount"
            type="number"
            value={amount}
            onChange={e => setAmount(Number(e.target.value))}
            fullWidth
            size="small"
            inputProps={{ min: 0, step: 0.01 }}
          />
        </Grid>
      </Grid>
      
      <Box sx={{ mt: 2, minHeight: 32 }}>
        {loading ? (
          <Box display="flex" alignItems="center" gap={1}>
            <CircularProgress size={20} />
            <Typography variant="body2" color="text.secondary">
              Converting...
            </Typography>
          </Box>
        ) : error ? (
          <Typography variant="body2" color="error">
            {error}
          </Typography>
        ) : result !== null && (
          <Typography variant="subtitle1">
            {amount} {from} = <b>{result?.toLocaleString(undefined, { maximumFractionDigits: 4 })} {to}</b>
          </Typography>
        )}
      </Box>
    </Card>
  );
};

export default CurrencyExchangeWidget; 
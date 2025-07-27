import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  CircularProgress,
  Alert,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from '@mui/material';
import {
  Assessment,
  Download,
  DateRange,
  TrendingUp,
  Warning,
  Security,
} from '@mui/icons-material';
import { adminApi } from '../services/api';

const ReportsPage = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [reportType, setReportType] = useState('transaction_summary');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [reportData, setReportData] = useState(null);

  const reportTypes = [
    { value: 'transaction_summary', label: 'Transaction Summary Report' },
    { value: 'alert_analysis', label: 'Alert Analysis Report' },
    { value: 'risk_assessment', label: 'Risk Assessment Report' },
    { value: 'user_activity', label: 'User Activity Report' },
  ];

  const generateReport = async () => {
    setLoading(true);
    setError(null);
    try {
      // Mock report generation - in a real app, this would call the backend
      const response = await adminApi.get('/admin/reports', {
        params: {
          type: reportType,
          dateFrom,
          dateTo,
        },
      });
      setReportData(response.data);
    } catch (err) {
      setError('Failed to generate report. Please try again.');
      // Mock data for demonstration
      setReportData({
        title: `${reportTypes.find(t => t.value === reportType)?.label}`,
        generatedAt: new Date().toISOString(),
        summary: {
          totalTransactions: 1250,
          totalAlerts: 89,
          highRiskCount: 23,
          mediumRiskCount: 45,
          lowRiskCount: 21,
        },
        details: [
          { date: '2024-01-15', transactions: 45, alerts: 3, riskScore: 'Medium' },
          { date: '2024-01-16', transactions: 52, alerts: 7, riskScore: 'High' },
          { date: '2024-01-17', transactions: 38, alerts: 2, riskScore: 'Low' },
        ],
      });
    } finally {
      setLoading(false);
    }
  };

  const downloadReport = () => {
    // Mock download functionality
    const dataStr = JSON.stringify(reportData, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${reportType}_report_${new Date().toISOString().split('T')[0]}.json`;
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>
        Reports
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Generate and download comprehensive system reports
      </Typography>

      {/* Report Configuration */}
      <Card sx={{ p: 3, mb: 4 }}>
        <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
          Report Configuration
        </Typography>
        <Grid container spacing={3}>
          <Grid item xs={12} md={4}>
            <FormControl fullWidth>
              <InputLabel>Report Type</InputLabel>
              <Select
                value={reportType}
                label="Report Type"
                onChange={(e) => setReportType(e.target.value)}
              >
                {reportTypes.map((type) => (
                  <MenuItem key={type.value} value={type.value}>
                    {type.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Date From"
              type="date"
              value={dateFrom}
              onChange={(e) => setDateFrom(e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Date To"
              type="date"
              value={dateTo}
              onChange={(e) => setDateTo(e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
        </Grid>
        <Box sx={{ mt: 3 }}>
          <Button
            variant="contained"
            startIcon={<Assessment />}
            onClick={generateReport}
            disabled={loading}
            sx={{ mr: 2 }}
          >
            {loading ? <CircularProgress size={20} /> : 'Generate Report'}
          </Button>
          {reportData && (
            <Button
              variant="outlined"
              startIcon={<Download />}
              onClick={downloadReport}
            >
              Download Report
            </Button>
          )}
        </Box>
      </Card>

      {/* Error Display */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Report Results */}
      {reportData && (
        <Card sx={{ p: 3 }}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
            <Typography variant="h6" sx={{ fontWeight: 600 }}>
              {reportData.title}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Generated: {new Date(reportData.generatedAt).toLocaleString()}
            </Typography>
          </Box>

          {/* Summary Cards */}
          <Grid container spacing={3} sx={{ mb: 4 }}>
            <Grid item xs={12} sm={6} md={3}>
              <Paper sx={{ p: 2, textAlign: 'center' }}>
                <TrendingUp color="primary" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4" sx={{ fontWeight: 700 }}>
                  {reportData.summary?.totalTransactions || 0}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Total Transactions
                </Typography>
              </Paper>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Paper sx={{ p: 2, textAlign: 'center' }}>
                <Warning color="error" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4" sx={{ fontWeight: 700 }}>
                  {reportData.summary?.totalAlerts || 0}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Total Alerts
                </Typography>
              </Paper>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Paper sx={{ p: 2, textAlign: 'center' }}>
                <Security color="warning" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4" sx={{ fontWeight: 700 }}>
                  {reportData.summary?.highRiskCount || 0}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  High Risk Alerts
                </Typography>
              </Paper>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Paper sx={{ p: 2, textAlign: 'center' }}>
                <Assessment color="success" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4" sx={{ fontWeight: 700 }}>
                  {reportData.summary?.mediumRiskCount || 0}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Medium Risk Alerts
                </Typography>
              </Paper>
            </Grid>
          </Grid>

          {/* Detailed Table */}
          {reportData.details && reportData.details.length > 0 && (
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Date</TableCell>
                    <TableCell align="right">Transactions</TableCell>
                    <TableCell align="right">Alerts</TableCell>
                    <TableCell>Risk Score</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {reportData.details.map((row, index) => (
                    <TableRow key={index}>
                      <TableCell>{row.date}</TableCell>
                      <TableCell align="right">{row.transactions}</TableCell>
                      <TableCell align="right">{row.alerts}</TableCell>
                      <TableCell>{row.riskScore}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </Card>
      )}
    </Box>
  );
};

export default ReportsPage; 
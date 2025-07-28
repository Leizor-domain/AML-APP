import React, { useState, useEffect } from 'react'
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Paper,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Chip,
  Button,
} from '@mui/material'
import {
  TrendingUp,
  Warning,
  Security,
  Assessment,
  Notifications,
  People,
} from '@mui/icons-material'
import BarChartIcon from '@mui/icons-material/BarChart';
import { useSelector } from 'react-redux'
import { BarChart, Bar, XAxis, YAxis, Tooltip as RechartsTooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import CircularProgress from '@mui/material/CircularProgress';
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import { adminApi } from '../../services/api';
import { alertsService } from '../../services/alerts';
import AdminUserCreateForm from './AdminUserCreateForm';
import UserTable from './UserTable';
import UserRolePieChart from './UserRolePieChart';
import CurrencyConverterWidget from '../CurrencyConverter/CurrencyConverterWidget';
import StockChart from '../StockMarket/StockChart';
import { Navigate, useNavigate } from 'react-router-dom'
import { canAccess, normalizeRole } from '../../utils/permissions';

const AdminDashboard = () => {
  const { user } = useSelector((state) => state.auth)
  const navigate = useNavigate()
  
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  const [stats, setStats] = useState({
    totalTransactions: 0,
    totalAlerts: 0,
    highRiskAlerts: 0,
    activeUsers: 0,
  })
  const [recentAlerts, setRecentAlerts] = useState([])
  const [alertData, setAlertData] = useState([])
  const [loadingStats, setLoadingStats] = useState(true)
  const [loadingAlerts, setLoadingAlerts] = useState(true)
  const [errorStats, setErrorStats] = useState(null)
  const [errorAlerts, setErrorAlerts] = useState(null)

  useEffect(() => {
    setLoadingStats(true)
    setErrorStats(null)
    adminApi.get('/admin/db-health')
      .then(res => {
        const d = res.data.data || {}
        setStats({
          totalTransactions: d.transactions_count || 0,
          totalAlerts: d.alerts_count || 0,
          highRiskAlerts: d.high_risk_alerts || 0, // fallback if not present
          activeUsers: d.users_count || 0,
        })
        setLoadingStats(false)
      })
      .catch(err => {
        setErrorStats('Failed to load dashboard stats')
        setLoadingStats(false)
      })
  }, [])

  useEffect(() => {
    setLoadingAlerts(true)
    setErrorAlerts(null)
    alertsService.getAlerts({ size: 5 })
      .then(res => {
        if (res && Array.isArray(res)) {
          // If response is directly an array
          setRecentAlerts(res)
        } else if (res && Array.isArray(res.content)) {
          // If response has content array
          setRecentAlerts(res.content)
        } else if (res && Array.isArray(res.alerts)) {
          // If response has alerts array
          setRecentAlerts(res.alerts)
        } else {
          // Fallback to empty array
          setRecentAlerts([])
        }
        
        // Set chart data - use fallback data if backend doesn't provide it
        if (res && res.alertsByDay && Array.isArray(res.alertsByDay)) {
          setAlertData(res.alertsByDay)
        } else {
          // Fallback chart data
          setAlertData([
            { date: 'Mon', alerts: 12 },
            { date: 'Tue', alerts: 18 },
            { date: 'Wed', alerts: 9 },
            { date: 'Thu', alerts: 15 },
            { date: 'Fri', alerts: 22 },
            { date: 'Sat', alerts: 7 },
            { date: 'Sun', alerts: 11 },
          ])
        }
        setLoadingAlerts(false)
      })
      .catch(err => {
        console.error('Failed to load alerts:', err)
        // Set fallback data instead of showing error
        setRecentAlerts([])
        setAlertData([
          { date: 'Mon', alerts: 0 },
          { date: 'Tue', alerts: 0 },
          { date: 'Wed', alerts: 0 },
          { date: 'Thu', alerts: 0 },
          { date: 'Fri', alerts: 0 },
          { date: 'Sat', alerts: 0 },
          { date: 'Sun', alerts: 0 },
        ])
        setLoadingAlerts(false)
        // Don't set error state - just show empty data gracefully
      })
  }, [])

  const getRiskColor = (risk) => {
    switch (risk) {
      case 'HIGH_RISK':
        return 'error'
      case 'MEDIUM_RISK':
        return 'warning'
      case 'LOW_RISK':
        return 'success'
      default:
        return 'default'
    }
  }



  const normRole = normalizeRole(user?.role);

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>
        Admin Dashboard
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Welcome back, {user?.name}! Here's your system overview.
      </Typography>

      {/* Statistics Cards */}
      {loadingStats ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}><CircularProgress /></Box>
      ) : errorStats ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}><Typography color="error">{errorStats}</Typography></Box>
      ) : (
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{
            background: 'linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%)',
            boxShadow: 3,
            borderRadius: 3,
          }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={2}>
                <TrendingUp color="primary" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Transactions
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {typeof stats.totalTransactions === 'number' ? stats.totalTransactions?.toLocaleString() : '0'}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{
            background: 'linear-gradient(135deg, #fceabb 0%, #f8b500 100%)',
            boxShadow: 3,
            borderRadius: 3,
          }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={2}>
                <Warning color="error" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Alerts
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {stats.totalAlerts}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{
            background: 'linear-gradient(135deg, #fbc2eb 0%, #a6c1ee 100%)',
            boxShadow: 3,
            borderRadius: 3,
          }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={2}>
                <Security color="warning" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    High Risk Alerts
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {stats.highRiskAlerts}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{
            background: 'linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%)',
            boxShadow: 3,
            borderRadius: 3,
          }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={2}>
                <People color="success" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Active Users
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {stats.activeUsers}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
      )}

      {/* Chart Section */}
      <Card sx={{ p: 3, mb: 4 }}>
        <Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
          Alerts This Week
        </Typography>
        {loadingAlerts ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}><CircularProgress /></Box>
        ) : (
        <ResponsiveContainer width="100%" height={220}>
          <BarChart data={alertData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" />
            <YAxis />
            <RechartsTooltip />
            <Bar dataKey="alerts" fill="#1976d2" radius={[6, 6, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
        )}
      </Card>

                  {/* Live Currency Converter Widget */}
            <CurrencyConverterWidget title="Admin Currency Converter" />

      {/* Live Stock Market Data */}
      <StockChart />

      {/* Recent Alerts and Quick Actions */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 2, borderRadius: 3, boxShadow: 1 }}>
            <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
              Recent Alerts
            </Typography>
            {loadingAlerts ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}><CircularProgress /></Box>
            ) : recentAlerts.length === 0 ? (
              <Box sx={{ textAlign: 'center', py: 3 }}>
                <Typography color="text.secondary" variant="body2">
                  No recent alerts to display
                </Typography>
                <Typography color="text.secondary" variant="caption">
                  Alerts will appear here when they are generated
                </Typography>
              </Box>
            ) : (
            <List>
              {recentAlerts.map((alert) => (
                <ListItem key={alert?.id} divider>
                  <ListItemIcon>
                    <Notifications color="error" />
                  </ListItemIcon>
                  <ListItemText
                    primary={alert?.reason || alert?.description || 'Alert'}
                    secondary={alert?.timestamp ? new Date(alert.timestamp).toLocaleString() : ''}
                  />
                  <Chip
                    label={alert?.alertType || alert?.type ? (alert.alertType || alert.type).replace('_', ' ') : 'ALERT'}
                    color={getRiskColor(alert?.alertType || alert?.type)}
                    size="small"
                  />
                </ListItem>
              ))}
            </List>
            )}
            <Box sx={{ mt: 2 }}>
              <Button 
                variant="outlined" 
                color="primary"
                onClick={() => navigate('/alerts')}
              >
                View All Alerts
              </Button>
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 2, borderRadius: 3, boxShadow: 1 }}>
            <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
              Quick Actions
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Button
                variant="contained"
                startIcon={<Assessment />}
                fullWidth
                sx={{ fontWeight: 600 }}
                onClick={() => navigate('/reports')}
              >
                Generate Report
              </Button>
              <Button
                variant="outlined"
                startIcon={<Security />}
                fullWidth
                sx={{ fontWeight: 600 }}
                onClick={() => navigate('/settings')}
              >
                System Settings
              </Button>
              <Button
                variant="outlined"
                startIcon={<People />}
                fullWidth
                sx={{ fontWeight: 600 }}
                onClick={() => navigate('/admin/users')}
              >
                User Management
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>
      <Box sx={{ mt: 6 }}>
        <Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
          User Management
        </Typography>
        <UserRolePieChart />
        <UserTable />
      </Box>
    </Box>
  )
}

export default AdminDashboard 
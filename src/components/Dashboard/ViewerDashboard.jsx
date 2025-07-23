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
  CircularProgress,
} from '@mui/material'
import {
  TrendingUp,
  Warning,
  Security,
  Assessment,
  Notifications,
  Visibility,
} from '@mui/icons-material'
import BarChartIcon from '@mui/icons-material/BarChart';
import { useSelector } from 'react-redux'
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import TooltipMUI from '@mui/material/Tooltip';
import { adminApi } from '../../services/api';
import { alertsService } from '../../services/alerts';

const ViewerDashboard = () => {
  const { user } = useSelector((state) => state.auth)
  const [stats, setStats] = useState({
    totalTransactions: 0,
    totalAlerts: 0,
    resolvedAlerts: 0,
    systemStatus: 'OPERATIONAL',
  })
  const [recentAlerts, setRecentAlerts] = useState([])
  const [statusData, setStatusData] = useState([])
  const [loadingStats, setLoadingStats] = useState(true)
  const [loadingAlerts, setLoadingAlerts] = useState(true)
  const [errorStats, setErrorStats] = useState(null)
  const [errorAlerts, setErrorAlerts] = useState(null)

  useEffect(() => {
    setLoadingStats(true)
    setErrorStats(null)
    adminApi.get('/public/db/health')
      .then(res => {
        const d = res.data.data || {}
        setStats({
          totalTransactions: d.transactions_count || 0,
          totalAlerts: d.alerts_count || 0,
          resolvedAlerts: d.resolved_alerts || 0,
          systemStatus: d.system_status || 'OPERATIONAL',
        })
        if (d.statusPie) {
          setStatusData(d.statusPie)
        } else {
          setStatusData([
            { name: 'Healthy', value: 70 },
            { name: 'Warning', value: 20 },
            { name: 'Critical', value: 10 },
          ])
        }
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
        const alerts = res.content || res.alerts || []
        setRecentAlerts(alerts)
        setLoadingAlerts(false)
      })
      .catch(err => {
        setErrorAlerts('Failed to load recent alerts')
        setLoadingAlerts(false)
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

  const getStatusColor = (status) => {
    switch (status) {
      case 'RESOLVED':
        return 'success'
      case 'IN_PROGRESS':
        return 'warning'
      case 'PENDING':
        return 'error'
      default:
        return 'default'
    }
  }

  // Mock data for system status
  const COLORS = ['#43a047', '#ffa726', '#e53935'];

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>
        Viewer Dashboard
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Welcome back, {user?.name}! Here's the system overview.
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
                    {stats.totalTransactions.toLocaleString()}
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
            background: 'linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%)',
            boxShadow: 3,
            borderRadius: 3,
          }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={2}>
                <Security color="success" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Resolved Alerts
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {stats.resolvedAlerts}
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
                <Assessment color="info" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    System Status
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {stats.systemStatus}
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
          System Status
        </Typography>
        {loadingStats ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}><CircularProgress /></Box>
        ) : errorStats ? (
          <Typography color="error">{errorStats}</Typography>
        ) : (
        <ResponsiveContainer width="100%" height={220}>
          <PieChart>
            <Pie data={statusData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={70} label>
              {statusData.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip />
            <Legend />
          </PieChart>
        </ResponsiveContainer>
        )}
      </Card>

      {/* Recent Alerts and Quick Actions */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 2, borderRadius: 3, boxShadow: 1 }}>
            <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
              Recent Alerts
            </Typography>
            {loadingAlerts ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}><CircularProgress /></Box>
            ) : errorAlerts ? (
              <Typography color="error">{errorAlerts}</Typography>
            ) : (
            <List>
              {recentAlerts.map((alert) => (
                <ListItem key={alert.id} divider>
                  <ListItemIcon>
                    <Notifications color="error" />
                  </ListItemIcon>
                  <ListItemText
                    primary={alert.description}
                    secondary={new Date(alert.timestamp).toLocaleString()}
                  />
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Chip
                      label={alert.type ? alert.type.replace('_', ' ') : ''}
                      color={getRiskColor(alert.type)}
                      size="small"
                    />
                    <Chip
                      label={alert.status ? alert.status.replace('_', ' ') : ''}
                      color={getStatusColor(alert.status)}
                      size="small"
                    />
                  </Box>
                </ListItem>
              ))}
            </List>
            )}
            <Box sx={{ mt: 2 }}>
              <Button variant="outlined" color="primary">
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
                startIcon={<Visibility />}
                fullWidth
                sx={{ fontWeight: 600 }}
              >
                View Reports
              </Button>
              <Button
                variant="outlined"
                startIcon={<Assessment />}
                fullWidth
                sx={{ fontWeight: 600 }}
              >
                System Status
              </Button>
              <Button
                variant="outlined"
                startIcon={<TrendingUp />}
                fullWidth
                sx={{ fontWeight: 600 }}
              >
                View Statistics
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  )
}

export default ViewerDashboard 
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
import { useSelector } from 'react-redux'

const AdminDashboard = () => {
  const { user } = useSelector((state) => state.auth)
  const [stats, setStats] = useState({
    totalTransactions: 0,
    totalAlerts: 0,
    highRiskAlerts: 0,
    activeUsers: 0,
  })

  const [recentAlerts, setRecentAlerts] = useState([])

  useEffect(() => {
    // In a real app, fetch dashboard data from API
    setStats({
      totalTransactions: 15420,
      totalAlerts: 342,
      highRiskAlerts: 23,
      activeUsers: 45,
    })

    setRecentAlerts([
      {
        id: 1,
        type: 'HIGH_RISK',
        description: 'Large transaction from sanctioned country',
        timestamp: '2024-01-15T10:30:00Z',
      },
      {
        id: 2,
        type: 'MEDIUM_RISK',
        description: 'Unusual transaction pattern detected',
        timestamp: '2024-01-15T09:15:00Z',
      },
    ])
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

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Admin Dashboard
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Welcome back, {user?.name}! Here's your system overview.
      </Typography>

      {/* Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <TrendingUp color="primary" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Transactions
                  </Typography>
                  <Typography variant="h4">
                    {stats.totalTransactions.toLocaleString()}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Warning color="error" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Alerts
                  </Typography>
                  <Typography variant="h4">{stats.totalAlerts}</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Security color="warning" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    High Risk Alerts
                  </Typography>
                  <Typography variant="h4">{stats.highRiskAlerts}</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <People color="success" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Active Users
                  </Typography>
                  <Typography variant="h4">{stats.activeUsers}</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Recent Alerts and Quick Actions */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Recent Alerts
            </Typography>
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
                  <Chip
                    label={alert.type.replace('_', ' ')}
                    color={getRiskColor(alert.type)}
                    size="small"
                  />
                </ListItem>
              ))}
            </List>
            <Box sx={{ mt: 2 }}>
              <Button variant="outlined" color="primary">
                View All Alerts
              </Button>
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Quick Actions
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Button
                variant="contained"
                startIcon={<Assessment />}
                fullWidth
              >
                Generate Report
              </Button>
              <Button
                variant="outlined"
                startIcon={<Security />}
                fullWidth
              >
                System Settings
              </Button>
              <Button
                variant="outlined"
                startIcon={<People />}
                fullWidth
              >
                User Management
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  )
}

export default AdminDashboard 
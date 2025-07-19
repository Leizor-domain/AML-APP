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
  Search,
} from '@mui/icons-material'
import { useSelector } from 'react-redux'

const AnalystDashboard = () => {
  const { user } = useSelector((state) => state.auth)
  const [stats, setStats] = useState({
    pendingAlerts: 0,
    analyzedToday: 0,
    highRiskCases: 0,
    averageResponseTime: 0,
  })

  const [pendingAlerts, setPendingAlerts] = useState([])

  useEffect(() => {
    // In a real app, fetch analyst-specific data from API
    setStats({
      pendingAlerts: 15,
      analyzedToday: 8,
      highRiskCases: 3,
      averageResponseTime: 2.5,
    })

    setPendingAlerts([
      {
        id: 1,
        type: 'HIGH_RISK',
        description: 'Suspicious transaction pattern',
        timestamp: '2024-01-15T10:30:00Z',
        priority: 'HIGH',
      },
      {
        id: 2,
        type: 'MEDIUM_RISK',
        description: 'Unusual amount transfer',
        timestamp: '2024-01-15T09:15:00Z',
        priority: 'MEDIUM',
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
        Analyst Dashboard
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Welcome back, {user?.name}! Here are your pending tasks.
      </Typography>

      {/* Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Warning color="error" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Pending Alerts
                  </Typography>
                  <Typography variant="h4">{stats.pendingAlerts}</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Assessment color="primary" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Analyzed Today
                  </Typography>
                  <Typography variant="h4">{stats.analyzedToday}</Typography>
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
                    High Risk Cases
                  </Typography>
                  <Typography variant="h4">{stats.highRiskCases}</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <TrendingUp color="success" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Avg Response (hrs)
                  </Typography>
                  <Typography variant="h4">{stats.averageResponseTime}</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Pending Alerts and Quick Actions */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Pending Alerts
            </Typography>
            <List>
              {pendingAlerts.map((alert) => (
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
                      label={alert.type.replace('_', ' ')}
                      color={getRiskColor(alert.type)}
                      size="small"
                    />
                    <Chip
                      label={alert.priority}
                      color={alert.priority === 'HIGH' ? 'error' : 'warning'}
                      size="small"
                    />
                  </Box>
                </ListItem>
              ))}
            </List>
            <Box sx={{ mt: 2 }}>
              <Button variant="outlined" color="primary">
                View All Pending Alerts
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
                startIcon={<Search />}
                fullWidth
              >
                Start Analysis
              </Button>
              <Button
                variant="outlined"
                startIcon={<Assessment />}
                fullWidth
              >
                Create Report
              </Button>
              <Button
                variant="outlined"
                startIcon={<Security />}
                fullWidth
              >
                Risk Assessment
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  )
}

export default AnalystDashboard 
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
  SupervisorAccount,
  People,
} from '@mui/icons-material'
import { useSelector } from 'react-redux'

const SupervisorDashboard = () => {
  const { user } = useSelector((state) => state.auth)
  const [stats, setStats] = useState({
    teamAlerts: 0,
    pendingReviews: 0,
    teamPerformance: 0,
    escalatedCases: 0,
  })

  const [teamAlerts, setTeamAlerts] = useState([])

  useEffect(() => {
    // In a real app, fetch supervisor-specific data from API
    setStats({
      teamAlerts: 28,
      pendingReviews: 12,
      teamPerformance: 85,
      escalatedCases: 5,
    })

    setTeamAlerts([
      {
        id: 1,
        type: 'HIGH_RISK',
        description: 'Large transaction from sanctioned country',
        analyst: 'John Doe',
        timestamp: '2024-01-15T10:30:00Z',
        status: 'PENDING_REVIEW',
      },
      {
        id: 2,
        type: 'MEDIUM_RISK',
        description: 'Unusual transaction pattern detected',
        analyst: 'Jane Smith',
        timestamp: '2024-01-15T09:15:00Z',
        status: 'ESCALATED',
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

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING_REVIEW':
        return 'warning'
      case 'ESCALATED':
        return 'error'
      case 'APPROVED':
        return 'success'
      default:
        return 'default'
    }
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Supervisor Dashboard
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Welcome back, {user?.name}! Here's your team overview.
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
                    Team Alerts
                  </Typography>
                  <Typography variant="h4">{stats.teamAlerts}</Typography>
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
                    Pending Reviews
                  </Typography>
                  <Typography variant="h4">{stats.pendingReviews}</Typography>
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
                    Team Performance
                  </Typography>
                  <Typography variant="h4">{stats.teamPerformance}%</Typography>
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
                    Escalated Cases
                  </Typography>
                  <Typography variant="h4">{stats.escalatedCases}</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Team Alerts and Quick Actions */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Team Alerts Requiring Review
            </Typography>
            <List>
              {teamAlerts.map((alert) => (
                <ListItem key={alert.id} divider>
                  <ListItemIcon>
                    <Notifications color="error" />
                  </ListItemIcon>
                  <ListItemText
                    primary={alert.description}
                    secondary={`${alert.analyst} • ${new Date(alert.timestamp).toLocaleString()}`}
                  />
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Chip
                      label={alert.type.replace('_', ' ')}
                      color={getRiskColor(alert.type)}
                      size="small"
                    />
                    <Chip
                      label={alert.status.replace('_', ' ')}
                      color={getStatusColor(alert.status)}
                      size="small"
                    />
                  </Box>
                </ListItem>
              ))}
            </List>
            <Box sx={{ mt: 2 }}>
              <Button variant="outlined" color="primary">
                Review All Alerts
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
                startIcon={<SupervisorAccount />}
                fullWidth
              >
                Review Cases
              </Button>
              <Button
                variant="outlined"
                startIcon={<People />}
                fullWidth
              >
                Team Management
              </Button>
              <Button
                variant="outlined"
                startIcon={<Assessment />}
                fullWidth
              >
                Performance Report
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  )
}

export default SupervisorDashboard 
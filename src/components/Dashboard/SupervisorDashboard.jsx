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
  SupervisorAccount,
  People,
} from '@mui/icons-material'
import BarChartIcon from '@mui/icons-material/BarChart';
import { useSelector } from 'react-redux'
import { AreaChart, Area, XAxis, YAxis, Tooltip as RechartsTooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import Tooltip from '@mui/material/Tooltip';
import { adminApi } from '../../services/api';
import { alertsService } from '../../services/alerts';
import CurrencyExchangeWidget from './CurrencyExchangeWidget';
import { Navigate } from 'react-router-dom'
import { canAccess, normalizeRole } from '../../utils/permissions';

const SupervisorDashboard = () => {
  const { user } = useSelector((state) => state.auth)
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  const [stats, setStats] = useState({
    teamAlerts: 0,
    pendingReviews: 0,
    teamPerformance: 0,
    escalatedCases: 0,
  })
  const [teamAlerts, setTeamAlerts] = useState([])
  const [teamData, setTeamData] = useState([])
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
          teamAlerts: d.team_alerts || 0,
          pendingReviews: d.pending_reviews || 0,
          teamPerformance: d.team_performance || 0,
          escalatedCases: d.escalated_cases || 0,
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
    alertsService.getAlerts({ team: true, size: 5 })
      .then(res => {
        const alerts = res.content || res.alerts || []
        setTeamAlerts(alerts)
        // If backend provides time-series, use it; else fallback
        if (res.teamPerformanceByDay) {
          setTeamData(res.teamPerformanceByDay)
        } else {
          setTeamData([
            { date: 'Mon', performance: 80 },
            { date: 'Tue', performance: 90 },
            { date: 'Wed', performance: 75 },
            { date: 'Thu', performance: 95 },
            { date: 'Fri', performance: 85 },
            { date: 'Sat', performance: 70 },
            { date: 'Sun', performance: 88 },
          ])
        }
        setLoadingAlerts(false)
      })
      .catch(err => {
        setErrorAlerts('Failed to load team alerts')
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
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>
        Supervisor Dashboard
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Welcome back, {user?.name}! Here's your team overview.
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
            background: 'linear-gradient(135deg, #fceabb 0%, #f8b500 100%)',
            boxShadow: 3,
            borderRadius: 3,
          }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={2}>
                <Warning color="error" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Team Alerts
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {typeof stats.teamAlerts === 'number' ? stats.teamAlerts?.toLocaleString() : '0'}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{
            background: 'linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%)',
            boxShadow: 3,
            borderRadius: 3,
          }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={2}>
                <Assessment color="primary" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Pending Reviews
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {typeof stats.pendingReviews === 'number' ? stats.pendingReviews?.toLocaleString() : '0'}
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
                <TrendingUp color="success" sx={{ fontSize: 36 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Team Performance
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {typeof stats.teamPerformance === 'number' ? stats.teamPerformance?.toLocaleString() : '0'}%
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
                    Escalated Cases
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {typeof stats.escalatedCases === 'number' ? stats.escalatedCases?.toLocaleString() : '0'}
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
          Team Performance This Week
        </Typography>
        {loadingAlerts ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}><CircularProgress /></Box>
        ) : errorAlerts ? (
          <Typography color="error">{errorAlerts}</Typography>
        ) : (
        <ResponsiveContainer width="100%" height={220}>
          <AreaChart data={teamData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" />
            <YAxis />
            <RechartsTooltip />
            <Area type="monotone" dataKey="performance" stroke="#1976d2" fill="#1976d2" fillOpacity={0.2} strokeWidth={3} />
          </AreaChart>
        </ResponsiveContainer>
        )}
      </Card>

      {/* Live Currency Exchange Widget */}
      <CurrencyExchangeWidget title="Team Currency Exchange" />

      {/* Team Alerts and Quick Actions */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 2, borderRadius: 3, boxShadow: 1 }}>
            <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
              Team Alerts Requiring Review
            </Typography>
            {loadingAlerts ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}><CircularProgress /></Box>
            ) : errorAlerts ? (
              <Typography color="error">{errorAlerts}</Typography>
            ) : (
            <List>
              {teamAlerts.map((alert) => (
                <ListItem key={alert?.id} divider>
                  <ListItemIcon>
                    <Notifications color="error" />
                  </ListItemIcon>
                  <ListItemText
                    primary={alert?.description || ''}
                    secondary={`${alert?.analyst || ''} 2 ${alert?.timestamp ? new Date(alert.timestamp).toLocaleString() : ''}`}
                  />
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Chip
                      label={alert?.type ? alert.type.replace('_', ' ') : ''}
                      color={getRiskColor(alert?.type)}
                      size="small"
                    />
                    <Chip
                      label={alert?.status ? alert.status.replace('_', ' ') : ''}
                      color={getStatusColor(alert?.status)}
                      size="small"
                    />
                  </Box>
                </ListItem>
              ))}
            </List>
            )}
            <Box sx={{ mt: 2 }}>
              <Button variant="outlined" color="primary">
                Review All Alerts
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
                startIcon={<SupervisorAccount />}
                fullWidth
                sx={{ fontWeight: 600 }}
              >
                Review Cases
              </Button>
              <Button
                variant="outlined"
                startIcon={<People />}
                fullWidth
                sx={{ fontWeight: 600 }}
              >
                Team Management
              </Button>
              <Button
                variant="outlined"
                startIcon={<Assessment />}
                fullWidth
                sx={{ fontWeight: 600 }}
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
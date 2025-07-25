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
  Search,
} from '@mui/icons-material'
import BarChartIcon from '@mui/icons-material/BarChart';
import { useSelector } from 'react-redux'
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import TooltipMUI from '@mui/material/Tooltip';
import { adminApi } from '../../services/api';
import { alertsService } from '../../services/alerts';

const AnalystDashboard = () => {
  const { user } = useSelector((state) => state.auth)
  const [stats, setStats] = useState({
    pendingAlerts: 0,
    analyzedToday: 0,
    highRiskCases: 0,
    averageResponseTime: 0,
  })
  const [pendingAlerts, setPendingAlerts] = useState([])
  const [reviewData, setReviewData] = useState([])
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
          pendingAlerts: d.pending_alerts || 0,
          analyzedToday: d.analyzed_today || 0,
          highRiskCases: d.high_risk_cases || 0,
          averageResponseTime: d.avg_response_time || 0,
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
    alertsService.getAlerts({ status: 'PENDING', size: 5 })
      .then(res => {
        const alerts = res.content || res.alerts || []
        setPendingAlerts(alerts)
        // If backend provides time-series, use it; else fallback
        if (res.reviewsByDay) {
          setReviewData(res.reviewsByDay)
        } else {
          setReviewData([
            { date: 'Mon', reviews: 5 },
            { date: 'Tue', reviews: 8 },
            { date: 'Wed', reviews: 6 },
            { date: 'Thu', reviews: 10 },
            { date: 'Fri', reviews: 7 },
            { date: 'Sat', reviews: 3 },
            { date: 'Sun', reviews: 4 },
          ])
        }
        setLoadingAlerts(false)
      })
      .catch(err => {
        setErrorAlerts('Failed to load pending alerts')
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

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>
        Analyst Dashboard
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Welcome back, {user?.name}! Here are your pending tasks.
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
                    Pending Alerts
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {typeof stats.pendingAlerts === 'number' ? stats.pendingAlerts?.toLocaleString() : '0'}
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
                    Analyzed Today
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {typeof stats.analyzedToday === 'number' ? stats.analyzedToday?.toLocaleString() : '0'}
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
                    High Risk Cases
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {typeof stats.highRiskCases === 'number' ? stats.highRiskCases?.toLocaleString() : '0'}
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
                    Avg Response (hrs)
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700 }}>
                    {typeof stats.averageResponseTime === 'number' ? stats.averageResponseTime?.toLocaleString() : '0'}
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
          Reviews Processed This Week
        </Typography>
        {loadingAlerts ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}><CircularProgress /></Box>
        ) : errorAlerts ? (
          <Typography color="error">{errorAlerts}</Typography>
        ) : (
        <ResponsiveContainer width="100%" height={220}>
          <LineChart data={reviewData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" />
            <YAxis />
            <Tooltip />
            <Line type="monotone" dataKey="reviews" stroke="#43a047" strokeWidth={3} dot={{ r: 6 }} activeDot={{ r: 8 }} />
          </LineChart>
        </ResponsiveContainer>
        )}
      </Card>

      {/* Pending Alerts and Quick Actions */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 2, borderRadius: 3, boxShadow: 1 }}>
            <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
              Pending Alerts
            </Typography>
            {loadingAlerts ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}><CircularProgress /></Box>
            ) : errorAlerts ? (
              <Typography color="error">{errorAlerts}</Typography>
            ) : (
            <List>
              {pendingAlerts.map((alert) => (
                <ListItem key={alert?.id} divider>
                  <ListItemIcon>
                    <Notifications color="error" />
                  </ListItemIcon>
                  <ListItemText
                    primary={alert?.description || ''}
                    secondary={alert?.timestamp ? new Date(alert.timestamp).toLocaleString() : ''}
                  />
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Chip
                      label={alert?.type ? alert.type.replace('_', ' ') : ''}
                      color={getRiskColor(alert?.type)}
                      size="small"
                    />
                    <Chip
                      label={alert?.priority || ''}
                      color={alert?.priority === 'HIGH' ? 'error' : 'warning'}
                      size="small"
                    />
                  </Box>
                </ListItem>
              ))}
            </List>
            )}
            <Box sx={{ mt: 2 }}>
              <Button variant="outlined" color="primary">
                View All Pending Alerts
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
                startIcon={<Search />}
                fullWidth
                sx={{ fontWeight: 600 }}
              >
                Start Analysis
              </Button>
              <Button
                variant="outlined"
                startIcon={<Assessment />}
                fullWidth
                sx={{ fontWeight: 600 }}
              >
                Create Report
              </Button>
              <Button
                variant="outlined"
                startIcon={<Security />}
                fullWidth
                sx={{ fontWeight: 600 }}
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
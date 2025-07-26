import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Box,
  Paper,
  Typography,
  Grid,
  Card,
  CardContent,
  Chip,
  Button,
  Divider,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
} from '@mui/material'
import {
  ArrowBack,
  Warning,
  Security,
  Assessment,
  Close,
  Flag,
  CheckCircle,
  Error,
} from '@mui/icons-material'
import { alertsService } from '../../services/alerts.js'
import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';
import Tooltip from '@mui/material/Tooltip';
import Avatar from '@mui/material/Avatar';
import ReportProblemIcon from '@mui/icons-material/ReportProblem';
import { canAccess, normalizeRole } from '../../utils/permissions';

const AlertDetails = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [alert, setAlert] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionDialog, setActionDialog] = useState({
    open: false,
    action: '',
    reason: '',
  })
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    fetchAlertDetails()
  }, [id])

  const fetchAlertDetails = async () => {
    setLoading(true)
    try {
      const response = await alertsService.getAlertById(id)
      setAlert(response)
    } catch (error) {
      console.error('Error fetching alert details:', error)
      setError('Failed to load alert details')
      // Set mock data for demo
      setAlert({
        id: id,
        riskLevel: 'HIGH',
        status: 'OPEN',
        description: 'Large transaction from sanctioned country',
        matchedRules: [
          { name: 'Sanction List Check', description: 'Transaction involves sanctioned entity' },
          { name: 'Amount Threshold', description: 'Amount exceeds threshold limit' },
        ],
        sanctionFlags: ['OFAC', 'UN'],
        timestamp: '2024-01-15T10:30:00Z',
        transactionId: 'TXN-001',
        transaction: {
          amount: 100000,
          currency: 'USD',
          senderName: 'John Doe',
          senderAccount: 'ACC-001',
          senderCountry: 'US',
          receiverName: 'Jane Smith',
          receiverAccount: 'ACC-002',
          receiverCountry: 'CA',
          transactionType: 'TRANSFER',
          description: 'Business payment',
        },
        analyst: 'Alice Johnson',
        notes: 'Requires immediate attention due to high risk indicators',
      })
    } finally {
      setLoading(false)
    }
  }

  const handleActionClick = (action) => {
    setActionDialog({
      open: true,
      action,
      reason: '',
    })
  }

  const handleActionSubmit = async () => {
    try {
      const { action, reason } = actionDialog
      if (action === 'dismiss') {
        await alertsService.dismissAlert(id, reason)
        setSnackbar({ open: true, message: 'Alert dismissed', severity: 'success' });
      } else if (action === 'false-positive') {
        await alertsService.tagAsFalsePositive(id, reason)
        setSnackbar({ open: true, message: 'Alert marked as false positive', severity: 'success' });
      }
      setActionDialog({ open: false, action: '', reason: '' })
      fetchAlertDetails() // Refresh the alert details
    } catch (error) {
      setSnackbar({ open: true, message: 'Failed to perform action', severity: 'error' });
    }
  }

  const getRiskColor = (risk) => {
    switch (risk) {
      case 'HIGH':
        return 'error'
      case 'MEDIUM':
        return 'warning'
      case 'LOW':
        return 'success'
      default:
        return 'default'
    }
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'OPEN':
        return 'error'
      case 'IN_REVIEW':
        return 'warning'
      case 'RESOLVED':
        return 'success'
      case 'DISMISSED':
        return 'default'
      default:
        return 'default'
    }
  }

  const getStatusIcon = (status) => {
    switch (status) {
      case 'OPEN':
        return <Error color="error" />
      case 'IN_REVIEW':
        return <Warning color="warning" />
      case 'RESOLVED':
        return <CheckCircle color="success" />
      case 'DISMISSED':
        return <Close color="default" />
      default:
        return <Warning />
    }
  }

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <Skeleton variant="rectangular" width={600} height={300} />
      </Box>
    )
  }

  if (error || !alert) {
    return (
      <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" minHeight="400px">
        <Avatar sx={{ bgcolor: 'warning.light', width: 56, height: 56, mb: 2 }}><ReportProblemIcon color="warning" /></Avatar>
        <Alert severity="error">{error || 'Alert not found'}</Alert>
      </Box>
    )
  }

  return (
    <Box>
      {/* Header */}
      <Box display="flex" alignItems="center" gap={2} mb={3}>
        <Tooltip title="Back to Alerts">
          <Button
            startIcon={<ArrowBack />}
            onClick={() => navigate('/alerts')}
            variant="outlined"
          >
            Back to Alerts
          </Button>
        </Tooltip>
        <Typography variant="h4">Alert #{alert?.id ?? ''}</Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Alert Overview */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3, mb: 3 }}>
            <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
              <Typography variant="h5" gutterBottom>
                Alert Overview
              </Typography>
              <Box display="flex" gap={1}>
                <Chip
                  icon={getStatusIcon(alert?.status)}
                  label={alert?.status ? alert.status.replace('_', ' ') : ''}
                  color={getStatusColor(alert?.status)}
                />
                <Chip
                  icon={<Warning />}
                  label={alert?.riskLevel || ''}
                  color={getRiskColor(alert?.riskLevel)}
                />
              </Box>
            </Box>

            <Typography variant="body1" paragraph>
              {alert.description}
            </Typography>

            <Typography variant="body2" color="text.secondary">
              Created: {alert?.timestamp ? new Date(alert.timestamp).toLocaleString() : ''}
            </Typography>

            {alert.analyst && (
              <Typography variant="body2" color="text.secondary">
                Assigned to: {alert?.analyst || ''}
              </Typography>
            )}
          </Paper>

          {/* Matched Rules */}
          <Paper sx={{ p: 3, mb: 3 }}>
            <Typography variant="h6" gutterBottom>
              Matched Rules
            </Typography>
            <List>
              {alert?.matchedRules?.map((rule, index) => (
                <ListItem key={index}>
                  <ListItemIcon>
                    <Security color="warning" />
                  </ListItemIcon>
                  <ListItemText
                    primary={rule?.name || ''}
                    secondary={rule?.description || ''}
                  />
                </ListItem>
              ))}
            </List>
          </Paper>

          {/* Transaction Details */}
          {alert.transaction && (
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Transaction Details
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Transaction ID
                  </Typography>
                  <Typography variant="body1">{alert.transactionId}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Amount
                  </Typography>
                  <Typography variant="body1">
                    {alert?.transaction ? new Intl.NumberFormat('en-US', {
                      style: 'currency',
                      currency: alert.transaction.currency,
                    }).format(alert.transaction.amount) : ''}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Type
                  </Typography>
                  <Typography variant="body1">{alert?.transaction?.transactionType || ''}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Description
                  </Typography>
                  <Typography variant="body1">{alert?.transaction?.description || ''}</Typography>
                </Grid>
              </Grid>

              <Divider sx={{ my: 2 }} />

              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" gutterBottom>
                    Sender Information
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Name: {alert?.transaction?.senderName || ''}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Account: {alert?.transaction?.senderAccount || ''}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Country: {alert?.transaction?.senderCountry || ''}
                  </Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" gutterBottom>
                    Receiver Information
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Name: {alert?.transaction?.receiverName || ''}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Account: {alert?.transaction?.receiverAccount || ''}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Country: {alert?.transaction?.receiverCountry || ''}
                  </Typography>
                </Grid>
              </Grid>
            </Paper>
          )}
        </Grid>

        {/* Sidebar */}
        <Grid item xs={12} md={4}>
          {/* Sanction Flags */}
          {alert?.sanctionFlags && alert.sanctionFlags.length > 0 && (
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Sanction Flags
                </Typography>
                <Box display="flex" flexWrap="wrap" gap={1}>
                  {alert.sanctionFlags.map((flag, index) => (
                    <Chip
                      key={index}
                      label={flag}
                      color="error"
                      size="small"
                    />
                  ))}
                </Box>
              </CardContent>
            </Card>
          )}

          {/* Actions */}
          {alert?.status === 'OPEN' && (
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Actions
                </Typography>
                <Box display="flex" flexDirection="column" gap={2}>
                  <Tooltip title="Dismiss Alert">
                    <Button
                      variant="outlined"
                      startIcon={<Close />}
                      onClick={() => handleActionClick('dismiss')}
                      fullWidth
                    >
                      Dismiss Alert
                    </Button>
                  </Tooltip>
                  <Tooltip title="Mark as False Positive">
                    <Button
                      variant="outlined"
                      startIcon={<Flag />}
                      onClick={() => handleActionClick('false-positive')}
                      fullWidth
                    >
                      Mark as False Positive
                    </Button>
                  </Tooltip>
                  {canAccess(normalizeRole(user?.role), 'escalate_alerts') && (
                    <Button onClick={handleEscalate} color="warning" variant="contained">Escalate</Button>
                  )}
                </Box>
              </CardContent>
            </Card>
          )}

          {/* Notes */}
          {alert?.notes && (
            <Card sx={{ mt: 3 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Notes
                </Typography>
                <Typography variant="body2">
                  {alert.notes}
                </Typography>
              </CardContent>
            </Card>
          )}
        </Grid>
      </Grid>

      {/* Action Dialog */}
      <Dialog open={actionDialog.open} onClose={() => setActionDialog({ open: false, action: '', reason: '' })}>
        <DialogTitle>
          {actionDialog.action === 'dismiss' ? 'Dismiss Alert' : 'Mark as False Positive'}
        </DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Reason"
            fullWidth
            multiline
            rows={3}
            value={actionDialog.reason}
            onChange={(e) => setActionDialog(prev => ({ ...prev, reason: e.target.value }))}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setActionDialog({ open: false, action: '', reason: '' })}>
            Cancel
          </Button>
          <Button onClick={handleActionSubmit} variant="contained">
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar open={snackbar.open} autoHideDuration={4000} onClose={() => setSnackbar({ ...snackbar, open: false })} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
        <MuiAlert elevation={6} variant="filled" onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </MuiAlert>
      </Snackbar>
    </Box>
  )
}

export default AlertDetails 
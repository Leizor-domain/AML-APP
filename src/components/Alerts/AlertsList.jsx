import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Typography,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  IconButton,
  Tooltip,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
} from '@mui/material'
import {
  Visibility,
  FilterList,
  Close,
  Flag,
  Warning,
} from '@mui/icons-material'
import { alertsService } from '../../services/alerts.js'

const AlertsList = () => {
  const navigate = useNavigate()
  const [alerts, setAlerts] = useState([])
  const [loading, setLoading] = useState(false)
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(10)
  const [totalCount, setTotalCount] = useState(0)
  const [filters, setFilters] = useState({
    riskLevel: '',
    status: '',
    dateFrom: '',
    dateTo: '',
  })
  const [actionDialog, setActionDialog] = useState({
    open: false,
    alertId: null,
    action: '',
    reason: '',
  })

  useEffect(() => {
    fetchAlerts()
  }, [page, rowsPerPage, filters])

  const fetchAlerts = async () => {
    setLoading(true)
    try {
      const params = {
        page: page + 1,
        size: rowsPerPage,
        ...filters,
      }
      const response = await alertsService.getAlerts(params)
      setAlerts(response.content || response.alerts || [])
      setTotalCount(response.totalElements || response.total || 0)
    } catch (error) {
      console.error('Error fetching alerts:', error)
      // Set mock data for demo
      setAlerts([
        {
          id: 1,
          riskLevel: 'HIGH',
          status: 'OPEN',
          description: 'Large transaction from sanctioned country',
          matchedRules: ['Sanction List Check', 'Amount Threshold'],
          sanctionFlags: ['OFAC', 'UN'],
          timestamp: '2024-01-15T10:30:00Z',
          transactionId: 'TXN-001',
        },
        {
          id: 2,
          riskLevel: 'MEDIUM',
          status: 'IN_REVIEW',
          description: 'Unusual transaction pattern detected',
          matchedRules: ['Pattern Analysis'],
          sanctionFlags: [],
          timestamp: '2024-01-15T09:15:00Z',
          transactionId: 'TXN-002',
        },
      ])
      setTotalCount(2)
    } finally {
      setLoading(false)
    }
  }

  const handleChangePage = (event, newPage) => {
    setPage(newPage)
  }

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10))
    setPage(0)
  }

  const handleFilterChange = (field, value) => {
    setFilters(prev => ({
      ...prev,
      [field]: value,
    }))
    setPage(0)
  }

  const handleActionClick = (alertId, action) => {
    setActionDialog({
      open: true,
      alertId,
      action,
      reason: '',
    })
  }

  const handleActionSubmit = async () => {
    try {
      const { alertId, action, reason } = actionDialog
      if (action === 'dismiss') {
        await alertsService.dismissAlert(alertId, reason)
      } else if (action === 'false-positive') {
        await alertsService.tagAsFalsePositive(alertId, reason)
      }
      setActionDialog({ open: false, alertId: null, action: '', reason: '' })
      fetchAlerts() // Refresh the list
    } catch (error) {
      console.error('Error performing action:', error)
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

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Alerts Management
      </Typography>

      {/* Filters */}
      <Paper sx={{ p: 2, mb: 2 }}>
        <Box display="flex" alignItems="center" gap={2} mb={2}>
          <FilterList />
          <Typography variant="h6">Filters</Typography>
        </Box>
        <Box display="flex" gap={2} flexWrap="wrap">
          <FormControl sx={{ minWidth: 120 }}>
            <InputLabel>Risk Level</InputLabel>
            <Select
              value={filters.riskLevel}
              label="Risk Level"
              onChange={(e) => handleFilterChange('riskLevel', e.target.value)}
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="HIGH">High</MenuItem>
              <MenuItem value="MEDIUM">Medium</MenuItem>
              <MenuItem value="LOW">Low</MenuItem>
            </Select>
          </FormControl>

          <FormControl sx={{ minWidth: 120 }}>
            <InputLabel>Status</InputLabel>
            <Select
              value={filters.status}
              label="Status"
              onChange={(e) => handleFilterChange('status', e.target.value)}
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="OPEN">Open</MenuItem>
              <MenuItem value="IN_REVIEW">In Review</MenuItem>
              <MenuItem value="RESOLVED">Resolved</MenuItem>
              <MenuItem value="DISMISSED">Dismissed</MenuItem>
            </Select>
          </FormControl>

          <TextField
            label="Date From"
            type="date"
            value={filters.dateFrom}
            onChange={(e) => handleFilterChange('dateFrom', e.target.value)}
            InputLabelProps={{ shrink: true }}
          />

          <TextField
            label="Date To"
            type="date"
            value={filters.dateTo}
            onChange={(e) => handleFilterChange('dateTo', e.target.value)}
            InputLabelProps={{ shrink: true }}
          />
        </Box>
      </Paper>

      {/* Table */}
      <Paper>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Risk Level</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Description</TableCell>
                <TableCell>Matched Rules</TableCell>
                <TableCell>Sanction Flags</TableCell>
                <TableCell>Date</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {alerts.map((alert) => (
                <TableRow key={alert?.id}>
                  <TableCell>{alert?.id}</TableCell>
                  <TableCell>
                    <Chip
                      label={alert?.riskLevel || ''}
                      color={getRiskColor(alert?.riskLevel)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={alert?.status ? alert.status.replace('_', ' ') : ''}
                      color={getStatusColor(alert?.status)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>{alert?.description || ''}</TableCell>
                  <TableCell>
                    {alert?.matchedRules?.map((rule, index) => (
                      <Chip
                        key={index}
                        label={rule}
                        size="small"
                        variant="outlined"
                        sx={{ mr: 0.5, mb: 0.5 }}
                      />
                    ))}
                  </TableCell>
                  <TableCell>
                    {alert?.sanctionFlags?.map((flag, index) => (
                      <Chip
                        key={index}
                        label={flag}
                        color="error"
                        size="small"
                        sx={{ mr: 0.5, mb: 0.5 }}
                      />
                    ))}
                  </TableCell>
                  <TableCell>
                    {alert?.timestamp ? new Date(alert.timestamp).toLocaleDateString() : ''}
                  </TableCell>
                  <TableCell>
                    <Box display="flex" gap={1}>
                      <Tooltip title="View Details">
                        <IconButton
                          size="small"
                          onClick={() => navigate(`/alerts/${alert?.id}`)}
                        >
                          <Visibility />
                        </IconButton>
                      </Tooltip>
                      {alert?.status === 'OPEN' && (
                        <>
                          <Tooltip title="Dismiss Alert">
                            <IconButton
                              size="small"
                              onClick={() => handleActionClick(alert?.id, 'dismiss')}
                            >
                              <Close />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Mark as False Positive">
                            <IconButton
                              size="small"
                              onClick={() => handleActionClick(alert?.id, 'false-positive')}
                            >
                              <Flag />
                            </IconButton>
                          </Tooltip>
                        </>
                      )}
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          rowsPerPageOptions={[5, 10, 25]}
          component="div"
          count={totalCount}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={handleChangePage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
      </Paper>

      {/* Action Dialog */}
      <Dialog open={actionDialog.open} onClose={() => setActionDialog({ open: false, alertId: null, action: '', reason: '' })}>
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
          <Button onClick={() => setActionDialog({ open: false, alertId: null, action: '', reason: '' })}>
            Cancel
          </Button>
          <Button onClick={handleActionSubmit} variant="contained">
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default AlertsList 
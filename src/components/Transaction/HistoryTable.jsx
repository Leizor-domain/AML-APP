import React, { useState, useEffect } from 'react'
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
} from '@mui/material'
import { Visibility, FilterList } from '@mui/icons-material'
import { transactionService } from '../../services/transaction.js'

const HistoryTable = () => {
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading] = useState(false)
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(10)
  const [totalCount, setTotalCount] = useState(0)
  const [filters, setFilters] = useState({
    status: '',
    transactionType: '',
    dateFrom: '',
    dateTo: '',
  })

  useEffect(() => {
    fetchTransactions()
  }, [page, rowsPerPage, filters])

  const fetchTransactions = async () => {
    setLoading(true)
    try {
      const params = {
        page: page + 1,
        size: rowsPerPage,
        ...filters,
      }
      const response = await transactionService.getTransactionHistory(params)
      setTransactions(response.content || response.transactions || [])
      setTotalCount(response.totalElements || response.total || 0)
    } catch (error) {
      console.error('Error fetching transactions:', error)
      // Set mock data for demo
      setTransactions([
        {
          id: 1,
          amount: 50000,
          currency: 'USD',
          senderName: 'John Doe',
          receiverName: 'Jane Smith',
          transactionType: 'TRANSFER',
          status: 'COMPLETED',
          riskScore: 'LOW',
          timestamp: '2024-01-15T10:30:00Z',
        },
        {
          id: 2,
          amount: 100000,
          currency: 'EUR',
          senderName: 'Alice Johnson',
          receiverName: 'Bob Wilson',
          transactionType: 'PAYMENT',
          status: 'FLAGGED',
          riskScore: 'HIGH',
          timestamp: '2024-01-15T09:15:00Z',
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

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED':
        return 'success'
      case 'FLAGGED':
        return 'warning'
      case 'BLOCKED':
        return 'error'
      case 'PENDING':
        return 'info'
      default:
        return 'default'
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

  const formatAmount = (amount, currency) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency,
    }).format(amount)
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Transaction History
      </Typography>

      {/* Filters */}
      <Paper sx={{ p: 2, mb: 2 }}>
        <Box display="flex" alignItems="center" gap={2} mb={2}>
          <FilterList />
          <Typography variant="h6">Filters</Typography>
        </Box>
        <Box display="flex" gap={2} flexWrap="wrap">
          <FormControl sx={{ minWidth: 120 }}>
            <InputLabel>Status</InputLabel>
            <Select
              value={filters.status}
              label="Status"
              onChange={(e) => handleFilterChange('status', e.target.value)}
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="COMPLETED">Completed</MenuItem>
              <MenuItem value="FLAGGED">Flagged</MenuItem>
              <MenuItem value="BLOCKED">Blocked</MenuItem>
              <MenuItem value="PENDING">Pending</MenuItem>
            </Select>
          </FormControl>

          <FormControl sx={{ minWidth: 150 }}>
            <InputLabel>Transaction Type</InputLabel>
            <Select
              value={filters.transactionType}
              label="Transaction Type"
              onChange={(e) => handleFilterChange('transactionType', e.target.value)}
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="TRANSFER">Transfer</MenuItem>
              <MenuItem value="PAYMENT">Payment</MenuItem>
              <MenuItem value="DEPOSIT">Deposit</MenuItem>
              <MenuItem value="WITHDRAWAL">Withdrawal</MenuItem>
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
                <TableCell>Amount</TableCell>
                <TableCell>Sender</TableCell>
                <TableCell>Receiver</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Risk Score</TableCell>
                <TableCell>Date</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions.map((transaction) => (
                <TableRow key={transaction.id}>
                  <TableCell>{transaction.id}</TableCell>
                  <TableCell>
                    {formatAmount(transaction.amount, transaction.currency)}
                  </TableCell>
                  <TableCell>{transaction.senderName}</TableCell>
                  <TableCell>{transaction.receiverName}</TableCell>
                  <TableCell>{transaction.transactionType}</TableCell>
                  <TableCell>
                    <Chip
                      label={transaction.status}
                      color={getStatusColor(transaction.status)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={transaction.riskScore}
                      color={getRiskColor(transaction.riskScore)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    {new Date(transaction.timestamp).toLocaleDateString()}
                  </TableCell>
                  <TableCell>
                    <Tooltip title="View Details">
                      <IconButton size="small">
                        <Visibility />
                      </IconButton>
                    </Tooltip>
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
    </Box>
  )
}

export default HistoryTable 
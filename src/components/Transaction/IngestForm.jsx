import React, { useState } from 'react'
import {
  Box,
  TextField,
  Button,
  Typography,
  Paper,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  Card,
  CardContent,
} from '@mui/material'
import { Upload, CheckCircle, Warning } from '@mui/icons-material'
import { transactionService } from '../../services/transaction.js'
import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';
import { useSelector } from 'react-redux';
import { canAccess, normalizeRole } from '../../utils/permissions';

const IngestForm = () => {
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState('')
  const [result, setResult] = useState(null)
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  const [formData, setFormData] = useState({
    amount: '',
    currency: 'USD',
    senderName: '',
    senderAccount: '',
    senderCountry: '',
    receiverName: '',
    receiverAccount: '',
    receiverCountry: '',
    transactionType: 'TRANSFER',
    description: '',
  })

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const isFormValid = formData.amount && formData.senderName && formData.senderAccount && formData.senderCountry && formData.receiverName && formData.receiverAccount && formData.receiverCountry;

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setSuccess(false)
    setResult(null)

    try {
      const response = await transactionService.ingestTransaction(formData)
      setResult(response)
      setSuccess(true)
      setSnackbar({ open: true, message: 'Transaction processed successfully', severity: 'success' });
      // Reset form on success
      setFormData({
        amount: '',
        currency: 'USD',
        senderName: '',
        senderAccount: '',
        senderCountry: '',
        receiverName: '',
        receiverAccount: '',
        receiverCountry: '',
        transactionType: 'TRANSFER',
        description: '',
      })
    } catch (error) {
      setError(error.response?.data?.message || 'Transaction ingestion failed')
      setSnackbar({ open: true, message: error.response?.data?.message || 'Transaction ingestion failed', severity: 'error' });
    } finally {
      setLoading(false)
    }
  }

  const getResultIcon = () => {
    if (!result) return null
    if (result.status === 'FLAGGED' || result.status === 'ALERT_GENERATED') {
      return <Warning color="warning" />
    }
    return <CheckCircle color="success" />
  }

  const getResultColor = () => {
    if (!result) return 'info'
    if (result.status === 'FLAGGED' || result.status === 'ALERT_GENERATED') {
      return 'warning'
    }
    return 'success'
  }

  // Batch file upload state
  const [batchFile, setBatchFile] = useState(null);
  const [batchLoading, setBatchLoading] = useState(false);
  const [batchResult, setBatchResult] = useState(null);
  const [batchError, setBatchError] = useState('');

  const handleBatchFileChange = (e) => {
    setBatchFile(e.target.files[0]);
    setBatchResult(null);
    setBatchError('');
  };

  const handleBatchUpload = async (e) => {
    e.preventDefault();
    if (!batchFile) return;
    setBatchLoading(true);
    setBatchResult(null);
    setBatchError('');
    try {
      const resp = await transactionService.batchIngest(batchFile);
      setBatchResult(resp);
      setSnackbar({ open: true, message: 'Batch ingestion complete', severity: 'success' });
    } catch (err) {
      setBatchError(err.response?.data?.error || 'Batch ingestion failed');
      setSnackbar({ open: true, message: err.response?.data?.error || 'Batch ingestion failed', severity: 'error' });
    } finally {
      setBatchLoading(false);
    }
  };

  const { user } = useSelector(state => state.auth);

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Transaction Ingestion
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Submit a new transaction for AML analysis
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Batch Ingestion (CSV or JSON)
            </Typography>
            <Box component="form" onSubmit={handleBatchUpload} sx={{ mb: 3 }}>
              <Grid container spacing={2} alignItems="center">
                <Grid item xs={12} sm={8}>
                  <Button
                    variant="outlined"
                    component="label"
                    startIcon={<Upload />}
                    fullWidth
                    disabled={batchLoading}
                  >
                    {batchFile ? batchFile.name : 'Select File'}
                    <input
                      type="file"
                      accept=".csv,.json"
                      hidden
                      onChange={handleBatchFileChange}
                    />
                  </Button>
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    startIcon={batchLoading ? <CircularProgress size={20} /> : <Upload />}
                    disabled={batchLoading || !batchFile}
                    fullWidth
                  >
                    {batchLoading ? 'Uploading...' : 'Upload & Ingest'}
                  </Button>
                </Grid>
              </Grid>
            </Box>
            {batchError && !batchLoading && (
              <Alert severity="error" sx={{ mb: 2 }}>{batchError}</Alert>
            )}
            {batchResult && !batchLoading && (
              <Alert severity="success" sx={{ mb: 2 }}>
                <Box>
                  <Typography variant="subtitle2">Batch Ingestion Summary</Typography>
                  <ul style={{ margin: 0, paddingLeft: 20 }}>
                    <li>Processed: {batchResult.processed}</li>
                    <li>Successful: {batchResult.successful}</li>
                    <li>Failed: {batchResult.failed}</li>
                    <li>Alerts Generated: {batchResult.alertsGenerated}</li>
                    {batchResult.errors && batchResult.errors.length > 0 && (
                      <li>
                        Errors:
                        <ul>
                          {batchResult.errors.map((err, idx) => (
                            <li key={idx}>{err}</li>
                          ))}
                        </ul>
                      </li>
                    )}
                  </ul>
                </Box>
              </Alert>
            )}
            <Typography variant="h6" gutterBottom>
              Transaction Details
            </Typography>
            {loading ? (
              <Skeleton variant="rectangular" width="100%" height={300} />
            ) : (
              <Box component="form" onSubmit={handleSubmit}>
                <Grid container spacing={2}>
                  {/* Amount and Currency */}
                  <Grid item xs={12} sm={6}>
                    <TextField
                      required
                      fullWidth
                      label="Amount"
                      name="amount"
                      type="number"
                      value={formData.amount}
                      onChange={handleChange}
                      disabled={loading}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <FormControl fullWidth required>
                      <InputLabel>Currency</InputLabel>
                      <Select
                        name="currency"
                        value={formData.currency}
                        label="Currency"
                        onChange={handleChange}
                        disabled={loading}
                      >
                        <MenuItem value="USD">USD</MenuItem>
                        <MenuItem value="EUR">EUR</MenuItem>
                        <MenuItem value="GBP">GBP</MenuItem>
                        <MenuItem value="JPY">JPY</MenuItem>
                        <MenuItem value="CAD">CAD</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>

                  {/* Transaction Type */}
                  <Grid item xs={12}>
                    <FormControl fullWidth required>
                      <InputLabel>Transaction Type</InputLabel>
                      <Select
                        name="transactionType"
                        value={formData.transactionType}
                        label="Transaction Type"
                        onChange={handleChange}
                        disabled={loading}
                      >
                        <MenuItem value="TRANSFER">Transfer</MenuItem>
                        <MenuItem value="PAYMENT">Payment</MenuItem>
                        <MenuItem value="DEPOSIT">Deposit</MenuItem>
                        <MenuItem value="WITHDRAWAL">Withdrawal</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>

                  {/* Sender Information */}
                  <Grid item xs={12}>
                    <Typography variant="h6" gutterBottom>
                      Sender Information
                    </Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      required
                      fullWidth
                      label="Sender Name"
                      name="senderName"
                      value={formData.senderName}
                      onChange={handleChange}
                      disabled={loading}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      required
                      fullWidth
                      label="Sender Account"
                      name="senderAccount"
                      value={formData.senderAccount}
                      onChange={handleChange}
                      disabled={loading}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <TextField
                      required
                      fullWidth
                      label="Sender Country"
                      name="senderCountry"
                      value={formData.senderCountry}
                      onChange={handleChange}
                      disabled={loading}
                    />
                  </Grid>

                  {/* Receiver Information */}
                  <Grid item xs={12}>
                    <Typography variant="h6" gutterBottom>
                      Receiver Information
                    </Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      required
                      fullWidth
                      label="Receiver Name"
                      name="receiverName"
                      value={formData.receiverName}
                      onChange={handleChange}
                      disabled={loading}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      required
                      fullWidth
                      label="Receiver Account"
                      name="receiverAccount"
                      value={formData.receiverAccount}
                      onChange={handleChange}
                      disabled={loading}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <TextField
                      required
                      fullWidth
                      label="Receiver Country"
                      name="receiverCountry"
                      value={formData.receiverCountry}
                      onChange={handleChange}
                      disabled={loading}
                    />
                  </Grid>

                  {/* Description */}
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Description"
                      name="description"
                      multiline
                      rows={3}
                      value={formData.description}
                      onChange={handleChange}
                      disabled={loading}
                    />
                  </Grid>

                  {/* Submit Button */}
                  <Grid item xs={12}>
                    {canAccess(normalizeRole(user?.role), 'upload_transactions') ? (
                      <Button
                        type="submit"
                        variant="contained"
                        size="large"
                        startIcon={loading ? <CircularProgress size={20} /> : <Upload />}
                        disabled={loading || !isFormValid}
                        fullWidth
                      >
                        {loading ? 'Processing...' : 'Submit Transaction'}
                      </Button>
                    ) : (
                      <Alert severity="warning" sx={{ mb: 2 }}>
                        Only Admins and Analysts can perform this action.
                      </Alert>
                    )}
                  </Grid>
                </Grid>
              </Box>
            )}
            {error && !loading && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}
            {success && result && !loading && (
              <Alert severity={getResultColor()} sx={{ mb: 2 }}>
                <Box display="flex" alignItems="center" gap={1}>
                  {getResultIcon()}
                  <span>
                    Transaction processed successfully. Status: {result.status}
                  </span>
                </Box>
              </Alert>
            )}
          </Paper>
        </Grid>

        {/* Result Details */}
        {result && !loading && (
          <Grid item xs={12} md={4}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Processing Result
                </Typography>
                <Box sx={{ mt: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Status: {result.status}
                  </Typography>
                  {result.riskScore && (
                    <Typography variant="body2" color="text.secondary">
                      Risk Score: {result.riskScore}
                    </Typography>
                  )}
                  {result.alertId && (
                    <Typography variant="body2" color="text.secondary">
                      Alert ID: {result.alertId}
                    </Typography>
                  )}
                  {result.matchedRules && result.matchedRules.length > 0 && (
                    <Box sx={{ mt: 2 }}>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        Matched Rules:
                      </Typography>
                      {result.matchedRules.map((rule, index) => (
                        <Typography key={index} variant="body2" color="text.secondary">
                          • {rule}
                        </Typography>
                      ))}
                    </Box>
                  )}
                </Box>
              </CardContent>
            </Card>
          </Grid>
        )}
      </Grid>
      <Snackbar open={snackbar.open} autoHideDuration={4000} onClose={() => setSnackbar({ ...snackbar, open: false })} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
        <MuiAlert elevation={6} variant="filled" onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </MuiAlert>
      </Snackbar>
    </Box>
  )
}

export default IngestForm 
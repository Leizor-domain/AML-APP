import React, { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate, Link } from 'react-router-dom'
import {
  Box,
  TextField,
  Button,
  Typography,
  Paper,
  Alert,
  CircularProgress,
} from '@mui/material'
import { loginStart, loginSuccess, loginFailure, clearError } from '../../store/authSlice.js'
import { authService } from '../../services/auth.js'

const LoginForm = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { loading, error, isAuthenticated, user } = useSelector((state) => state.auth)

  const [formData, setFormData] = useState({
    username: '',
    password: '',
  })
  
  const [localError, setLocalError] = useState('')

  // Clear local error when Redux error changes
  useEffect(() => {
    if (error) {
      setLocalError(error)
    } else {
      setLocalError('')
    }
  }, [error])

  // Clear error when form data changes
  useEffect(() => {
    if (localError) {
      setLocalError('')
      dispatch(clearError())
    }
  }, [formData.username, formData.password, localError, dispatch])

  // Redirect on successful authentication
  useEffect(() => {
    if (isAuthenticated && user && user.role) {
      const role = user.role.toLowerCase().replace('role_', '')
      navigate(`/${role}/dashboard`)
    }
  }, [isAuthenticated, user, navigate])

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    // Basic validation
    if (!formData.username.trim() || !formData.password.trim()) {
      setLocalError('Please enter both username and password')
      return
    }

    dispatch(loginStart())
    setLocalError('')

    try {
      const response = await authService.login(formData)
      dispatch(loginSuccess(response))
      
      // Navigation will be handled by useEffect when isAuthenticated changes
    } catch (error) {
      const errorMessage = error.response?.data?.message || 
                          error.message || 
                          'Login failed. Please check your credentials.'
      dispatch(loginFailure(errorMessage))
    }
  }

  const displayError = localError || error

  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: 'background.default',
      }}
    >
      <Paper
        elevation={3}
        sx={{
          p: 4,
          width: '100%',
          maxWidth: 400,
        }}
      >
        <Typography variant="h4" component="h1" gutterBottom align="center">
          AML Engine Login
        </Typography>
        
        {displayError && (
          <Alert severity="error" sx={{ mb: 2 }} data-testid="login-error">
            {displayError}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="username"
            label="Username"
            name="username"
            autoComplete="username"
            autoFocus
            value={formData.username}
            onChange={handleChange}
            disabled={loading}
            inputProps={{ 'data-testid': 'username-input' }}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            autoComplete="current-password"
            value={formData.password}
            onChange={handleChange}
            disabled={loading}
            inputProps={{ 'data-testid': 'password-input' }}
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
            disabled={loading}
            data-testid="login-button"
          >
            {loading ? <CircularProgress size={24} /> : 'Sign In'}
          </Button>
          <Box sx={{ textAlign: 'center' }}>
            {/* Sign Up link removed for production security */}
          </Box>
        </Box>
      </Paper>
    </Box>
  )
}

export default LoginForm 
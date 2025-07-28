import React from 'react'
import LoginForm from '../components/Auth/LoginForm.jsx'
import { Button, Box } from '@mui/material'
import { useNavigate } from 'react-router-dom'

const LoginPage = () => {
  const navigate = useNavigate();
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 6 }}>
      <Button
        variant="outlined"
        color="primary"
        sx={{ mb: 3, fontWeight: 600, borderRadius: 2, px: 4 }}
        onClick={() => navigate('/')}
      >
        Home
      </Button>
      <LoginForm />
    </Box>
  )
}

export default LoginPage 
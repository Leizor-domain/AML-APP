import React from 'react'
import { useSelector } from 'react-redux'
import { Navigate } from 'react-router-dom'
import { CircularProgress, Box } from '@mui/material'
import jwtDecode from 'jwt-decode';

const isTokenExpired = (token) => {
  if (!token) return true;
  try {
    const decoded = jwtDecode(token);
    return decoded.exp < Date.now() / 1000;
  } catch {
    return true;
  }
};

const normalizeRole = (role) => {
  if (!role) return null;
  let r = role.toUpperCase();
  if (!r.startsWith('ROLE_')) r = 'ROLE_' + r;
  return r;
};

const ProtectedRoute = ({ children, requiredRole = null }) => {
  const { isAuthenticated, user, loading, token } = useSelector((state) => state.auth);

  if (loading) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="100vh"
      >
        <CircularProgress />
      </Box>
    );
  }

  if (!isAuthenticated || isTokenExpired(token)) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && normalizeRole(user?.role) !== normalizeRole(requiredRole)) {
    // Redirect to user's appropriate dashboard
    return <Navigate to={`/${user?.role?.toLowerCase().replace('role_', '')}/dashboard`} replace />;
  }

  return children;
};

export default ProtectedRoute 
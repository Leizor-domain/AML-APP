import React from 'react'
import { useSelector } from 'react-redux'
import { Navigate } from 'react-router-dom'
import { CircularProgress, Box } from '@mui/material'
import jwtDecode from 'jwt-decode';
import { normalizeRole } from '../../utils/permissions';

const isTokenExpired = (token) => {
  if (!token) return true;
  try {
    const decoded = jwtDecode(token);
    return decoded.exp < Date.now() / 1000;
  } catch {
    return true;
  }
};

const ProtectedRoute = ({ children, requiredRole = null }) => {
  const { isAuthenticated, user, loading, token } = useSelector((state) => state.auth);

  // Debug logging
  console.log('🛡️ ProtectedRoute - Auth state:', { isAuthenticated, user, loading });
  console.log('🛡️ ProtectedRoute - Required role:', requiredRole);
  console.log('🛡️ ProtectedRoute - User role:', user?.role);
  console.log('🛡️ ProtectedRoute - Normalized user role:', normalizeRole(user?.role));
  console.log('🛡️ ProtectedRoute - Normalized required role:', normalizeRole(requiredRole));

  if (loading) {
    console.log('🛡️ ProtectedRoute - Loading state, showing spinner');
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
    console.log('🛡️ ProtectedRoute - Not authenticated or token expired, redirecting to login');
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && normalizeRole(user?.role) !== normalizeRole(requiredRole)) {
    console.log('🛡️ ProtectedRoute - Role mismatch, redirecting to user dashboard');
    // Redirect to user's appropriate dashboard
    return <Navigate to={`/${user?.role?.toLowerCase().replace('role_', '')}/dashboard`} replace />;
  }

  console.log('🛡️ ProtectedRoute - Access granted, rendering children');
  return children;
};

export default ProtectedRoute 
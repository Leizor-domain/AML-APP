import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { Box } from '@mui/material'
import Navbar from './components/Layout/Navbar.jsx'
import Sidebar from './components/Layout/Sidebar.jsx'
import ProtectedRoute from './components/Layout/ProtectedRoute.jsx'
import LoginPage from './pages/LoginPage.jsx'
import AdminDashboard from './components/Dashboard/AdminDashboard.jsx'
import AnalystDashboard from './components/Dashboard/AnalystDashboard.jsx'
import SupervisorDashboard from './components/Dashboard/SupervisorDashboard.jsx'
import ViewerDashboard from './components/Dashboard/ViewerDashboard.jsx'
import IngestPage from './pages/IngestPage.jsx'
import AlertsPage from './pages/AlertsPage.jsx'
import AlertDetailsPage from './pages/AlertDetailsPage.jsx'
import { canAccess, normalizeRole } from './utils/permissions';

function App() {
  const { isAuthenticated, user } = useSelector((state) => state.auth)
  
  // Debug logging
  console.log('🚀 App.jsx - Auth state:', { isAuthenticated, user });
  console.log('🚀 App.jsx - User role:', user?.role);
  console.log('🚀 App.jsx - Normalized role:', normalizeRole(user?.role));

  const getDashboardComponent = (role) => {
    const normalizedRole = normalizeRole(role);
    console.log('🏠 getDashboardComponent - role:', role, 'normalized:', normalizedRole);
    
    switch (normalizedRole) {
      case 'ROLE_ADMIN':
        return <AdminDashboard />;
      case 'ROLE_ANALYST':
        return <AnalystDashboard />;
      case 'ROLE_SUPERVISOR':
        return <SupervisorDashboard />;
      case 'ROLE_VIEWER':
        return <ViewerDashboard />;
      default:
        console.log('⚠️ getDashboardComponent - defaulting to ViewerDashboard for role:', normalizedRole);
        return <ViewerDashboard />;
    }
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      {isAuthenticated && <Navbar />}
      <Box sx={{ display: 'flex', flex: 1 }}>
        {isAuthenticated && <Sidebar />}
        <Box
          component="main"
          sx={{
            flexGrow: 1,
            p: 3,
            backgroundColor: 'background.default',
            minHeight: '100vh',
          }}
        >
          <Routes>
            {/* Public Routes */}
            <Route
              path="/login"
              element={
                isAuthenticated ? (
                  <Navigate to={`/${user?.role?.toLowerCase().replace('role_', '')}/dashboard`} replace />
                ) : (
                  <LoginPage />
                )
              }
            />
            {/* /register route removed for production security */}

            {/* Protected Routes */}
            <Route
              path="/admin/dashboard"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  {getDashboardComponent('ADMIN')}
                </ProtectedRoute>
              }
            />
            <Route
              path="/analyst/dashboard"
              element={
                <ProtectedRoute requiredRole="ANALYST">
                  {getDashboardComponent('ANALYST')}
                </ProtectedRoute>
              }
            />
            <Route
              path="/supervisor/dashboard"
              element={
                <ProtectedRoute requiredRole="SUPERVISOR">
                  {getDashboardComponent('SUPERVISOR')}
                </ProtectedRoute>
              }
            />
            <Route
              path="/viewer/dashboard"
              element={
                <ProtectedRoute requiredRole="VIEWER">
                  {getDashboardComponent('VIEWER')}
                </ProtectedRoute>
              }
            />

            {/* Transaction Routes */}
            <Route
              path="/ingest"
              element={
                <ProtectedRoute requiredRole="ANALYST">
                  <IngestPage />
                </ProtectedRoute>
              }
            />

            {/* Alert Routes */}
            <Route
              path="/alerts"
              element={
                <ProtectedRoute requiredRole="VIEWER">
                  <AlertsPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/alerts/:id"
              element={
                <ProtectedRoute requiredRole="VIEWER">
                  <AlertDetailsPage />
                </ProtectedRoute>
              }
            />

            {/* Default redirect */}
            <Route
              path="/"
              element={
                isAuthenticated ? (
                  <Navigate to={`/${user?.role?.toLowerCase().replace('role_', '')}/dashboard`} replace />
                ) : (
                  <Navigate to="/login" replace />
                )
              }
            />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Box>
      </Box>
    </Box>
  )
}

export default App 
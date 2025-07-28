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
import ReportsPage from './pages/ReportsPage.jsx'
import SettingsPage from './pages/SettingsPage.jsx'
import UserManagementPage from './pages/UserManagementPage.jsx'
import HomePage from './pages/HomePage.jsx'
import { canAccess, normalizeRole } from './utils/permissions';

function App() {
  const { isAuthenticated, user } = useSelector((state) => state.auth)

  const getDashboardComponent = (role) => {
    switch (role?.toUpperCase()) {
      case 'ADMIN':
      case 'ROLE_ADMIN':
        return <AdminDashboard />;
      case 'ANALYST':
      case 'ROLE_ANALYST':
        return <AnalystDashboard />;
      case 'SUPERVISOR':
      case 'ROLE_SUPERVISOR':
        return <SupervisorDashboard />;
      case 'VIEWER':
      case 'ROLE_VIEWER':
        return <ViewerDashboard />;
      default:
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
                <ProtectedRoute requiredRole="view_dashboard">
                  {getDashboardComponent('ADMIN')}
                </ProtectedRoute>
              }
            />
            <Route
              path="/analyst/dashboard"
              element={
                <ProtectedRoute requiredRole="view_dashboard">
                  {getDashboardComponent('ANALYST')}
                </ProtectedRoute>
              }
            />
            <Route
              path="/supervisor/dashboard"
              element={
                <ProtectedRoute requiredRole="view_dashboard">
                  {getDashboardComponent('SUPERVISOR')}
                </ProtectedRoute>
              }
            />
            <Route
              path="/viewer/dashboard"
              element={
                <ProtectedRoute requiredRole="view_dashboard">
                  {getDashboardComponent('VIEWER')}
                </ProtectedRoute>
              }
            />

            {/* Transaction Routes */}
            <Route
              path="/ingest"
              element={
                <ProtectedRoute requiredRole="upload_transactions">
                  <IngestPage />
                </ProtectedRoute>
              }
            />

            {/* Alert Routes */}
            <Route
              path="/alerts"
              element={
                <ProtectedRoute requiredRole="view_alerts">
                  <AlertsPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/alerts/:id"
              element={
                <ProtectedRoute requiredRole="view_alerts">
                  <AlertDetailsPage />
                </ProtectedRoute>
              }
            />

            {/* Reports Route */}
            <Route
              path="/reports"
              element={
                <ProtectedRoute requiredRole="generate_report">
                  <ReportsPage />
                </ProtectedRoute>
              }
            />

            {/* Settings Route */}
            <Route
              path="/settings"
              element={
                <ProtectedRoute requiredRole="system_settings">
                  <SettingsPage />
                </ProtectedRoute>
              }
            />

            {/* User Management Route */}
            <Route
              path="/admin/users"
              element={
                <ProtectedRoute requiredRole="manage_users">
                  <UserManagementPage />
                </ProtectedRoute>
              }
            />

            {/* Default redirect / Home Page */}
            <Route
              path="/"
              element={
                isAuthenticated ? (
                  <Navigate to={`/${user?.role?.toLowerCase().replace('role_', '')}/dashboard`} replace />
                ) : (
                  <HomePage />
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
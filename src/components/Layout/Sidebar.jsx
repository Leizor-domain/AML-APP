import React, { useState } from 'react'
import { useSelector } from 'react-redux'
import { useNavigate, useLocation } from 'react-router-dom'
import {
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  ListItemButton,
  Divider,
  Box,
  IconButton,
  Typography,
  useTheme,
  useMediaQuery,
} from '@mui/material'
import {
  Dashboard,
  Upload,
  Notifications,
  Assessment,
  Settings,
  Security,
  Menu as MenuIcon,
} from '@mui/icons-material'
import { canAccess, normalizeRole } from '../../utils/permissions';

const drawerWidth = 240

const Sidebar = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const { user } = useSelector((state) => state.auth)
  const theme = useTheme()
  const isMobile = useMediaQuery(theme.breakpoints.down('md'))
  const [mobileOpen, setMobileOpen] = useState(false)

  const normRole = normalizeRole(user?.role);
  const menuItems = [
    canAccess(normRole, 'VIEW_DASHBOARD') && {
      text: 'Dashboard',
      icon: <Dashboard />,
      path: `/${normRole?.toLowerCase().replace('role_', '')}/dashboard`,
    },
    canAccess(normRole, 'UPLOAD_TRANSACTIONS') && {
      text: 'Transaction Ingestion',
      icon: <Upload />,
      path: '/ingest',
    },
    canAccess(normRole, 'VIEW_ALERTS') && {
      text: 'Alerts',
      icon: <Notifications />,
      path: '/alerts',
    },
    normRole === 'ROLE_ADMIN' && {
      text: 'Reports',
      icon: <Assessment />,
      path: '/reports',
    },
    normRole === 'ROLE_ADMIN' && {
      text: 'Settings',
      icon: <Settings />,
      path: '/settings',
    },
  ].filter(Boolean);

  const drawerContent = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Logo/Title */}
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', py: 3 }}>
        <Box
          sx={{
            width: 40,
            height: 40,
            borderRadius: '50%',
            background: theme.palette.primary.main,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontWeight: 700,
            fontSize: 22,
            mr: 1,
            boxShadow: 2,
          }}
        >
          AML
        </Box>
        <Typography variant="h6" sx={{ fontWeight: 700, letterSpacing: 1 }}>
          Engine
        </Typography>
      </Box>
      <Divider />
      <List sx={{ flex: 1 }}>
        {menuItems.map((item, idx) => (
          <ListItem key={item.text} disablePadding sx={{ display: 'block' }}>
            <ListItemButton
              selected={location.pathname === item.path}
              onClick={() => {
                navigate(item.path)
                if (isMobile) setMobileOpen(false)
              }}
              sx={{
                mx: 1,
                my: 0.5,
                borderRadius: 2,
                backgroundColor: location.pathname === item.path ? theme.palette.action.selected : 'transparent',
                '&:hover': {
                  backgroundColor: theme.palette.action.hover,
                },
                transition: 'background 0.2s',
              }}
            >
              <ListItemIcon sx={{ color: location.pathname === item.path ? theme.palette.primary.main : 'inherit' }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText
                primary={item.text}
                primaryTypographyProps={{ fontWeight: location.pathname === item.path ? 700 : 500 }}
              />
            </ListItemButton>
            {/* Section divider after core items */}
            {idx === 2 && <Divider sx={{ my: 1 }} />}
          </ListItem>
        ))}
      </List>
      <Box sx={{ flexGrow: 1 }} />
      {/* Optional: Add footer or version info here */}
    </Box>
  )

  return (
    <>
      {isMobile && (
        <IconButton
          color="inherit"
          aria-label="open drawer"
          edge="start"
          onClick={() => setMobileOpen(true)}
          sx={{ position: 'fixed', top: 16, left: 16, zIndex: theme.zIndex.drawer + 1 }}
        >
          <MenuIcon />
        </IconButton>
      )}
      <Drawer
        variant={isMobile ? 'temporary' : 'permanent'}
        open={isMobile ? mobileOpen : true}
        onClose={() => setMobileOpen(false)}
        ModalProps={{ keepMounted: true }}
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: drawerWidth,
            boxSizing: 'border-box',
            background: theme.palette.background.paper,
            borderRight: `1px solid ${theme.palette.divider}`,
          },
          display: { xs: 'block', md: 'block' },
        }}
      >
        {drawerContent}
      </Drawer>
    </>
  )
}

export default Sidebar 
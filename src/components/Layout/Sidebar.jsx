import React from 'react'
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
} from '@mui/material'
import {
  Dashboard,
  Upload,
  Notifications,
  Assessment,
  Settings,
  Security,
} from '@mui/icons-material'

const drawerWidth = 240

const Sidebar = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const { user } = useSelector((state) => state.auth)

  const menuItems = [
    {
      text: 'Dashboard',
      icon: <Dashboard />,
      path: `/${user?.role?.toLowerCase()}/dashboard`,
    },
    {
      text: 'Transaction Ingestion',
      icon: <Upload />,
      path: '/ingest',
    },
    {
      text: 'Alerts',
      icon: <Notifications />,
      path: '/alerts',
    },
  ]

  // Add role-specific menu items
  if (user?.role === 'ADMIN') {
    menuItems.push(
      {
        text: 'Reports',
        icon: <Assessment />,
        path: '/reports',
      },
      {
        text: 'Settings',
        icon: <Settings />,
        path: '/settings',
      }
    )
  }

  if (user?.role === 'ANALYST' || user?.role === 'SUPERVISOR') {
    menuItems.push({
      text: 'Risk Assessment',
      icon: <Security />,
      path: '/risk-assessment',
    })
  }

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: drawerWidth,
          boxSizing: 'border-box',
        },
      }}
    >
      <Box sx={{ overflow: 'auto', mt: 8 }}>
        <List>
          {menuItems.map((item) => (
            <ListItem key={item.text} disablePadding>
              <ListItemButton
                selected={location.pathname === item.path}
                onClick={() => navigate(item.path)}
              >
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.text} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
        <Divider />
      </Box>
    </Drawer>
  )
}

export default Sidebar 
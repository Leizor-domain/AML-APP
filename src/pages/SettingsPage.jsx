import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Switch,
  FormControlLabel,
  Divider,
  Alert,
  CircularProgress,
  Paper,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
} from '@mui/material';
import {
  Settings,
  Security,
  Notifications,
  Storage,
  Api,
  Save,
  Refresh,
  Warning,
  CheckCircle,
} from '@mui/icons-material';
import { adminApi } from '../services/api';

const SettingsPage = () => {
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [settings, setSettings] = useState({
    // Security Settings
    sessionTimeout: 30,
    maxLoginAttempts: 5,
    requireMFA: false,
    passwordPolicy: 'strong',
    
    // Notification Settings
    emailNotifications: true,
    alertNotifications: true,
    reportNotifications: false,
    
    // System Settings
    autoBackup: true,
    backupFrequency: 'daily',
    logRetention: 90,
    
    // API Settings
    rateLimitEnabled: true,
    rateLimitRequests: 1000,
    rateLimitWindow: 60,
  });

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await adminApi.get('/admin/settings');
      setSettings(response.data);
    } catch (err) {
      setError('Failed to load settings. Using default values.');
      // Keep default settings if API fails
    } finally {
      setLoading(false);
    }
  };

  const saveSettings = async () => {
    setSaving(true);
    setError(null);
    setSuccess(null);
    try {
      await adminApi.put('/admin/settings', settings);
      setSuccess('Settings saved successfully!');
    } catch (err) {
      setError('Failed to save settings. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleSettingChange = (key, value) => {
    setSettings(prev => ({
      ...prev,
      [key]: value
    }));
  };

  const resetToDefaults = () => {
    setSettings({
      sessionTimeout: 30,
      maxLoginAttempts: 5,
      requireMFA: false,
      passwordPolicy: 'strong',
      emailNotifications: true,
      alertNotifications: true,
      reportNotifications: false,
      autoBackup: true,
      backupFrequency: 'daily',
      logRetention: 90,
      rateLimitEnabled: true,
      rateLimitRequests: 1000,
      rateLimitWindow: 60,
    });
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>
        System Settings
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Configure system preferences and security settings
      </Typography>

      {/* Action Buttons */}
      <Box sx={{ mb: 3, display: 'flex', gap: 2 }}>
        <Button
          variant="contained"
          startIcon={<Save />}
          onClick={saveSettings}
          disabled={saving}
        >
          {saving ? <CircularProgress size={20} /> : 'Save Settings'}
        </Button>
        <Button
          variant="outlined"
          startIcon={<Refresh />}
          onClick={loadSettings}
          disabled={loading}
        >
          Refresh
        </Button>
        <Button
          variant="outlined"
          startIcon={<Warning />}
          onClick={resetToDefaults}
        >
          Reset to Defaults
        </Button>
      </Box>

      {/* Status Messages */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}
      {success && (
        <Alert severity="success" sx={{ mb: 3 }}>
          {success}
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Security Settings */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={1} mb={2}>
                <Security color="primary" />
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  Security Settings
                </Typography>
              </Box>
              
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Session Timeout (minutes)"
                    type="number"
                    value={settings.sessionTimeout}
                    onChange={(e) => handleSettingChange('sessionTimeout', parseInt(e.target.value))}
                    inputProps={{ min: 5, max: 480 }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Max Login Attempts"
                    type="number"
                    value={settings.maxLoginAttempts}
                    onChange={(e) => handleSettingChange('maxLoginAttempts', parseInt(e.target.value))}
                    inputProps={{ min: 3, max: 10 }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel>Password Policy</InputLabel>
                    <Select
                      value={settings.passwordPolicy}
                      label="Password Policy"
                      onChange={(e) => handleSettingChange('passwordPolicy', e.target.value)}
                    >
                      <MenuItem value="basic">Basic (8+ characters)</MenuItem>
                      <MenuItem value="strong">Strong (12+ chars, special chars)</MenuItem>
                      <MenuItem value="very_strong">Very Strong (16+ chars, complex)</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={settings.requireMFA}
                        onChange={(e) => handleSettingChange('requireMFA', e.target.checked)}
                      />
                    }
                    label="Require Multi-Factor Authentication"
                  />
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* Notification Settings */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={1} mb={2}>
                <Notifications color="primary" />
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  Notification Settings
                </Typography>
              </Box>
              
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={settings.emailNotifications}
                        onChange={(e) => handleSettingChange('emailNotifications', e.target.checked)}
                      />
                    }
                    label="Email Notifications"
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={settings.alertNotifications}
                        onChange={(e) => handleSettingChange('alertNotifications', e.target.checked)}
                      />
                    }
                    label="Alert Notifications"
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={settings.reportNotifications}
                        onChange={(e) => handleSettingChange('reportNotifications', e.target.checked)}
                      />
                    }
                    label="Report Notifications"
                  />
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* System Settings */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={1} mb={2}>
                <Storage color="primary" />
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  System Settings
                </Typography>
              </Box>
              
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={settings.autoBackup}
                        onChange={(e) => handleSettingChange('autoBackup', e.target.checked)}
                      />
                    }
                    label="Automatic Backup"
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel>Backup Frequency</InputLabel>
                    <Select
                      value={settings.backupFrequency}
                      label="Backup Frequency"
                      onChange={(e) => handleSettingChange('backupFrequency', e.target.value)}
                    >
                      <MenuItem value="daily">Daily</MenuItem>
                      <MenuItem value="weekly">Weekly</MenuItem>
                      <MenuItem value="monthly">Monthly</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Log Retention (days)"
                    type="number"
                    value={settings.logRetention}
                    onChange={(e) => handleSettingChange('logRetention', parseInt(e.target.value))}
                    inputProps={{ min: 30, max: 365 }}
                  />
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* API Settings */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={1} mb={2}>
                <Api color="primary" />
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  API Settings
                </Typography>
              </Box>
              
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={settings.rateLimitEnabled}
                        onChange={(e) => handleSettingChange('rateLimitEnabled', e.target.checked)}
                      />
                    }
                    label="Enable Rate Limiting"
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Rate Limit (requests)"
                    type="number"
                    value={settings.rateLimitRequests}
                    onChange={(e) => handleSettingChange('rateLimitRequests', parseInt(e.target.value))}
                    inputProps={{ min: 100, max: 10000 }}
                    disabled={!settings.rateLimitEnabled}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Rate Limit Window (seconds)"
                    type="number"
                    value={settings.rateLimitWindow}
                    onChange={(e) => handleSettingChange('rateLimitWindow', parseInt(e.target.value))}
                    inputProps={{ min: 10, max: 3600 }}
                    disabled={!settings.rateLimitEnabled}
                  />
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* System Status */}
      <Card sx={{ mt: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
            System Status
          </Typography>
          <List>
            <ListItem>
              <ListItemIcon>
                <CheckCircle color="success" />
              </ListItemIcon>
              <ListItemText
                primary="Database Connection"
                secondary="Connected and operational"
              />
            </ListItem>
            <ListItem>
              <ListItemIcon>
                <CheckCircle color="success" />
              </ListItemIcon>
              <ListItemText
                primary="API Services"
                secondary="All services running normally"
              />
            </ListItem>
            <ListItem>
              <ListItemIcon>
                <CheckCircle color="success" />
              </ListItemIcon>
              <ListItemText
                primary="Security Monitoring"
                secondary="Active and monitoring"
              />
            </ListItem>
          </List>
        </CardContent>
      </Card>
    </Box>
  );
};

export default SettingsPage; 
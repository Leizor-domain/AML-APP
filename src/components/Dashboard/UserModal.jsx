import React, { useEffect, useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  MenuItem,
  FormControlLabel,
  Switch,
  Alert,
  CircularProgress
} from '@mui/material';
import { adminApi } from '../../services/api';

const roles = [
  { value: 'ADMIN', label: 'Admin' },
  { value: 'SUPERVISOR', label: 'Supervisor' },
  { value: 'ANALYST', label: 'Analyst' },
  { value: 'VIEWER', label: 'Viewer' },
];

const UserModal = ({ open, mode, user, onClose }) => {
  const isEdit = mode === 'edit';
  const [form, setForm] = useState({
    username: '',
    password: '',
    role: 'VIEWER',
    enabled: true,
    email: '',
    name: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (isEdit && user) {
      setForm({
        username: user.username,
        password: '',
        role: user.role,
        enabled: user.enabled,
        email: user.email || '',
        name: user.name || '',
      });
    } else {
      setForm({ username: '', password: '', role: 'VIEWER', enabled: true, email: '', name: '' });
    }
    setError('');
    setSuccess('');
  }, [open, isEdit, user]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleStatusChange = (e) => {
    setForm({ ...form, enabled: e.target.checked });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');
    if (!form.username || (!isEdit && !form.password)) {
      setError('Username and password are required');
      setLoading(false);
      return;
    }
    try {
      if (isEdit) {
        await adminApi.put(`/users/${user.id}`, {
          ...form,
          password: undefined, // Don't send password unless changed
        });
      } else {
        await adminApi.post('/users/create', {
          username: form.username,
          password: form.password,
          role: form.role
        });
      }
      setSuccess('User saved successfully');
      setTimeout(() => onClose(true), 1000);
    } catch (err) {
      if (err.response?.status === 409) {
        setError('Username already exists');
      } else {
        setError(err.response?.data?.message || 'Failed to save user');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={() => onClose(false)} maxWidth="xs" fullWidth>
      <DialogTitle>{isEdit ? 'Edit User' : 'Create User'}</DialogTitle>
      <DialogContent>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
        <form onSubmit={handleSubmit} id="user-form">
          <TextField
            margin="normal"
            fullWidth
            label="Username"
            name="username"
            value={form.username}
            onChange={handleChange}
            disabled={isEdit}
            required
          />
          <TextField
            margin="normal"
            fullWidth
            label="Password"
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            required={!isEdit}
            helperText={isEdit ? 'Leave blank to keep current password' : ''}
          />
          <TextField
            margin="normal"
            fullWidth
            label="Email"
            name="email"
            value={form.email}
            onChange={handleChange}
          />
          <TextField
            margin="normal"
            fullWidth
            label="Full Name"
            name="name"
            value={form.name}
            onChange={handleChange}
          />
          <TextField
            margin="normal"
            select
            fullWidth
            label="Role"
            name="role"
            value={form.role}
            onChange={handleChange}
            required
          >
            {roles.map((option) => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </TextField>
          <FormControlLabel
            control={<Switch checked={form.enabled} onChange={handleStatusChange} color="success" />}
            label={form.enabled ? 'Active' : 'Inactive'}
          />
        </form>
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onClose(false)} disabled={loading}>Cancel</Button>
        <Button
          type="submit"
          form="user-form"
          variant="contained"
          disabled={loading}
        >
          {loading ? <CircularProgress size={20} /> : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default UserModal; 
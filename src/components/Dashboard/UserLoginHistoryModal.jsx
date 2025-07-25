import React, { useEffect, useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Typography, CircularProgress } from '@mui/material';
import { adminApi } from '../../services/api';

const UserLoginHistoryModal = ({ open, userId, onClose }) => {
  const [history, setHistory] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (open && userId) {
      setLoading(true);
      adminApi.get(`/users/${userId}/login-history`)
        .then(res => setHistory(res.data))
        .catch(() => setHistory(null))
        .finally(() => setLoading(false));
    }
  }, [open, userId]);

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>User Login History</DialogTitle>
      <DialogContent>
        {loading ? <CircularProgress size={24} /> : history ? (
          <>
            <Typography variant="body1"><b>Last Login Time:</b> {history.lastLoginAt ? new Date(history.lastLoginAt).toLocaleString() : 'Never'}</Typography>
            <Typography variant="body1"><b>Last Login IP:</b> {history.lastLoginIp || 'Unknown'}</Typography>
          </>
        ) : <Typography>No login history found.</Typography>}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Close</Button>
      </DialogActions>
    </Dialog>
  );
};

export default UserLoginHistoryModal; 
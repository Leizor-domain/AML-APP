import React, { useEffect, useState } from 'react';
import {
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  TableSortLabel,
  Paper,
  IconButton,
  Tooltip,
  Chip,
  TextField,
  InputAdornment,
  CircularProgress,
  Switch,
  Button,
  Avatar,
  Snackbar,
  Alert,
  Skeleton,
  Fab,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Typography,
  MenuItem,
  Grid
} from '@mui/material';
import { Search, Edit, Delete, History, Add, Person as PersonIcon, Security as SecurityIcon, SupervisorAccount as SupervisorIcon, Visibility as VisibilityIcon, People, Security, Search as SearchIcon } from '@mui/icons-material';
import AddIcon from '@mui/icons-material/Add';
import { adminApi } from '../../services/api';
import UserModal from './UserModal';
import UserRolePieChart from './UserRolePieChart';
import UserLoginHistoryModal from './UserLoginHistoryModal';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useCallback } from 'react';
import { deepPurple, blue, green, orange, red } from '@mui/material/colors';

const roleColors = {
  ADMIN: 'error',
  SUPERVISOR: 'warning',
  ANALYST: 'info',
  VIEWER: 'success',
};

const roleIcons = {
  ADMIN: <People fontSize="small" />,
  SUPERVISOR: <Security fontSize="small" />,
  ANALYST: <Search fontSize="small" />,
  VIEWER: <History fontSize="small" />,
};

const UserTable = () => {
  const [users, setUsers] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [sortBy, setSortBy] = useState('username');
  const [sortDir, setSortDir] = useState('asc');
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState('create');
  const [statusFilter, setStatusFilter] = useState('all');
  const [loginHistoryOpen, setLoginHistoryOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [confirmDialog, setConfirmDialog] = useState({ open: false, user: null, action: '' });
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const [searchInput, setSearchInput] = useState('');

  // Debounced search
  const debouncedSearch = useCallback(
    debounce((value) => {
      setSearch(value);
      setPage(0);
    }, 400),
    []
  );

  useEffect(() => {
    fetchUsers();
    // eslint-disable-next-line
  }, [page, rowsPerPage, sortBy, sortDir, search, statusFilter]);

  useEffect(() => {
    const handler = setTimeout(() => setSearch(searchInput), 400);
    return () => clearTimeout(handler);
  }, [searchInput]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size: rowsPerPage,
        sortBy,
        sortDir,
      };
      if (search) params.search = search;
      if (statusFilter !== 'all') params.status = statusFilter === 'active';
      const res = await adminApi.get('/users', { params });
      setUsers(res.data.content || []);
      setTotal(res.data.totalElements || 0);
    } catch (e) {
      setUsers([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSort = (property) => {
    const isAsc = sortBy === property && sortDir === 'asc';
    setSortBy(property);
    setSortDir(isAsc ? 'desc' : 'asc');
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleSearchChange = (e) => {
    debouncedSearch(e.target.value);
  };

  const handleStatusToggle = (user) => {
    setConfirmDialog({ open: true, user, action: user.enabled ? 'disable' : 'enable' });
  };

  const handleDelete = (user) => {
    setConfirmDialog({ open: true, user, action: 'disable' });
  };

  const handleConfirm = async () => {
    if (!confirmDialog.user) return;
    try {
      if (confirmDialog.action === 'disable' || confirmDialog.action === 'enable') {
        await adminApi.patch(`/users/${confirmDialog.user.id}/status`, null, { params: { enabled: confirmDialog.action === 'enable' } });
        setSnackbar({ open: true, message: `User ${confirmDialog.action === 'enable' ? 'enabled' : 'disabled'} successfully`, severity: 'success' });
      } else if (confirmDialog.action === 'delete') {
        await adminApi.delete(`/users/${confirmDialog.user.id}`);
        setSnackbar({ open: true, message: 'User disabled successfully', severity: 'success' });
      }
      fetchUsers();
    } catch (e) {
      setSnackbar({ open: true, message: 'Failed to update user', severity: 'error' });
    } finally {
      setConfirmDialog({ open: false, user: null, action: '' });
    }
  };

  const handleEdit = (user) => {
    setSelectedUser(user);
    setModalMode('edit');
    setModalOpen(true);
  };

  const handleCreate = () => {
    setSelectedUser(null);
    setModalMode('create');
    setModalOpen(true);
  };

  const handleModalClose = (refresh) => {
    setModalOpen(false);
    setSelectedUser(null);
    if (refresh) fetchUsers();
  };

  const handleExportCsv = async () => {
    try {
      const res = await adminApi.get('/users/export', { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'users.csv');
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (e) {
      alert('Failed to export users to CSV.');
      console.error(e);
    }
  };

  const handleLoginHistoryClick = (userId) => {
    setSelectedUserId(userId);
    setLoginHistoryOpen(true);
  };

  return (
    <Box>
      <Grid container spacing={2} alignItems="center" justifyContent="space-between" sx={{ mb: 2 }}>
        <Grid item xs={12} sm={6}><UserRolePieChart /></Grid>
        <Grid item xs={12} sm={6} sx={{ textAlign: { xs: 'left', sm: 'right' } }}>
          <Button variant="outlined" onClick={handleExportCsv} sx={{ height: 40, mr: 2 }}>Export CSV</Button>
          <Button variant="contained" color="primary" startIcon={<AddIcon />} onClick={handleCreate} sx={{ height: 40 }}>Create User</Button>
        </Grid>
      </Grid>
      <Paper>
        <Box sx={{ p: 2, display: 'flex', flexDirection: isMobile ? 'column' : 'row', gap: 2, alignItems: 'center' }}>
          <TextField
            placeholder="Search users..."
            variant="outlined"
            size="small"
            onChange={handleSearchChange}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Search />
                </InputAdornment>
              ),
            }}
            sx={{ width: isMobile ? '100%' : 300 }}
          />
          <TextField
            select
            label="Status"
            value={statusFilter}
            onChange={e => setStatusFilter(e.target.value)}
            size="small"
            sx={{ width: 120 }}
          >
            <MenuItem value="all">All</MenuItem>
            <MenuItem value="active">Active</MenuItem>
            <MenuItem value="disabled">Disabled</MenuItem>
          </TextField>
        </Box>
        <TableContainer>
          <Table size={isMobile ? 'small' : 'medium'}>
            <TableHead>
              <TableRow>
                <TableCell>Avatar</TableCell>
                <TableCell>
                  <TableSortLabel
                    active={sortBy === 'username'}
                    direction={sortBy === 'username' ? sortDir : 'asc'}
                    onClick={() => handleSort('username')}
                  >
                    Username
                  </TableSortLabel>
                </TableCell>
                <TableCell>
                  <TableSortLabel
                    active={sortBy === 'role'}
                    direction={sortBy === 'role' ? sortDir : 'asc'}
                    onClick={() => handleSort('role')}
                  >
                    Role
                  </TableSortLabel>
                </TableCell>
                <TableCell>
                  <TableSortLabel
                    active={sortBy === 'createdAt'}
                    direction={sortBy === 'createdAt' ? sortDir : 'asc'}
                    onClick={() => handleSort('createdAt')}
                  >
                    Created Date
                  </TableSortLabel>
                </TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                Array.from({ length: rowsPerPage }).map((_, idx) => (
                  <TableRow key={idx}>
                    <TableCell><Skeleton variant="circular" width={32} height={32} /></TableCell>
                    <TableCell><Skeleton width={80} /></TableCell>
                    <TableCell><Skeleton width={60} /></TableCell>
                    <TableCell><Skeleton width={100} /></TableCell>
                    <TableCell><Skeleton width={60} /></TableCell>
                    <TableCell><Skeleton width={120} /></TableCell>
                  </TableRow>
                ))
              ) : users.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} align="center">
                    <Box sx={{ py: 4 }}>
                      <Avatar sx={{ bgcolor: blue[100], width: 56, height: 56, mb: 2 }}><People color="primary" /></Avatar>
                      <Typography variant="h6" color="text.secondary">No users found</Typography>
                      <Typography variant="body2" color="text.secondary">Try adjusting your search or filters.</Typography>
                    </Box>
                  </TableCell>
                </TableRow>
              ) : (
                users.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>
                      <Avatar sx={{ bgcolor: deepPurple[500] }}>{user.username?.[0]?.toUpperCase() || '?'}</Avatar>
                    </TableCell>
                    <TableCell>{user.username}</TableCell>
                    <TableCell>
                      <Tooltip title={user.role} arrow>
                        <Chip
                          icon={roleIcons[user.role]}
                          label={user.role}
                          color={roleColors[user.role] || 'default'}
                          size="small"
                        />
                      </Tooltip>
                    </TableCell>
                    <TableCell>{user.createdAt ? new Date(user.createdAt).toLocaleString() : ''}</TableCell>
                    <TableCell>
                      <Switch
                        checked={user.enabled}
                        onChange={() => handleStatusToggle(user)}
                        color={user.enabled ? 'success' : 'default'}
                        inputProps={{ 'aria-label': 'Enable/Disable user' }}
                      />
                      <span style={{ marginLeft: 8 }}>{user.enabled ? 'Active' : 'Disabled'}</span>
                    </TableCell>
                    <TableCell>
                      <Tooltip title="Edit">
                        <IconButton onClick={() => handleEdit(user)}><Edit /></IconButton>
                      </Tooltip>
                      <Tooltip title="Disable">
                        <IconButton onClick={() => handleDelete(user)}><Delete /></IconButton>
                      </Tooltip>
                      <Tooltip title="Login History">
                        <IconButton onClick={() => handleLoginHistoryClick(user.id)}><History /></IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          component="div"
          count={total}
          page={page}
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
          rowsPerPageOptions={[5, 10, 25, 50]}
          labelDisplayedRows={({ from, to, count }) => `${from}-${to} of ${count}`}
        />
      </Paper>
      <UserModal
        open={modalOpen}
        mode={modalMode}
        user={selectedUser}
        onClose={handleModalClose}
      />
      <UserLoginHistoryModal open={loginHistoryOpen} userId={selectedUserId} onClose={() => setLoginHistoryOpen(false)} />
      <Snackbar open={snackbar.open} autoHideDuration={4000} onClose={() => setSnackbar({ ...snackbar, open: false })} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
        <Alert onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
      <Dialog open={confirmDialog.open} onClose={() => setConfirmDialog({ open: false, user: null, action: '' })}>
        <DialogTitle>Confirm {confirmDialog.action === 'delete' ? 'Disable' : confirmDialog.action === 'enable' ? 'Enable' : 'Disable'} User</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to {confirmDialog.action} user <b>{confirmDialog.user?.username}</b>?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmDialog({ open: false, user: null, action: '' })}>Cancel</Button>
          <Button onClick={handleConfirm} color="primary" variant="contained">Confirm</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

// Debounce helper
function debounce(func, wait) {
  let timeout;
  return function (...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => func.apply(this, args), wait);
  };
}

export default UserTable; 
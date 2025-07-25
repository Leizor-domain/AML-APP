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
  Typography
} from '@mui/material';
import { Search, Edit, Delete, History, Add, Person as PersonIcon, Security as SecurityIcon, SupervisorAccount as SupervisorIcon, Visibility as VisibilityIcon } from '@mui/icons-material';
import { adminApi } from '../../services/api';
import UserModal from './UserModal';
import UserRolePieChart from './UserRolePieChart';
import UserLoginHistoryModal from './UserLoginHistoryModal';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';

const roleColors = {
  ADMIN: 'error',
  SUPERVISOR: 'warning',
  ANALYST: 'info',
  VIEWER: 'success',
};

const roleIcons = {
  ADMIN: <SecurityIcon fontSize="small" />,
  SUPERVISOR: <SupervisorIcon fontSize="small" />,
  ANALYST: <SearchIcon fontSize="small" />,
  VIEWER: <VisibilityIcon fontSize="small" />,
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
  const [confirmDialog, setConfirmDialog] = useState({ open: false, user: null });
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const [searchInput, setSearchInput] = useState('');

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
    setSearch(e.target.value);
    setPage(0);
  };

  const handleStatusToggle = async (user) => {
    await adminApi.patch(`/users/${user.id}/status`, null, { params: { enabled: !user.enabled } });
    fetchUsers();
  };

  const handleDelete = (user) => {
    setConfirmDialog({ open: true, user });
  };

  const confirmDelete = async () => {
    try {
      await adminApi.delete(`/users/${confirmDialog.user.id}`);
      setSnackbar({ open: true, message: 'User disabled successfully', severity: 'success' });
      fetchUsers();
    } catch {
      setSnackbar({ open: true, message: 'Failed to disable user', severity: 'error' });
    } finally {
      setConfirmDialog({ open: false, user: null });
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
      <Snackbar open={snackbar.open} autoHideDuration={4000} onClose={() => setSnackbar({ ...snackbar, open: false })}>
        <Alert onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
      <Dialog open={confirmDialog.open} onClose={() => setConfirmDialog({ open: false, user: null })}>
        <DialogTitle>Disable User</DialogTitle>
        <DialogContent>Are you sure you want to disable user <b>{confirmDialog.user?.username}</b>?</DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmDialog({ open: false, user: null })}>Cancel</Button>
          <Button onClick={confirmDelete} color="error" variant="contained">Disable</Button>
        </DialogActions>
      </Dialog>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <UserRolePieChart />
        <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate} sx={{ height: 40 }}>Create User</Button>
      </Box>
      <Paper>
        <TableContainer>
          <Table>
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
              <TableRow>
                <TableCell colSpan={6}>
                  <TextField
                    fullWidth
                    placeholder="Search users..."
                    value={searchInput}
                    onChange={e => setSearchInput(e.target.value)}
                    InputProps={{
                      startAdornment: <InputAdornment position="start"><Search /></InputAdornment>,
                    }}
                    size="small"
                  />
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                Array.from({ length: rowsPerPage }).map((_, idx) => (
                  <TableRow key={idx}>
                    <TableCell><Skeleton variant="circular" width={40} height={40} /></TableCell>
                    <TableCell colSpan={5}><Skeleton height={32} /></TableCell>
                  </TableRow>
                ))
              ) : users.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} align="center">
                    <PersonIcon sx={{ fontSize: 48, color: 'grey.400', mb: 1 }} />
                    <Typography variant="body2" color="text.secondary">No users found.</Typography>
                  </TableCell>
                </TableRow>
              ) : (
                users.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>
                      <Avatar>{user.username?.[0]?.toUpperCase() || '?'}</Avatar>
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
          labelDisplayedRows={({ from, to, count }) => `${from}-${to} of ${count} users`}
        />
      </Paper>
      <UserModal
        open={modalOpen}
        mode={modalMode}
        user={selectedUser}
        onClose={handleModalClose}
        setSnackbar={setSnackbar}
      />
      <UserLoginHistoryModal open={loginHistoryOpen} userId={selectedUserId} onClose={() => setLoginHistoryOpen(false)} />
      {isMobile && (
        <Fab color="primary" aria-label="add" onClick={handleCreate} sx={{ position: 'fixed', bottom: 24, right: 24 }}>
          <AddIcon />
        </Fab>
      )}
    </Box>
  );
};

export default UserTable; 
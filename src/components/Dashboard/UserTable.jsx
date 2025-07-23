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
  Button
} from '@mui/material';
import { Search, Edit, Delete, History, Add } from '@mui/icons-material';
import axios from 'axios';
import UserModal from './UserModal';
import UserRolePieChart from './UserRolePieChart';
import UserLoginHistoryModal from './UserLoginHistoryModal';

const roleColors = {
  ADMIN: 'error',
  SUPERVISOR: 'warning',
  ANALYST: 'info',
  VIEWER: 'success',
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
      const res = await axios.get('/users', { params });
      setUsers(res.data.content || []);
      setTotal(res.data.totalElements || 0);
    } catch (e) {
      setUsers([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
    // eslint-disable-next-line
  }, [page, rowsPerPage, sortBy, sortDir, search, statusFilter]);

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
    await axios.patch(`/users/${user.id}/status`, null, { params: { enabled: !user.enabled } });
    fetchUsers();
  };

  const handleDelete = async (user) => {
    if (window.confirm(`Disable user ${user.username}?`)) {
      await axios.delete(`/users/${user.id}`);
      fetchUsers();
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
      const res = await axios.get('/users/export', { responseType: 'blob' });
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
      <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 2, mb: 2 }}>
        <UserRolePieChart />
        <Button variant="outlined" onClick={handleExportCsv} sx={{ alignSelf: 'flex-start', height: 40 }}>Export CSV</Button>
      </Box>
      <Paper>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
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
                <TableRow>
                  <TableCell colSpan={5} align="center">
                    <CircularProgress size={32} />
                  </TableCell>
                </TableRow>
              ) : users.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} align="center">
                    No users found.
                  </TableCell>
                </TableRow>
              ) : (
                users.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>{user.username}</TableCell>
                    <TableCell>
                      <Tooltip title={user.role} arrow>
                        <Chip
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
        />
      </Paper>
      <UserModal
        open={modalOpen}
        mode={modalMode}
        user={selectedUser}
        onClose={handleModalClose}
      />
      <UserLoginHistoryModal open={loginHistoryOpen} userId={selectedUserId} onClose={() => setLoginHistoryOpen(false)} />
    </Box>
  );
};

export default UserTable; 
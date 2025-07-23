import React, { useEffect, useState } from 'react';
import { Box, Typography, CircularProgress } from '@mui/material';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import axios from 'axios';

const COLORS = ['#FF6384', '#FFCE56', '#36A2EB', '#4CAF50'];
const ROLE_LABELS = ['ADMIN', 'SUPERVISOR', 'ANALYST', 'VIEWER'];

const UserRolePieChart = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios.get('/users/role-distribution', { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } })
      .then(res => {
        const result = Object.entries(res.data).map(([role, count]) => ({ name: role, value: count }));
        setData(result);
      })
      .catch(() => setData([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <CircularProgress size={24} />;
  if (!data.length) return <Typography>No data</Typography>;

  return (
    <Box sx={{ width: '100%', height: 300 }}>
      <Typography variant="subtitle1" sx={{ mb: 1, fontWeight: 600 }}>User Role Distribution</Typography>
      <ResponsiveContainer width="100%" height={250}>
        <PieChart>
          <Pie data={data} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80} label>
            {data.map((entry, idx) => (
              <Cell key={`cell-${idx}`} fill={COLORS[idx % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </Box>
  );
};

export default UserRolePieChart; 
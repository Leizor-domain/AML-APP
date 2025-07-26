import { createSlice } from '@reduxjs/toolkit'
import jwtDecode from 'jwt-decode'

// Import normalizeRole from utils instead of duplicating
const normalizeRole = (role) => {
  if (!role) return null;
  let r = role.toUpperCase();
  if (!r.startsWith('ROLE_')) r = 'ROLE_' + r;
  return r;
};

const getInitialState = () => {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const decoded = jwtDecode(token);
      const currentTime = Date.now() / 1000;
      if (decoded.exp > currentTime) {
        decoded.role = normalizeRole(decoded.role);
        return {
          isAuthenticated: true,
          token,
          user: decoded,
          loading: false,
          error: null,
        };
      }
    } catch (error) {
      localStorage.removeItem('token');
    }
  }
  return {
    isAuthenticated: false,
    token: null,
    user: null,
    loading: false,
    error: null,
  };
};

const authSlice = createSlice({
  name: 'auth',
  initialState: getInitialState(),
  reducers: {
    loginStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    loginSuccess: (state, action) => {
      state.isAuthenticated = true;
      state.token = action.payload.token;
      state.user = {
        ...action.payload.user,
        role: normalizeRole(action.payload.user.role),
      };
      state.loading = false;
      state.error = null;
      localStorage.setItem('token', action.payload.token);
    },
    loginFailure: (state, action) => {
      state.isAuthenticated = false;
      state.token = null;
      state.user = null;
      state.loading = false;
      state.error = action.payload;
      localStorage.removeItem('token');
    },
    logout: (state) => {
      state.isAuthenticated = false;
      state.token = null;
      state.user = null;
      state.loading = false;
      state.error = null;
      localStorage.removeItem('token');
    },
    clearError: (state) => {
      state.error = null;
    },
  },
});

export const { loginStart, loginSuccess, loginFailure, logout, clearError } = authSlice.actions
export default authSlice.reducer 

// Add a helper to clear token from both Redux and localStorage
export const resetToken = () => {
  localStorage.removeItem('token');
  // Optionally, dispatch logout if you have access to dispatch
}; 
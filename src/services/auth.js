import { authApi } from './api.js'

export const authService = {
  login: async (credentials) => {
    const response = await authApi.post('/users/login', credentials)
    return response.data
  },

  register: async (userData) => {
    // Registration is now admin-only. Use /users/create as an authenticated admin (if needed).
    const response = await authApi.post('/users/create', userData)
    return response.data
  },

  logout: () => {
    localStorage.removeItem('token')
  },
} 
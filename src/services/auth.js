import { authApi } from './api.js'

export const authService = {
  login: async (credentials) => {
    const response = await authApi.post('/users/login', credentials)
    return response.data
  },

  register: async (userData) => {
    const response = await authApi.post('/users/register', userData)
    return response.data
  },

  logout: () => {
    localStorage.removeItem('token')
  },
} 
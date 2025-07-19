import axios from 'axios'

// Dual server configuration
const ADMIN_API_URL = import.meta.env.VITE_ADMIN_API_URL || 'http://localhost:8080'
const PORTAL_API_URL = import.meta.env.VITE_PORTAL_API_URL || 'http://localhost:8081'

// Create separate axios instances for each server
const adminApi = axios.create({
  baseURL: ADMIN_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

const portalApi = axios.create({
  baseURL: PORTAL_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Create unauthenticated API instance for auth endpoints
const authApi = axios.create({
  baseURL: ADMIN_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add JWT token to both APIs
const addAuthToken = (config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}

adminApi.interceptors.request.use(addAuthToken, (error) => Promise.reject(error))
portalApi.interceptors.request.use(addAuthToken, (error) => Promise.reject(error))

// Response interceptor to handle token expiration
const handleAuthError = (error) => {
  if (error.response?.status === 401) {
    localStorage.removeItem('token')
    window.location.href = '/login'
  }
  return Promise.reject(error)
}

adminApi.interceptors.response.use((response) => response, handleAuthError)
portalApi.interceptors.response.use((response) => response, handleAuthError)

export { adminApi, portalApi, authApi }
export default adminApi // Default export for backward compatibility 
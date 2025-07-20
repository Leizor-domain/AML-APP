import { adminApi } from './api.js'

export const alertsService = {
  getAlerts: async (params = {}) => {
    const response = await adminApi.get('/alerts', { params })
    return response.data
  },

  getAlertById: async (id) => {
    const response = await adminApi.get(`/alerts/${id}`)
    return response.data
  },

  dismissAlert: async (id, reason) => {
    const response = await adminApi.patch(`/alerts/${id}/dismiss`, { reason })
    return response.data
  },

  tagAsFalsePositive: async (id, reason) => {
    const response = await adminApi.patch(`/alerts/${id}/false-positive`, { reason })
    return response.data
  },

  updateAlertStatus: async (id, status) => {
    const response = await adminApi.patch(`/alerts/${id}/status`, { status })
    return response.data
  },
} 
import { adminApi } from './api.js'

export const alertsService = {
  getAlerts: async (params = {}) => {
    try {
      const response = await adminApi.get('/alerts', { params })
      return response.data
    } catch (error) {
      console.error('Failed to fetch alerts:', error)
      return null
    }
  },

  getAlertById: async (id) => {
    try {
      const response = await adminApi.get(`/alerts/${id}`)
      return response.data
    } catch (error) {
      console.error('Failed to fetch alert by id:', error)
      return null
    }
  },

  dismissAlert: async (id, reason) => {
    try {
      const response = await adminApi.patch(`/alerts/${id}/dismiss`, { reason })
      return response.data
    } catch (error) {
      console.error('Failed to dismiss alert:', error)
      return null
    }
  },

  tagAsFalsePositive: async (id, reason) => {
    try {
      const response = await adminApi.patch(`/alerts/${id}/false-positive`, { reason })
      return response.data
    } catch (error) {
      console.error('Failed to tag alert as false positive:', error)
      return null
    }
  },

  updateAlertStatus: async (id, status) => {
    try {
      const response = await adminApi.patch(`/alerts/${id}/status`, { status })
      return response.data
    } catch (error) {
      console.error('Failed to update alert status:', error)
      return null
    }
  },
} 
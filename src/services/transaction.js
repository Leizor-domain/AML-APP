import { portalApi } from './api.js'

export const transactionService = {
  ingestTransaction: async (transactionData) => {
    const response = await portalApi.post('/ingest', transactionData)
    return response.data
  },

  getTransactionHistory: async (params = {}) => {
    const response = await portalApi.get('/transactions', { params })
    return response.data
  },

  getTransactionById: async (id) => {
    const response = await portalApi.get(`/transactions/${id}`)
    return response.data
  },
} 
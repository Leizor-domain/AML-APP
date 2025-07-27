import { portalApi } from './api.js'
import { adminApi } from './api.js'

export const transactionService = {
  ingestTransaction: async (transactionData) => {
    const response = await portalApi.post('/ingest', transactionData)
    return response.data
  },

  getTransactionHistory: async (params = {}) => {
    const response = await adminApi.get('/ingest/transactions', { params })
    return response.data
  },

  getTransactionById: async (id) => {
    const response = await adminApi.get(`/ingest/transactions/${id}`)
    return response.data
  },

  /**
   * Batch ingest transactions via file upload (CSV or JSON)
   * @param {File} file
   * @returns {Promise<Object>} summary response
   */
  batchIngest: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await adminApi.post('/ingest/file', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },
} 
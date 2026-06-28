import axiosClient from './axiosClient';

export const transactionApi = {
  addTransaction: async (data: any) => {
    const response = await axiosClient.post('/transactions', data);
    return response.data;
  },
  getTransactions: async (params: any) => {
    const response = await axiosClient.get('/transactions', { params });
    return response.data;
  },
  downloadTransactions: async (params: any, format: string = 'JSON') => {
    const response = await axiosClient.get('/transactions/download', {
      params: { ...params, format },
      responseType: 'blob'
    });
    return response.data;
  },
  getDashboardSummary: async (financialYear: string) => {
    const response = await axiosClient.get('/transactions/dashboard-summary', {
      params: { financialYear }
    });
    return response.data;
  },
  getAvailableFinancialYears: async () => {
    const response = await axiosClient.get('/transactions/financial-years');
    return response.data;
  }
};

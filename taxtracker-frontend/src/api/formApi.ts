import axiosClient from './axiosClient';

export const formApi = {
  saveDraft: async (data: any) => {
    const response = await axiosClient.post('/forms/90c/draft', data);
    return response.data;
  },
  saveForm: async (data: any) => {
    const response = await axiosClient.post('/forms/90c', data);
    return response.data;
  },
  getForm: async (financialYear: string) => {
    const response = await axiosClient.get('/forms/90c', { params: { financialYear } });
    return response.data;
  },
  uploadDocument: async (data: any) => {
    const response = await axiosClient.post('/uploads', data);
    return response.data;
  },
  submitForm: async (formId: number) => {
    const response = await axiosClient.post('/submissions', { formId });
    return response.data;
  }
};

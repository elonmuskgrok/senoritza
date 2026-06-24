import axiosClient from './axiosClient';

export const authApi = {
  login: async (data: any) => {
    const response = await axiosClient.post('/auth/login', data);
    return response.data;
  },
  register: async (data: any) => {
    const response = await axiosClient.post('/auth/register', data);
    return response.data;
  }
};

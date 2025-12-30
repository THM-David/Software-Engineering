import api from './api';

export const semesterzeitService = {

    getAll: async () => {
        const response = await api.get('/semesterzeit');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/semesterzeit/${id}`);
        return response.data;
    },

    create: async (semesterzeitData) => {
        const response = await api.post('/semesterzeit', semesterzeitData);
        return response.data;
    },

    update: async (id, semesterzeitData) => {
        const response = await api.put(`/semesterzeit/${id}`. semesterzeitData);
        return response.data;
    },

    delete: async (id) => {
        const response = await api.delete(`/semesterzeit/${id}`);
        return response.data;
    }
}
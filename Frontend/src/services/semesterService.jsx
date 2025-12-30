import api from './api';

export const semesterService = {

    getAll: async () => {
        const response = await api.get('/semester');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/semester/${id}`);
        return response.data;
    },

    create: async (semsterDaten) => {
        const response = await api.post('/semester', semsterDaten);
        return response.data;
    },

    update: async (id, semsterDaten) => {
        const response = await api.put(`/semester/${id}`. semsterDaten);
        return response.data;
    },

    delete: async (id) => {
        const response = await api.delete(`/semester/${id}`);
        return response.data;
    }
}
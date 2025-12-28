import api from './api';

export const betreuerService = {

    getAll: async () => {
        const response = await api.get('/betreuer');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/betreuer/${id}`);
        return response.data;
    },

    create: async (betreuerDaten) => {
        const response = await api.post('/betreuer', betreuerDaten);
        return response.data;
    },

    update: async (id, betreuer) => {
        const response = await api.put(`/betreuer/${id}`);
        return response.data;
    },

    delete: async (id) => {
        const response = await api.delete(`betreuer/${id}`);
        return response.data;
    }
}
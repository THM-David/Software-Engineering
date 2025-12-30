import api from './api';

export const zeitdatenService = {

    getAll: async () => {
        const response = await api.get('/zeitdaten');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/zeitdaten/${id}`);
        return response.data;
    },

    create: async (zeitdatenDaten) => {
        const response = await api.post('/zeitdaten', zeitdatenDaten);
        return response.data;
    },

    update: async (id, zeitdatenDaten) => {
        const response = await api.put(`/zeitdaten/${id}`, zeitdatenDaten);
        return response.data;
    },

    delet: async(id) => {
        const response = await api.delete(`/zeitdaten/${id}`);
        return response.data;
    }
}
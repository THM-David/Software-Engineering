import api from './api';

export const notenbestandteilService = {

    getAll: async () => {
        const response = await api.get('/notenbestandteil');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/notenbestandteil/${id}`);
        return response.data;
    },

    create: async (notenbestandteilDaten) => {
        const response = await api.post('/notenbestandteil', notenbestandteilDaten);
        return response.data;
    },

    update: async (id, notenbestandteilDaten) => {
        const response = await api.put(`/notenbestandteil/${id}`. notenbestandteilDaten);
        return response.data;
    },

    delete: async (id) => {
        const response = await api.delete(`/notenbestandteil/${id}`);
        return response.data;
    }
}
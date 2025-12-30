import api from './api';

export const swsBerechnungService = {

    getAll: async () => {
        const response = await api.get('/sws-berechnungen');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/sws-berechnungen/${id}`);
        return response.data;
    },

    create: async (swsBerechnungData) => {
        const response = await api.post('/sws-berechnungen', swsBerechnungData);
        return response.data;
    },

    update: async (id, swsBerechnungData) => {
        const response = await api.put(`/sws-berechnungen/${id}`. swsBerechnungData);
        return response.data;
    },

    delete: async (id) => {
        const response = await api.delete(`/sws-berechnungen/${id}`);
        return response.data;
    }
}
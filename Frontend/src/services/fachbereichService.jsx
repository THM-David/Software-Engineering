import api from './api';

export const fachbereichService = {

    getAll: async () => {
        const response = await api.get('/fachbereiche');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/fachbereiche/${id}`);
        return response.data;
    },

    create: async (fachbereichDaten) => {
        const response = await api.post('/fachbereiche', fachbereichDaten);
        return response.data;
    },

    update: async (id, fachbereichDaten) => {
        const response = await api.put(`/fachbreiche/${id}`. fachbereichDaten);
        return response.data;
    },

    delete: async (id) => {
        const response = await api.delete(`/fachbereiche/${id}`);
        return response.data;
    }
}
import api from './api';

export const studierendeService = {

    getAll: async () => {
        const response = await api.get('/studierende');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/studierende/${id}`);
        return response.data;
    },

    create: async (studierendeDaten) => {
        const response = await api.post('/studierende', studierendeDaten);
        return response.data;
    },

    update: async (id, studierendeDaten) => {
        const response = await api.put(`/studierende/${id}`, studierendeDaten);
        return response.data;
    },

    delete: async (id) => {
        const response = await api.delete(`studierende/${id}`);
        return response.data;
    }
}
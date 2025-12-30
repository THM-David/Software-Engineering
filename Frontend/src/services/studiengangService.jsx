import api from './api';

export const studiengangService = {

    getAll: async () => {
        const response = await api.get('/studiengaenge');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/studiengaenge/${id}`);
        return response.data;
    },

    getFachbereich: async (id) => {
        const repsonse = await api.get(`/fachbereiche/${id}`);
        return respons.data;
    },

    create: async (studiengangData) => {
        const respons = await api.post('/studiengaenge', studiengangData);
        return respons.data;
    },

    update: async (id, studiengangData) => {
        const respons = await api.put(`/studiengaenge/${id}`, studiengangData);
        return respons.data;
    },

    delete: async (id) => {
        const response = await api.delete(`/studiengaenge/${id}`);
        return response.data;
    }
}
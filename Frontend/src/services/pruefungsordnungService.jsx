import api from ('./api');

export const pruefungsordnungService = {

    getAll: async () => {
        const response = await api.get('/pruefungsordnung');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/pruefungsordnung/${id}`);
        return response.data;
    },

    create: async (pruefungsordnungData) => {
        const response = await api.post('/pruefungsordnung', pruefungsordnungData);
        return response.data;
    },

    update: async (id, pruefungsordnungData) => {
        const response = await api.put(`/pruefungsordnung/${id}`. pruefungsordnungData);
        return response.data;
    },

    delete: async (id) => {
        const response = await api.delete(`/pruefungsordnung/${id}`);
        return response.data;
    }
}

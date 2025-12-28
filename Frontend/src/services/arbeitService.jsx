// services/arbeitService.js
import api from './api';

export const arbeitService = {
    // Alle Arbeiten holen
    getAll: async () => {
        const response = await api.get('/wissenschaftliche-arbeiten');
        return response.data;
    },
    
    // Arbeit nach ID holen
    getById: async (id) => {
        const response = await api.get(`/wissenschaftliche-arbeiten/${id}`);
        return response.data;
    },

    // Neue Arbeit erstellen
    create: async (arbeitDaten) => {
        const response = await api.post('/wissenschaftliche-arbeiten', arbeitDaten);
        return response.data;
    },
    
    //Arbeit aktualisieren
    update: async (id, arbeit) => {
        const response = await api.post(`wissenschaftliche-arbeiten/${id}`)
        return response.data;
    }, 

    //Referenten laden um Dropdown zu füllen
    getReferenten: async () => {
        const response = await api.get('/betreuer');
        return response.data;
    },

    //Studierende laden um Dropdown zu füllen
    getStudierende: async () => {
        const response = await api.get('/studierende');
        return response.data;
    }
};
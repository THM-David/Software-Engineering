import api from "/api";

export const wissenschaftlicheArbeitService = {
  //Alle Arbeiten holen
  getAlleArbeiten: async () => {
    const response = await api.get('/arbeiten');
    return response.data;
  },

  //Neue Arbeit erstellen
  createArbeit: async (arbeitDaten) => {
    const response = await api.post('/arbeiten', arbeitDaten);
    return response.data;
  },

  //Arbeit nach ID holen
  getArbeitById: async (id) => {
    const response = await api.get('/arbeit/${id}');
    return response.data;
  }
};

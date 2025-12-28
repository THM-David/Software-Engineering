import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { arbeitService } from '../services/ArbeitService';

const ArbeitenMaske = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;

    const [studenten, setStudenten] = useState([]);
    const [referenten , setReferenten] = useState([]);
    const [formData, setFormData] = useState({
        studierendenId: '',
        studiengangId: '',
        pruefungsordnungId: '',
        semesterId: '',
        erstprueferId:'',
        zweitprueferId:'',
        titel: '',
        typ: '',
        status: '',
    });

    //Funktionen um Dropdowniteams zu laden
    useEffect(() => {
        const loadReferenten = async () => {
            try {
                const data = await arbeitService.getReferenten();
                setReferenten(data);
            } catch (error) {
                console.error("Fehler beim Laden der Referenten", error);
            }
        };
        const loadStudiernde = async () => {
            try {
                const data = await arbeitService.getStudierende();
                setStudenten(data);
            } catch (error) {
                console.error("Fehler beim Laden von Studenten", error);
            }
        };
        loadReferenten();
        loadStudiernde();
    }, []);

    const handleChange = (e) => {
        const {name, value} = e.target;

        if(name === 'zweitpruefer_id' && value === formData.erstpruefer_id && value !== "") {
            alert("Erst- und Zweitprüfer dürfen nicht identisch sein!");
            return;
        }
        if (name === 'erstprueferId' && value === formData.zweitprueferId && value !== "") {
            alert("Erst- und Zweitprüfer dürfen nicht identisch sein!");
            return;
        }

        setFormData({ ...formData, [name]: value});
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            if(isEdit) {
                await arbeitService.update(id, formData);
            } else {
                await arbeitService.create(formData);
            }
            navigate('/wissenschaftliche-arbeit');
        } catch (error) {
            console.error('Fehler:', error);
            alert('Fehler beim Speichern!');            
        }
    }

    return (
        <div className="max-w-4xl mx-auto mt-6">
            <div className="flex items-center justify-between mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-gray-800 tracking-tight">
                        {isEdit ? 'Arbeit bearbeiten' : 'Neue Arbeit anlegen'}
                    </h1>
                    <p className="text-gray-500 mt-2">Erfassen Sie Titel, Typ und Zuordnungen der Abschlussarbeit.</p>
                </div>
                <button onClick={() => navigate('/arbeiten')} className="text-gray-500 hover:text-gray-700 font-medium">Abbrechen</button>
            </div>

            <div className="bg-white shadow-lg rounded-xl overflow-hidden border border-gray-100">
                <div className="p-8">
                    <form className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        {/* Titel der Arbeit (Volle Breite) */}
                        <div className="col-span-1 md:col-span-2 space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Titel der Arbeit</label>
                            <input
                                type="text"
                                name="titel"
                                onChange={handleChange}
                                value={formData.titel}
                                required
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none"
                                placeholder="Thema der Bachelorarbeit..."
                            />
                        </div>

                        {/* Typ */}
                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Typ</label>
                            <select 
                                name="typ"
                                value={formData.typ}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none">
                                <option value="">Bitte auswählen</option>
                                <option value="Bachelorarbeit">Bachelorarbeit</option>
                                <option value="Masterarbeit">Masterarbeit</option>
                                <option value="Projektarbeit">Projektarbeit</option>
                            </select>
                        </div>

                        {/* Status */}
                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Status</label>
                            <select
                                name="status"
                                value={formData.status}
                                onChange={handleChange}
                                required 
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none">
                                <option value="">Bitte auswählen</option>
                                <option value="Angemeldet">Angemeldet</option>
                                <option value="Geplant">Geplant</option>
                                <option value="In Bearbeitung">In Bearbeitung</option>
                                <option value="Abgeschlossen">Abgeschlossen</option>
                                <option value="Abgebrochen">Abgebrochen</option>
                                <option value="Bewertet">Bewertet</option>
                            </select>
                        </div>

                        {/* Student Zuordnung */}
                        <div className="col-span-1 md:col-span-2 space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Student zuordnen</label>
                            <select 
                                name="studierendenId"
                                value={formData.studierendenId}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none">
                                <option value="">Student auswählen...</option>
                                {studenten.map(s => (
                                    <option key={s.studierendenId} value={s.studierendenId}>
                                        {s.vorname} {s.nachname}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Referent Zuordnung */}
                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Erstprüfer</label>
                            <select 
                                name="erstpruefer_id"
                                value={formData.erstprueferId}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none"
                            >
                                <option value="">Referent auswählen...</option>
                                {referenten.map(r => (
                                    <option key={r.id} value={r.id}>
                                        {r.vorname} {r.nachname}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Zweitprüfer (Optional)</label>
                            <select 
                                name="zweitpruefer_id"
                                value={formData.zweitprueferId}
                                onChange={handleChange}
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none"
                            >
                                <option value="">Referent auswählen...</option>
                                {referenten.map(r1 => (
                                    <option 
                                        key={r1.id} 
                                        value={r1.id}
                                        disabled={String(r1.betreuer_id || r1.id) === String(formData.erstpruefer_id)}
                                    >
                                        {r1.vorname} {r1.nachname}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </form>
                </div>

                <div className="px-8 py-5 bg-gray-50 border-t border-gray-100 flex justify-end space-x-4">
                    <button onClick={() => navigate('/arbeiten')} className="px-6 py-2.5 rounded-lg border border-gray-300 text-gray-700 font-medium hover:bg-white transition-all">Abbrechen</button>
                    <button 
                        type="submit"
                        onClick={handleSubmit}
                        className="px-6 py-2.5 rounded-lg bg-indigo-600 text-white font-medium hover:bg-indigo-700 shadow-md transition-all">Speichern</button>
                </div>
            </div>
        </div>
    );
};

export default ArbeitenMaske;
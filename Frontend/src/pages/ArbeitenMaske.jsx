import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';

const ArbeitenMaske = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;

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
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none"
                                placeholder="Thema der Bachelorarbeit..."
                            />
                        </div>

                        {/* Typ */}
                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Typ</label>
                            <select className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none">
                                <option>Bachelorarbeit</option>
                                <option>Masterarbeit</option>
                                <option>Projektarbeit</option>
                            </select>
                        </div>

                        {/* Status */}
                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Status</label>
                            <select className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none">
                                <option>Angemeldet</option>
                                <option>In Bearbeitung</option>
                                <option>Abgegeben</option>
                                <option>Bewertet</option>
                            </select>
                        </div>

                        {/* Student Zuordnung */}
                        <div className="col-span-1 md:col-span-2 space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Student zuordnen</label>
                            <select className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none">
                                <option>Student auswählen...</option>
                                {/* Hier später dynamisch befüllen */}
                            </select>
                        </div>

                        {/* Referent Zuordnung */}
                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Erstprüfer</label>
                            <select className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none">
                                <option>Referent auswählen...</option>
                            </select>
                        </div>

                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Zweitprüfer (Optional)</label>
                            <select className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none">
                                <option>Referent auswählen...</option>
                            </select>
                        </div>
                    </form>
                </div>

                <div className="px-8 py-5 bg-gray-50 border-t border-gray-100 flex justify-end space-x-4">
                    <button onClick={() => navigate('/arbeiten')} className="px-6 py-2.5 rounded-lg border border-gray-300 text-gray-700 font-medium hover:bg-white transition-all">Abbrechen</button>
                    <button className="px-6 py-2.5 rounded-lg bg-indigo-600 text-white font-medium hover:bg-indigo-700 shadow-md transition-all">Speichern</button>
                </div>
            </div>
        </div>
    );
};

export default ArbeitenMaske;
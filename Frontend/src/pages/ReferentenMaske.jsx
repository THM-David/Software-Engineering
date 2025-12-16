import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';

const ReferentenMaske = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;

    return (
        <div className="max-w-4xl mx-auto mt-6">
            <div className="flex items-center justify-between mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-gray-800 tracking-tight">
                        {isEdit ? 'Referent bearbeiten' : 'Neuen Referenten anlegen'}
                    </h1>
                    <p className="text-gray-500 mt-2">Verwalten Sie hier die Daten der Dozenten und Betreuer.</p>
                </div>
                <button onClick={() => navigate('/referenten')} className="text-gray-500 hover:text-gray-700 font-medium">Abbrechen</button>
            </div>

            <div className="bg-white shadow-lg rounded-xl overflow-hidden border border-gray-100">
                <div className="p-8">
                    <form className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        {/* Titel / Anrede */}
                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Titel (Optional)</label>
                            <select className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none">
                                <option value="">Kein Titel</option>
                                <option value="Prof. Dr.">Prof. Dr.</option>
                                <option value="Dr.">Dr.</option>
                            </select>
                        </div>
                        <div className="hidden md:block"></div> {/* Spacer */}

                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Vorname</label>
                            <input type="text" className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none" />
                        </div>

                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Nachname</label>
                            <input type="text" className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none" />
                        </div>

                        <div className="col-span-1 md:col-span-2 space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">E-Mail Adresse</label>
                            <input type="email" className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none" />
                        </div>

                        <div className="col-span-1 md:col-span-2 space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Fachbereich</label>
                            <input type="text" className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none" placeholder="z.B. MNI" />
                        </div>
                    </form>
                </div>

                <div className="px-8 py-5 bg-gray-50 border-t border-gray-100 flex justify-end space-x-4">
                    <button onClick={() => navigate('/referenten')} className="px-6 py-2.5 rounded-lg border border-gray-300 text-gray-700 font-medium hover:bg-white transition-all">Abbrechen</button>
                    <button className="px-6 py-2.5 rounded-lg bg-indigo-600 text-white font-medium hover:bg-indigo-700 shadow-md transition-all">Speichern</button>
                </div>
            </div>
        </div>
    );
};

export default ReferentenMaske;
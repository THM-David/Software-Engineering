import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { studierendeService } from '../services/studentService';

const StudierendenMaske = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;

    const [formData, setFormData] = useState({
        matrikelnummer: '',
        vorname: '',
        nachname: '',
        email: '',
        geburtsdatum: '',
        adresse: ''
    });

    // Handler für Input-Änderungen
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // Submit-Handler
    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            if(isEdit) {
                await studierendeService.update(id, formData);
            }else {
                await studierendeService.create(formData);
            }
            navigate('/studierender')
        } catch (error) {
            console.error('Fehler:', error);
            alert('Fehler beim Speichern!');
        };
    };

    return (
        <div className="max-w-4xl mx-auto mt-6">
            <div className="flex items-center justify-between mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-gray-800 tracking-tight">
                        {isEdit ? 'Student bearbeiten' : 'Neuen Studenten anlegen'}
                    </h1>
                    <p className="text-gray-500 mt-2">Tragen Sie die persönlichen Daten des Studierenden ein.</p>
                </div>
                <button
                    onClick={() => navigate('/studierende')}
                    className="text-gray-500 hover:text-gray-700 font-medium"
                >
                    Abbrechen
                </button>
            </div>

            <div className="bg-white shadow-lg rounded-xl overflow-hidden border border-gray-100">
                <div className="p-8">
                    <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        {/* Vorname */}
                        <div className="space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Vorname</label>
                            <input
                                type="text"
                                name="vorname"
                                value={formData.vorname}
                                onChange={handleChange}
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-shadow outline-none"
                                placeholder="Max"
                            />
                        </div>

                        {/* Nachname */}
                        <div className="space-y-2">
                            <label onSubmit={handleSubmit} className="block text-sm font-semibold text-gray-700">Nachname</label>
                            <input
                                type="text"
                                name="nachname"
                                value={formData.nachname}
                                onChange={handleChange}
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-shadow outline-none"
                                placeholder="Mustermann"
                            />
                        </div>

                        {/* Matrikelnummer */}
                        <div className="space-y-2">
                            <label onSubmit={handleSubmit} className="block text-sm font-semibold text-gray-700">Matrikelnummer</label>
                            <input
                                type="text"
                                name="matrikelnummer"
                                value={formData.matrikelnummer}
                                onChange={handleChange}
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-shadow outline-none"
                                placeholder="1234567"
                            />
                        </div>

                        {/* E-Mail */}
                        <div className="space-y-2">
                            <label onSubmit={handleSubmit} className="block text-sm font-semibold text-gray-700">E-Mail Adresse</label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-shadow outline-none"
                                placeholder="max.mustermann@mni.thm.de"
                            />
                        </div>

                        {/* Geburtsdatum */}
                        <div className="space-y-2">
                            <label onSubmit={handleSubmit} className="block text-sm font-semibold text-gray-700">Geburtsdatum</label>
                            <input
                                type="date"
                                name="geburtsdatum"
                                value={formData.geburtsdatum}
                                onChange={handleChange}
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-shadow outline-none"
                            />
                        </div>

                        {/* Adresse */}
                        <div className="space-y-2">
                            <label onSubmit={handleSubmit} className="block text-sm font-semibold text-gray-700">Adresse</label>
                            <input
                                type="text"
                                name="adresse"
                                value={formData.adresse}
                                onChange={handleChange}
                                className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-shadow outline-none"
                                placeholder="Straße, PLZ, Ort"
                            />
                        </div>

                        {/* Studiengang (Optional, falls benötigt) */}
                        <div className="col-span-1 md:col-span-2 space-y-2">
                            <label className="block text-sm font-semibold text-gray-700">Studiengang</label>
                            <select className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white outline-none">
                                <option>Bitte wählen...</option>
                                <option>Informatik (B.Sc.)</option>
                                <option>Ingenieur-Informatik (B.Sc.)</option>
                            </select>
                        </div>
                    </form>
                </div>

                <div className="px-8 py-5 bg-gray-50 border-t border-gray-100 flex justify-end items-center space-x-4">
                    <button
                        onClick={() => navigate('/studierende')}
                        className="px-6 py-2.5 rounded-lg border border-gray-300 text-gray-700 font-medium hover:bg-white hover:shadow-sm transition-all"
                    >
                        Abbrechen
                    </button>
                    <button
                        type="submit"
                        onClick={handleSubmit}
                        className="px-6 py-2.5 rounded-lg bg-indigo-600 text-white font-medium hover:bg-indigo-700 shadow-md hover:shadow-lg transition-all transform hover:-translate-y-0.5">
                        Speichern
                    </button>
                </div>
            </div>
        </div>
    );
};

export default StudierendenMaske;
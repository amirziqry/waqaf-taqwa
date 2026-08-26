import React, { useState } from 'react';
import {Save, CheckCircle2, AlertCircle } from 'lucide-react';
import api from '../../api/client';

export const AdminCampaignsPage: React.FC = () => {
  const [formData, setFormData] = useState({
    title: '',
    category: 'Masjid',
    targetAmount: '',
    location: '',
    description: '',
  });

  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setSuccess(false);
    setErrorMsg('');

    try {
      await api.post('/campaigns', {
        title: formData.title,
        category: formData.category,
        targetAmount: Number(formData.targetAmount),
        location: formData.location,
        description: formData.description,
        collectedAmount: 0,
        status: 'ACTIVE',
      });

      setSuccess(true);
      setFormData({
        title: '',
        category: 'Masjid',
        targetAmount: '',
        location: '',
        description: '',
      });
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Gagal menyimpan kempen. Sila semak semula.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto bg-white p-6 rounded-3xl border border-slate-100 shadow-sm space-y-6">
      <div>
        <h2 className="text-lg font-black text-[#0F2028]">Cipta / Kemas Kini Projek Waqaf</h2>
        <p className="text-xs text-slate-400">Projek ini akan dipaparkan secara langsung kepada pewakaf di laman eksplorasi</p>
      </div>

      {success && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-2xl flex items-center gap-3 text-xs font-bold text-emerald-800">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
          <span>Kempen berjaya disimpan dan disiarkan ke platform awam!</span>
        </div>
      )}

      {errorMsg && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-2xl flex items-center gap-3 text-xs font-bold text-red-600">
          <AlertCircle className="w-5 h-5 text-red-600 shrink-0" />
          <span>{errorMsg}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Tajuk Kempen</label>
          <input
            type="text"
            required
            value={formData.title}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            placeholder="cth. Pemasangan Panel Suria Kompleks Tahfiz"
            className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Kategori Waqaf</label>
            <select
              value={formData.category}
              onChange={(e) => setFormData({ ...formData, category: e.target.value })}
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none"
            >
              <option value="Masjid">Masjid & Surau</option>
              <option value="Pendidikan">Pendidikan & Tahfiz</option>
              <option value="Kesihatan">Kesihatan & Kebajikan</option>
              <option value="Infrastruktur">Infrastruktur Komuniti</option>
            </select>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Sasaran Dana (RM)</label>
            <input
              type="number"
              required
              min="100"
              value={formData.targetAmount}
              onChange={(e) => setFormData({ ...formData, targetAmount: e.target.value })}
              placeholder="50000"
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none"
            />
          </div>
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Lokasi Projek</label>
          <input
            type="text"
            required
            value={formData.location}
            onChange={(e) => setFormData({ ...formData, location: e.target.value })}
            placeholder="cth. Shah Alam, Selangor"
            className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none"
          />
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Penerangan & Matlamat</label>
          <textarea
            rows={4}
            required
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            placeholder="Nyatakan penerangan projek, kos terperinci dan manfaat kepada komuniti..."
            className="w-full p-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none resize-none"
          />
        </div>

        <button
          type="submit"
          disabled={saving}
          className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] disabled:bg-slate-300 text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99]"
        >
          {saving ? 'Sedang Menyimpan...' : 'Terbitkan Kempen'}
          {!saving && <Save className="w-4 h-4" />}
        </button>
      </form>
    </div>
  );
};
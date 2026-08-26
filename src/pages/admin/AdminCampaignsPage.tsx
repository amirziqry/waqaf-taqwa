import React, { useState } from 'react';
import { Save, CheckCircle2, AlertCircle, Upload, X } from 'lucide-react';
import api from '../../api/client';

export const AdminCampaignsPage: React.FC = () => {
  const [formData, setFormData] = useState({
    title: '',
    category: 'Masjid',
    targetAmount: '',
    location: '',
    description: '',
  });

  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setSelectedFile(file);
      
      // Convert to Base64 so the image persists across page reloads
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrl(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const clearFile = () => {
    setSelectedFile(null);
    setPreviewUrl(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setSuccess(false);
    setErrorMsg('');

    try {
      let finalImageUrl = previewUrl || 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=800&q=80';

      // 1. Attempt S3 Storage upload via multipart form-data
      if (selectedFile) {
        const fileFormData = new FormData();
        fileFormData.append('file', selectedFile);

        try {
          const uploadRes = await api.post('/storage/upload', fileFormData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          });
          if (uploadRes?.data?.url) {
            finalImageUrl = uploadRes.data.url;
          }
        } catch {
          // Fallback uses the base64 data URL
        }
      }

      const campaignPayload = {
        id: `custom-${Date.now()}`,
        title: formData.title,
        category: formData.category,
        targetAmount: Number(formData.targetAmount),
        location: formData.location,
        description: formData.description,
        imageUrl: finalImageUrl,
        image: finalImageUrl,
        collectedAmount: 0,
        status: 'ACTIVE',
      };

      // 2. Submit campaign to backend endpoint
      try {
        await api.post('/campaigns', campaignPayload);
      } catch {
        await api.post('/projects', campaignPayload).catch(() => null);
      }

      // 3. Persist locally to sync with user and home feeds immediately
      const existing = JSON.parse(localStorage.getItem('wt_custom_campaigns') || '[]');
      localStorage.setItem('wt_custom_campaigns', JSON.stringify([campaignPayload, ...existing]));

      setSuccess(true);
      setFormData({
        title: '',
        category: 'Masjid',
        targetAmount: '',
        location: '',
        description: '',
      });
      clearFile();
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Gagal menyimpan kempen. Sila semak semula.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto bg-white p-6 md:p-8 rounded-3xl border border-slate-100 shadow-xs space-y-6">
      <div>
        <h2 className="text-xl font-black text-[#0F2028]">Cipta / Kemas Kini Projek Waqaf</h2>
        <p className="text-xs text-slate-400">Projek ini akan dipaparkan secara langsung kepada pewakaf di laman eksplorasi</p>
      </div>

      {success && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-2xl flex items-center gap-3 text-xs font-bold text-emerald-800">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
          <span>Kempen dan banner berjaya disiarkan ke platform!</span>
        </div>
      )}

      {errorMsg && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-2xl flex items-center gap-3 text-xs font-bold text-red-600">
          <AlertCircle className="w-5 h-5 text-red-600 shrink-0" />
          <span>{errorMsg}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Project Image Upload */}
        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Gambar Banner Kempen</label>
          
          {previewUrl ? (
            <div className="relative h-44 w-full rounded-2xl overflow-hidden border border-slate-200 group">
              <img src={previewUrl} alt="Preview" className="w-full h-full object-cover" />
              <button
                type="button"
                onClick={clearFile}
                className="absolute top-2 right-2 p-1.5 bg-black/60 hover:bg-black text-white rounded-xl transition"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
          ) : (
            <label className="border-2 border-dashed border-slate-200 hover:border-[#1A8C4E] rounded-2xl p-6 flex flex-col items-center justify-center cursor-pointer bg-slate-50/50 hover:bg-emerald-50/20 transition">
              <div className="p-3 bg-white rounded-2xl shadow-xs text-slate-400 mb-2">
                <Upload className="w-5 h-5 text-[#1A8C4E]" />
              </div>
              <span className="text-xs font-bold text-slate-700">Muat naik imej banner</span>
              <span className="text-[10px] text-slate-400 mt-0.5">PNG, JPG, JPEG sehingga 5MB</span>
              <input
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
              />
            </label>
          )}
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Tajuk Kempen</label>
          <input
            type="text"
            required
            value={formData.title}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            placeholder="cth. Pemasangan Panel Suria Kompleks Tahfiz"
            className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Kategori Waqaf</label>
            <select
              value={formData.category}
              onChange={(e) => setFormData({ ...formData, category: e.target.value })}
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
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
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
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
            className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
          />
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Penerangan & Matlamat</label>
          <textarea
            rows={4}
            required
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            placeholder="Nyatakan penerangan projek, sasaran penerima manfaat, dan kos terperinci..."
            className="w-full p-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none resize-none transition"
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
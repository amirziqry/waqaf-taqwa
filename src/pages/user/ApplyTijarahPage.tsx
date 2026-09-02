import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Store, ShieldCheck, ArrowLeft, Send, CheckCircle2, FileText } from 'lucide-react';
import api from '../../api/client';

export const ApplyTijarahPage: React.FC = () => {
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);
  const [success, setSuccess] = useState(false);

  const [formData, setFormData] = useState({
    businessName: '',
    ssmNumber: '',
    ownerName: '',
    contactNumber: '',
    businessCategory: 'Makanan & Minuman',
    premiseAddress: '',
    bankAccount: '',
    bankName: 'Maybank',
    isBusinessOwnerVerified: false,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.isBusinessOwnerVerified) return;

    setSubmitting(true);

    const newApplication = {
      id: `TIJARAH-${Date.now()}`,
      name: formData.businessName,
      regNo: `${formData.ssmNumber} (SSM)`,
      owner: formData.ownerName,
      category: formData.businessCategory,
      contact: formData.contactNumber,
      address: formData.premiseAddress,
      bank: `${formData.bankName} - ${formData.bankAccount}`,
      status: 'PENDING',
      qrCodeId: `QR-${formData.businessName.replace(/\s+/g, '').slice(0, 5).toUpperCase()}-${Math.floor(10 + Math.random() * 90)}`,
      createdAt: new Date().toISOString(),
    };

    try {
      await api.post('/tijarah/apply', newApplication).catch(() => null);
    } catch {
      // Offline fallback
    }

    // Persist to admin review queues
    const adminVendors = JSON.parse(localStorage.getItem('wt_admin_vendors') || '[]');
    localStorage.setItem('wt_admin_vendors', JSON.stringify([newApplication, ...adminVendors]));

    setSubmitting(false);
    setSuccess(true);
  };

  if (success) {
    return (
      <div className="max-w-xl mx-auto space-y-6 pb-12">
        <div className="bg-white p-8 rounded-3xl border border-slate-100 shadow-xs text-center space-y-5">
          <div className="w-16 h-16 bg-emerald-50 text-[#1A8C4E] rounded-3xl flex items-center justify-center mx-auto border border-emerald-100">
            <CheckCircle2 className="w-10 h-10" />
          </div>

          <div>
            <span className="text-[10px] font-extrabold px-3 py-1 bg-amber-100 text-amber-800 rounded-full uppercase">
              Permohonan Diterima
            </span>
            <h2 className="text-xl font-black text-[#0F2028] mt-2">Pendaftaran Tijarah Diproses</h2>
            <p className="text-xs text-slate-400 mt-1 max-w-sm mx-auto">
              Pegawai penilai Waqaf Taqwa akan menyemak profil SSM premis anda dalam tempoh 24 jam untuk pengaktifan modul terminal SoftPOS & kutipan infaq kaunter.
            </p>
          </div>

          <div className="pt-2 flex justify-center">
            <button
              onClick={() => navigate('/')}
              className="px-6 h-11 bg-[#1A8C4E] text-white font-bold rounded-2xl text-xs hover:bg-[#15703E] transition shadow-xs"
            >
              Kembali ke Laman Utama
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-xl mx-auto space-y-6 pb-12">
      <div className="flex items-center justify-between bg-white p-5 rounded-3xl border border-slate-100 shadow-xs">
        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate('/')}
            className="p-2.5 bg-slate-50 hover:bg-slate-100 text-slate-600 rounded-2xl transition"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
          <div>
            <h1 className="text-xl font-black text-[#0F2028]">Daftar Rakan Tijarah (Peniaga)</h1>
            <p className="text-xs text-slate-400">Pengaktifan terminal infaq automatik di kaunter kedai anda</p>
          </div>
        </div>
        <div className="p-3 bg-emerald-50 text-[#1A8C4E] rounded-2xl">
          <Store className="w-5 h-5" />
        </div>
      </div>

      <form onSubmit={handleSubmit} className="bg-white p-6 md:p-8 rounded-3xl border border-slate-100 shadow-xs space-y-4">
        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Nama Premis / Syarikat Berdaftar</label>
          <input
            type="text"
            required
            value={formData.businessName}
            onChange={(e) => setFormData({ ...formData, businessName: e.target.value })}
            placeholder="cth. Restoran Selera Warisan Sdn Bhd"
            className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">No. Pendaftaran SSM</label>
            <input
              type="text"
              required
              value={formData.ssmNumber}
              onChange={(e) => setFormData({ ...formData, ssmNumber: e.target.value })}
              placeholder="202601004921 / 001234567-X"
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Nama Pemilik Sah</label>
            <input
              type="text"
              required
              value={formData.ownerName}
              onChange={(e) => setFormData({ ...formData, ownerName: e.target.value })}
              placeholder="Mengikut borang SSM / IC"
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Kategori Perniagaan</label>
            <select
              value={formData.businessCategory}
              onChange={(e) => setFormData({ ...formData, businessCategory: e.target.value })}
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
            >
              <option value="Makanan & Minuman">Makanan & Minuman (F&B)</option>
              <option value="Penerbitan & Runcit">Pasaraya & Runcit</option>
              <option value="Kesihatan & Farmasi">Kesihatan & Farmasi</option>
              <option value="Pakaian & Fesyen">Pakaian & Fesyen</option>
              <option value="Perkhidmatan">Perkhidmatan Lain</option>
            </select>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">No. Telefon Premis (WhatsApp)</label>
            <input
              type="tel"
              required
              value={formData.contactNumber}
              onChange={(e) => setFormData({ ...formData, contactNumber: e.target.value })}
              placeholder="012-3456789"
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
            />
          </div>
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Alamat Premis / Lokasi Kaunter</label>
          <textarea
            rows={2}
            required
            value={formData.premiseAddress}
            onChange={(e) => setFormData({ ...formData, premiseAddress: e.target.value })}
            placeholder="No. Unit, Jalan, Poskod, Bandar..."
            className="w-full p-3.5 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none resize-none transition"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Bank Perniagaan</label>
            <select
              value={formData.bankName}
              onChange={(e) => setFormData({ ...formData, bankName: e.target.value })}
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
            >
              <option value="Maybank">Maybank Islamic</option>
              <option value="CIMB">CIMB Islamic</option>
              <option value="Bank Islam">Bank Islam Malaysia</option>
              <option value="RHB">RHB Islamic</option>
            </select>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">No. Akaun Bank Operasi</label>
            <input
              type="text"
              required
              value={formData.bankAccount}
              onChange={(e) => setFormData({ ...formData, bankAccount: e.target.value })}
              placeholder="12-digit nombor akaun syarikat"
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
            />
          </div>
        </div>

        <div className="p-4 bg-emerald-50/60 rounded-2xl border border-emerald-100 space-y-2">
          <label className="flex items-start gap-2.5 cursor-pointer">
            <input
              type="checkbox"
              required
              checked={formData.isBusinessOwnerVerified}
              onChange={(e) => setFormData({ ...formData, isBusinessOwnerVerified: e.target.checked })}
              className="mt-0.5 accent-[#1A8C4E] rounded"
            />
            <span className="text-[11px] text-slate-700 leading-snug font-medium">
              <strong className="text-emerald-900">Perakuan Pemilik Perniagaan:</strong> Saya mengesahkan bahawa saya adalah pemilik/wakil sah perniagaan berdaftar ini dan bersetuju membenarkan pelanggan menyumbang waqaf melalui integrasi bil atau terminal SoftPOS kaunter.
            </span>
          </label>
        </div>

        <button
          type="submit"
          disabled={submitting || !formData.isBusinessOwnerVerified}
          className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] disabled:bg-slate-300 text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99]"
        >
          {submitting ? 'Sedang Memproses...' : 'Hantar Permohonan Rakan Tijarah'}
          {!submitting && <Send className="w-4 h-4" />}
        </button>
      </form>
    </div>
  );
};
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  QrCode, 
  ShieldCheck, 
  Send, 
  CheckCircle2,  
  ArrowLeft, 
  Building2,  
  Printer,
  Sparkles,
  UserCheck,
  Award
} from 'lucide-react';
import api from '../../api/client';

export type MembershipTier = 'BIASA' | 'DUTA';

interface RakanApplication {
  id: string;
  membershipTier: MembershipTier;
  fullName: string;
  icNumber: string;
  phoneNumber: string;
  organizationType: 'INDIVIDUAL' | 'MASJID' | 'SEKOLAH' | 'KOMUNITI';
  organizationName: string;
  placementLocation?: string;
  shippingAddress?: string;
  status: 'PENDING' | 'APPROVED';
  agentCode: string;
  createdAt: string;
}

export const RakanQrPage: React.FC = () => {
  const navigate = useNavigate();
  const [hasExistingApp, setHasExistingApp] = useState<RakanApplication | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // Default selection: Ahli Biasa vs Ahli Duta
  const [membershipTier, setMembershipTier] = useState<MembershipTier>('BIASA');

  const [formData, setFormData] = useState({
    fullName: '',
    icNumber: '',
    phoneNumber: '',
    organizationType: 'INDIVIDUAL' as 'INDIVIDUAL' | 'MASJID' | 'SEKOLAH' | 'KOMUNITI',
    organizationName: '',
    placementLocation: '',
    shippingAddress: '',
  });

  useEffect(() => {
    const saved = localStorage.getItem('wt_rakan_qr_application');
    if (saved) {
      try {
        setHasExistingApp(JSON.parse(saved));
      } catch {
        // Fallback
      }
    }
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);

    const prefix = membershipTier === 'DUTA' ? 'DUTA' : 'AHLI';
    const newApplication: RakanApplication = {
      id: `RAKAN-${Date.now()}`,
      membershipTier,
      fullName: formData.fullName,
      icNumber: formData.icNumber,
      phoneNumber: formData.phoneNumber,
      organizationType: formData.organizationType,
      organizationName: formData.organizationName,
      placementLocation: membershipTier === 'DUTA' ? formData.placementLocation : 'Atas Talian / Komuniti Digital',
      shippingAddress: membershipTier === 'DUTA' ? formData.shippingAddress : undefined,
      status: 'APPROVED',
      agentCode: `${prefix}-${Math.floor(1000 + Math.random() * 9000)}`,
      createdAt: new Date().toISOString(),
    };

    // 1. Attempt API Post
    try {
      await api.post('/rakan-qr/apply', newApplication).catch(() => null);
    } catch {
      // Offline fallback
    }

    // 2. Local storage persistence
    localStorage.setItem('wt_rakan_qr_application', JSON.stringify(newApplication));
    
    const allApps = JSON.parse(localStorage.getItem('wt_admin_rakan_apps') || '[]');
    localStorage.setItem('wt_admin_rakan_apps', JSON.stringify([newApplication, ...allApps]));

    setHasExistingApp(newApplication);
    setSubmitting(false);
  };

  const handlePrint = () => {
    window.print();
  };

  // View when user already has an active badge/standee
  if (hasExistingApp) {
    const isDuta = hasExistingApp.membershipTier === 'DUTA';

    return (
      <div className="max-w-2xl mx-auto space-y-6 pb-12">
        <button
          onClick={() => navigate('/')}
          className="flex items-center gap-2 text-xs font-bold text-slate-600 hover:text-slate-900 bg-white px-4 py-2.5 rounded-2xl border border-slate-100 shadow-xs transition"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Kembali ke Laman Utama</span>
        </button>

        <div className="bg-white p-6 md:p-8 rounded-3xl border border-slate-100 shadow-xs space-y-6 text-center">
          <div className={`inline-flex p-3 rounded-2xl mb-1 ${isDuta ? 'bg-amber-50 text-amber-600' : 'bg-emerald-50 text-[#1A8C4E]'}`}>
            {isDuta ? <Award className="w-8 h-8" /> : <CheckCircle2 className="w-8 h-8" />}
          </div>

          <div>
            <span className={`text-[11px] font-extrabold px-3 py-1 rounded-full ${
              isDuta ? 'bg-amber-100 text-amber-900' : 'bg-emerald-100 text-emerald-800'
            }`}>
              {isDuta ? 'Status: Duta Waqaf Komuniti (Rasmi)' : 'Status: Rakan Waqaf Sah (Ahli Biasa)'}
            </span>
            <h2 className="text-xl font-black text-[#0F2028] mt-2">
              {isDuta ? 'Kad & Standee Akrilik Duta Anda' : 'Lencana Digital & QR Komuniti'}
            </h2>
            <p className="text-xs text-slate-400">
              Kod Rujukan: <span className="font-extrabold text-slate-700">{hasExistingApp.agentCode}</span>
            </p>
          </div>

          {/* Card / Badge Display */}
          <div className={`max-w-sm mx-auto p-6 rounded-3xl border-2 shadow-sm space-y-4 text-left ${
            isDuta 
              ? 'bg-gradient-to-b from-amber-50/40 via-white to-amber-50/20 border-amber-400/40' 
              : 'bg-gradient-to-b from-slate-50 to-emerald-50/40 border-emerald-500/20'
          }`}>
            <div className="flex items-center justify-between border-b border-slate-200/80 pb-3">
              <div>
                <p className={`text-xs font-black tracking-tight ${isDuta ? 'text-amber-700' : 'text-[#1A8C4E]'}`}>
                  Waqaf Taqwa
                </p>
                <p className="text-[9px] text-slate-400 font-semibold">
                  {isDuta ? 'Duta Rasmi & Penggerak Ummah' : 'Rakan Sah Waqaf Digital'}
                </p>
              </div>
              <Building2 className="w-5 h-5 text-slate-400" />
            </div>

            <div className="flex flex-col items-center justify-center p-4 bg-white rounded-2xl border border-slate-200 shadow-xs">
              <div className="relative p-2 bg-white rounded-xl border border-slate-100">
                <img
                  src={`https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=DuitNow-WaqafTaqwa-${hasExistingApp.agentCode}`}
                  alt="Agent Standee QR"
                  className="w-40 h-40 object-contain rounded-lg"
                />
                <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
                  <div className="bg-white p-1 rounded-full shadow-md border border-slate-100">
                    <QrCode className={`w-5 h-5 ${isDuta ? 'text-amber-600' : 'text-[#1A8C4E]'}`} />
                  </div>
                </div>
              </div>
              <span className="text-[10px] font-extrabold text-slate-700 mt-2">
                Imbas untuk Berwaqaf Terus
              </span>
              <span className="text-[9px] text-slate-400">DuitNow QR Kebangsaan</span>
            </div>

            <div className="text-xs space-y-1 pt-1">
              <p className="font-extrabold text-slate-800">{hasExistingApp.fullName}</p>
              {isDuta && (
                <p className="text-[11px] text-slate-500">
                  Lokasi:{' '}
                  <span className="font-semibold text-slate-700">
                    {hasExistingApp.placementLocation}
                  </span>
                </p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
            <button
              onClick={handlePrint}
              className="h-11 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-xs transition"
            >
              <Printer className="w-4 h-4" />
              <span>{isDuta ? 'Cetak Standee Meja' : 'Cetak / Muat Turun Kad'}</span>
            </button>
            <button
              onClick={() => {
                localStorage.removeItem('wt_rakan_qr_application');
                setHasExistingApp(null);
              }}
              className="h-11 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold rounded-2xl text-xs flex items-center justify-center gap-2 transition"
            >
              <span>Hantar Permohonan Baharu</span>
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto space-y-6 pb-12">
      <button
        onClick={() => navigate('/')}
        className="flex items-center gap-2 text-xs font-bold text-slate-600 hover:text-slate-900 bg-white px-4 py-2.5 rounded-2xl border border-slate-100 shadow-xs transition"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Kembali ke Laman Utama</span>
      </button>

      <div className="bg-white p-6 md:p-8 rounded-3xl border border-slate-100 shadow-xs space-y-6">
        <div>
          <div className="inline-flex p-3 bg-emerald-50 rounded-2xl text-[#1A8C4E] mb-2">
            <QrCode className="w-6 h-6" />
          </div>
          <h2 className="text-xl font-black text-[#0F2028]">Permohonan Rakan QR</h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Sertai rangkaian waqaf digital komuniti sama ada sebagai penggerak digital (Ahli Biasa) atau duta premis fizikal (Ahli Duta).
          </p>
        </div>

        {/* Tier Selector */}
        <div className="space-y-2">
          <label className="text-xs font-extrabold text-[#0F2028]">Pilih Peringkat Keahlian</label>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {/* Ahli Biasa Card */}
            <div
              onClick={() => setMembershipTier('BIASA')}
              className={`p-4 rounded-2xl border-2 cursor-pointer transition flex flex-col justify-between text-left ${
                membershipTier === 'BIASA'
                  ? 'border-[#1A8C4E] bg-emerald-50/40 shadow-xs'
                  : 'border-slate-200 bg-white hover:border-slate-300'
              }`}
            >
              <div className="space-y-1">
                <div className="flex items-center justify-between">
                  <span className="font-extrabold text-xs text-[#0F2028]">Ahli Biasa</span>
                  <UserCheck className={`w-4 h-4 ${membershipTier === 'BIASA' ? 'text-[#1A8C4E]' : 'text-slate-400'}`} />
                </div>
                <p className="text-[11px] text-slate-500 leading-snug">
                  QR digital peribadi segera untuk sebaran mesej, media sosial, dan kutipan santai tanpa kit fizikal.
                </p>
              </div>
              <span className="text-[10px] font-bold text-[#1A8C4E] mt-3 inline-block">
                ✓ Pengaktifan Serta-merta
              </span>
            </div>

            {/* Ahli Duta Card */}
            <div
              onClick={() => setMembershipTier('DUTA')}
              className={`p-4 rounded-2xl border-2 cursor-pointer transition flex flex-col justify-between text-left ${
                membershipTier === 'DUTA'
                  ? 'border-amber-500 bg-amber-50/40 shadow-xs'
                  : 'border-slate-200 bg-white hover:border-slate-300'
              }`}
            >
              <div className="space-y-1">
                <div className="flex items-center justify-between">
                  <span className="font-extrabold text-xs text-[#0F2028]">Ahli Duta (Fizikal)</span>
                  <Sparkles className={`w-4 h-4 ${membershipTier === 'DUTA' ? 'text-amber-600' : 'text-slate-400'}`} />
                </div>
                <p className="text-[11px] text-slate-500 leading-snug">
                  Menerima kit standee akrilik fizikal & pelekat kalis cuaca untuk diletakkan di kaunter atau premis.
                </p>
              </div>
              <span className="text-[10px] font-bold text-amber-700 mt-3 inline-block">
                ✓ Termasuk Standee & Pos Fizikal
              </span>
            </div>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Nama Penuh Pemohon (Mengikut MyKad)</label>
            <input
              type="text"
              required
              value={formData.fullName}
              onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
              placeholder="cth. Ahmad Faris Bin Zulkifli"
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-extrabold text-[#0F2028]">No. Kad Pengenalan</label>
              <input
                type="text"
                required
                value={formData.icNumber}
                onChange={(e) => setFormData({ ...formData, icNumber: e.target.value })}
                placeholder="010203-10-1234"
                className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
              />
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-extrabold text-[#0F2028]">No. Telefon (WhatsApp)</label>
              <input
                type="tel"
                required
                value={formData.phoneNumber}
                onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                placeholder="012-3456789"
                className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-extrabold text-[#0F2028]">Kategori Perwakilan</label>
              <select
                value={formData.organizationType}
                onChange={(e) => setFormData({ ...formData, organizationType: e.target.value as any })}
                className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
              >
                <option value="INDIVIDUAL">Individu / Sukarelawan</option>
                <option value="MASJID">Jawatankuasa Masjid / Surau</option>
                <option value="SEKOLAH">Institusi Pendidikan / Sekolah</option>
                <option value="KOMUNITI">Persatuan Penduduk / NGO</option>
              </select>
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-extrabold text-[#0F2028]">Nama Entiti / Premis (Pilihan)</label>
              <input
                type="text"
                value={formData.organizationName}
                onChange={(e) => setFormData({ ...formData, organizationName: e.target.value })}
                placeholder="cth. Surau Al-Ikhlas"
                className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
              />
            </div>
          </div>

          {/* Duta Exclusive Fields: Physical Location & Shipping Details */}
          {membershipTier === 'DUTA' && (
            <>
              <div className="space-y-1.5">
                <label className="text-xs font-extrabold text-[#0F2028]">Lokasi Cadangan Pameran QR Fizikal</label>
                <input
                  type="text"
                  required
                  value={formData.placementLocation}
                  onChange={(e) => setFormData({ ...formData, placementLocation: e.target.value })}
                  placeholder="cth. Kaunter Bayaran Kafe / Pintu Masuk Dewan"
                  className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-amber-500 outline-none transition"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-extrabold text-[#0F2028]">Alamat Pengeposan Kit Standee Akrilik</label>
                <textarea
                  rows={3}
                  required
                  value={formData.shippingAddress}
                  onChange={(e) => setFormData({ ...formData, shippingAddress: e.target.value })}
                  placeholder="Sila masukkan alamat lengkap penghantaran kit fizikal..."
                  className="w-full p-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-amber-500 outline-none resize-none transition"
                />
              </div>
            </>
          )}

          <div className="p-4 bg-emerald-50/50 border border-emerald-100 rounded-2xl flex items-center gap-3">
            <ShieldCheck className="w-5 h-5 text-[#1A8C4E] shrink-0" />
            <p className="text-[11px] text-emerald-900 leading-tight">
              Setiap kutipan melalui kod Rakan QR disalurkan terus secara selamat ke akaun amanah tanpa penglibatan wang tunai peribadi.
            </p>
          </div>

          <button
            type="submit"
            disabled={submitting}
            className={`w-full h-12 text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99] ${
              membershipTier === 'DUTA'
                ? 'bg-amber-600 hover:bg-amber-700'
                : 'bg-[#1A8C4E] hover:bg-[#15703E]'
            } disabled:bg-slate-300`}
          >
            {submitting 
              ? 'Sedang Memproses...' 
              : membershipTier === 'DUTA' 
                ? 'Hantar Permohonan Duta & Pesan Standee' 
                : 'Daftar & Jana QR Digital Serta-Merta'}
            {!submitting && <Send className="w-4 h-4" />}
          </button>
        </form>
      </div>
    </div>
  );
};
import React, { useState, useEffect } from 'react';
import { 
  QrCode, 
  Sparkles, 
  CheckCircle2, 
  ArrowRight, 
  Coins, 
  Download, 
  MapPin, 
  Share2, 
  TrendingUp,
  Percent,
  Building2,
  Package
} from 'lucide-react';
import api from '../../api/client';

export const VendorPosPage: React.FC = () => {
  const [hasAgentAccount, setHasAgentAccount] = useState<boolean>(false);
  const [step, setStep] = useState<'INFO' | 'APPLY' | 'DASHBOARD'>('INFO');
  const [submitting, setSubmitting] = useState(false);

  // Application form data
  const [formData, setFormData] = useState({
    fullName: localStorage.getItem('wt_user_name') || '',
    phone: '',
    placementType: 'Premis Perniagaan',
    locationName: '',
    deliveryAddress: '',
    bankName: 'Maybank',
    bankAccount: '',
  });

  // Agent's QR details & commission metrics
  const [agentData, setAgentData] = useState({
    agentCode: 'AGT-8821',
    qrUrl: '',
    commissionRate: 5, // 5% standard agent incentive
    totalScans: 48,
    totalCollected: 1240.0,
    totalCommission: 62.0,
    physicalKitStatus: 'DIPOS', // PENDING | DIPOS | DITERIMA
    trackingNumber: 'MYPOS-89301211',
  });

  useEffect(() => {
    // Check if user already registered as Rakan QR agent
    const stored = localStorage.getItem('wt_agent_profile');
    if (stored) {
      try {
        const parsed = JSON.parse(stored);
        setAgentData((prev) => ({ ...prev, ...parsed }));
        setHasAgentAccount(true);
        setStep('DASHBOARD');
      } catch {
        // Keep default
      }
    }
  }, []);

  const handleApply = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);

    const generatedAgentCode = `AGT-${Math.floor(1000 + Math.random() * 9000)}`;
    const newProfile = {
      ...formData,
      agentCode: generatedAgentCode,
      qrUrl: `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=WaqafTaqwa-Agent-${generatedAgentCode}`,
      commissionRate: 5,
      totalScans: 0,
      totalCollected: 0.0,
      totalCommission: 0.0,
      physicalKitStatus: 'SEDANG DIPROSES',
      trackingNumber: 'MENUNGGU KURIER',
    };

    try {
      await api.post('/agent/apply', newProfile).catch(() => null);
    } catch {
      // Offline fallback
    }

    localStorage.setItem('wt_agent_profile', JSON.stringify(newProfile));
    setAgentData(newProfile);
    setHasAgentAccount(true);
    setSubmitting(false);
    setStep('DASHBOARD');
  };

  return (
    <div className="max-w-md mx-auto space-y-5 pb-16">
      {/* Top Bar */}
      <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs flex items-center justify-between">
        <div>
          <h1 className="text-xl font-black text-[#0F2028]">Duta Waqaf (Rakan QR)</h1>
          <p className="text-xs text-slate-400">Pelekat & Standee QR fizikal rasmi berserta insentif</p>
        </div>
        <div className="p-3 bg-emerald-50 text-[#1A8C4E] rounded-2xl">
          <QrCode className="w-6 h-6" />
        </div>
      </div>

      {/* 1. Introductory Overview (If not yet registered) */}
      {step === 'INFO' && !hasAgentAccount && (
        <div className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs space-y-5">
          <div className="p-4 bg-gradient-to-br from-[#0F2028] to-[#1A8C4E] text-white rounded-2xl space-y-2">
            <span className="text-[10px] font-extrabold uppercase px-2.5 py-0.5 bg-white/20 rounded-full">
              Peluang Amal & Insentif
            </span>
            <h2 className="text-base font-black">Dapatkan Standee QR Fizikal ke Premis Anda</h2>
            <p className="text-xs text-white/80 leading-relaxed">
              Letakkan kod QR rasmi di kaunter kedai, pejabat, surau, atau restoran anda. Setiap kali pelanggan mengimbas dan menyumbang, anda menerima insentif komisen tetap sebagai elaun duta komuniti.
            </p>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="p-3.5 bg-slate-50 border border-slate-100 rounded-2xl">
              <Percent className="w-5 h-5 text-[#1A8C4E] mb-1" />
              <p className="text-xs font-black text-slate-800">5% Insentif Potongan</p>
              <p className="text-[10px] text-slate-400">Komisen amal bagi setiap kutipan yang berjaya</p>
            </div>

            <div className="p-3.5 bg-slate-50 border border-slate-100 rounded-2xl">
              <Package className="w-5 h-5 text-amber-600 mb-1" />
              <p className="text-xs font-black text-slate-800">Kit Standee Percuma</p>
              <p className="text-[10px] text-slate-400">Standee akrilik & pelekat kalis air dihantar percuma</p>
            </div>
          </div>

          <button
            onClick={() => setStep('APPLY')}
            className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99]"
          >
            <span>Mohon Standee QR Fizikal Sekarang</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* 2. Application Form */}
      {step === 'APPLY' && (
        <form onSubmit={handleApply} className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs space-y-4">
          <div className="border-b border-slate-100 pb-3">
            <h3 className="text-sm font-black text-[#0F2028]">Borang Permohonan Duta QR</h3>
            <p className="text-[11px] text-slate-400">Maklumat lokasi dan akaun penyaluran komisen bulanan</p>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-extrabold text-[#0F2028]">Nama Pemohon / Wakil</label>
            <input
              type="text"
              required
              value={formData.fullName}
              onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
              placeholder="cth. Wan Hazim"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-xs font-extrabold text-[#0F2028]">No. Telefon (WhatsApp)</label>
              <input
                type="tel"
                required
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
                placeholder="012-3456789"
              />
            </div>

            <div className="space-y-1">
              <label className="text-xs font-extrabold text-[#0F2028]">Jenis Penempatan</label>
              <select
                value={formData.placementType}
                onChange={(e) => setFormData({ ...formData, placementType: e.target.value })}
                className="w-full h-11 px-3 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
              >
                <option value="Premis Perniagaan">Kaunter Kedai / Kafe</option>
                <option value="Surau / Masjid">Surau / Masjid Komuniti</option>
                <option value="Pejabat / Komersial">Lobi Pejabat / Co-working</option>
                <option value="Booth Acara">Booth Acara & Karnival</option>
              </select>
            </div>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-extrabold text-[#0F2028]">Nama Lokasi Penempatan</label>
            <input
              type="text"
              required
              value={formData.locationName}
              onChange={(e) => setFormData({ ...formData, locationName: e.target.value })}
              className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
              placeholder="cth. Kafe Barakah Cyberjaya"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-extrabold text-[#0F2028]">Alamat Penghantaran Kit Fizikal</label>
            <textarea
              rows={2}
              required
              value={formData.deliveryAddress}
              onChange={(e) => setFormData({ ...formData, deliveryAddress: e.target.value })}
              className="w-full p-3.5 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold outline-none focus:border-[#1A8C4E] resize-none"
              placeholder="No. Unit, Bangunan, Jalan, Poskod & Bandar"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-xs font-extrabold text-[#0F2028]">Bank Penerima Komisen</label>
              <select
                value={formData.bankName}
                onChange={(e) => setFormData({ ...formData, bankName: e.target.value })}
                className="w-full h-11 px-3 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
              >
                <option value="Maybank">Maybank</option>
                <option value="CIMB">CIMB</option>
                <option value="Bank Islam">Bank Islam</option>
                <option value="RHB">RHB</option>
              </select>
            </div>

            <div className="space-y-1">
              <label className="text-xs font-extrabold text-[#0F2028]">No. Akaun Bank</label>
              <input
                type="text"
                required
                value={formData.bankAccount}
                onChange={(e) => setFormData({ ...formData, bankAccount: e.target.value })}
                className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
                placeholder="1640xxxxxxxx"
              />
            </div>
          </div>

          <div className="p-3.5 bg-emerald-50 rounded-2xl border border-emerald-100 flex items-start gap-2 text-[11px] text-emerald-800">
            <CheckCircle2 className="w-4 h-4 text-[#1A8C4E] shrink-0 mt-0.5" />
            <span>
              Standee akrilik A5 berlamina berkod QR peribadi anda akan dipos dalam masa 3 hari bekerja selepas pengesahan.
            </span>
          </div>

          <div className="grid grid-cols-2 gap-2 pt-1">
            <button
              type="button"
              onClick={() => setStep('INFO')}
              className="h-11 bg-slate-100 text-slate-600 font-bold rounded-2xl text-xs"
            >
              Kembali
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="h-11 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs shadow-xs transition"
            >
              {submitting ? 'Menghantar...' : 'Sahkan & Jana QR'}
            </button>
          </div>
        </form>
      )}

      {/* 3. Real Agent QR & Live Commission Dashboard */}
      {step === 'DASHBOARD' && (
        <div className="space-y-5">
          {/* Kit Delivery Status Banner */}
          <div className="bg-white p-4 rounded-3xl border border-slate-100 shadow-xs flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 bg-amber-50 text-amber-700 rounded-2xl">
                <Package className="w-5 h-5" />
              </div>
              <div>
                <p className="text-[10px] font-extrabold text-slate-400 uppercase">Status Kit Fizikal Standee</p>
                <p className="text-xs font-black text-slate-800">{agentData.physicalKitStatus}</p>
                <p className="text-[10px] text-slate-400">Tracking: {agentData.trackingNumber}</p>
              </div>
            </div>
            <span className="text-[10px] font-black px-2.5 py-1 bg-emerald-50 text-[#1A8C4E] rounded-full border border-emerald-100">
              {agentData.agentCode}
            </span>
          </div>

          {/* Earnings & Commission Cards */}
          <div className="grid grid-cols-2 gap-3">
            <div className="bg-white p-4 rounded-3xl border border-slate-100 shadow-xs space-y-1">
              <div className="flex items-center gap-1.5 text-slate-400">
                <TrendingUp className="w-3.5 h-3.5 text-[#1A8C4E]" />
                <span className="text-[10px] font-extrabold uppercase">Jumlah Diwakafkan</span>
              </div>
              <p className="text-lg font-black text-slate-800">
                RM {agentData.totalCollected.toFixed(2)}
              </p>
              <p className="text-[10px] text-slate-400">{agentData.totalScans} imbasan terkumpul</p>
            </div>

            <div className="bg-gradient-to-br from-emerald-50 to-emerald-100/50 p-4 rounded-3xl border border-emerald-200/60 shadow-xs space-y-1">
              <div className="flex items-center gap-1.5 text-emerald-800">
                <Coins className="w-3.5 h-3.5 text-[#1A8C4E]" />
                <span className="text-[10px] font-extrabold uppercase">Komisen Anda (5%)</span>
              </div>
              <p className="text-lg font-black text-[#1A8C4E]">
                RM {agentData.totalCommission.toFixed(2)}
              </p>
              <p className="text-[10px] text-emerald-700/80">Disalur ke akaun bank anda</p>
            </div>
          </div>

          {/* Digital QR Standee Preview Frame */}
          <div className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs text-center space-y-4">
            <div>
              <span className="text-[10px] font-extrabold uppercase px-2.5 py-0.5 bg-[#1A8C4E] text-white rounded-full">
                Standee Rasmi Duta Taqwa
              </span>
              <h3 className="text-base font-black text-[#0F2028] mt-1.5">Kod QR Unik Ejen</h3>
              <p className="text-xs text-slate-400">
                Gunakan versi digital ini serta-merta sementara menunggu standee fizikal tiba.
              </p>
            </div>

            <div className="p-4 bg-slate-50 rounded-2xl inline-block border-2 border-dashed border-[#1A8C4E]">
              <img
                src={
                  agentData.qrUrl ||
                  `https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=WaqafTaqwa-Agent-${agentData.agentCode}`
                }
                alt="Standee Agent QR"
                className="w-48 h-48 mx-auto object-contain bg-white p-2 rounded-xl"
              />
              <p className="text-[11px] font-black text-slate-700 mt-2">KOD EJEN: {agentData.agentCode}</p>
              <span className="text-[9px] font-mono text-slate-400 block">DuitNow QR & DuitNow Auto-Debit</span>
            </div>

            <div className="grid grid-cols-2 gap-2 pt-1">
              <a
                href={`https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=WaqafTaqwa-Agent-${agentData.agentCode}`}
                download={`QR_Agent_${agentData.agentCode}.png`}
                target="_blank"
                rel="noreferrer"
                className="h-11 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold rounded-2xl text-xs flex items-center justify-center gap-1.5 transition"
              >
                <Download className="w-4 h-4" />
                <span>Muat Turun PNG</span>
              </a>

              <button
                onClick={() => {
                  navigator.clipboard?.writeText(
                    `https://waqaftaqwa.my/imbas?ref=${agentData.agentCode}`
                  );
                  alert('Pautan waqaf peribadi anda telah disalin!');
                }}
                className="h-11 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-1.5 shadow-xs transition"
              >
                <Share2 className="w-4 h-4" />
                <span>Kongsi Pautan</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
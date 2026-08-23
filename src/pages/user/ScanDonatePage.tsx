import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Flashlight, MapPin, CheckCircle2, Fingerprint } from 'lucide-react';

const AMOUNTS = [3, 5, 10];

export const ScanDonatePage: React.FC = () => {
  const navigate = useNavigate();
  const [selectedAmt, setSelectedAmt] = useState(5);
  const [taxExemption, setTaxExemption] = useState(true);

  return (
    <div className="bg-[#121820] min-h-full flex flex-col justify-between relative text-white">
      {/* Top Header */}
      <div className="px-4 py-3 flex items-center justify-between z-10">
        <button onClick={() => navigate(-1)} className="w-9 h-9 bg-white/10 rounded-full flex items-center justify-center">
          <ArrowLeft className="w-5 h-5 text-white" />
        </button>
        <h2 className="font-bold text-sm">Imbas & Derma</h2>
        <button className="w-9 h-9 bg-white/10 rounded-full flex items-center justify-center">
          <Flashlight className="w-4 h-4 text-white" />
        </button>
      </div>

      {/* Dynamic QR Scanner Visual Mock */}
      <div className="flex-1 flex flex-col items-center justify-center py-6 px-4">
        <div className="relative w-64 h-80 bg-slate-900/90 rounded-3xl border border-slate-700 p-4 flex flex-col items-center justify-between shadow-2xl">
          {/* Scanner Overlay Markers */}
          <div className="absolute top-4 left-4 w-6 h-6 border-t-2 border-l-2 border-emerald-400" />
          <div className="absolute top-4 right-4 w-6 h-6 border-t-2 border-r-2 border-emerald-400" />
          <div className="absolute bottom-4 left-4 w-6 h-6 border-b-2 border-l-2 border-emerald-400" />
          <div className="absolute bottom-4 right-4 w-6 h-6 border-b-2 border-r-2 border-emerald-400" />

          {/* QR Pattern Placeholder */}
          <div className="w-44 h-44 bg-white p-2 rounded-xl mt-4 flex items-center justify-center">
            <img
              src="https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=WaqafTaqwa_LarkinSentral_RM5"
              alt="DuitNow Dynamic QR"
              className="w-full h-full"
            />
          </div>

          <div className="bg-[#1A8C4E] text-white text-[11px] font-bold px-4 py-1.5 rounded-full flex items-center gap-1.5 shadow-md">
            <CheckCircle2 className="w-3.5 h-3.5" /> Dynamic QR Key Verified
          </div>
          <span className="text-[10px] text-slate-400 pb-1">Tap to Scan</span>
        </div>
      </div>

      {/* Bottom Payment Sheet */}
      <div className="bg-white rounded-t-[32px] p-5 text-slate-800 space-y-4 shadow-2xl">
        <div className="w-12 h-1 bg-slate-200 rounded-full mx-auto" />

        {/* Location Info */}
        <div className="bg-slate-50 border border-slate-200 rounded-2xl p-3 flex items-center gap-2.5">
          <MapPin className="w-5 h-5 text-rose-500 flex-shrink-0" />
          <div>
            <span className="text-[9px] uppercase font-extrabold text-slate-400 tracking-wider">Lokasi Imbasan</span>
            <p className="text-xs font-extrabold text-[#0F2028]">Masjid Larkin Sentral</p>
          </div>
        </div>

        {/* Amount Selector */}
        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-slate-800">Pilih Jumlah Sumbangan</label>
          <div className="grid grid-cols-3 gap-2.5">
            {AMOUNTS.map((amt) => (
              <button
                key={amt}
                onClick={() => setSelectedAmt(amt)}
                className={`py-2.5 rounded-2xl font-black text-sm border transition ${
                  selectedAmt === amt
                    ? 'bg-emerald-100 text-emerald-900 border-[#1A8C4E]'
                    : 'bg-white border-slate-200 text-slate-700'
                }`}
              >
                RM {amt}
              </button>
            ))}
          </div>
        </div>

        {/* Tax Deduction Toggle */}
        <div className="flex items-center justify-between pt-1">
          <div>
            <h5 className="font-bold text-xs text-slate-800">Minta Resit Potongan Cukai</h5>
            <p className="text-[10px] text-slate-400">Hasilkan Tax Hash unik bagi transaksi ini</p>
          </div>
          <button
            onClick={() => setTaxExemption(!taxExemption)}
            className={`w-11 h-6 flex items-center rounded-full p-1 transition-colors ${taxExemption ? 'bg-[#1A8C4E]' : 'bg-slate-300'}`}
          >
            <div className={`bg-white w-4 h-4 rounded-full shadow-md transform transition-transform ${taxExemption ? 'translate-x-5' : 'translate-x-0'}`} />
          </button>
        </div>

        {/* 1-Tap Biometric Payment Button */}
        <button
          onClick={() => navigate('/resit')}
          className="w-full h-14 bg-[#1A8C4E] hover:bg-[#15703E] text-white rounded-2xl px-5 flex items-center justify-between shadow-[0_4px_14px_rgba(26,140,78,0.3)] transition"
        >
          <div className="text-left">
            <span className="block font-black text-sm">Bayar Sekarang</span>
            <span className="block text-[10px] text-emerald-100 font-medium">1-Tap Touch ID / PIN</span>
          </div>
          <Fingerprint className="w-8 h-8 opacity-90" />
        </button>

        <p className="text-center text-[10px] text-slate-400">
          Secured & Powered by <span className="font-bold text-red-600">DuitNow</span>
        </p>
      </div>
    </div>
  );
};
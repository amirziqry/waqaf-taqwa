import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronDown, Fingerprint, Lock, ShieldCheck, Sun, X } from 'lucide-react';

export const AutoWaqafPage: React.FC = () => {
  const navigate = useNavigate();
  const [selectedFreq, setSelectedFreq] = useState<'subuh' | 'jumaat' | 'bulanan'>('subuh');
  const [selectedAmt, setSelectedAmt] = useState(5);
  const [roundUp, setRoundUp] = useState(true);
  const [akadAgreed, setAkadAgreed] = useState(true);
  const [paymentMethod, setPaymentMethod] = useState<'debit' | 'fpx' | 'ewallet'>('debit');

  return (
    <div className="bg-white min-h-full p-5 space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-slate-100 pb-3">
        <h2 className="font-black text-base text-[#0F2028]">Tetapan Auto Waqaf</h2>
        <button onClick={() => navigate(-1)} className="p-1 rounded-full text-slate-400 hover:bg-slate-100">
          <X className="w-5 h-5" />
        </button>
      </div>

      {/* Kekerapan Sumbangan */}
      <div className="space-y-1.5">
        <label className="text-xs font-bold text-slate-700">Kekerapan Sumbangan</label>
        <div className="grid grid-cols-3 gap-2">
          <button
            onClick={() => setSelectedFreq('subuh')}
            className={`py-2 px-2 rounded-xl text-xs font-bold flex items-center justify-center gap-1 transition ${
              selectedFreq === 'subuh' ? 'bg-[#1A8C4E] text-white' : 'border border-slate-200 text-slate-700'
            }`}
          >
            <Sun className="w-3.5 h-3.5" /> Daily Subuh
          </button>
          <button
            onClick={() => setSelectedFreq('jumaat')}
            className={`py-2 px-2 rounded-xl text-xs font-bold transition ${
              selectedFreq === 'jumaat' ? 'bg-[#1A8C4E] text-white' : 'border border-slate-200 text-slate-700'
            }`}
          >
            Setiap Jumaat
          </button>
          <button
            onClick={() => setSelectedFreq('bulanan')}
            className={`py-2 px-2 rounded-xl text-xs font-bold transition ${
              selectedFreq === 'bulanan' ? 'bg-[#1A8C4E] text-white' : 'border border-slate-200 text-slate-700'
            }`}
          >
            Bulanan
          </button>
        </div>
      </div>

      {/* Jumlah Sumbangan */}
      <div className="space-y-1.5">
        <label className="text-xs font-bold text-slate-700">Jumlah Sumbangan</label>
        <div className="grid grid-cols-4 gap-2">
          {[2, 5, 10].map((amt) => (
            <button
              key={amt}
              onClick={() => setSelectedAmt(amt)}
              className={`py-2 rounded-xl text-xs font-black border transition ${
                selectedAmt === amt
                  ? 'border-[#1A8C4E] text-[#1A8C4E] bg-emerald-50/50'
                  : 'border-slate-200 text-slate-700'
              }`}
            >
              RM {amt}
            </button>
          ))}
          <input
            type="text"
            placeholder="Lain-lain (RM)"
            className="text-[11px] text-center border border-slate-200 rounded-xl px-1 outline-none focus:border-[#1A8C4E]"
          />
        </div>
      </div>

      {/* Pilih Projek Waqaf Dropdown */}
      <div className="space-y-1.5">
        <label className="text-xs font-bold text-slate-700">Pilih Projek Waqaf</label>
        <div className="p-3 border border-slate-200 rounded-2xl flex items-center justify-between cursor-pointer">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-xl bg-emerald-50 text-[#1A8C4E] flex items-center justify-center font-bold text-xs">
              🌐
            </div>
            <div>
              <h5 className="font-bold text-xs text-slate-800">Waqaf Felda / Waqaf Taqwa</h5>
              <p className="text-[10px] text-slate-400">Pembangunan komuniti & fasiliti ibadah</p>
            </div>
          </div>
          <ChevronDown className="w-4 h-4 text-slate-400" />
        </div>
      </div>

      {/* Spare Change Round-Up Toggle */}
      <div className="p-3.5 bg-slate-50/80 border border-slate-200 rounded-2xl flex items-center justify-between">
        <div className="space-y-0.5 max-w-[240px]">
          <div className="flex items-center gap-1.5">
            <span className="font-extrabold text-xs text-slate-800">Aktifkan Spare Change Round-Up</span>
            <span className="text-[9px] bg-amber-100 text-amber-800 font-bold px-1.5 rounded">BARU</span>
          </div>
          <p className="text-[10px] text-slate-500">
            Bundarkan transaksi harian anda & salurkan lebihan sebagai waqaf.
          </p>
        </div>
        <button
          onClick={() => setRoundUp(!roundUp)}
          className={`w-11 h-6 flex items-center rounded-full p-1 transition-colors ${roundUp ? 'bg-[#1A8C4E]' : 'bg-slate-300'}`}
        >
          <div className={`bg-white w-4 h-4 rounded-full shadow-md transform transition-transform ${roundUp ? 'translate-x-5' : 'translate-x-0'}`} />
        </button>
      </div>

      {/* Akad Wakalah Syariah Box */}
      <div className="space-y-2">
        <div className="flex items-center gap-1.5 text-xs font-bold text-slate-800">
          <ShieldCheck className="w-4 h-4 text-[#1A8C4E]" />
          <span>Akad Wakalah Syariah</span>
        </div>
        <div className="bg-slate-50 border border-slate-200 rounded-2xl p-3.5 space-y-2.5">
          <p className="text-[11px] text-slate-600 italic leading-relaxed">
            "Dengan ini saya melantik pihak pengurusan Waqaf Taqwa sebagai wakil (Wakil Wakalah) untuk menguruskan wang sumbangan waqaf saya mengikut hukum syarak..."
          </p>
          <label className="flex items-start gap-2.5 cursor-pointer pt-1 border-t border-slate-200/60">
            <input
              type="checkbox"
              checked={akadAgreed}
              onChange={(e) => setAkadAgreed(e.target.checked)}
              className="mt-0.5 h-4 w-4 rounded text-[#1A8C4E] focus:ring-[#1A8C4E]"
            />
            <span className="text-[10.5px] font-bold text-slate-800">
              Saya bersetuju dengan terma akad wakalah di atas (Akuan Bersetuju)
            </span>
          </label>
        </div>
      </div>

      {/* Kaedah Pembayaran */}
      <div className="space-y-1.5">
        <label className="text-xs font-bold text-slate-700">Kaedah Pembayaran</label>
        <div className="grid grid-cols-3 gap-2">
          <button
            onClick={() => setPaymentMethod('debit')}
            className={`p-2.5 rounded-xl border flex flex-col items-center justify-center gap-1 text-[11px] font-bold ${
              paymentMethod === 'debit' ? 'border-[#1A8C4E] bg-emerald-50 text-[#1A8C4E]' : 'border-slate-200 text-slate-600'
            }`}
          >
            💳 Direct Debit
          </button>
          <button
            onClick={() => setPaymentMethod('fpx')}
            className={`p-2.5 rounded-xl border flex flex-col items-center justify-center gap-1 text-[11px] font-bold ${
              paymentMethod === 'fpx' ? 'border-[#1A8C4E] bg-emerald-50 text-[#1A8C4E]' : 'border-slate-200 text-slate-600'
            }`}
          >
            🏛 FPX Gateway
          </button>
          <button
            onClick={() => setPaymentMethod('ewallet')}
            className={`p-2.5 rounded-xl border flex flex-col items-center justify-center gap-1 text-[11px] font-bold ${
              paymentMethod === 'ewallet' ? 'border-[#1A8C4E] bg-emerald-50 text-[#1A8C4E]' : 'border-slate-200 text-slate-600'
            }`}
          >
            👛 e-Wallet
          </button>
        </div>
      </div>

      {/* Biometric Confirmation CTA */}
      <div className="pt-2 space-y-2">
        <button
          onClick={() => navigate('/transaksi')}
          className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition"
        >
          <Fingerprint className="w-5 h-5" /> Sahkan Auto Waqaf (Guna PIN/Biometrik)
        </button>
        <p className="text-center text-[9px] text-slate-400 flex items-center justify-center gap-1">
          <Lock className="w-3 h-3" /> Transaksi selamat dengan jaminan keselamatan bank peringkat-tinggi
        </p>
      </div>
    </div>
  );
};
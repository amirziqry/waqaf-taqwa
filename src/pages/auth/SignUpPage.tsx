import React, { useState } from 'react';
import { CheckCircle2, User, Store, IdCard, Upload } from 'lucide-react';

export const SignUpPage: React.FC = () => {
  const [phoneNumber, setPhoneNumber] = useState('12 345 6789');
  const [otp] = useState<string[]>(['5', '2', '0', '9', '', '']);
  const [accountType, setAccountType] = useState<'personal' | 'merchant'>('personal');
  const [elderlyMode, setElderlyMode] = useState(true);

  return (
    <div className="p-6 space-y-6 bg-white min-h-full">
      {/* Phone Number Input */}
      <div className="space-y-2">
        <label className="text-base font-extrabold text-[#0F2028]">Nombor Telefon</label>
        <div className="flex gap-2">
          <div className="w-16 h-12 bg-slate-50 border border-slate-200 rounded-xl flex items-center justify-center font-bold text-slate-700 text-sm">
            +60
          </div>
          <div className="flex-1 h-12 bg-slate-50 border border-slate-200 rounded-xl px-4 flex items-center justify-between">
            <input
              type="text"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              className="bg-transparent font-bold text-sm w-full outline-none text-slate-800"
            />
            <CheckCircle2 className="w-5 h-5 text-[#1A8C4E]" />
          </div>
        </div>
        <button
          onClick={() => {}}
          className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-xl text-sm mt-3 shadow-[0_4px_12px_rgba(26,140,78,0.2)] transition active:scale-[0.99]"
        >
          Hantar OTP
        </button>
      </div>

      {/* OTP Boxes */}
      <div className="space-y-2 pt-1">
        <label className="text-sm font-bold text-[#0F2028]">Masukkan Kod OTP</label>
        <div className="grid grid-cols-6 gap-2">
          {otp.map((digit, i) => (
            <input
              key={i}
              type="text"
              maxLength={1}
              value={digit}
              readOnly
              className={`h-12 border text-center text-lg font-black rounded-xl ${
                i === 4
                  ? 'border-[#1A8C4E] bg-emerald-50/40 ring-1 ring-[#1A8C4E]'
                  : 'border-slate-200 bg-slate-50 text-slate-800'
              }`}
            />
          ))}
        </div>
        <div className="flex justify-between items-center text-xs pt-1">
          <button className="text-[#1A8C4E] font-bold underline">Hantar Semula</button>
          <span className="text-slate-500 font-medium">⏱ 00:59</span>
        </div>
      </div>

      {/* Account Type Selection */}
      <div className="space-y-2.5">
        <label className="text-sm font-bold text-[#0F2028]">Pilih Jenis Akaun</label>
        <div className="grid grid-cols-2 gap-3">
          {/* Personal Card */}
          <div
            onClick={() => setAccountType('personal')}
            className={`p-3.5 rounded-2xl border-2 cursor-pointer transition ${
              accountType === 'personal'
                ? 'border-[#1A8C4E] bg-emerald-50/40'
                : 'border-slate-200 bg-white'
            }`}
          >
            <div className="flex justify-between items-start mb-2">
              <User className="w-5 h-5 text-[#1A8C4E]" />
              <div className={`w-4 h-4 rounded-full ${accountType === 'personal' ? 'bg-[#1A8C4E]' : 'border border-slate-300'}`} />
            </div>
            <h4 className="font-extrabold text-xs text-[#0F2028]">Akaun Peribadi</h4>
            <p className="text-[10px] text-slate-500 mt-1 leading-tight">• Derma & Waqaf<br/>• Jejak Sumbangan</p>
          </div>

          {/* Merchant Card */}
          <div
            onClick={() => setAccountType('merchant')}
            className={`p-3.5 rounded-2xl border-2 cursor-pointer transition ${
              accountType === 'merchant'
                ? 'border-[#1A8C4E] bg-emerald-50/40'
                : 'border-slate-200 bg-white'
            }`}
          >
            <div className="flex justify-between items-start mb-2">
              <Store className="w-5 h-5 text-slate-700" />
              <div className={`w-4 h-4 rounded-full ${accountType === 'merchant' ? 'bg-[#1A8C4E]' : 'border border-slate-300'}`} />
            </div>
            <h4 className="font-extrabold text-xs text-[#0F2028]">Akaun Peniaga</h4>
            <p className="text-[10px] text-slate-500 mt-1 leading-tight">• Terima Wakaf<br/>• Laporan Kewangan</p>
          </div>
        </div>
      </div>

      {/* Senior Friendly Mode Toggle */}
      <div className="p-3.5 bg-slate-50 border border-slate-200 rounded-2xl flex items-center justify-between">
        <div>
          <h4 className="font-bold text-xs text-[#0F2028]">Mod Mesra Warga Emas</h4>
          <p className="text-[10px] text-slate-500">Aktifkan teks besar untuk paparan lebih jelas</p>
        </div>
        <button
          onClick={() => setElderlyMode(!elderlyMode)}
          className={`w-11 h-6 flex items-center rounded-full p-1 transition-colors ${elderlyMode ? 'bg-[#1A8C4E]' : 'bg-slate-300'}`}
        >
          <div className={`bg-white w-4 h-4 rounded-full shadow-md transform transition-transform ${elderlyMode ? 'translate-x-5' : 'translate-x-0'}`} />
        </button>
      </div>

      {/* eKYC Container */}
      <div className="space-y-3 pt-1">
        <h3 className="font-extrabold text-sm text-[#0F2028]">Pengesahan Identiti (eKYC)</h3>

        {/* MyKad Upload Area */}
        <div className="border-2 border-dashed border-[#1A8C4E]/60 bg-emerald-50/20 rounded-2xl p-4 flex flex-col items-center text-center space-y-2">
          <IdCard className="w-8 h-8 text-[#1A8C4E]" />
          <div>
            <h5 className="font-bold text-xs text-slate-800">Muat Naik MyKad (Depan)</h5>
            <p className="text-[10px] text-slate-400">Sila pastikan gambar jelas & tidak kabur</p>
          </div>
          <button className="bg-emerald-50 border border-emerald-300 text-[#1A8C4E] px-4 py-1.5 rounded-xl font-bold text-xs flex items-center gap-1.5">
            <Upload className="w-3.5 h-3.5" /> Pilih Fail
          </button>
        </div>

        {/* Live Selfie Sensor Box */}
        <div className="bg-[#19242E] rounded-2xl p-4 flex flex-col items-center justify-center space-y-3 relative overflow-hidden">
          <div className="absolute top-3 left-3 flex items-center gap-1.5 text-[9px] font-bold text-red-500 tracking-wider">
            <span className="w-2 h-2 rounded-full bg-red-500 animate-pulse" /> LIVE SENSOR
          </div>
          <div className="w-28 h-36 border-2 border-dashed border-slate-400/80 rounded-full mt-4" />
          <p className="text-white text-[11px] font-bold">Selfie Pengesahan</p>
          <p className="text-slate-400 text-[9px] -mt-2">Posisikan wajah anda di dalam garisan bujur</p>
        </div>
      </div>
    </div>
  );
};
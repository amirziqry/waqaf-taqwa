import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { CheckCircle2, User, Store, Upload, Camera, ArrowRight, Loader2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const AuthPage: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();

  // Form State
  const [step, setStep] = useState<1 | 2 | 3 | 4>(1);
  const [phoneNumber, setPhoneNumber] = useState('');
  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const [selectedRole, setSelectedRole] = useState<'personal' | 'merchant'>('personal');
  const [seniorMode, setSeniorMode] = useState(false);
  
  // Loading & Timer States
  const [isLoading, setIsLoading] = useState(false);
  const [timer, setTimer] = useState(59);
  const otpInputsRef = useRef<(HTMLInputElement | null)[]>([]);

  // 1. Countdown timer for OTP
  useEffect(() => {
    let interval: any;
    if (step === 2 && timer > 0) {
      interval = setInterval(() => setTimer((prev) => prev - 1), 1000);
    }
    return () => clearInterval(interval);
  }, [step, timer]);

  // 2. Mock Backend: Request OTP
  const handleSendOtp = (e: React.FormEvent) => {
    e.preventDefault();
    if (phoneNumber.length < 8) return;
    
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      setStep(2);
      setTimer(59);
    }, 800);
  };

  // 3. Mock Backend: Verify OTP
  const handleOtpChange = (index: number, value: string) => {
    if (isNaN(Number(value))) return;
    const newOtp = [...otp];
    newOtp[index] = value.slice(-1);
    setOtp(newOtp);

    // Auto-focus next box
    if (value && index < 5) {
      otpInputsRef.current[index + 1]?.focus();
    }

    // When 6 digits filled -> simulate auto-verify
    if (newOtp.every((digit) => digit !== '')) {
      setIsLoading(true);
      setTimeout(() => {
        setIsLoading(false);
        setStep(3);
      }, 700);
    }
  };

  // 4. Mock Backend: Complete eKYC & Log In
  const handleCompleteRegistration = () => {
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      login(selectedRole);
      
      if (selectedRole === 'merchant') {
        navigate('/merchant');
      } else {
        navigate('/');
      }
    }, 1000);
  };

  return (
    <div
      className={`min-h-screen bg-[#FAFAFA] text-[#0F2028] px-4 py-8 max-w-md mx-auto flex flex-col justify-center ${
        seniorMode ? 'text-lg' : 'text-base'
      }`}
    >
      {/* Brand Header */}
      <div className="text-center mb-6">
        <h1 className="text-2xl font-black tracking-tight text-[#0F2028]">WAQAF TAQWA</h1>
        <p className="text-xs text-gray-500 mt-1">Platform Wakaf Digital Berimpak Tinggi</p>
      </div>

      <div className="bg-white rounded-3xl p-6 shadow-sm border border-gray-100 space-y-6">

        {/* SECTION 1: NOMBOR TELEFON */}
        <div>
          <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-2">
            Nombor Telefon
          </label>
          <div className="flex gap-2">
            <div className="w-16 h-12 bg-gray-50 border border-gray-200 rounded-xl flex items-center justify-center font-semibold text-gray-700">
              +60
            </div>
            <div className="flex-1 relative">
              <input
                type="tel"
                disabled={step > 1}
                placeholder="12 345 6789"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                className="w-full h-12 px-4 rounded-xl border border-gray-200 focus:border-[#1A8C4E] focus:ring-1 focus:ring-[#1A8C4E] outline-none font-medium text-gray-800 disabled:bg-gray-50"
              />
              {phoneNumber.length >= 8 && (
                <CheckCircle2 className="w-5 h-5 text-[#1A8C4E] absolute right-3 top-3.5" />
              )}
            </div>
          </div>

          {step === 1 && (
            <button
              onClick={handleSendOtp}
              disabled={isLoading || phoneNumber.length < 8}
              className="w-full mt-4 h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-semibold rounded-xl flex items-center justify-center gap-2 transition disabled:opacity-50"
            >
              {isLoading ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Hantar OTP'}
            </button>
          )}
        </div>

        {/* SECTION 2: MASUKKAN KOD OTP */}
        {step >= 2 && (
          <div className="pt-2 border-t border-gray-100 space-y-3">
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider">
              Masukkan Kod OTP
            </label>
            <div className="flex justify-between gap-1.5">
              {otp.map((digit, idx) => (
                <input
                  key={idx}
                  ref={(el) => {
  otpInputsRef.current[idx] = el;
}}
                  type="text"
                  maxLength={1}
                  disabled={step > 2}
                  value={digit}
                  onChange={(e) => handleOtpChange(idx, e.target.value)}
                  className="w-11 h-12 text-center text-xl font-bold bg-white border border-gray-300 rounded-xl focus:border-[#1A8C4E] focus:ring-2 focus:ring-[#1A8C4E]/20 outline-none disabled:bg-gray-50"
                />
              ))}
            </div>
            <div className="flex justify-between items-center text-xs pt-1">
              <button 
                onClick={() => setTimer(59)}
                disabled={timer > 0} 
                className="text-[#1A8C4E] font-medium underline disabled:text-gray-400"
              >
                Hantar Semula
              </button>
              <span className="text-gray-400 font-mono">00:{timer < 10 ? `0${timer}` : timer}</span>
            </div>
          </div>
        )}

        {/* SECTION 3: PILIH JENIS AKAUN */}
        {step >= 3 && (
          <div className="pt-2 border-t border-gray-100 space-y-3">
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider">
              Pilih Jenis Akaun
            </label>
            <div className="grid grid-cols-2 gap-3">
              {/* Personal Card */}
              <div
                onClick={() => setSelectedRole('personal')}
                className={`p-4 rounded-2xl border-2 cursor-pointer transition flex flex-col justify-between ${
                  selectedRole === 'personal'
                    ? 'border-[#1A8C4E] bg-emerald-50/30'
                    : 'border-gray-100 bg-gray-50'
                }`}
              >
                <div className="flex justify-between items-center mb-2">
                  <User className="w-5 h-5 text-gray-700" />
                  <div className={`w-3.5 h-3.5 rounded-full border ${selectedRole === 'personal' ? 'bg-[#1A8C4E] border-[#1A8C4E]' : 'border-gray-300'}`} />
                </div>
                <h3 className="font-bold text-sm text-[#0F2028]">Akaun Peribadi</h3>
                <p className="text-[11px] text-gray-500 mt-1 leading-tight">
                  • Derma & Wakaf<br />• Jejak Sumbangan
                </p>
              </div>

              {/* Merchant Card */}
              <div
                onClick={() => setSelectedRole('merchant')}
                className={`p-4 rounded-2xl border-2 cursor-pointer transition flex flex-col justify-between ${
                  selectedRole === 'merchant'
                    ? 'border-[#1A8C4E] bg-emerald-50/30'
                    : 'border-gray-100 bg-gray-50'
                }`}
              >
                <div className="flex justify-between items-center mb-2">
                  <Store className="w-5 h-5 text-gray-700" />
                  <div className={`w-3.5 h-3.5 rounded-full border ${selectedRole === 'merchant' ? 'bg-[#1A8C4E] border-[#1A8C4E]' : 'border-gray-300'}`} />
                </div>
                <h3 className="font-bold text-sm text-[#0F2028]">Akaun Peniaga</h3>
                <p className="text-[11px] text-gray-500 mt-1 leading-tight">
                  • Terima Wakaf<br />• Laporan Kewangan
                </p>
              </div>
            </div>

            {/* Senior Mode Toggle */}
            <div className="flex items-center justify-between p-3 bg-gray-50 rounded-xl border border-gray-100 mt-3">
              <div>
                <h4 className="text-xs font-bold text-gray-800">Mod Mesra Warga Emas</h4>
                <p className="text-[10px] text-gray-500">Aktifkan teks besar & kontras tinggi</p>
              </div>
              <input
                type="checkbox"
                checked={seniorMode}
                onChange={(e) => setSeniorMode(e.target.checked)}
                className="w-5 h-5 accent-[#1A8C4E] cursor-pointer"
              />
            </div>

            {step === 3 && (
              <button
                onClick={() => setStep(4)}
                className="w-full mt-2 h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-semibold rounded-xl flex items-center justify-center gap-2"
              >
                Seterusnya <ArrowRight className="w-4 h-4" />
              </button>
            )}
          </div>
        )}

        {/* SECTION 4: PENGESAHAN IDENTITI (eKYC) */}
        {step >= 4 && (
          <div className="pt-2 border-t border-gray-100 space-y-4">
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider">
              Pengesahan Identiti (eKYC)
            </label>

            {/* MyKad Upload Box */}
            <div className="border-2 border-dashed border-[#1A8C4E]/40 bg-emerald-50/20 rounded-2xl p-4 text-center">
              <Upload className="w-6 h-6 text-[#1A8C4E] mx-auto mb-1" />
              <h4 className="text-xs font-bold text-gray-800">Muat Naik MyKad (Depan)</h4>
              <p className="text-[10px] text-gray-400 mt-0.5">Sila pastikan gambar jelas & tidak kabur</p>
              <button className="mt-2 text-xs bg-white border border-gray-200 px-3 py-1.5 rounded-lg font-medium text-gray-700 hover:bg-gray-50">
                Pilih Fail
              </button>
            </div>

            {/* Camera Selfie Liveness Sensor */}
            <div className="bg-[#0F2028] text-white rounded-2xl p-4 flex flex-col items-center relative overflow-hidden">
              <div className="flex items-center gap-1.5 text-[10px] text-red-400 font-mono mb-2 self-start">
                <span className="w-2 h-2 rounded-full bg-red-500 animate-pulse" /> LIVE SENSOR
              </div>
              <div className="w-24 h-28 border-2 border-dashed border-gray-500 rounded-full flex items-center justify-center my-2">
                <Camera className="w-6 h-6 text-gray-400" />
              </div>
              <h4 className="text-xs font-bold mt-1">Selfie Pengesahan</h4>
              <p className="text-[10px] text-gray-400">Posisikan wajah anda di dalam garisan bujur</p>
            </div>

            {/* Submit Button */}
            <button
              onClick={handleCompleteRegistration}
              disabled={isLoading}
              className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-semibold rounded-xl flex items-center justify-center gap-2 transition"
            >
              {isLoading ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Sahkan & Selesai'}
            </button>
          </div>
        )}

      </div>
    </div>
  );
};
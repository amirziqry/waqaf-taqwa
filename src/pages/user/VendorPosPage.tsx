import React, { useState } from 'react';
import { QrCode, Store, CheckCircle2, RotateCcw, ArrowRight } from 'lucide-react';

export const VendorPosPage: React.FC = () => {
  const [billAmount, setBillAmount] = useState<string>('');
  const [roundUpWaqaf, setRoundUpWaqaf] = useState<number>(0);
  const [step, setStep] = useState<'INPUT' | 'QR_TERMINAL' | 'COMPLETED'>('INPUT');
  const [, setQrPayload] = useState<string>('');

  const handleCalculateWaqaf = (e: React.FormEvent) => {
    e.preventDefault();
    const parsed = parseFloat(billAmount);
    if (isNaN(parsed) || parsed <= 0) return;

    // Standard Malaysian retail rounding calculation to nearest integer Ringgit
    const ceil = Math.ceil(parsed);
    const diff = ceil > parsed ? Number((ceil - parsed).toFixed(2)) : 1.0;
    setRoundUpWaqaf(diff);

    // Generate compliant dynamic DuitNow POS payload
    const refCode = `POS-${Date.now().toString().slice(-6)}`;
    setQrPayload(`duitnow://pay?merchant=WTQ_STORE_01&amount=${diff.toFixed(2)}&ref=${refCode}`);
    setStep('QR_TERMINAL');
  };

  const handleSimulateCustomerPayment = () => {
    // Record to local vendor transaction stream
    const record = {
      id: `POS-TXN-${Date.now().toString().slice(-6)}`,
      referenceNo: `WTQ-POS-${Math.random().toString(36).substring(2, 7).toUpperCase()}`,
      amount: roundUpWaqaf,
      donorName: 'Pelanggan Kaunter Runcit (POS)',
      campaignTitle: 'Dana Infaq Runcit Komuniti',
      paymentMethod: 'SOFTPOS_DUITNOW',
      taxDeductible: true,
      verificationHash: `0x${Math.random().toString(16).substring(2, 18)}`,
      createdAt: new Date().toISOString(),
      status: 'SUCCESS',
    };

    const stored = JSON.parse(localStorage.getItem('wt_transactions') || '[]');
    localStorage.setItem('wt_transactions', JSON.stringify([record, ...stored]));

    setStep('COMPLETED');
  };

  return (
    <div className="max-w-md mx-auto space-y-6">
      {/* Top Banner */}
      <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs flex items-center justify-between">
        <div>
          <h1 className="text-xl font-black text-[#0F2028]">Terminal SoftPOS Peniaga</h1>
          <p className="text-xs text-slate-400">Kutipan pembundaran waqaf di kaunter juruwang</p>
        </div>
        <div className="p-3 bg-amber-50 text-amber-700 rounded-2xl">
          <Store className="w-6 h-6" />
        </div>
      </div>

      {step === 'INPUT' && (
        <form onSubmit={handleCalculateWaqaf} className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs space-y-5">
          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Jumlah Bil Jualan Pelanggan (RM)</label>
            <div className="h-12 bg-slate-50 border border-slate-200 rounded-2xl px-4 flex items-center gap-2 focus-within:bg-white focus-within:border-[#1A8C4E] transition">
              <span className="font-extrabold text-sm text-slate-400">RM</span>
              <input
                type="number"
                step="0.01"
                min="0.10"
                required
                value={billAmount}
                onChange={(e) => setBillAmount(e.target.value)}
                placeholder="cth. 18.30"
                className="w-full bg-transparent text-sm font-black outline-none text-slate-800"
              />
            </div>
            <p className="text-[11px] text-slate-400">Sistem akan membundarkan baki bil untuk dijadikan infaq waqaf.</p>
          </div>

          <button
            type="submit"
            className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99]"
          >
            <span>Jana Kod QR Pelanggan</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>
      )}

      {step === 'QR_TERMINAL' && (
        <div className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs text-center space-y-5">
          <div>
            <span className="text-xs font-bold text-slate-400">Tunjukkan Kod QR Kepada Pelanggan</span>
            <h2 className="text-2xl font-black text-[#1A8C4E] mt-1">RM {roundUpWaqaf.toFixed(2)}</h2>
            <p className="text-[11px] text-slate-500">Infaq Pembundaran Bil (Jumlah Bil: RM {billAmount})</p>
          </div>

          {/* Dynamic POS QR Code Frame */}
          <div className="p-4 bg-slate-50 rounded-2xl inline-block border-2 border-dashed border-[#1A8C4E]">
            <QrCode className="w-48 h-48 mx-auto text-slate-800" />
            <span className="text-[10px] font-mono text-slate-400 mt-2 block">Imbas guna DuitNow / MAE / TNG</span>
          </div>

          <div className="space-y-2 pt-2">
            <button
              onClick={handleSimulateCustomerPayment}
              className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-xs transition"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>Sahkan Pembayaran Diterima</span>
            </button>

            <button
              onClick={() => setStep('INPUT')}
              className="w-full h-10 bg-slate-100 hover:bg-slate-200 text-slate-600 font-bold rounded-2xl text-xs transition"
            >
              Batal / Masukkan Bil Baharu
            </button>
          </div>
        </div>
      )}

      {step === 'COMPLETED' && (
        <div className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs text-center space-y-5">
          <div className="w-14 h-14 bg-emerald-50 text-[#1A8C4E] rounded-2xl flex items-center justify-center mx-auto border border-emerald-100">
            <CheckCircle2 className="w-8 h-8" />
          </div>

          <div>
            <h2 className="text-lg font-black text-[#0F2028]">Kutipan Berjaya Direkodkan!</h2>
            <p className="text-xs text-slate-400 mt-1">RM {roundUpWaqaf.toFixed(2)} telah disalurkan ke tabung waqaf utama.</p>
          </div>

          <button
            onClick={() => {
              setBillAmount('');
              setStep('INPUT');
            }}
            className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-xs transition"
          >
            <RotateCcw className="w-4 h-4" />
            <span>Transaksi Seterusnya</span>
          </button>
        </div>
      )}
    </div>
  );
};
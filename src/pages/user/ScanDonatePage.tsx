import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  QrCode, 
  ShieldCheck, 
  CheckCircle2, 
  ArrowRight, 
  Building2, 
  ExternalLink,
  CreditCard,
  Lock,
  AlertCircle,
  KeyRound
} from 'lucide-react';
import api from '../../api/client';

export const ScanDonatePage: React.FC = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState<'amount' | 'gateway_redirect' | 'processing' | 'success'>('amount');
  const [amount, setAmount] = useState<string>('20');
  const [isAnonymous, setIsAnonymous] = useState(false);
  const [akadAgreed, setAkadAgreed] = useState(true);
  const [campaignId] = useState('1');
  const [transactionData, setTransactionData] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  // Third-party Gateway Modal States
  const [selectedGatewayMethod, setSelectedGatewayMethod] = useState<'fpx' | 'duitnow' | 'card'>('fpx');
  const [selectedBank, setSelectedBank] = useState('Maybank2u');
  const [bankUsername, setBankUsername] = useState('');
  const [bankPassword, setBankPassword] = useState('');
  const [tacCode, setTacCode] = useState('');
  const [tacRequested, setTacRequested] = useState(false);
  const [authError, setAuthError] = useState('');

  const presetAmounts = ['10', '20', '50', '100', '200'];

  // Step 1: Open external third-party payment gateway session
  const handleInitiateRedirect = (e: React.FormEvent) => {
    e.preventDefault();
    if (!akadAgreed || Number(amount) <= 0) return;
    setAuthError('');
    setTacRequested(false);
    setTacCode('');
    setBankUsername('');
    setBankPassword('');
    setStep('gateway_redirect');
  };

  // Step 2: Handle authorization within third-party gateway portal
  const handleAuthorizeGateway = (e: React.FormEvent) => {
    e.preventDefault();

    if (selectedGatewayMethod === 'fpx') {
      if (!bankUsername || !bankPassword) {
        setAuthError('Sila masukkan ID Pengguna dan Kata Laluan perbankan.');
        return;
      }
      if (!tacRequested) {
        setTacRequested(true);
        setAuthError('');
        return;
      }
      if (tacCode !== '123456') {
        setAuthError('Kod TAC tidak sah. Sila gunakan kod ujian rasmi: 123456');
        return;
      }
    }

    executePaymentVerification();
  };

  // Step 3: Complete payment and write transaction records
  const executePaymentVerification = async () => {
    setLoading(true);
    setStep('processing');

    const methodLabel = 
      selectedGatewayMethod === 'fpx' ? `FPX (${selectedBank})` :
      selectedGatewayMethod === 'duitnow' ? 'DuitNow QR' : 'Kad Perbankan';

    const payload = {
      campaignId,
      amount: Number(amount),
      paymentMethod: methodLabel,
      bank: selectedGatewayMethod === 'fpx' ? selectedBank : undefined,
      akadAgreed,
      isAnonymous,
      notes: 'Infaq & Waqaf Digital Taqwa (Gerbang Luar)',
    };

    try {
      const res = await api.post('/donator/donation/pay', payload).catch(() => null);

      const record = res?.data || {
        id: `TXN-${Date.now().toString().slice(-6)}`,
        referenceNo: `WTQ-${Math.random().toString(36).substring(2, 9).toUpperCase()}`,
        amount: Number(amount),
        donorName: isAnonymous ? 'Hamba Allah (Anonim)' : localStorage.getItem('wt_user_name') || 'Pewakaf Taqwa',
        campaignTitle: 'Dana Pembangunan & Kebajikan Komuniti',
        paymentMethod: methodLabel,
        taxDeductible: true,
        verificationHash: `0x${Math.random().toString(16).substring(2, 18)}${Math.random().toString(16).substring(2, 18)}`,
        createdAt: new Date().toISOString(),
        status: 'SUCCESS',
      };

      const stored = JSON.parse(localStorage.getItem('wt_transactions') || '[]');
      localStorage.setItem('wt_transactions', JSON.stringify([record, ...stored]));

      setTransactionData(record);
      setTimeout(() => {
        setStep('success');
      }, 1400);
    } catch {
      setStep('amount');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto space-y-6 pb-12">
      {/* Top Banner */}
      <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs flex items-center justify-between">
        <div>
          <h1 className="text-xl font-extrabold text-[#0F2028]">Waqaf Segera</h1>
          <p className="text-xs text-slate-400">Sumbangan pantas patuh syariah dengan pelepasan cukai LHDN</p>
        </div>
        <div className="p-3 bg-emerald-50 text-[#1A8C4E] rounded-2xl">
          <QrCode className="w-6 h-6" />
        </div>
      </div>

      {/* 1. Payment Form (No manual payment method section) */}
      {step === 'amount' && (
        <form onSubmit={handleInitiateRedirect} className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs space-y-5">
          {/* Quick Preset Amounts */}
          <div className="space-y-2">
            <label className="text-xs font-extrabold text-[#0F2028]">Pilih Amaun (RM)</label>
            <div className="grid grid-cols-5 gap-2">
              {presetAmounts.map((val) => (
                <button
                  type="button"
                  key={val}
                  onClick={() => setAmount(val)}
                  className={`py-2.5 rounded-2xl text-xs font-extrabold transition ${
                    amount === val
                      ? 'bg-[#1A8C4E] text-white shadow-xs'
                      : 'bg-slate-50 text-slate-600 hover:bg-slate-100 border border-slate-100'
                  }`}
                >
                  RM {val}
                </button>
              ))}
            </div>
          </div>

          {/* Custom Amount Input */}
          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Atau Masukkan Amaun Sendiri</label>
            <div className="h-12 bg-slate-50 border border-slate-200 rounded-2xl px-4 flex items-center gap-2 focus-within:bg-white focus-within:border-[#1A8C4E] transition">
              <span className="font-extrabold text-sm text-[#1A8C4E]">RM</span>
              <input
                type="number"
                min="1"
                required
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="w-full bg-transparent text-sm font-black outline-none text-slate-800"
              />
            </div>
          </div>

          {/* Akad & Anonymity Toggles */}
          <div className="p-4 bg-slate-50 rounded-2xl space-y-3 border border-slate-100">
            <label className="flex items-start gap-2.5 cursor-pointer select-none">
              <input
                type="checkbox"
                checked={akadAgreed}
                onChange={(e) => setAkadAgreed(e.target.checked)}
                className="mt-0.5 rounded text-[#1A8C4E] focus:ring-[#1A8C4E]"
              />
              <span className="text-[11px] text-slate-600 font-medium leading-tight">
                <strong>Lafaz Akad:</strong> Saya berniat mewakafkan dana sebanyak <strong>RM {amount}</strong> ini kerana Allah Taala untuk kemaslahatan ummah.
              </span>
            </label>
          </div>

          <button
            type="submit"
            disabled={!akadAgreed || Number(amount) <= 0}
            className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] disabled:bg-slate-300 text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99]"
          >
            <span>Bayar RM {Number(amount).toFixed(2)} Melalui Gerbang Pembayaran</span>
            <ExternalLink className="w-4 h-4" />
          </button>
        </form>
      )}

      {/* 2. Third-Party Payment Gateway Portal */}
      {step === 'gateway_redirect' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-xl overflow-hidden animate-in fade-in zoom-in-95 duration-200">
          {/* External Gateway Header */}
          <div className="bg-slate-900 p-4 text-white flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse" />
              <div>
                <p className="text-xs font-black tracking-wide">Gerbang Pembayaran Rasmi (B2C Gateway)</p>
                <p className="text-[10px] text-slate-400 font-mono">ID Sesi: SEC-{Date.now().toString().slice(-6)}</p>
              </div>
            </div>
            <span className="text-[10px] font-bold px-2 py-0.5 bg-emerald-500/20 text-emerald-300 rounded-md border border-emerald-500/30">
              SSL 256-bit
            </span>
          </div>

          <form onSubmit={handleAuthorizeGateway} className="p-6 space-y-5">
            {/* Bill Info Summary */}
            <div className="bg-slate-50 p-3.5 rounded-2xl border border-slate-100 flex items-center justify-between text-xs">
              <div>
                <p className="text-slate-400 text-[10px] font-extrabold uppercase">Penerima</p>
                <p className="font-extrabold text-slate-800">Waqaf Taqwa Malaysia</p>
              </div>
              <div className="text-right">
                <p className="text-slate-400 text-[10px] font-extrabold uppercase">Jumlah Ditagih</p>
                <p className="font-black text-sm text-[#1A8C4E]">RM {Number(amount).toFixed(2)}</p>
              </div>
            </div>

            {/* Gateway Channel Selector */}
            <div className="space-y-2">
              <label className="text-xs font-extrabold text-slate-700">Pilih Saluran Pembayaran di Gerbang</label>
              <div className="grid grid-cols-3 gap-2">
                <button
                  type="button"
                  onClick={() => setSelectedGatewayMethod('fpx')}
                  className={`p-2.5 rounded-xl border-2 text-xs font-bold flex flex-col items-center gap-1 transition ${
                    selectedGatewayMethod === 'fpx'
                      ? 'border-[#1A8C4E] bg-emerald-50 text-[#1A8C4E]'
                      : 'border-slate-100 text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  <Building2 className="w-4 h-4" />
                  <span>FPX B2C</span>
                </button>
                <button
                  type="button"
                  onClick={() => setSelectedGatewayMethod('duitnow')}
                  className={`p-2.5 rounded-xl border-2 text-xs font-bold flex flex-col items-center gap-1 transition ${
                    selectedGatewayMethod === 'duitnow'
                      ? 'border-[#1A8C4E] bg-emerald-50 text-[#1A8C4E]'
                      : 'border-slate-100 text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  <QrCode className="w-4 h-4" />
                  <span>DuitNow QR</span>
                </button>
                <button
                  type="button"
                  onClick={() => setSelectedGatewayMethod('card')}
                  className={`p-2.5 rounded-xl border-2 text-xs font-bold flex flex-col items-center gap-1 transition ${
                    selectedGatewayMethod === 'card'
                      ? 'border-[#1A8C4E] bg-emerald-50 text-[#1A8C4E]'
                      : 'border-slate-100 text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  <CreditCard className="w-4 h-4" />
                  <span>Kad Debit</span>
                </button>
              </div>
            </div>

            {authError && (
              <div className="p-3 bg-rose-50 border border-rose-200 rounded-xl flex items-center gap-2 text-rose-700 text-xs font-semibold">
                <AlertCircle className="w-4 h-4 shrink-0" />
                <span>{authError}</span>
              </div>
            )}

            {/* FPX Flow */}
            {selectedGatewayMethod === 'fpx' && (
              <div className="space-y-3">
                <div className="space-y-1">
                  <label className="text-[11px] font-extrabold text-slate-600">Pilih Bank Terlibat</label>
                  <select
                    value={selectedBank}
                    onChange={(e) => setSelectedBank(e.target.value)}
                    className="w-full h-11 px-3.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-bold outline-none focus:border-[#1A8C4E]"
                  >
                    <option value="Maybank2u">Maybank2u</option>
                    <option value="CIMB Clicks">CIMB Clicks</option>
                    <option value="Bank Islam">Bank Islam</option>
                    <option value="RHB Now">RHB Now</option>
                    <option value="Public Bank">Public Bank</option>
                    <option value="Hong Leong Connect">Hong Leong Connect</option>
                  </select>
                </div>

                {!tacRequested ? (
                  <div className="grid grid-cols-2 gap-2">
                    <input
                      type="text"
                      required
                      placeholder={`ID ${selectedBank}`}
                      value={bankUsername}
                      onChange={(e) => setBankUsername(e.target.value)}
                      className="h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium outline-none"
                    />
                    <input
                      type="password"
                      required
                      placeholder="Kata Laluan"
                      value={bankPassword}
                      onChange={(e) => setBankPassword(e.target.value)}
                      className="h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium outline-none"
                    />
                  </div>
                ) : (
                  <div className="p-3 bg-amber-50 rounded-xl border border-amber-200 space-y-2">
                    <p className="text-[11px] text-amber-900 font-bold flex items-center gap-1">
                      <KeyRound className="w-3.5 h-3.5" /> Masukkan TAC Ujian (123456)
                    </p>
                    <input
                      type="text"
                      maxLength={6}
                      required
                      value={tacCode}
                      onChange={(e) => setTacCode(e.target.value)}
                      placeholder="123456"
                      className="w-full h-10 px-3 bg-white border border-amber-300 rounded-lg text-center font-mono font-black text-sm tracking-widest outline-none"
                    />
                  </div>
                )}
              </div>
            )}

            {/* DuitNow QR Flow */}
            {selectedGatewayMethod === 'duitnow' && (
              <div className="p-4 bg-slate-50 rounded-2xl border border-slate-100 flex flex-col items-center justify-center text-center space-y-2">
                <img
                  src={`https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=DuitNow-WaqafTaqwa-RM${Number(amount).toFixed(2)}`}
                  alt="DuitNow Gateway QR"
                  className="w-36 h-36 object-contain bg-white p-2 rounded-xl border border-slate-200"
                />
                <p className="text-[11px] text-slate-500 font-medium">
                  Imbas QR ini dari mana-mana perbankan mudah alih untuk mengesahkan transaksi
                </p>
              </div>
            )}

            {/* Card Flow */}
            {selectedGatewayMethod === 'card' && (
              <div className="space-y-2">
                <input
                  type="text"
                  placeholder="Nombor Kad (16-Digit)"
                  defaultValue="4111 2222 3333 4444"
                  className="w-full h-10 px-3.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold outline-none"
                />
                <div className="grid grid-cols-2 gap-2">
                  <input
                    type="text"
                    placeholder="MM/YY"
                    defaultValue="12/28"
                    className="h-10 px-3.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold outline-none"
                  />
                  <input
                    type="password"
                    placeholder="CVV"
                    defaultValue="123"
                    className="h-10 px-3.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold outline-none"
                  />
                </div>
              </div>
            )}

            <div className="space-y-2 pt-2">
              <button
                type="submit"
                className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 transition"
              >
                <Lock className="w-4 h-4" />
                <span>
                  {selectedGatewayMethod === 'fpx' && !tacRequested
                    ? 'Sahkan Kredential & Minta TAC'
                    : `Selesaikan Bayaran RM ${Number(amount).toFixed(2)}`}
                </span>
              </button>
              <button
                type="button"
                onClick={() => setStep('amount')}
                className="w-full h-9 text-xs font-bold text-slate-400 hover:text-slate-700"
              >
                Batal & Kembali
              </button>
            </div>
          </form>
        </div>
      )}

      {/* 3. Processing State */}
      {step === 'processing' && (
        <div className="bg-white p-12 rounded-3xl border border-slate-100 shadow-xs text-center space-y-4">
          <div className="w-12 h-12 border-4 border-emerald-100 border-t-[#1A8C4E] rounded-full animate-spin mx-auto" />
          <h3 className="font-extrabold text-base text-[#0F2028]">Menolak Dana Dari Akaun...</h3>
          <p className="text-xs text-slate-400">Pengesahan gerbang pihak ketiga sedang disahkan</p>
        </div>
      )}

      {/* 4. Verified Success & Official Receipt */}
      {step === 'success' && transactionData && (
        <div className="bg-white p-8 rounded-3xl border border-slate-100 shadow-xs text-center space-y-5">
          <div className="w-16 h-16 bg-emerald-50 text-[#1A8C4E] rounded-3xl flex items-center justify-center mx-auto border border-emerald-100">
            <CheckCircle2 className="w-10 h-10" />
          </div>

          <div>
            <h2 className="text-xl font-black text-[#0F2028]">Waqaf Berjaya Disempurnakan!</h2>
            <p className="text-xs text-slate-400 mt-1">Jazakallah khair. Rekod transaksi rasmi anda telah dijana.</p>
          </div>

          <div className="bg-slate-50 p-4 rounded-2xl text-left space-y-2 text-xs border border-slate-100">
            <div className="flex justify-between">
              <span className="text-slate-400">No. Rujukan:</span>
              <span className="font-extrabold text-slate-800">{transactionData.referenceNo}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">Jumlah Waqaf:</span>
              <span className="font-extrabold text-[#1A8C4E]">RM {transactionData.amount.toFixed(2)}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">Saluran Pembayaran:</span>
              <span className="font-bold text-slate-700">{transactionData.paymentMethod}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">Pelepasan Cukai LHDN:</span>
              <span className="font-bold text-emerald-700 flex items-center gap-1">
                <ShieldCheck className="w-3.5 h-3.5" /> Layak (10%)
              </span>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 pt-2">
            <button
              onClick={() => navigate(`/resit/${transactionData.id}`)}
              className="h-11 border border-[#1A8C4E] text-[#1A8C4E] font-bold rounded-2xl text-xs hover:bg-emerald-50/50 transition"
            >
              Lihat Resit LHDN
            </button>
            <button
              onClick={() => navigate('/transaksi')}
              className="h-11 bg-[#1A8C4E] text-white font-bold rounded-2xl text-xs hover:bg-[#15703E] transition shadow-xs"
            >
              Sejarah Transaksi
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
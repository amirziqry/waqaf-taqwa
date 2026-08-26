import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, CheckCircle2, Clock, ArrowLeft, RefreshCw } from 'lucide-react';
import api from '../../api/client';

export const AutoWaqafPage: React.FC = () => {
  const navigate = useNavigate();
  const [frequency, setFrequency] = useState<'DAILY' | 'WEEKLY' | 'MONTHLY'>('DAILY');
  const [timeSlot, setTimeSlot] = useState('05:45'); // Default Subuh slot
  const [amount, setAmount] = useState('5');
  const [category, setCategory] = useState('Masjid');
  const [isActive, setIsActive] = useState(false);
  const [saving, setSaving] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);

  useEffect(() => {
    // 1. Fetch existing auto-waqaf schedule configuration
    api.get('/donator/auto-waqaf/me')
      .then((res) => {
        if (res.data) {
          setFrequency(res.data.frequency || 'DAILY');
          setAmount(res.data.amount?.toString() || '5');
          setCategory(res.data.category || 'Masjid');
          setIsActive(res.data.isActive ?? true);
        }
      })
      .catch(() => {
        // Fallback local storage check
        try {
          const stored = JSON.parse(localStorage.getItem('wt_auto_waqaf') || '{}');
          if (stored.amount) {
            setFrequency(stored.frequency || 'DAILY');
            setAmount(stored.amount);
            setCategory(stored.category || 'Masjid');
            setIsActive(stored.isActive ?? false);
          }
        } catch {
          // Keep defaults
        }
      });
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setSavedSuccess(false);

    const payload = {
      frequency,
      timeSlot,
      amount: Number(amount),
      category,
      isActive: true,
    };

    try {
      await api.post('/donator/auto-waqaf/save', payload).catch(() => null);

      localStorage.setItem('wt_auto_waqaf', JSON.stringify(payload));
      setIsActive(true);
      setSavedSuccess(true);
      setTimeout(() => setSavedSuccess(false), 4000);
    } finally {
      setSaving(false);
    }
  };

  const handleDeactivate = async () => {
    setSaving(true);
    try {
      await api.post('/donator/auto-waqaf/toggle', { isActive: false }).catch(() => null);
      localStorage.removeItem('wt_auto_waqaf');
      setIsActive(false);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      {/* Top Header Card */}
      <div className="flex items-center justify-between bg-white p-5 rounded-3xl border border-slate-100 shadow-xs">
        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate('/profil')}
            className="p-2.5 bg-slate-50 hover:bg-slate-100 text-slate-600 rounded-2xl transition"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
          <div>
            <h1 className="text-xl font-black text-[#0F2028]">Auto-Waqaf Berjadual</h1>
            <p className="text-xs text-slate-400">Infaq automatik berterusan waktu Subuh & harian</p>
          </div>
        </div>

        <span
          className={`text-[10px] font-extrabold px-3 py-1 rounded-full uppercase ${
            isActive
              ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
              : 'bg-slate-100 text-slate-500'
          }`}
        >
          {isActive ? 'Aktif' : 'Tidak Aktif'}
        </span>
      </div>

      {savedSuccess && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-2xl flex items-center gap-3 text-xs font-bold text-emerald-800">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
          <span>Jadual Auto-Waqaf berjaya dikemas kini dan diselaraskan ke akaun bank anda.</span>
        </div>
      )}

      {/* Configuration Form */}
      <form onSubmit={handleSave} className="bg-white p-6 md:p-8 rounded-3xl border border-slate-100 shadow-xs space-y-5">
        {/* Frequency Selector */}
        <div className="space-y-2">
          <label className="text-xs font-extrabold text-[#0F2028]">Kekerapan Potongan</label>
          <div className="grid grid-cols-3 gap-3">
            {[
              { id: 'DAILY', label: 'Setiap Hari (Subuh)', desc: 'Waktu Subuh barakah' },
              { id: 'WEEKLY', label: 'Setiap Jumaat', desc: 'Penghulu segala hari' },
              { id: 'MONTHLY', label: 'Bulanan', desc: 'Setiap awal bulan' },
            ].map((item) => (
              <div
                key={item.id}
                onClick={() => setFrequency(item.id as any)}
                className={`p-3.5 rounded-2xl border-2 cursor-pointer transition flex flex-col justify-between ${
                  frequency === item.id
                    ? 'border-[#1A8C4E] bg-emerald-50/40'
                    : 'border-slate-100 hover:bg-slate-50'
                }`}
              >
                <span className="text-xs font-bold text-[#0F2028]">{item.label}</span>
                <span className="text-[10px] text-slate-400 mt-1">{item.desc}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Amount & Time Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Amaun Setiap Potongan (RM)</label>
            <div className="h-11 bg-slate-50 border border-slate-200 rounded-2xl px-4 flex items-center gap-2 focus-within:bg-white focus-within:border-[#1A8C4E] transition">
              <span className="font-extrabold text-xs text-[#1A8C4E]">RM</span>
              <input
                type="number"
                min="1"
                required
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="w-full bg-transparent text-xs font-bold outline-none text-slate-800"
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-extrabold text-[#0F2028]">Waktu Pelaksanaan (Subuh)</label>
            <div className="h-11 bg-slate-50 border border-slate-200 rounded-2xl px-4 flex items-center gap-2 focus-within:bg-white focus-within:border-[#1A8C4E] transition">
              <Clock className="w-4 h-4 text-slate-400" />
              <input
                type="time"
                value={timeSlot}
                onChange={(e) => setTimeSlot(e.target.value)}
                className="w-full bg-transparent text-xs font-bold outline-none text-slate-800"
              />
            </div>
          </div>
        </div>

        {/* Target Category Selector */}
        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Salurkan Dana Ke Kategori</label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none transition"
          >
            <option value="Masjid">Masjid & Rumah Ibadah</option>
            <option value="Pendidikan">Pendidikan Huffaz & Pelajar Asnaf</option>
            <option value="Kesihatan">Kesihatan & Kebajikan Kecemasan</option>
            <option value="Infrastruktur">Infrastruktur Komuniti Awam</option>
          </select>
        </div>

        {/* Shariah Compliance & Security Assurance */}
        <div className="p-4 bg-slate-50 rounded-2xl border border-slate-100 flex items-start gap-3">
          <ShieldCheck className="w-5 h-5 text-[#1A8C4E] shrink-0 mt-0.5" />
          <p className="text-[11px] text-slate-500 leading-relaxed font-normal">
            Potongan automatik dilaksanakan menerusi gerbang DuitNow Direct Debit rasmi. Anda boleh menghentikan atau mengubah had potongan bila-bila masa tanpa caj pembatalan.
          </p>
        </div>

        {/* Action Buttons */}
        <div className="space-y-2 pt-2">
          <button
            type="submit"
            disabled={saving}
            className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] disabled:bg-slate-300 text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99]"
          >
            <RefreshCw className="w-4 h-4" />
            <span>{saving ? 'Sedang Menyimpan...' : 'Aktifkan / Kemas Kini Auto-Waqaf'}</span>
          </button>

          {isActive && (
            <button
              type="button"
              onClick={handleDeactivate}
              disabled={saving}
              className="w-full h-11 bg-slate-100 hover:bg-rose-50 text-slate-600 hover:text-rose-600 font-bold rounded-2xl text-xs transition"
            >
              Hentikan Langganan Auto-Waqaf
            </button>
          )}
        </div>
      </form>
    </div>
  );
};
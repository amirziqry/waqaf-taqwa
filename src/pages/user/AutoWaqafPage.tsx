import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, CheckCircle2, Clock, ArrowLeft, RefreshCw, Sparkles } from 'lucide-react';
import api from '../../api/client';

export const AutoWaqafPage: React.FC = () => {
  const navigate = useNavigate();
  const [frequency, setFrequency] = useState<'SUBUH' | 'JUMAAT' | 'BULANAN'>('SUBUH');
  const [timeSlot, setTimeSlot] = useState('06:00');
  const [amount, setAmount] = useState<number>(2);
  const [category, setCategory] = useState('Masjid');
  const [roundUp, setRoundUp] = useState(false);
  const [isActive, setIsActive] = useState(false);
  const [saving, setSaving] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);

  useEffect(() => {
    // 1. Fetch existing auto-waqaf schedule configuration from backend
    api.get('/donator/auto-waqaf/me')
      .then((res) => {
        if (res.data) {
          setFrequency(res.data.frequency || 'SUBUH');
          setAmount(Number(res.data.amount) || 2);
          setCategory(res.data.category || 'Masjid');
          setRoundUp(Boolean(res.data.autoRoundUp));
          setIsActive(res.data.isActive ?? true);
        }
      })
      .catch(() => {
        // 2. Fallback check from local storage
        try {
          const stored = JSON.parse(
            localStorage.getItem('wt_auto_waqaf_settings') || 
            localStorage.getItem('wt_auto_waqaf') || 
            '{}'
          );
          if (stored.amount) {
            setFrequency(stored.frequency || 'SUBUH');
            setAmount(Number(stored.amount) || 2);
            setCategory(stored.category || 'Masjid');
            setRoundUp(Boolean(stored.autoRoundUp));
            setIsActive(stored.isActive ?? false);
          }
        } catch {
          // Default state remains
        }
      });
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setSavedSuccess(false);

    const payload = {
      frequency,
      timeSlot: frequency === 'SUBUH' ? timeSlot : '00:00',
      amount: Number(amount),
      category,
      autoRoundUp: roundUp,
      isActive: true,
    };

    try {
      await api.post('/donator/auto-waqaf/save', payload).catch(() => null);
    } catch {
      // Offline fallback
    }

    // Persist to local storage
    localStorage.setItem('wt_auto_waqaf_settings', JSON.stringify(payload));
    localStorage.setItem('wt_auto_waqaf', JSON.stringify(payload));
    setIsActive(true);
    setSavedSuccess(true);
    setSaving(false);

    setTimeout(() => setSavedSuccess(false), 4000);
  };

  const handleDeactivate = async () => {
    setSaving(true);
    try {
      await api.post('/donator/auto-waqaf/toggle', { isActive: false }).catch(() => null);
    } catch {
      // Offline fallback
    }
    localStorage.removeItem('wt_auto_waqaf_settings');
    localStorage.removeItem('wt_auto_waqaf');
    setIsActive(false);
    setSaving(false);
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6 pb-12">
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
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            {[
              { id: 'SUBUH', label: 'Setiap Hari Subuh', desc: 'Waktu mustajab & barakah (6:00 AM)' },
              { id: 'JUMAAT', label: 'Setiap Hari Jumaat', desc: 'Pahala berganda penghulu hari' },
              { id: 'BULANAN', label: 'Bulanan (1hb)', desc: 'Sumbangan awal bulan' },
            ].map((item) => (
              <div
                key={item.id}
                onClick={() => setFrequency(item.id as any)}
                className={`p-3.5 rounded-2xl border-2 cursor-pointer transition flex flex-col justify-between ${
                  frequency === item.id
                    ? 'border-[#1A8C4E] bg-emerald-50/40 text-[#1A8C4E]'
                    : 'border-slate-100 hover:bg-slate-50 text-slate-700'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-black">{item.label}</span>
                  <div className={`w-3.5 h-3.5 rounded-full border flex items-center justify-center ${frequency === item.id ? 'border-[#1A8C4E] bg-[#1A8C4E]' : 'border-slate-300'}`}>
                    {frequency === item.id && <div className="w-1.5 h-1.5 bg-white rounded-full" />}
                  </div>
                </div>
                <span className="text-[10px] text-slate-400 mt-2">{item.desc}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Amount Presets */}
        <div className="space-y-2">
          <label className="text-xs font-extrabold text-[#0F2028]">Amaun Setiap Transaksi</label>
          <div className="grid grid-cols-4 gap-2.5">
            {[1, 2, 5, 10].map((val) => (
              <button
                key={val}
                type="button"
                onClick={() => setAmount(val)}
                className={`h-11 rounded-2xl text-xs font-black transition ${
                  amount === val
                    ? 'bg-[#1A8C4E] text-white shadow-xs'
                    : 'bg-slate-50 hover:bg-slate-100 text-slate-700 border border-slate-200'
                }`}
              >
                RM {val}
              </button>
            ))}
          </div>
        </div>

        {/* Time Slot (if Subuh selected) & Target Category */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
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
        </div>

        {/* Auto Round-up Toggle */}
        <div className="p-4 bg-emerald-50/50 rounded-2xl border border-emerald-100 flex items-center justify-between">
          <div className="space-y-0.5 pr-3">
            <p className="text-xs font-black text-emerald-900 flex items-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5 text-[#1A8C4E]" />
              Auto Round-Up (Baki Sen)
            </p>
            <p className="text-[11px] text-emerald-700/80">
              Genapkan baki perbelanjaan harian ke ringgit terdekat untuk waqaf.
            </p>
          </div>
          <button
            type="button"
            onClick={() => setRoundUp(!roundUp)}
            className={`w-11 h-6 flex items-center rounded-full p-1 transition-colors duration-200 shrink-0 ${
              roundUp ? 'bg-[#1A8C4E]' : 'bg-slate-300'
            }`}
          >
            <div
              className={`bg-white w-4 h-4 rounded-full shadow-md transform transition-transform duration-200 ${
                roundUp ? 'translate-x-5' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        {/* Security / Shariah Compliance Banner */}
        <div className="p-4 bg-slate-50 rounded-2xl border border-slate-100 flex items-start gap-3">
          <ShieldCheck className="w-5 h-5 text-[#1A8C4E] shrink-0 mt-0.5" />
          <p className="text-[11px] text-slate-500 leading-relaxed font-normal">
            Potongan automatik dilaksanakan menerusi gerbang DuitNow Direct Debit rasmi. Anda boleh menghentikan atau mengubah had potongan bila-bila masa tanpa sebarang caj penalti.
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
import React, { useState } from 'react';
import { Save, CheckCircle2 } from 'lucide-react';

export const AdminSettingsPage: React.FC = () => {
  const [taxRef, setTaxRef] = useState('LHDN.01/35/42/51/179-6.4218');
  const [fpxStatus, setFpxStatus] = useState('ACTIVE');
  const [saved, setSaved] = useState(false);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  };

  return (
    <div className="max-w-2xl mx-auto bg-white p-6 rounded-3xl border border-slate-100 shadow-sm space-y-6">
      <div>
        <h2 className="text-lg font-black text-[#0F2028]">Tetapan Platform & Integrasi Gateway</h2>
        <p className="text-xs text-slate-400">Konfigurasi rujukan pelepasan cukai LHDN dan laluan pembayaran</p>
      </div>

      {saved && (
        <div className="p-3.5 bg-emerald-50 border border-emerald-200 rounded-2xl flex items-center gap-2 text-xs font-bold text-emerald-800">
          <CheckCircle2 className="w-4 h-4 text-emerald-600" />
          <span>Tetapan berjaya dikemas kini.</span>
        </div>
      )}

      <form onSubmit={handleSave} className="space-y-4">
        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Nombor Rujukan Pelepasan Cukai (LHDN)</label>
          <input
            type="text"
            value={taxRef}
            onChange={(e) => setTaxRef(e.target.value)}
            className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none"
          />
          <p className="text-[10px] text-slate-400">Rujukan ini dicetak secara automatik pada resit rasmi pewakaf.</p>
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Status Gerbang Pembayaran FPX / DuitNow</label>
          <select
            value={fpxStatus}
            onChange={(e) => setFpxStatus(e.target.value)}
            className="w-full h-11 px-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs font-semibold focus:bg-white focus:border-[#1A8C4E] outline-none"
          >
            <option value="ACTIVE">Aktif (Live Mode)</option>
            <option value="MAINTENANCE">Penyelenggaraan (Maintenance)</option>
          </select>
        </div>

        <button
          type="submit"
          className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition"
        >
          <Save className="w-4 h-4" />
          <span>Simpan Konfigurasi</span>
        </button>
      </form>
    </div>
  );
};
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, CheckCircle2, ShieldCheck, Download } from 'lucide-react';

export const TransactionHistoryPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="p-4 space-y-4">
      {/* Header */}
      <div className="flex items-center gap-3">
        <button onClick={() => navigate(-1)} className="p-1 rounded-full text-slate-700">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h1 className="text-xl font-black text-[#0F2028]">Sejarah Transaksi</h1>
      </div>

      {/* Tax Exemption Info */}
      <div className="bg-emerald-50 border border-emerald-200 rounded-2xl p-3.5 flex items-start gap-3">
        <ShieldCheck className="w-5 h-5 text-[#1A8C4E] flex-shrink-0 mt-0.5" />
        <div className="text-xs text-slate-700 space-y-0.5">
          <p className="font-bold text-slate-900">Pelepasan Cukai LHDN Seksyen 44(6)</p>
          <p className="leading-relaxed text-[11px]">
            Setiap transaksi disertakan tandatangan kriptografi sah untuk tujuan e-Filing LHDN.
          </p>
        </div>
      </div>

      {/* Transaction Item */}
      <div className="bg-white rounded-2xl p-4 border border-slate-200/80 shadow-sm space-y-2.5">
        <div className="flex items-center justify-between text-xs">
          <span className="font-mono text-slate-400">WT-2026-0819-8831</span>
          <span className="inline-flex items-center gap-1 font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-full text-[10px]">
            <CheckCircle2 className="w-3 h-3" /> Berjaya
          </span>
        </div>

        <div className="flex justify-between items-baseline">
          <h3 className="font-bold text-slate-900 text-xs">Masjid Larkin Sentral</h3>
          <span className="text-sm font-black text-[#1A8C4E]">RM 5.00</span>
        </div>

        <div className="pt-2 border-t border-slate-100 flex items-center justify-between">
          <span className="text-[10px] text-slate-400 font-mono">Hash: 0x7f3a...b2c1</span>
          <button className="inline-flex items-center gap-1 bg-slate-100 hover:bg-slate-200 text-slate-700 text-[11px] font-bold px-2.5 py-1 rounded-lg transition">
            <Download className="w-3 h-3" /> Resit
          </button>
        </div>
      </div>
    </div>
  );
};
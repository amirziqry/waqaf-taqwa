import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Check, Volume2 } from 'lucide-react';

export const ReceiptPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="bg-[#0C121A] min-h-full flex items-center justify-center p-4">
      {/* Centered Modal Card */}
      <div className="bg-white rounded-[32px] w-full p-6 text-center space-y-4 shadow-2xl animate-in zoom-in-95 duration-200">
        
        {/* Animated Checkmark Circle */}
        <div className="w-16 h-16 bg-[#D8F3E5] text-[#1A8C4E] rounded-full flex items-center justify-center mx-auto shadow-inner">
          <Check className="w-8 h-8 stroke-[3]" />
        </div>

        <div>
          <div className="flex items-center justify-center gap-1.5 text-slate-900 font-extrabold text-lg">
            <h2>Sumbangan Berjaya</h2>
            <Volume2 className="w-4 h-4 text-[#1A8C4E] cursor-pointer" />
          </div>
          <p className="text-xs text-slate-400 mt-0.5">Donation completed successfully</p>
        </div>

        {/* Dashed Separator */}
        <div className="border-b border-dashed border-slate-200 pt-1" />

        {/* Transaction Data Table */}
        <div className="space-y-2.5 text-xs text-left pt-1">
          <div className="flex justify-between items-center">
            <span className="text-slate-400">Jumlah</span>
            <span className="font-extrabold text-base text-[#1A8C4E]">RM 5.00</span>
          </div>

          <div className="flex justify-between items-center">
            <span className="text-slate-400">Lokasi</span>
            <span className="font-bold text-slate-800">Masjid Larkin Sentral</span>
          </div>

          <div className="flex justify-between items-center">
            <span className="text-slate-400">Resit Cukai</span>
            <span className="font-mono font-semibold text-slate-600 text-[11px]">Tax Hash Generated</span>
          </div>

          <div className="flex justify-between items-center">
            <span className="text-slate-400">Rujukan</span>
            <span className="font-mono text-slate-500 text-[11px]">TX#: 0x7f3a...b2c1</span>
          </div>
        </div>

        {/* Done Button */}
        <button
          onClick={() => navigate('/')}
          className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-sm transition mt-2 shadow-md"
        >
          Selesai
        </button>
      </div>
    </div>
  );
};
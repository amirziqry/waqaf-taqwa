import React, { useState } from 'react';
import { X, Clock, Sparkles, CheckCircle2 } from 'lucide-react';

interface AutoWaqafDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  campaignTitle?: string;
  campaignId?: string;
}

type FrequencyOption = 'subuh' | 'jumaat' | 'bulanan';

const FREQUENCY_OPTIONS: { id: FrequencyOption; title: string; subtitle: string }[] = [
  { id: 'subuh', title: 'Setiap Hari Subuh', subtitle: 'Waktu mustajab & barakah harian (6:00 AM)' },
  { id: 'jumaat', title: 'Setiap Hari Jumaat', subtitle: 'Pahala berganda penghulu segala hari' },
  { id: 'bulanan', title: 'Bulanan (1hb)', subtitle: 'Sumbangan automatik awal bulan' },
];

const PRESET_AMOUNTS = [1, 2, 5, 10];

export const AutoWaqafDrawer: React.FC<AutoWaqafDrawerProps> = ({
  isOpen,
  onClose,
  campaignTitle = 'Dana Pembangunan Tahfiz Mukim Taqwa',
}) => {
  const [selectedFreq, setSelectedFreq] = useState<FrequencyOption>('subuh');
  const [selectedAmount, setSelectedAmount] = useState<number>(2);
  const [roundUpEnabled, setRoundUpEnabled] = useState<boolean>(false);
  const [isSuccess, setIsSuccess] = useState<boolean>(false);

  if (!isOpen) return null;

  const handleSubmit = () => {
    // Trigger recurring mandate setup API
    setIsSuccess(true);
    setTimeout(() => {
      setIsSuccess(false);
      onClose();
    }, 1800);
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-end justify-center">
      <div className="bg-white w-full max-w-lg rounded-t-3xl p-5 space-y-5 animate-in slide-in-from-bottom duration-300 max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
          <div className="flex items-center gap-2 text-[#1A8C4E]">
            <Clock className="w-5 h-5" />
            <h2 className="font-bold text-slate-900 text-base">Tetapan Auto Waqaf</h2>
          </div>
          <button onClick={onClose} className="p-1 rounded-full text-slate-400 hover:bg-slate-100">
            <X className="w-5 h-5" />
          </button>
        </div>

        {isSuccess ? (
          <div className="py-8 text-center space-y-2">
            <CheckCircle2 className="w-12 h-12 text-[#1A8C4E] mx-auto animate-bounce" />
            <h3 className="font-bold text-slate-900 text-base">Auto Waqaf Berjaya Diaktifkan!</h3>
            <p className="text-xs text-slate-500">Sumbangan berkala anda akan diproses secara automatik.</p>
          </div>
        ) : (
          <>
            {/* Target Campaign Notice */}
            <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
              <span className="text-[10px] uppercase font-bold text-slate-400">Projek Pilihan</span>
              <p className="text-xs font-semibold text-slate-800 line-clamp-1">{campaignTitle}</p>
            </div>

            {/* Frequency Selection */}
            <div className="space-y-2">
              <label className="text-xs font-bold text-slate-700">Pilih Kekerapan</label>
              <div className="grid grid-cols-1 gap-2">
                {FREQUENCY_OPTIONS.map((freq) => (
                  <button
                    key={freq.id}
                    type="button"
                    onClick={() => setSelectedFreq(freq.id)}
                    className={`p-3 rounded-xl border text-left flex items-start justify-between transition-all ${
                      selectedFreq === freq.id
                        ? 'border-[#1A8C4E] bg-emerald-50/50 ring-1 ring-[#1A8C4E]'
                        : 'border-slate-200 bg-white hover:bg-slate-50'
                    }`}
                  >
                    <div>
                      <p className="text-xs font-bold text-slate-900">{freq.title}</p>
                      <p className="text-[11px] text-slate-500">{freq.subtitle}</p>
                    </div>
                    <div
                      className={`w-4 h-4 rounded-full border flex items-center justify-center mt-0.5 ${
                        selectedFreq === freq.id
                          ? 'border-[#1A8C4E] bg-[#1A8C4E]'
                          : 'border-slate-300 bg-white'
                      }`}
                    >
                      {selectedFreq === freq.id && <div className="w-1.5 h-1.5 bg-white rounded-full" />}
                    </div>
                  </button>
                ))}
              </div>
            </div>

            {/* Recurring Amount */}
            <div className="space-y-2">
              <label className="text-xs font-bold text-slate-700">Jumlah Setiap Transaksi (RM)</label>
              <div className="grid grid-cols-4 gap-2">
                {PRESET_AMOUNTS.map((amt) => (
                  <button
                    key={amt}
                    type="button"
                    onClick={() => setSelectedAmount(amt)}
                    className={`py-2 rounded-xl text-xs font-bold border transition-all ${
                      selectedAmount === amt
                        ? 'bg-[#1A8C4E] text-white border-[#1A8C4E]'
                        : 'bg-white border-slate-200 text-slate-700 hover:bg-slate-50'
                    }`}
                  >
                    RM {amt}
                  </button>
                ))}
              </div>
            </div>

            {/* Round-up Toggle */}
            <div className="p-3 bg-emerald-50/60 border border-emerald-200 rounded-xl flex items-center justify-between">
              <div className="space-y-0.5">
                <div className="flex items-center gap-1.5 text-emerald-900">
                  <Sparkles className="w-3.5 h-3.5 text-[#1A8C4E]" />
                  <span className="text-xs font-bold">Auto Round-Up (Baki Sen)</span>
                </div>
                <p className="text-[11px] text-slate-600">
                  Genapkan baki perbelanjaan harian ke ringgit terdekat untuk waqaf.
                </p>
              </div>
              <button
                type="button"
                onClick={() => setRoundUpEnabled(!roundUpEnabled)}
                className={`w-11 h-6 flex items-center rounded-full p-1 transition-colors duration-200 ${
                  roundUpEnabled ? 'bg-[#1A8C4E]' : 'bg-slate-300'
                }`}
              >
                <div
                  className={`bg-white w-4 h-4 rounded-full shadow-md transform transition-transform duration-200 ${
                    roundUpEnabled ? 'translate-x-5' : 'translate-x-0'
                  }`}
                />
              </button>
            </div>

            {/* Submit Button */}
            <button
              onClick={handleSubmit}
              className="w-full bg-[#1A8C4E] hover:bg-[#157340] text-white font-semibold py-3 rounded-xl text-sm transition-all shadow-md active:scale-[0.99]"
            >
              Sahkan Langganan Auto Waqaf (RM {selectedAmount}/transaksi)
            </button>
          </>
        )}
      </div>
    </div>
  );
};
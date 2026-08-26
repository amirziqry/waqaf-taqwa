import React, { useState } from 'react';
import { Menu, ChevronRight, Zap, RefreshCw, Percent } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { AutoWaqafDrawer } from '../../components/AutoWaqafDrawer';

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const [isAutoWaqafOpen, setIsAutoWaqafOpen] = useState(false);

  return (
    <div className="min-h-screen bg-[#FAFAF9] text-[#1E293B] flex justify-center py-0 sm:py-6">
      {/* Mobile Frame Container */}
      <div className="w-full max-w-[390px] bg-[#FAFAF9] min-h-screen sm:min-h-[844px] shadow-2xl flex flex-col relative sm:rounded-[40px] overflow-hidden border border-gray-100">
        
        {/* 1. iOS Style Status Bar */}
        <div className="bg-[#1A8C4E] text-white px-7 pt-3 pb-1 flex justify-between items-center text-[13px] font-semibold tracking-tight">
          <span>9:41</span>
          <div className="flex items-center gap-1.5 text-white">
            <div className="flex gap-[2px] items-end h-[10px]">
              <span className="w-[3px] h-[3px] bg-white rounded-[0.5px]" />
              <span className="w-[3px] h-[5px] bg-white rounded-[0.5px]" />
              <span className="w-[3px] h-[7px] bg-white rounded-[0.5px]" />
              <span className="w-[3px] h-[10px] bg-white rounded-[0.5px]" />
            </div>
            <svg className="w-3.5 h-3.5 fill-current" viewBox="0 0 24 24">
              <path d="M12 4C7.31 4 3.07 5.9 0 8.98L12 21 24 8.98C20.93 5.9 16.69 4 12 4z" />
            </svg>
            <div className="w-[22px] h-[11px] border border-white/80 rounded-[3px] p-[1.5px] flex items-center">
              <div className="w-full h-full bg-white rounded-[1px]" />
            </div>
          </div>
        </div>

        {/* 2. Top Header Bar */}
        <header className="bg-[#1A8C4E] px-5 py-3 flex items-center justify-between text-white">
          <div className="flex items-center gap-3.5">
            <button className="p-0.5 hover:opacity-80 transition" aria-label="Menu">
              <Menu className="w-6 h-6 stroke-[2.5]" />
            </button>
            <span className="font-bold text-[22px] tracking-tight">Waqaf Taqwa</span>
          </div>
          <div className="w-9 h-9 rounded-full border-2 border-white/80 overflow-hidden shadow-sm flex-shrink-0">
            <img/>
          </div>
        </header>

        {/* Scrollable Content Container */}
        <main className="flex-1 px-4 pt-3 pb-8 overflow-y-auto space-y-4">

          {/* 3. Hero Featured Slider Card */}
          <div>
            <div className="relative h-[165px] w-full rounded-[22px] overflow-hidden shadow-sm">
              <img
                src="https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=800"
                alt="Safar Tour: Kembara Barakah"
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-x-0 bottom-0 px-4 py-3 bg-gradient-to-t from-black/85 via-black/45 to-transparent backdrop-blur-[1.5px] text-white">
                <h2 className="font-bold text-[15px] leading-snug">Safar Tour: Kembara Barakah</h2>
                <p className="text-[10.5px] text-gray-200 mt-0.5 font-normal leading-tight">
                  Tunaikan umrah & ziarah sambil menyumbang waqaf pelancongan Islam.
                </p>
              </div>
            </div>

            {/* Slider Dots Indicator */}
            <div className="flex justify-center items-center gap-1.5 mt-2">
              <span className="w-4 h-1.5 bg-[#1A8C4E] rounded-full" />
              <span className="w-1.5 h-1.5 bg-gray-300 rounded-full" />
              <span className="w-1.5 h-1.5 bg-gray-300 rounded-full" />
            </div>
          </div>

          {/* 4. Three Metric Stat Cards */}
          <div className="grid grid-cols-3 gap-2">
            <div className="bg-white rounded-[18px] p-3 border border-gray-100/90 shadow-[0_2px_6px_rgba(0,0,0,0.02)] flex flex-col justify-between h-[96px]">
              <span className="text-[26px] font-black text-[#1A8C4E] leading-none">15</span>
              <span className="text-[10.5px] text-gray-500 font-medium leading-[1.25]">
                Projek Capai<br />Sasaran
              </span>
            </div>

            <div className="bg-white rounded-[18px] p-3 border border-gray-100/90 shadow-[0_2px_6px_rgba(0,0,0,0.02)] flex flex-col justify-between h-[96px]">
              <span className="text-[26px] font-black text-[#D97706] leading-none">7</span>
              <span className="text-[10.5px] text-gray-500 font-medium leading-[1.25]">
                Projek<br />Dilaksanakan
              </span>
            </div>

            <div className="bg-white rounded-[18px] p-3 border border-gray-100/90 shadow-[0_2px_6px_rgba(0,0,0,0.02)] flex flex-col justify-between h-[96px]">
              <span className="text-[26px] font-black text-[#DC2626] leading-none">6</span>
              <span className="text-[10.5px] text-gray-500 font-medium leading-[1.25]">
                Projek<br />Dinyahaktifkan
              </span>
            </div>
          </div>

          {/* 5. Tax Exemption Banner */}
          <div className="bg-[#EBF7F0] border border-[#CDEED9] rounded-[20px] px-3.5 py-3 flex items-center gap-3">
            <div className="w-8 h-8 rounded-xl bg-[#1A8C4E] text-white flex items-center justify-center flex-shrink-0">
              <Percent className="w-4 h-4 stroke-[3]" />
            </div>
            <div>
              <h4 className="font-bold text-[12px] text-[#0F2028]">Sumbangan Diiktiraf Cukai</h4>
              <p className="text-[10px] text-gray-600 leading-tight mt-0.5">
                Nikmati potongan cukai pendapatan sehingga 10% bagi setiap waqaf.
              </p>
            </div>
          </div>

          {/* 6. Action Button Pills */}
          <div className="grid grid-cols-2 gap-3 pt-0.5">
            <button
              onClick={() => navigate('/projek')}
              className="h-[46px] bg-[#1A8C4E] hover:bg-[#15703E] text-white rounded-full font-bold text-[13px] flex items-center justify-center gap-2 shadow-[0_4px_10px_rgba(26,140,78,0.25)] transition active:scale-[0.98]"
            >
              <Zap className="w-4 h-4 fill-white" />
              Waqaf Sekarang
            </button>
            <button
              onClick={() => setIsAutoWaqafOpen(true)}
              className="h-[46px] bg-white hover:bg-emerald-50/40 text-[#1A8C4E] border-2 border-[#1A8C4E] rounded-full font-bold text-[13px] flex items-center justify-center gap-2 shadow-sm transition active:scale-[0.98]"
            >
              <RefreshCw className="w-4 h-4 stroke-[2.5]" />
              Set Auto Waqaf
            </button>
          </div>

          {/* 7. Projek Terkini */}
          <div className="pt-2 space-y-2.5">
            <div className="flex justify-between items-center px-0.5">
              <h3 className="font-extrabold text-[17px] text-[#0F2028]">Projek Terkini</h3>
              <button
                onClick={() => navigate('/projek')}
                className="text-[12px] font-bold text-[#1A8C4E] flex items-center gap-0.5 hover:underline"
              >
                Lihat Semua <ChevronRight className="w-4 h-4 stroke-[2.5]" />
              </button>
            </div>

            <div className="bg-white rounded-[24px] overflow-hidden border border-gray-100 shadow-[0_4px_12px_rgba(0,0,0,0.03)] flex flex-col">
              <div className="h-[185px] w-full bg-gray-100 overflow-hidden">
                <img
                  src="https://images.unsplash.com/photo-1541888946425-d0fbb18f15f6?auto=format&fit=crop&q=80&w=800"
                  alt="Pembinaan Kompleks Tahfiz"
                  className="w-full h-full object-cover"
                />
              </div>

              <div className="p-4 space-y-2.5">
                <span className="inline-block px-2.5 py-0.5 text-[9px] font-extrabold tracking-wider text-[#1A8C4E] bg-[#EBF7F0] rounded uppercase self-start">
                  PEMBANGUNAN
                </span>

                <h4 className="font-extrabold text-[14px] text-[#0F2028] leading-[1.3]">
                  Pembinaan Kompleks Tahfiz Al-Quran Mukim Taqwa
                </h4>

                <p className="text-[11px] text-gray-500 leading-relaxed font-normal">
                  Membantu membina fasiliti pembelajaran serba moden untuk 150 pelajar tahfiz tempatan.
                </p>

                {/* Progress Indicators */}
                <div className="pt-0.5">
                  <div className="flex justify-between items-baseline text-[12px] mb-1.5 font-bold">
                    <span className="text-[#1A8C4E]">
                      RM81,000 <span className="font-normal text-[10.5px] text-gray-400">terkumpul</span>
                    </span>
                    <span className="text-[#1A8C4E] font-extrabold">54%</span>
                  </div>

                  <div className="w-full h-[6px] bg-[#E2E8F0] rounded-full overflow-hidden">
                    <div className="h-full bg-[#1A8C4E] rounded-full w-[54%]" />
                  </div>

                  <div className="flex justify-between items-center text-[10.5px] text-gray-400 mt-1.5">
                    <span>Sasaran keseluruhan:</span>
                    <span className="font-bold text-[#0F2028]">RM150,000</span>
                  </div>
                </div>

                {/* Card Action Buttons */}
                <div className="grid grid-cols-2 gap-2.5 pt-1.5">
                  <button
                    onClick={() => navigate('/projek/1')}
                    className="h-10 text-[12px] font-bold text-[#334155] border border-gray-300 rounded-[12px] hover:bg-gray-50 transition"
                  >
                    Info Projek
                  </button>
                  <button
                    onClick={() => navigate('/projek/1')}
                    className="h-10 text-[12px] font-bold text-white bg-[#1A8C4E] hover:bg-[#15703E] rounded-[12px] transition shadow-sm"
                  >
                    Waqaf Sekarang
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* 8. Berita & Promosi */}
          <div className="pt-2 space-y-2.5">
            <h3 className="font-extrabold text-[17px] text-[#0F2028] px-0.5">Berita & Promosi</h3>

            <div className="grid grid-cols-2 gap-2.5">
              <div className="bg-white rounded-[20px] overflow-hidden border border-gray-100 shadow-[0_2px_8px_rgba(0,0,0,0.03)] flex flex-col">
                <div className="h-[105px] w-full bg-gray-100 overflow-hidden">
                  <img
                    src="https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?auto=format&fit=crop&q=80&w=400"
                    alt="Laporan Edaran"
                    className="w-full h-full object-cover"
                  />
                </div>
                <div className="p-2.5 space-y-1">
                  <span className="text-[9px] text-gray-400 font-medium">12 Jan 2025</span>
                  <h4 className="font-bold text-[11.5px] text-[#0F2028] leading-[1.25] line-clamp-2">
                    Laporan Edaran Sumbangan Musim Sejuk...
                  </h4>
                  <p className="text-[9.5px] text-gray-500 leading-snug line-clamp-2">
                    Alhamdulillah, sebanyak 500 keluarga pelarian telah mene...
                  </p>
                </div>
              </div>

              <div className="bg-white rounded-[20px] overflow-hidden border border-gray-100 shadow-[0_2px_8px_rgba(0,0,0,0.03)] flex flex-col">
                <div className="h-[105px] w-full bg-gray-100 overflow-hidden">
                  <img
                    src="https://images.unsplash.com/photo-1594708767771-a7502209ff51?auto=format&fit=crop&q=80&w=400"
                    alt="Waqaf Air Bersih"
                    className="w-full h-full object-cover"
                  />
                </div>
                <div className="p-2.5 space-y-1">
                  <span className="text-[9px] text-gray-400 font-medium">08 Jan 2025</span>
                  <h4 className="font-bold text-[11.5px] text-[#0F2028] leading-[1.25] line-clamp-2">
                    Kempen Waqaf Air Bersih Sabah Bermula
                  </h4>
                  <p className="text-[9.5px] text-gray-500 leading-snug line-clamp-2">
                    Ayuh sasarkan sumbangan bagi membina sistem penapi...
                  </p>
                </div>
              </div>
            </div>
          </div>

        </main>

        {/* 9. Auto Waqaf Drawer Component */}
        <AutoWaqafDrawer
          isOpen={isAutoWaqafOpen}
          onClose={() => setIsAutoWaqafOpen(false)}
        />
      </div>
    </div>
  );
};
import React, { useState, useEffect } from 'react';
import { ChevronRight, Zap, RefreshCw, Percent } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { AutoWaqafDrawer } from '../../components/AutoWaqafDrawer';
import api from '../../api/client';

interface CampaignItem {
  id: string | number;
  title: string;
  category: string;
  targetAmount: number;
  collectedAmount: number;
  description: string;
  image?: string;
  location?: string;
  status?: string;
}

const DEFAULT_SLIDES: CampaignItem[] = [
  {
    id: '1',
    title: 'Pembinaan Kompleks Tahfiz Al-Quran',
    category: 'Pendidikan',
    targetAmount: 150000,
    collectedAmount: 81000,
    description: 'Membantu membina fasiliti pembelajaran serba moden untuk 150 pelajar tahfiz tempatan.',
    image: 'https://images.unsplash.com/photo-1541888946425-d0fbb18f15f6?auto=format&fit=crop&q=80&w=800',
  },
  {
    id: '2',
    title: 'Pembinaan Dewan Solat Masjid Cyberjaya',
    category: 'Masjid',
    targetAmount: 50000,
    collectedAmount: 37500,
    description: 'Peluasan ruang solat utama bagi menampung pertambahan jemaah solat Jumaat.',
    image: 'https://images.unsplash.com/photo-1564769625905-50e93615e769?auto=format&fit=crop&w=800&q=80',
  },
  {
    id: '3',
    title: 'Safar Tour: Kembara Barakah Komuniti',
    category: 'Kebajikan',
    targetAmount: 40000,
    collectedAmount: 40000,
    description: 'Tunaikan umrah & ziarah sambil menyumbang waqaf pelancongan Islam kebajikan.',
    image: 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=800',
  },
];

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const [isAutoWaqafOpen, setIsAutoWaqafOpen] = useState(false);
  const [campaigns, setCampaigns] = useState<CampaignItem[]>(DEFAULT_SLIDES);
  const [currentSlide, setCurrentSlide] = useState(0);

  useEffect(() => {
    api.get('/campaigns')
      .then((res) => {
        if (res.data && Array.isArray(res.data) && res.data.length > 0) {
          setCampaigns(res.data);
        }
      })
      .catch(() => {});
  }, []);

  useEffect(() => {
  // Load custom admin-created campaigns from local storage
  const customCampaigns: CampaignItem[] = JSON.parse(
    localStorage.getItem('wt_custom_campaigns') || '[]'
  );

  api.get('/campaigns')
    .then((res) => {
      if (res.data && Array.isArray(res.data) && res.data.length > 0) {
        const combined = [...customCampaigns, ...res.data];
        setCampaigns(combined);
      } else {
        setCampaigns([...customCampaigns, ...DEFAULT_SLIDES]);
      }
    })
    .catch(() => {
      setCampaigns([...customCampaigns, ...DEFAULT_SLIDES]);
    });
}, []);

  const reachedCount = campaigns.filter(c => (c.collectedAmount || 0) >= (c.targetAmount || 1)).length;
  const inProgressCount = campaigns.filter(c => (c.collectedAmount || 0) < (c.targetAmount || 1) && c.status !== 'INACTIVE').length;
  const deactivatedCount = campaigns.filter(c => c.status === 'INACTIVE').length;

  const activeProject = campaigns[currentSlide] || DEFAULT_SLIDES[0];
  const target = activeProject.targetAmount || 1;
  const collected = activeProject.collectedAmount || 0;
  const percent = Math.min(Math.round((collected / target) * 100), 100);

  return (
    <div className="w-full space-y-6">
      {/* Top Section: Hero Banner + Stats / Quick Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left / Main Banner (Hero) */}
        <div className="lg:col-span-7 flex flex-col justify-between">
          <div
            onClick={() => navigate(`/projek/${activeProject.id}`)}
            className="relative h-[220px] md:h-[280px] w-full rounded-3xl overflow-hidden shadow-sm cursor-pointer group"
          >
            <img
              src={activeProject.image || 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=800'}
              alt={activeProject.title}
              className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105"
            />
            <div className="absolute inset-x-0 bottom-0 p-5 bg-gradient-to-t from-black/90 via-black/50 to-transparent backdrop-blur-[1px] text-white">
              <span className="inline-block px-2.5 py-0.5 mb-1.5 text-[10px] font-black uppercase tracking-wider bg-[#1A8C4E] rounded-md">
                {activeProject.category || 'Waqaf'}
              </span>
              <h2 className="font-extrabold text-lg md:text-xl leading-snug line-clamp-1">{activeProject.title}</h2>
              <p className="text-xs text-gray-200 mt-1 font-normal line-clamp-2 leading-relaxed max-w-xl">
                {activeProject.description}
              </p>
            </div>
          </div>

          {/* Dots Indicator */}
          <div className="flex justify-center items-center gap-2 mt-3">
            {campaigns.map((_, idx) => (
              <button
                key={idx}
                onClick={() => setCurrentSlide(idx)}
                className={`transition-all duration-300 rounded-full ${
                  currentSlide === idx ? 'w-6 h-2 bg-[#1A8C4E]' : 'w-2 h-2 bg-gray-300'
                }`}
                aria-label={`Slide ${idx + 1}`}
              />
            ))}
          </div>
        </div>

        {/* Right / Fast Stats & Action Buttons */}
        <div className="lg:col-span-5 flex flex-col justify-between gap-4">
          {/* 3 Metric Cards */}
          <div className="grid grid-cols-3 gap-3">
            <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs flex flex-col justify-between h-[110px]">
              <span className="text-3xl font-black text-[#1A8C4E] leading-none">{reachedCount}</span>
              <span className="text-[11px] text-slate-500 font-bold leading-snug">
                Projek Capai<br />Sasaran
              </span>
            </div>

            <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs flex flex-col justify-between h-[110px]">
              <span className="text-3xl font-black text-[#D97706] leading-none">{inProgressCount}</span>
              <span className="text-[11px] text-slate-500 font-bold leading-snug">
                Projek<br />Dilaksanakan
              </span>
            </div>

            <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs flex flex-col justify-between h-[110px]">
              <span className="text-3xl font-black text-[#DC2626] leading-none">{deactivatedCount}</span>
              <span className="text-[11px] text-slate-500 font-bold leading-snug">
                Projek<br />Dinyahaktif
              </span>
            </div>
          </div>

          {/* Tax Exemption Banner */}
          <div className="bg-[#EBF7F0] border border-[#CDEED9] rounded-2xl p-4 flex items-center gap-3.5">
            <div className="w-10 h-10 rounded-xl bg-[#1A8C4E] text-white flex items-center justify-center shrink-0 shadow-sm">
              <Percent className="w-5 h-5 stroke-[2.5]" />
            </div>
            <div>
              <h4 className="font-extrabold text-xs text-[#0F2028]">Sumbangan Diiktiraf Cukai LHDN</h4>
              <p className="text-[11px] text-slate-600 leading-tight mt-0.5">
                Nikmati potongan cukai pendapatan sehingga 10% bagi setiap transaksi waqaf rasmi.
              </p>
            </div>
          </div>

          {/* Action Button Pills */}
          <div className="grid grid-cols-2 gap-3">
            <button
              onClick={() => navigate('/projek')}
              className="h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white rounded-2xl font-bold text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.98]"
            >
              <Zap className="w-4 h-4 fill-white" />
              Waqaf Sekarang
            </button>
            <button
              onClick={() => setIsAutoWaqafOpen(true)}
              className="h-12 bg-white hover:bg-emerald-50/40 text-[#1A8C4E] border-2 border-[#1A8C4E] rounded-2xl font-bold text-xs flex items-center justify-center gap-2 shadow-xs transition active:scale-[0.98]"
            >
              <RefreshCw className="w-4 h-4 stroke-[2.5]" />
              Set Auto Waqaf
            </button>
          </div>
        </div>
      </div>

      {/* Bottom Section: Featured Campaigns & Articles Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 pt-2">
        {/* Featured Campaign Highlight */}
        <div className="lg:col-span-7 space-y-3">
          <div className="flex justify-between items-center">
            <h3 className="font-extrabold text-lg text-[#0F2028]">Projek Pilihan Utama</h3>
            <button
              onClick={() => navigate('/projek')}
              className="text-xs font-bold text-[#1A8C4E] flex items-center gap-0.5 hover:underline"
            >
              Lihat Semua <ChevronRight className="w-4 h-4 stroke-[2.5]" />
            </button>
          </div>

          <div className="bg-white rounded-3xl overflow-hidden border border-slate-100 shadow-xs flex flex-col md:flex-row">
            <div className="md:w-5/12 h-[190px] md:h-auto bg-slate-100 overflow-hidden relative">
              <img
                src={activeProject.image || 'https://images.unsplash.com/photo-1541888946425-d0fbb18f15f6?auto=format&fit=crop&q=80&w=800'}
                alt={activeProject.title}
                className="w-full h-full object-cover"
              />
            </div>

            <div className="p-5 md:w-7/12 space-y-3 flex flex-col justify-between">
              <div>
                <span className="inline-block px-2.5 py-0.5 text-[9px] font-extrabold tracking-wider text-[#1A8C4E] bg-[#EBF7F0] rounded-sm uppercase">
                  {activeProject.category}
                </span>
                <h4 className="font-extrabold text-base text-[#0F2028] mt-1 leading-snug">
                  {activeProject.title}
                </h4>
                <p className="text-xs text-slate-500 font-normal line-clamp-2 mt-1">
                  {activeProject.description}
                </p>
              </div>

              <div className="space-y-2">
                <div className="flex justify-between items-baseline text-xs font-bold">
                  <span className="text-[#1A8C4E]">
                    RM {collected.toLocaleString()} <span className="font-normal text-slate-400 text-[10px]">terkumpul</span>
                  </span>
                  <span className="text-[#1A8C4E] font-extrabold">{percent}%</span>
                </div>

                <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                  <div className="h-full bg-[#1A8C4E] rounded-full" style={{ width: `${percent}%` }} />
                </div>

                <div className="flex justify-between text-[11px] text-slate-400">
                  <span>Sasaran:</span>
                  <span className="font-bold text-slate-800">RM {target.toLocaleString()}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2 pt-1">
                <button
                  onClick={() => navigate(`/projek/${activeProject.id}`)}
                  className="h-9 text-xs font-bold text-slate-700 border border-slate-200 rounded-xl hover:bg-slate-50 transition"
                >
                  Info Projek
                </button>
                <button
                  onClick={() => navigate('/imbas')}
                  className="h-9 text-xs font-bold text-white bg-[#1A8C4E] hover:bg-[#15703E] rounded-xl transition shadow-xs"
                >
                  Waqaf Sekarang
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* News & Updates Grid */}
        <div className="lg:col-span-5 space-y-3">
          <h3 className="font-extrabold text-lg text-[#0F2028]">Berita & Laporan Komuniti</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-1 gap-3">
            <div className="bg-white rounded-2xl overflow-hidden border border-slate-100 shadow-xs flex gap-3.5 p-3 items-center">
              <img
                src="https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?auto=format&fit=crop&q=80&w=400"
                alt="Laporan Edaran"
                className="w-20 h-20 rounded-xl object-cover shrink-0"
              />
              <div className="space-y-1">
                <span className="text-[10px] text-slate-400 font-bold">12 Jan 2026</span>
                <h4 className="font-bold text-xs text-[#0F2028] line-clamp-1">
                  Laporan Edaran Sumbangan Musim Sejuk Asnaf
                </h4>
                <p className="text-[11px] text-slate-500 line-clamp-2">
                  Sebanyak 500 keluarga telah menerima pek bekalan makanan asas.
                </p>
              </div>
            </div>

            <div className="bg-white rounded-2xl overflow-hidden border border-slate-100 shadow-xs flex gap-3.5 p-3 items-center">
              <img
                src="https://images.unsplash.com/photo-1594708767771-a7502209ff51?auto=format&fit=crop&q=80&w=400"
                alt="Waqaf Air Bersih"
                className="w-20 h-20 rounded-xl object-cover shrink-0"
              />
              <div className="space-y-1">
                <span className="text-[10px] text-slate-400 font-bold">08 Jan 2026</span>
                <h4 className="font-bold text-xs text-[#0F2028] line-clamp-1">
                  Kempen Waqaf Sistem Air Bersih Pedalaman
                </h4>
                <p className="text-[11px] text-slate-500 line-clamp-2">
                  Penyiapan pemasangan 3 pam graviti air bersih di perkampungan.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <AutoWaqafDrawer
        isOpen={isAutoWaqafOpen}
        onClose={() => setIsAutoWaqafOpen(false)}
      />
    </div>
  );
};
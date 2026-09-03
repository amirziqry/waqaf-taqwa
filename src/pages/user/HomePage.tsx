import React, { useState, useEffect } from 'react';
import { 
  Zap, 
  RefreshCw,  
  Building2, 
  Store, 
  QrCode,
  Sparkles,
  ArrowRight
} from 'lucide-react';
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

const FALLBACK_PROJECTS: CampaignItem[] = [
  {
    id: '1',
    title: 'Pembinaan Dewan Solat Masjid Cyberjaya',
    category: 'Masjid',
    targetAmount: 50000,
    collectedAmount: 37500,
    description: 'Peluasan ruang solat utama bagi menampung pertambahan jemaah solat Jumaat dan aktiviti komuniti.',
  },
  {
    id: '2',
    title: 'Dana Pendidikan Huffaz Asnaf',
    category: 'Pendidikan',
    targetAmount: 30000,
    collectedAmount: 18400,
    description: 'Bantuan pembiayaan yuran pengajian, asrama, dan penyediaan mushaf Al-Quran bagi pelajar asnaf.',
  },
];

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const [isAutoWaqafOpen, setIsAutoWaqafOpen] = useState(false);
  const [campaigns, setCampaigns] = useState<CampaignItem[]>(FALLBACK_PROJECTS);
  const [agentCardSlide, setAgentCardSlide] = useState<number>(0);

  useEffect(() => {
    const customCampaigns: CampaignItem[] = JSON.parse(
      localStorage.getItem('wt_custom_campaigns') || '[]'
    );

    api.get('/campaigns')
      .then((res) => {
        if (res.data && Array.isArray(res.data) && res.data.length > 0) {
          setCampaigns([...customCampaigns, ...res.data]);
        } else {
          setCampaigns([...customCampaigns, ...FALLBACK_PROJECTS]);
        }
      })
      .catch(() => {
        setCampaigns([...customCampaigns, ...FALLBACK_PROJECTS]);
      });
  }, []);

  // 5-second Rakan QR / Tijarah auto-slide
  useEffect(() => {
    const agentInterval = setInterval(() => {
      setAgentCardSlide((prev) => (prev === 0 ? 1 : 0));
    }, 5000);

    return () => clearInterval(agentInterval);
  }, []);

  const totalWaqafContributed = campaigns.reduce(
    (acc, curr) => acc + (curr.collectedAmount || 0),
    0
  );

  return (
    <div className="w-full max-w-5xl mx-auto px-4 sm:px-6 md:px-8 py-4 sm:py-6 space-y-8 pb-24">
      {/* 1. Header with Logo */}
      <div className="flex items-center justify-between bg-white p-4 sm:p-5 rounded-3xl border border-slate-100 shadow-xs">
        <div className="flex items-center gap-3.5">
          <img
            src="/logo.png"
            alt="Waqaf Taqwa Logo"
            className="w-10 h-10 object-contain shrink-0"
          />
          <div>
            <h1 className="text-base font-black text-[#0F2028] leading-tight">Waqaf Taqwa</h1>
            <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">
              Platform Pengurusan Waqaf Digital
            </p>
          </div>
        </div>

        <div className="hidden sm:flex items-center gap-2">
          <span className="text-[11px] font-extrabold px-3.5 py-1.5 bg-emerald-50 text-[#1A8C4E] rounded-full border border-emerald-100">
            Patuh Syariah
          </span>
        </div>
      </div>

      {/* 2. Prominent & Popping Jumlah Waqaf Terkumpul Hero Banner */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-[#0B2E1C] via-[#125B35] to-[#1A8C4E] p-7 sm:p-10 text-white shadow-xl shadow-emerald-950/20">
        <div className="relative z-10 space-y-4 max-w-2xl">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 bg-white/15 backdrop-blur-md rounded-full text-[10px] font-extrabold uppercase tracking-widest text-emerald-200 border border-white/15">
            <Sparkles className="w-3.5 h-3.5 text-amber-300" />
            Jumlah Waqaf Terkumpul
          </div>

          <div>
            <span className="text-xs sm:text-sm font-bold text-emerald-200/90 block mb-1 tracking-wider uppercase">
              Dana Keseluruhan Disumbang Ummah
            </span>
            <div className="flex items-baseline gap-2 sm:gap-3">
              <span className="text-2xl sm:text-3xl font-black text-amber-300">RM</span>
              <h2 className="text-4xl sm:text-5xl lg:text-6xl font-black tracking-tight text-white drop-shadow-sm">
                {totalWaqafContributed.toLocaleString('ms-MY', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </h2>
            </div>
          </div>

          <p className="text-xs sm:text-sm text-emerald-100/90 leading-relaxed max-w-xl">
            Sumbangan infaq dan waqaf telah disalurkan secara langsung ke {campaigns.length} inisiatif pembangunan masjid, pusat pendidikan huffaz, dan kebajikan komuniti asnaf.
          </p>

          <div className="pt-2 flex flex-wrap items-center gap-3">
            <button
              onClick={() => navigate('/projek')}
              className="h-11 px-6 bg-white hover:bg-emerald-50 text-[#0B2E1C] rounded-2xl font-black text-xs flex items-center gap-2 shadow-sm transition active:scale-[0.98]"
            >
              <span>Terokai Senarai Projek</span>
              <ArrowRight className="w-4 h-4" />
            </button>
            <button
              onClick={() => setIsAutoWaqafOpen(true)}
              className="h-11 px-5 bg-white/15 hover:bg-white/20 text-white border border-white/25 rounded-2xl font-bold text-xs flex items-center gap-2 backdrop-blur-sm transition active:scale-[0.98]"
            >
              <RefreshCw className="w-3.5 h-3.5 text-amber-300" />
              <span>Jadualkan Auto-Waqaf</span>
            </button>
          </div>
        </div>

        {/* Decorative Background Accents */}
        <div className="absolute -right-12 -bottom-12 w-64 h-64 bg-emerald-400/20 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute right-8 top-8 p-4 bg-white/10 backdrop-blur-md rounded-3xl border border-white/10 text-emerald-200 hidden sm:block">
          <Building2 className="w-10 h-10" />
        </div>
      </div>

      {/* 3. Middle Grid: Sliding Agent Banner & Action Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 sm:gap-8">
        {/* Left / Sliding Agent Card */}
        <div className="lg:col-span-7">
          <div className="relative overflow-hidden rounded-3xl shadow-xs h-full">
            {agentCardSlide === 0 ? (
              <div className="bg-gradient-to-r from-[#0F2028] to-[#1A8C4E] p-6 sm:p-8 text-white flex flex-col sm:flex-row items-center justify-between gap-5 h-full transition-all duration-500">
                <div className="space-y-2 text-center sm:text-left">
                  <span className="text-[10px] font-extrabold uppercase px-2.5 py-0.5 bg-white/20 rounded-full">
                    Inisiatif Duta Komuniti
                  </span>
                  <h3 className="text-lg font-black flex items-center gap-2 justify-center sm:justify-start">
                    <QrCode className="w-5 h-5" />
                    Jadilah Duta Waqaf (Rakan QR)
                  </h3>
                  <p className="text-xs text-white/80 leading-relaxed">
                    Mohon pelekat dan standee QR fizikal rasmi untuk premis, surau, atau acara anda.
                  </p>
                </div>
                <button
                  onClick={() => navigate('/rakan-qr')}
                  className="px-5 py-2.5 bg-white text-[#0F2028] hover:bg-slate-100 rounded-2xl text-xs font-bold shrink-0 transition shadow-xs"
                >
                  Mohon QR Fizikal
                </button>
              </div>
            ) : (
              <div className="bg-gradient-to-r from-[#064E3B] to-[#047857] p-6 sm:p-8 text-white flex flex-col sm:flex-row items-center justify-between gap-5 h-full transition-all duration-500">
                <div className="space-y-2 text-center sm:text-left">
                  <span className="text-[10px] font-extrabold uppercase px-2.5 py-0.5 bg-amber-400/25 text-amber-200 rounded-full">
                    Khas Untuk Premis Perniagaan
                  </span>
                  <h3 className="text-lg font-black flex items-center gap-2 justify-center sm:justify-start">
                    <Store className="w-5 h-5 text-amber-300" />
                    Mohon Rakan Tijarah (Peniaga)
                  </h3>
                  <p className="text-xs text-white/80 leading-relaxed">
                    Daftar premis SSM anda untuk mengaktifkan terminal dan kutipan infaq kaunter rasmi.
                  </p>
                </div>
                <button
                  onClick={() => navigate('/apply-tijarah')}
                  className="px-5 py-2.5 bg-amber-400 hover:bg-amber-300 text-slate-900 rounded-2xl text-xs font-black shrink-0 transition shadow-xs"
                >
                  Daftar Peniaga
                </button>
              </div>
            )}

            {/* Slider Dots */}
            <div className="absolute bottom-3 right-4 flex items-center gap-1.5">
              <button
                onClick={() => setAgentCardSlide(0)}
                className={`h-1.5 rounded-full transition-all ${
                  agentCardSlide === 0 ? 'w-5 bg-white' : 'w-2 bg-white/40'
                }`}
                aria-label="Slide Rakan QR"
              />
              <button
                onClick={() => setAgentCardSlide(1)}
                className={`h-1.5 rounded-full transition-all ${
                  agentCardSlide === 1 ? 'w-5 bg-white' : 'w-2 bg-white/40'
                }`}
                aria-label="Slide Rakan Tijarah"
              />
            </div>
          </div>
        </div>

        {/* Right / Quick Action Trigger Cards */}
        <div className="lg:col-span-5 flex flex-col justify-between gap-4">
          <div className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs flex items-center justify-between">
            <div className="space-y-1">
              <span className="text-[10px] font-extrabold uppercase text-slate-400 tracking-wider">
                Sumbangan Segera
              </span>
              <h4 className="font-extrabold text-sm text-[#0F2028]">Imbas & Salur Terus</h4>
              <p className="text-[11px] text-slate-500">Pilih amaun dan saluran terus ke tabung utama.</p>
            </div>
            <button
              onClick={() => navigate('/imbas')}
              className="h-11 px-5 bg-[#1A8C4E] hover:bg-[#15703E] text-white rounded-2xl font-bold text-xs flex items-center gap-1.5 transition shadow-xs"
            >
              <Zap className="w-4 h-4 fill-white" />
              <span>Waqaf</span>
            </button>
          </div>

          <div className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs flex items-center justify-between">
            <div className="space-y-1">
              <span className="text-[10px] font-extrabold uppercase text-slate-400 tracking-wider">
                Langganan Amal
              </span>
              <h4 className="font-extrabold text-sm text-[#0F2028]">Infaq Berkala Automatik</h4>
              <p className="text-[11px] text-slate-500">Tetapkan potongan mingguan atau bulanan.</p>
            </div>
            <button
              onClick={() => setIsAutoWaqafOpen(true)}
              className="h-11 px-5 bg-emerald-50 hover:bg-emerald-100 text-[#1A8C4E] rounded-2xl font-bold text-xs flex items-center gap-1.5 transition"
            >
              <RefreshCw className="w-4 h-4 stroke-[2.5]" />
              <span>Tetapan</span>
            </button>
          </div>
        </div>
      </div>

      {/* 4. Community News & Updates */}
      <div className="space-y-3.5 pt-2">
        <h3 className="font-extrabold text-lg text-[#0F2028] px-1">Berita & Laporan Komuniti</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="bg-white rounded-2xl overflow-hidden border border-slate-100 shadow-xs flex gap-4 p-4 items-center">
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
              <p className="text-[11px] text-slate-500 line-clamp-2 leading-relaxed">
                Sebanyak 500 keluarga telah menerima pek bekalan makanan asas.
              </p>
            </div>
          </div>

          <div className="bg-white rounded-2xl overflow-hidden border border-slate-100 shadow-xs flex gap-4 p-4 items-center">
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
              <p className="text-[11px] text-slate-500 line-clamp-2 leading-relaxed">
                Penyiapan pemasangan 3 pam graviti air bersih di perkampungan.
              </p>
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
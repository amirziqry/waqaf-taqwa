import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Share2, MapPin, Calendar, CheckCircle } from 'lucide-react';

export const CampaignDetailPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="bg-white min-h-full pb-6">
      {/* Top App Bar */}
      <div className="px-4 py-3 flex items-center justify-between border-b border-slate-100">
        <button onClick={() => navigate(-1)} className="p-1 text-slate-800">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h2 className="font-extrabold text-base text-[#0F2028]">Perincian Projek</h2>
        <button className="p-1 text-slate-800">
          <Share2 className="w-5 h-5" />
        </button>
      </div>

      {/* Hero Image Slider */}
      <div className="relative h-56 w-full">
        <img
          src="https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=800"
          alt="Surau Al-Hidayah"
          className="w-full h-full object-cover"
        />
        <div className="absolute bottom-2.5 inset-x-0 flex justify-center gap-1.5">
          <span className="w-4 h-1.5 bg-emerald-600 rounded-full" />
          <span className="w-1.5 h-1.5 bg-white/70 rounded-full" />
          <span className="w-1.5 h-1.5 bg-white/70 rounded-full" />
        </div>
      </div>

      {/* Body Content */}
      <div className="p-4 space-y-4">
        <div>
          <h1 className="text-xl font-black text-[#0F2028]">Pembinaan Surau Al-Hidayah</h1>
          <div className="mt-2 space-y-1 text-xs text-slate-500 font-medium">
            <p className="flex items-center gap-1.5">
              <MapPin className="w-3.5 h-3.5 text-slate-400" /> Masjid Larkin Sentral, Johor
            </p>
            <p className="flex items-center gap-1.5">
              <Calendar className="w-3.5 h-3.5 text-slate-400" /> Tamat: 15 Ogos 2026
            </p>
          </div>
        </div>

        {/* Tag Badges */}
        <div className="flex gap-2">
          {['Pendidikan', 'Masjid', 'Pembinaan'].map((tag) => (
            <span key={tag} className="text-[11px] font-bold text-emerald-800 bg-emerald-50 border border-emerald-100 px-3 py-0.5 rounded-full">
              {tag}
            </span>
          ))}
        </div>

        {/* Amount Progress Card */}
        <div className="bg-slate-50/70 border border-slate-200/80 rounded-2xl p-4 space-y-2">
          <div className="flex items-baseline gap-1">
            <span className="text-2xl font-black text-[#1A8C4E]">RM 98,500</span>
            <span className="text-xs text-slate-400 font-bold">/ RM 150,000</span>
          </div>
          <div className="w-full h-2 bg-emerald-100/60 rounded-full overflow-hidden">
            <div className="h-full bg-[#1A8C4E] rounded-full w-[65%]" />
          </div>
          <div className="flex justify-between text-xs text-slate-500 font-bold pt-0.5">
            <span>65% <span className="font-normal text-slate-400">Tercapai</span></span>
            <span>412 <span className="font-normal text-slate-400">Pewakaf</span></span>
          </div>
        </div>

        {/* Project Description */}
        <div className="space-y-2 text-xs text-slate-600 leading-relaxed">
          <h3 className="font-extrabold text-sm text-[#0F2028]">Tentang Projek Ini</h3>
          <p>
            Surau Al-Hidayah bakal dibina khusus untuk kemudahan komuniti setempat serta pengguna Masjid Larkin Sentral yang kian meningkat. Dengan kapasiti memuatkan sehingga 200 jemaah pada satu-satu masa, surau ini akan dilengkapi bilik wuduk yang mesra warga emas dan bilik kuliah mini.
          </p>
          <p>
            Projek pembangunan ini merangkumi kerja-kerja struktur asas, pendawaian elektrik yang selamat, serta penyediaan kelengkapan solat seperti permaidani, sejadah, dan sistem pembesar suara berkualiti tinggi untuk keselesaan jemaah.
          </p>
          <p>
            Setiap sumbangan waqaf anda bukan sekadar membantu membina dinding konkrit, malah ia merupakan pelaburan akhirat berterusan yang membolehkan syiar Islam terus bersinar segar di tengah-tengah bandaraya Johor Bahru.
          </p>
        </div>

        {/* Shariah Compliance Badge */}
        <div className="p-3.5 bg-emerald-50/70 border border-emerald-200 rounded-2xl flex items-center gap-3">
          <CheckCircle className="w-6 h-6 text-[#1A8C4E] fill-emerald-100 flex-shrink-0" />
          <div>
            <h5 className="font-extrabold text-xs text-[#1A8C4E]">Disahkan Patuh Syariah</h5>
            <p className="text-[10px] text-emerald-800">Akad Wakalah diuruskan mengikut garis panduan Islam.</p>
          </div>
        </div>

        {/* Bottom CTA */}
        <button
          onClick={() => navigate('/imbas')}
          className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-xl text-sm shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition"
        >
          Waqaf Sekarang
        </button>
      </div>
    </div>
  );
};
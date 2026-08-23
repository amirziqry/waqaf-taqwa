import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search } from 'lucide-react';

const CATEGORIES = ['Semua', 'Masjid', 'Pendidikan', 'Kesihatan'];

const PROJECTS = [
  {
    id: '1',
    category: 'Masjid',
    categoryBg: 'bg-emerald-600',
    title: 'Pembinaan Masjid Al-Hidayah',
    target: 150000,
    collectedPercent: 65,
    description: 'Membina masjid baru di kawasan pedalaman untuk masyarakat setempat.',
    image: 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=600',
  },
  {
    id: '2',
    category: 'Pendidikan',
    categoryBg: 'bg-blue-600',
    title: 'Pusat Tahfiz Anak Yatim',
    target: 80000,
    collectedPercent: 42,
    description: 'Menyediakan infrastruktur pendidikan tahfiz untuk anak-anak yatim piatu.',
    image: 'https://images.unsplash.com/photo-1588072432836-e10032774350?auto=format&fit=crop&q=80&w=600',
  },
  {
    id: '3',
    category: 'Kesihatan',
    categoryBg: 'bg-rose-500',
    title: 'Klinik Komuniti Desa Harmoni',
    target: 200000,
    collectedPercent: 50,
    description: 'Pusat rawatan hemodialisis dan klinik komuniti percuma.',
    image: 'https://images.unsplash.com/photo-1586773860418-d37222d8fce3?auto=format&fit=crop&q=80&w=600',
  },
];

export const ProjectsExplorePage: React.FC = () => {
  const navigate = useNavigate();
  const [activeCategory, setActiveCategory] = useState('Semua');

  return (
    <div className="p-4 space-y-4">
      {/* Title */}
      <h1 className="text-2xl font-black text-[#0F2028]">Projek Wakaf</h1>

      {/* Search Input */}
      <div className="relative">
        <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
        <input
          type="text"
          placeholder="Cari projek..."
          className="w-full pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-xs outline-none focus:border-[#1A8C4E]"
        />
      </div>

      {/* Filter Category Chips */}
      <div className="flex gap-2 overflow-x-auto no-scrollbar pb-1">
        {CATEGORIES.map((cat) => (
          <button
            key={cat}
            onClick={() => setActiveCategory(cat)}
            className={`px-4 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap transition ${
              activeCategory === cat
                ? 'bg-[#1A8C4E] text-white font-bold'
                : 'bg-white border border-slate-200 text-slate-600'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Project Cards */}
      <div className="space-y-4 pt-1">
        {PROJECTS.map((item) => (
          <div key={item.id} className="bg-white rounded-3xl overflow-hidden border border-slate-200/80 shadow-sm">
            {/* Image Banner */}
            <div className="h-44 w-full relative overflow-hidden">
              <img src={item.image} alt={item.title} className="w-full h-full object-cover" />
              <span className={`absolute top-3 left-3 text-white text-[10px] font-bold px-2.5 py-0.5 rounded-md ${item.categoryBg}`}>
                {item.category}
              </span>
            </div>

            <div className="p-4 space-y-3">
              <h3 className="font-extrabold text-base text-[#0F2028]">{item.title}</h3>
              <p className="text-xs font-bold text-slate-700 flex items-center gap-1.5">
                <span className="w-3.5 h-3.5 rounded-full bg-emerald-100 text-[#1A8C4E] flex items-center justify-center text-[9px] font-black">◎</span>
                Sasaran: RM {item.target.toLocaleString()}
              </p>

              {/* Progress Bar */}
              <div className="space-y-1">
                <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                  <div className="h-full bg-[#1A8C4E] rounded-full" style={{ width: `${item.collectedPercent}%` }} />
                </div>
                <p className="text-[11px] font-bold text-[#1A8C4E]">{item.collectedPercent}% terkumpul</p>
              </div>

              <p className="text-xs text-slate-500 leading-relaxed font-normal">{item.description}</p>

              {/* Actions */}
              <div className="grid grid-cols-2 gap-2.5 pt-1">
                <button
                  onClick={() => navigate(`/projek/${item.id}`)}
                  className="h-10 text-xs font-bold text-[#1A8C4E] border border-[#1A8C4E] rounded-xl hover:bg-emerald-50/50 transition"
                >
                  Info Projek
                </button>
                <button
                  onClick={() => navigate('/imbas')}
                  className="h-10 text-xs font-bold text-white bg-[#1A8C4E] hover:bg-[#15703E] rounded-xl transition"
                >
                  Waqaf Sekarang
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
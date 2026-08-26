import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search } from 'lucide-react';
import api from '../../api/client';

interface CampaignItem {
  id: string | number;
  title: string;
  category: string;
  targetAmount: number;
  collectedAmount: number;
  description: string;
  image?: string;
}

const CATEGORIES = ['Semua', 'Masjid', 'Pendidikan', 'Kesihatan', 'Infrastruktur'];

const FALLBACK_PROJECTS: CampaignItem[] = [
  {
    id: '1',
    title: 'Pembinaan Dewan Solat Masjid Cyberjaya',
    category: 'Masjid',
    targetAmount: 50000,
    collectedAmount: 37500,
    description: 'Peluasan ruang solat utama bagi menampung pertambahan jemaah solat Jumaat dan aktiviti komuniti.',
    image: 'https://images.unsplash.com/photo-1564769625905-50e93615e769?auto=format&fit=crop&w=800&q=80',
  },
  {
    id: '2',
    title: 'Dana Pendidikan Huffaz Asnaf',
    category: 'Pendidikan',
    targetAmount: 30000,
    collectedAmount: 18400,
    description: 'Bantuan pembiayaan yuran pengajian, asrama, dan penyediaan mushaf Al-Quran bagi pelajar asnaf.',
    image: 'https://images.unsplash.com/photo-1584697964190-7bb9b1f72787?auto=format&fit=crop&w=800&q=80',
  },
];

export const ProjectsExplorePage: React.FC = () => {
  const navigate = useNavigate();
  const [activeCategory, setActiveCategory] = useState('Semua');
  const [searchQuery, setSearchQuery] = useState('');
  const [campaigns, setCampaigns] = useState<CampaignItem[]>(FALLBACK_PROJECTS);
  const [, setLoading] = useState(false);

  useEffect(() => {
  setLoading(true);
  
  // Load custom admin-created campaigns from local storage
  const customCampaigns: CampaignItem[] = JSON.parse(
    localStorage.getItem('wt_custom_campaigns') || '[]'
  );

  api.get('/campaigns')
    .then((res) => {
      if (res.data && Array.isArray(res.data) && res.data.length > 0) {
        // Merge API campaigns with custom campaigns (custom on top)
        setCampaigns([...customCampaigns, ...res.data]);
      } else {
        setCampaigns([...customCampaigns, ...FALLBACK_PROJECTS]);
      }
    })
    .catch(() => {
      setCampaigns([...customCampaigns, ...FALLBACK_PROJECTS]);
    })
    .finally(() => setLoading(false));
}, []);

  // Filter by Category and Search Query
  const filteredProjects = campaigns.filter((item) => {
    const matchesCategory =
      activeCategory === 'Semua' ||
      item.category?.toLowerCase() === activeCategory.toLowerCase();
    const matchesSearch =
      (item.title || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
      (item.description || '').toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  return (
    <div className="w-full space-y-6">
      {/* Header & Search Bar */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-5 rounded-3xl border border-slate-100 shadow-xs">
        <div>
          <h1 className="text-2xl font-black text-[#0F2028]">Projek Wakaf</h1>
          <p className="text-xs text-slate-400">Terokai inisiatif pembangunan, pendidikan, dan masjid</p>
        </div>

        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Cari nama atau kategori..."
            className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-2xl text-xs outline-none focus:bg-white focus:border-[#1A8C4E] transition font-medium"
          />
        </div>
      </div>

      {/* Filter Chips */}
      <div className="flex gap-2 overflow-x-auto pb-1">
        {CATEGORIES.map((cat) => (
          <button
            key={cat}
            onClick={() => setActiveCategory(cat)}
            className={`px-4 py-2 rounded-2xl text-xs font-bold transition whitespace-nowrap ${
              activeCategory === cat
                ? 'bg-[#1A8C4E] text-white shadow-xs'
                : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Multi-Column Responsive Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {filteredProjects.map((item) => {
          const target = item.targetAmount || 1;
          const collected = item.collectedAmount || 0;
          const collectedPercent = Math.min(Math.round((collected / target) * 100), 100);

          return (
            <div
              key={item.id}
              className="bg-white rounded-3xl overflow-hidden border border-slate-100 shadow-xs flex flex-col justify-between hover:shadow-md transition"
            >
              <div>
                <div className="h-48 w-full relative overflow-hidden bg-slate-100">
                  <img
                    src={
                      item.image ||
                      'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=800&q=80'
                    }
                    alt={item.title}
                    className="w-full h-full object-cover"
                  />
                  <span className="absolute top-3 left-3 text-white text-[10px] font-extrabold px-2.5 py-0.5 rounded-lg bg-[#1A8C4E] shadow-sm">
                    {item.category}
                  </span>
                </div>

                <div className="p-5 space-y-3">
                  <h3 className="font-extrabold text-base text-[#0F2028] line-clamp-1">{item.title}</h3>
                  <p className="text-xs text-slate-500 font-normal line-clamp-2 leading-relaxed">
                    {item.description}
                  </p>
                </div>
              </div>

              <div className="p-5 pt-0 space-y-3.5">
                <div className="space-y-1.5">
                  <div className="flex justify-between text-xs font-bold">
                    <span className="text-[#1A8C4E]">RM {collected.toLocaleString()}</span>
                    <span className="text-slate-400">{collectedPercent}%</span>
                  </div>
                  <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-[#1A8C4E] rounded-full"
                      style={{ width: `${collectedPercent}%` }}
                    />
                  </div>
                  <div className="flex justify-between text-[10px] text-slate-400">
                    <span>Sasaran: RM {target.toLocaleString()}</span>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-2 pt-1">
                  <button
                    onClick={() => navigate(`/projek/${item.id}`)}
                    className="h-10 text-xs font-bold text-slate-700 border border-slate-200 rounded-xl hover:bg-slate-50 transition"
                  >
                    Info
                  </button>
                  <button
                    onClick={() => navigate('/imbas')}
                    className="h-10 text-xs font-bold text-white bg-[#1A8C4E] hover:bg-[#15703E] rounded-xl transition shadow-xs"
                  >
                    Waqaf
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
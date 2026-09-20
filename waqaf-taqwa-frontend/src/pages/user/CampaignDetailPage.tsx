import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, MapPin, ShieldCheck, Zap, } from 'lucide-react';
import api from '../../api/client';

interface CampaignItem {
  id: string | number;
  title: string;
  category: string;
  targetAmount: number;
  collectedAmount: number;
  description: string;
  location?: string;
  image?: string;
  imageUrl?: string;
}

const FALLBACK_PROJECTS: CampaignItem[] = [
  {
    id: '1',
    title: 'Pembinaan Kompleks Tahfiz Al-Quran Mukim Taqwa',
    category: 'Pendidikan',
    targetAmount: 150000,
    collectedAmount: 81000,
    location: 'Cyberjaya, Selangor',
    description: 'Membantu membina fasiliti pembelajaran serba moden untuk 150 pelajar tahfiz tempatan merangkumi asrama, dewan solat, dan perpustakaan digital.',
    image: 'https://images.unsplash.com/photo-1541888946425-d0fbb18f15f6?auto=format&fit=crop&q=80&w=800',
  },
  {
    id: '2',
    title: 'Pembinaan Dewan Solat Masjid Cyberjaya',
    category: 'Masjid',
    targetAmount: 50000,
    collectedAmount: 37500,
    location: 'Cyberjaya, Selangor',
    description: 'Peluasan ruang solat utama bagi menampung pertambahan jemaah solat Jumaat dan aktiviti pengimarahan masjid.',
    image: 'https://images.unsplash.com/photo-1564769625905-50e93615e769?auto=format&fit=crop&w=800&q=80',
  },
  {
    id: '3',
    title: 'Dana Pendidikan Huffaz Asnaf',
    category: 'Pendidikan',
    targetAmount: 30000,
    collectedAmount: 18400,
    location: 'Kuala Lumpur',
    description: 'Bantuan pembiayaan yuran pengajian, asrama, dan penyediaan mushaf Al-Quran bagi pelajar asnaf.',
    image: 'https://images.unsplash.com/photo-1584697964190-7bb9b1f72787?auto=format&fit=crop&w=800&q=80',
  },
];

export const CampaignDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [project, setProject] = useState<CampaignItem | null>(null);

  useEffect(() => {
    const customCampaigns: CampaignItem[] = JSON.parse(
      localStorage.getItem('wt_custom_campaigns') || '[]'
    );

    const allLocal = [...customCampaigns, ...FALLBACK_PROJECTS];
    const matchLocal = allLocal.find((item) => String(item.id) === String(id));

    if (matchLocal) {
      setProject(matchLocal);
    }

    // Also attempt fetching from backend API
    api.get(`/campaigns/${id}`)
      .then((res) => {
        if (res.data) setProject(res.data);
      })
      .catch(() => {
        if (!matchLocal) {
          setProject(FALLBACK_PROJECTS[0]);
        }
      });
  }, [id]);

  if (!project) return null;

  const target = project.targetAmount || 1;
  const collected = project.collectedAmount || 0;
  const percent = Math.min(Math.round((collected / target) * 100), 100);
  const bannerImage = project.image || project.imageUrl || 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=800&q=80';

  return (
    <div className="max-w-3xl mx-auto space-y-6 pb-12">
      {/* Top Header Back Button */}
      <button
        onClick={() => navigate('/projek')}
        className="flex items-center gap-2 text-xs font-bold text-slate-600 hover:text-slate-900 bg-white px-4 py-2.5 rounded-2xl border border-slate-100 shadow-xs transition"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Kembali ke Senarai Projek</span>
      </button>

      {/* Main Campaign Detail Card */}
      <div className="bg-white rounded-3xl overflow-hidden border border-slate-100 shadow-xs space-y-6">
        {/* Banner Image */}
        <div className="h-64 md:h-80 w-full relative overflow-hidden bg-slate-100">
          <img src={bannerImage} alt={project.title} className="w-full h-full object-cover" />
          <span className="absolute top-4 left-4 text-white text-xs font-extrabold px-3 py-1 rounded-xl bg-[#1A8C4E] shadow-sm">
            {project.category}
          </span>
        </div>

        {/* Campaign Info */}
        <div className="p-6 md:p-8 pt-0 space-y-6">
          <div className="space-y-2">
            <h1 className="text-2xl font-black text-[#0F2028] leading-snug">{project.title}</h1>
            {project.location && (
              <p className="text-xs font-bold text-slate-400 flex items-center gap-1.5">
                <MapPin className="w-4 h-4 text-slate-400" />
                {project.location}
              </p>
            )}
          </div>

          {/* Progress Bar & Financials */}
          <div className="bg-slate-50 p-5 rounded-2xl border border-slate-100 space-y-3">
            <div className="flex justify-between items-baseline text-sm font-black">
              <span className="text-[#1A8C4E]">
                RM {collected.toLocaleString()} <span className="font-normal text-xs text-slate-400">terkumpul</span>
              </span>
              <span className="text-[#1A8C4E]">{percent}%</span>
            </div>

            <div className="w-full h-2.5 bg-slate-200/70 rounded-full overflow-hidden">
              <div className="h-full bg-[#1A8C4E] rounded-full transition-all duration-500" style={{ width: `${percent}%` }} />
            </div>

            <div className="flex justify-between text-xs text-slate-400">
              <span>Sasaran Projek:</span>
              <span className="font-extrabold text-slate-800">RM {target.toLocaleString()}</span>
            </div>
          </div>

          {/* Description */}
          <div className="space-y-2">
            <h3 className="font-extrabold text-sm text-[#0F2028]">Penerangan & Matlamat Projek</h3>
            <p className="text-xs text-slate-600 leading-relaxed font-normal whitespace-pre-line">
              {project.description}
            </p>
          </div>

          {/* Shariah & Tax Assurance */}
          <div className="p-4 bg-emerald-50/50 border border-emerald-100 rounded-2xl flex items-center gap-3">
            <ShieldCheck className="w-5 h-5 text-[#1A8C4E] shrink-0" />
            <p className="text-[11px] text-emerald-900 leading-tight font-medium">
              Sumbangan kepada inisiatif ini disahkan patuh syariah dan layak menerima potongan cukai pendapatan LHDN sehingga 10%.
            </p>
          </div>

          {/* Action CTA */}
          <button
            onClick={() => navigate('/imbas')}
            className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99]"
          >
            <Zap className="w-4 h-4 fill-white" />
            <span>Waqaf Kepada Projek Ini Sekarang</span>
          </button>
        </div>
      </div>
    </div>
  );
};
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, TrendingUp, Users, Store, CheckCircle, Trash2 } from 'lucide-react';
import api from '../../api/client';

interface Campaign {
  id: string;
  title: string;
  category: string;
  targetAmount: number;
  collectedAmount: number;
  status: 'ACTIVE' | 'COMPLETED' | 'PENDING' | string;
  location?: string;
}

const DEFAULT_CAMPAIGNS: Campaign[] = [
  {
    id: '1',
    title: 'Pembinaan Dewan Solat Masjid Cyberjaya',
    category: 'Masjid',
    targetAmount: 50000,
    collectedAmount: 37500,
    status: 'ACTIVE',
    location: 'Cyberjaya, Selangor',
  },
  {
    id: '2',
    title: 'Dana Pendidikan Huffaz Asnaf',
    category: 'Pendidikan',
    targetAmount: 30000,
    collectedAmount: 18400,
    status: 'ACTIVE',
    location: 'Kuala Lumpur',
  },
];

export const AdminDashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const [campaigns, setCampaigns] = useState<Campaign[]>([]);

  const [metrics] = useState({
    totalFunds: 428500,
    donatorFunds: 310000,
    vendorFunds: 118500,
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    const custom: Campaign[] = JSON.parse(
      localStorage.getItem('wt_custom_campaigns') || '[]'
    );

    try {
      const response = await api.get('/campaigns').catch(() => null);
      if (response && response.data && Array.isArray(response.data) && response.data.length > 0) {
        setCampaigns([...custom, ...response.data]);
      } else {
        setCampaigns([...custom, ...DEFAULT_CAMPAIGNS]);
      }
    } catch {
      setCampaigns([...custom, ...DEFAULT_CAMPAIGNS]);
    }
  };

  const handleDeleteCampaign = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    if (!window.confirm('Adakah anda pasti mahu memadamkan kempen ini?')) return;

    // 1. Remove from local storage cache
    const custom: Campaign[] = JSON.parse(
      localStorage.getItem('wt_custom_campaigns') || '[]'
    );
    const updatedCustom = custom.filter((item) => String(item.id) !== String(id));
    localStorage.setItem('wt_custom_campaigns', JSON.stringify(updatedCustom));

    // 2. Attempt backend deletion
    try {
      await api.delete(`/campaigns/${id}`).catch(() => null);
    } catch {
      // Ignore API errors for mock/custom items
    }

    // 3. Update active state
    setCampaigns((prev) => prev.filter((item) => String(item.id) !== String(id)));
  };

  return (
    <div className="space-y-6">
      {/* Metric Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs relative overflow-hidden">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-500">Jumlah Dana Keseluruhan</span>
            <div className="p-2 bg-emerald-50 rounded-xl text-[#1A8C4E]">
              <TrendingUp className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-[#0F2028] mt-2">
            RM {metrics.totalFunds.toLocaleString()}
          </p>
          <span className="text-[10px] font-bold text-emerald-600 flex items-center gap-1 mt-1">
            +14.2% daripada bulan lepas
          </span>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-500">Sumbangan Pewakaf Awam</span>
            <div className="p-2 bg-blue-50 rounded-xl text-blue-600">
              <Users className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-[#0F2028] mt-2">
            RM {metrics.donatorFunds.toLocaleString()}
          </p>
          <span className="text-[10px] font-bold text-slate-400 mt-1 block">
            Melalui DuitNow QR & Auto-Waqaf
          </span>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-500">Kutipan SoftPOS Peniaga</span>
            <div className="p-2 bg-amber-50 rounded-xl text-amber-600">
              <Store className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-[#0F2028] mt-2">
            RM {metrics.vendorFunds.toLocaleString()}
          </p>
          <span className="text-[10px] font-bold text-slate-400 mt-1 block">
            Terminal runcit & pembundaran baki
          </span>
        </div>
      </div>

      {/* Campaigns Table & Action Panel */}
      <div className="bg-white rounded-3xl border border-slate-100 shadow-xs overflow-hidden">
        <div className="p-5 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h3 className="font-extrabold text-[#0F2028] text-base">Senarai Kempen & Projek Aktif</h3>
            <p className="text-xs text-slate-400">Data kempen yang boleh diakses pengguna</p>
          </div>
          <button
            onClick={() => navigate('/admin/kempen')}
            className="flex items-center gap-2 px-4 py-2.5 bg-[#1A8C4E] hover:bg-[#15703E] text-white rounded-2xl text-xs font-bold shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition"
          >
            <Plus className="w-4 h-4" />
            <span>Cipta Kempen Baharu</span>
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-50/75 border-b border-slate-100 text-[11px] font-extrabold text-slate-500 uppercase tracking-wider">
                <th className="py-3.5 px-5">Nama Projek</th>
                <th className="py-3.5 px-4">Kategori</th>
                <th className="py-3.5 px-4">Sasaran</th>
                <th className="py-3.5 px-4">Terkumpul</th>
                <th className="py-3.5 px-4">Kemajuan</th>
                <th className="py-3.5 px-4">Status</th>
                <th className="py-3.5 px-5 text-right">Tindakan</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-xs font-semibold">
              {campaigns.map((camp) => {
                const target = camp.targetAmount || 1;
                const collected = camp.collectedAmount || 0;
                const percentage = Math.min(Math.round((collected / target) * 100), 100);

                return (
                  <tr key={camp.id} className="hover:bg-slate-50/50 transition">
                    <td className="py-4 px-5">
                      <p className="font-extrabold text-slate-800">{camp.title}</p>
                      <span className="text-[10px] text-slate-400 font-normal">{camp.location || 'Selangor'}</span>
                    </td>
                    <td className="py-4 px-4">
                      <span className="px-2.5 py-1 bg-slate-100 text-slate-600 rounded-lg text-[10px] font-bold">
                        {camp.category}
                      </span>
                    </td>
                    <td className="py-4 px-4 text-slate-700">RM {target.toLocaleString()}</td>
                    <td className="py-4 px-4 font-extrabold text-[#1A8C4E]">
                      RM {collected.toLocaleString()}
                    </td>
                    <td className="py-4 px-4">
                      <div className="flex items-center gap-2">
                        <div className="w-16 h-1.5 bg-slate-100 rounded-full overflow-hidden">
                          <div className="h-full bg-[#1A8C4E] rounded-full" style={{ width: `${percentage}%` }} />
                        </div>
                        <span className="text-[10px] text-slate-500 font-bold">{percentage}%</span>
                      </div>
                    </td>
                    <td className="py-4 px-4">
                      <span className="inline-flex items-center gap-1 text-[10px] font-extrabold px-2.5 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
                        <CheckCircle className="w-3 h-3" />
                        Aktif
                      </span>
                    </td>
                    <td className="py-4 px-5 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => navigate('/admin/kempen')}
                          className="text-xs font-bold text-[#1A8C4E] hover:underline px-1.5 py-1"
                        >
                          Ubah
                        </button>
                        <button
                          onClick={(e) => handleDeleteCampaign(String(camp.id), e)}
                          className="p-1.5 text-rose-500 hover:text-rose-700 hover:bg-rose-50 rounded-xl transition"
                          title="Padam Kempen"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
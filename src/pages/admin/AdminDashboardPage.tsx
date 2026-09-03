import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Plus, 
  TrendingUp, 
  Store, 
  CheckCircle, 
  Trash2, 
  QrCode, 
  ShieldCheck, 
  UserCheck, 
  XCircle, 
  Package,
  X
} from 'lucide-react';
import api from '../../api/client';

interface Campaign {
  id: string;
  title: string;
  category: string;
  targetAmount: number;
  collectedAmount: number;
  status: 'ACTIVE' | 'COMPLETED' | 'PENDING' | string;
  location?: string;
  description?: string;
  image?: string;
}

interface TijarahApp {
  id: string;
  name: string;
  regNo: string;
  owner: string;
  category: string;
  contact: string;
  address: string;
  bank: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt: string;
}

interface RakanQrApp {
  agentCode: string;
  fullName: string;
  phone: string;
  placementType: string;
  locationName: string;
  deliveryAddress: string;
  physicalKitStatus: string;
  trackingNumber: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}

interface AdminUser {
  id: string;
  username: string;
  fullName: string;
  email: string;
  role: string;
  status: 'APPROVED' | 'PENDING';
  registeredAt: string;
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

const DEFAULT_TIJARAH_APPS: TijarahApp[] = [
  {
    id: 'TIJARAH-101',
    name: 'Restoran Nasi Kandar Subaidah',
    regNo: '202601004122 (SSM)',
    owner: 'Hj. Subaidah Mohamed',
    category: 'Makanan & Minuman',
    contact: '019-3382910',
    address: 'No 12, Jalan Cyber Point 2, 63000 Cyberjaya',
    bank: 'Maybank - 564120993821',
    status: 'PENDING',
    createdAt: '2026-02-08',
  }
];

const DEFAULT_RAKAN_QR: RakanQrApp[] = [
  {
    agentCode: 'AGT-4402',
    fullName: 'Ustaz Azhar Ghazali',
    phone: '012-9981122',
    placementType: 'Surau / Masjid',
    locationName: 'Surau Al-Hidayah Mukim 4',
    deliveryAddress: 'Lot 14, Kampung Melayu, 63000 Cyberjaya',
    physicalKitStatus: 'SEDANG DIPROSES',
    trackingNumber: 'MENUNGGU KURIER',
    status: 'PENDING',
  }
];

const DEFAULT_ADMIN_USERS: AdminUser[] = [
  {
    id: 'ADM-01',
    username: 'admin_utama',
    fullName: 'Ustaz Shahrir (Ketua Eksekutif)',
    email: 'shahrir@waqaftaqwa.my',
    role: 'SUPER_ADMIN',
    status: 'APPROVED',
    registeredAt: '2026-01-01',
  },
  {
    id: 'ADM-02',
    username: 'adminbaru@taqwa.com',
    fullName: 'Pegawai Operasi Baru',
    email: 'adminbaru@taqwa.com',
    role: 'ADMIN',
    status: 'PENDING',
    registeredAt: '2026-02-07',
  }
];

export const AdminDashboardPage: React.FC = () => {
  
  const [activeTab, setActiveTab] = useState<'campaigns' | 'tijarah' | 'rakanqr' | 'admins'>('campaigns');
  
  const [campaigns, setCampaigns] = useState<Campaign[]>([]);
  const [tijarahApps, setTijarahApps] = useState<TijarahApp[]>([]);
  const [rakanQrApps, setRakanQrApps] = useState<RakanQrApp[]>([]);
  const [adminUsers, setAdminUsers] = useState<AdminUser[]>([]);

  // Create Campaign Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newCampaign, setNewCampaign] = useState({
    title: '',
    category: 'Masjid',
    targetAmount: '',
    location: '',
    description: '',
    image: '',
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    const custom: Campaign[] = JSON.parse(localStorage.getItem('wt_custom_campaigns') || '[]');
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

    const storedTijarah: TijarahApp[] = JSON.parse(localStorage.getItem('wt_admin_vendors') || '[]');
    setTijarahApps(storedTijarah.length > 0 ? storedTijarah : DEFAULT_TIJARAH_APPS);

    const singleAgent = localStorage.getItem('wt_agent_profile');
    if (singleAgent) {
      try {
        const parsed = JSON.parse(singleAgent);
        setRakanQrApps([parsed, ...DEFAULT_RAKAN_QR.filter(a => a.agentCode !== parsed.agentCode)]);
      } catch {
        setRakanQrApps(DEFAULT_RAKAN_QR);
      }
    } else {
      setRakanQrApps(DEFAULT_RAKAN_QR);
    }

    const storedAdmins = JSON.parse(localStorage.getItem('wt_admin_users') || '[]');
    setAdminUsers(storedAdmins.length > 0 ? storedAdmins : DEFAULT_ADMIN_USERS);
  };

  const handleCreateCampaign = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCampaign.title || !newCampaign.targetAmount) return;

    const created: Campaign = {
      id: `CMP-${Date.now().toString().slice(-5)}`,
      title: newCampaign.title,
      category: newCampaign.category,
      targetAmount: Number(newCampaign.targetAmount),
      collectedAmount: 0,
      status: 'ACTIVE',
      location: newCampaign.location || 'Selangor',
      description: newCampaign.description || 'Pembangunan kemudahan dan kebajikan ummah berterusan.',
      image: newCampaign.image || 'https://images.unsplash.com/photo-1564769625905-50e93615e769?auto=format&fit=crop&w=800&q=80',
    };

    const custom: Campaign[] = JSON.parse(localStorage.getItem('wt_custom_campaigns') || '[]');
    const updatedCustom = [created, ...custom];
    localStorage.setItem('wt_custom_campaigns', JSON.stringify(updatedCustom));

    setCampaigns((prev) => [created, ...prev]);
    setIsModalOpen(false);
    setNewCampaign({
      title: '',
      category: 'Masjid',
      targetAmount: '',
      location: '',
      description: '',
      image: '',
    });
  };

  const handleDeleteCampaign = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    if (!window.confirm('Adakah anda pasti mahu memadamkan kempen ini?')) return;

    const custom: Campaign[] = JSON.parse(localStorage.getItem('wt_custom_campaigns') || '[]');
    const updatedCustom = custom.filter((item) => String(item.id) !== String(id));
    localStorage.setItem('wt_custom_campaigns', JSON.stringify(updatedCustom));

    try {
      await api.delete(`/campaigns/${id}`).catch(() => null);
    } catch {}

    setCampaigns((prev) => prev.filter((item) => String(item.id) !== String(id)));
  };

  const handleUpdateTijarahStatus = (id: string, newStatus: 'APPROVED' | 'REJECTED') => {
    const updated = tijarahApps.map((item) => item.id === id ? { ...item, status: newStatus } : item);
    setTijarahApps(updated);
    localStorage.setItem('wt_admin_vendors', JSON.stringify(updated));
  };

  const handleUpdateQrDispatch = (agentCode: string, kitStatus: string) => {
    const updated = rakanQrApps.map((item) => 
      item.agentCode === agentCode ? { ...item, physicalKitStatus: kitStatus, trackingNumber: 'MYPOS-77492100' } : item
    );
    setRakanQrApps(updated);
    localStorage.setItem('wt_agent_profile', JSON.stringify(updated[0]));
  };

  const handleApproveAdmin = (id: string) => {
    const updated = adminUsers.map((item) => item.id === id ? { ...item, status: 'APPROVED' as const } : item);
    setAdminUsers(updated);
    localStorage.setItem('wt_admin_users', JSON.stringify(updated));
  };

  const handleDeleteAdmin = (id: string) => {
    if (!window.confirm('Nyahaktifkan akaun pentadbir ini?')) return;
    const updated = adminUsers.filter((item) => item.id !== id);
    setAdminUsers(updated);
    localStorage.setItem('wt_admin_users', JSON.stringify(updated));
  };

  const totalFunds = campaigns.reduce((acc, curr) => acc + (curr.collectedAmount || 0), 0);
  const pendingTijarahCount = tijarahApps.filter((a) => a.status === 'PENDING').length;
  const pendingAdminCount = adminUsers.filter((a) => a.status === 'PENDING').length;

  const tabOptions = [
    { id: 'campaigns', label: 'Projek Kempen', count: campaigns.length },
    { id: 'tijarah', label: 'Rakan Tijarah (SSM)', count: pendingTijarahCount },
    { id: 'rakanqr', label: 'Kit Standee QR', count: rakanQrApps.length },
    { id: 'admins', label: 'Staf & Admin', count: pendingAdminCount },
  ];

  return (
    <div className="max-w-6xl mx-auto space-y-6 pb-20">
      {/* Header Banner */}
      <div className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-black text-[#0F2028]">Portal Pentadbir (Admin Hub)</h1>
            <span className="px-2.5 py-0.5 bg-emerald-50 text-[#1A8C4E] rounded-full text-[10px] font-black tracking-wider uppercase border border-emerald-200">
              Akses Penuh
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-0.5">
            Kawalan kelulusan kempen waqaf, pendaftaran rakan niaga SSM, kit fizikal, dan kakitangan sistem
          </p>
        </div>

        {/* Triggers Create Campaign Modal */}
        <button
          type="button"
          onClick={() => setIsModalOpen(true)}
          className="flex items-center gap-2 px-5 py-2.5 bg-[#1A8C4E] hover:bg-[#15703E] text-white rounded-2xl text-xs font-bold shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition shrink-0"
        >
          <Plus className="w-4 h-4" />
          <span>Cipta Projek Kempen</span>
        </button>
      </div>

      {/* Primary KPI Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-[11px] font-extrabold uppercase text-slate-400">Jumlah Terkumpul</span>
            <div className="p-2 bg-emerald-50 rounded-xl text-[#1A8C4E]">
              <TrendingUp className="w-4 h-4" />
            </div>
          </div>
          <p className="text-xl font-black text-[#0F2028] mt-2">
            RM {totalFunds.toLocaleString('ms-MY', { minimumFractionDigits: 2 })}
          </p>
          <span className="text-[10px] font-bold text-emerald-600 mt-1 block">Aktif & Disahkan LHDN</span>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-[11px] font-extrabold uppercase text-slate-400">Permohonan Tijarah</span>
            <div className="p-2 bg-amber-50 rounded-xl text-amber-600">
              <Store className="w-4 h-4" />
            </div>
          </div>
          <p className="text-xl font-black text-amber-600 mt-2">{pendingTijarahCount} Menunggu</p>
          <span className="text-[10px] font-bold text-slate-400 mt-1 block">Semakan SSM Premis</span>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-[11px] font-extrabold uppercase text-slate-400">Duta Standee QR</span>
            <div className="p-2 bg-blue-50 rounded-xl text-blue-600">
              <QrCode className="w-4 h-4" />
            </div>
          </div>
          <p className="text-xl font-black text-blue-600 mt-2">{rakanQrApps.length} Ejen</p>
          <span className="text-[10px] font-bold text-slate-400 mt-1 block">Komisen 5% Ditauliahkan</span>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-[11px] font-extrabold uppercase text-slate-400">Kelulusan Admin</span>
            <div className="p-2 bg-purple-50 rounded-xl text-purple-600">
              <ShieldCheck className="w-4 h-4" />
            </div>
          </div>
          <p className="text-xl font-black text-purple-700 mt-2">{pendingAdminCount} Menunggu</p>
          <span className="text-[10px] font-bold text-slate-400 mt-1 block">Kebenaran Staf Baharu</span>
        </div>
      </div>

      {/* Admin Module Tabs */}
      <div className="flex gap-2 border-b border-slate-200 pb-2 overflow-x-auto">
        {tabOptions.map((tab) => (
          <button
            key={tab.id}
            type="button"
            onClick={() => setActiveTab(tab.id as any)}
            className={`px-4 py-2.5 rounded-2xl text-xs font-black transition flex items-center gap-2 whitespace-nowrap ${
              activeTab === tab.id
                ? 'bg-[#1A8C4E] text-white shadow-xs'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            <span>{tab.label}</span>
            {tab.count > 0 && (
              <span className={`px-2 py-0.5 rounded-full text-[10px] ${
                activeTab === tab.id ? 'bg-white/20 text-white' : 'bg-slate-100 text-slate-700'
              }`}>
                {tab.count}
              </span>
            )}
          </button>
        ))}
      </div>

      {/* TAB 1: Campaigns Table */}
      {activeTab === 'campaigns' && (
        <div className="bg-white rounded-3xl border border-slate-100 shadow-xs overflow-hidden">
          <div className="p-5 border-b border-slate-100 flex items-center justify-between">
            <div>
              <h3 className="font-extrabold text-[#0F2028] text-base">Senarai Kempen & Projek Aktif</h3>
              <p className="text-xs text-slate-400">Pengurusan perolehan infaq dan status paparan awam</p>
            </div>
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
                            type="button"
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
      )}

      {/* TAB 2: Permohonan Rakan Tijarah */}
      {activeTab === 'tijarah' && (
        <div className="bg-white rounded-3xl border border-slate-100 shadow-xs overflow-hidden">
          <div className="p-5 border-b border-slate-100">
            <h3 className="font-extrabold text-[#0F2028] text-base">Permohonan Rakan Tijarah (Peniaga SSM)</h3>
            <p className="text-xs text-slate-400">Pengesahan premis perniagaan untuk pengaktifan modul infaq kaunter</p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-50/75 border-b border-slate-100 text-[11px] font-extrabold text-slate-500 uppercase tracking-wider">
                  <th className="py-3.5 px-5">Premis & SSM</th>
                  <th className="py-3.5 px-4">Pemilik / Hubungan</th>
                  <th className="py-3.5 px-4">Kategori & Lokasi</th>
                  <th className="py-3.5 px-4">Akaun Bank</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-5 text-right">Keputusan</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-xs font-semibold">
                {tijarahApps.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50/50 transition">
                    <td className="py-4 px-5">
                      <p className="font-extrabold text-slate-800">{item.name}</p>
                      <span className="text-[10px] text-slate-400 font-mono">{item.regNo}</span>
                    </td>
                    <td className="py-4 px-4">
                      <p className="text-slate-800">{item.owner}</p>
                      <span className="text-[10px] text-slate-400">{item.contact}</span>
                    </td>
                    <td className="py-4 px-4 max-w-xs truncate">
                      <span className="px-2 py-0.5 bg-slate-100 rounded text-[10px] block w-fit mb-1">{item.category}</span>
                      <span className="text-[10px] text-slate-500 font-normal">{item.address}</span>
                    </td>
                    <td className="py-4 px-4 text-slate-700 font-mono text-[11px]">
                      {item.bank}
                    </td>
                    <td className="py-4 px-4">
                      <span className={`text-[10px] font-black px-2.5 py-0.5 rounded-full uppercase ${
                        item.status === 'APPROVED' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' :
                        item.status === 'REJECTED' ? 'bg-rose-50 text-rose-700 border border-rose-200' :
                        'bg-amber-50 text-amber-700 border border-amber-200'
                      }`}>
                        {item.status}
                      </span>
                    </td>
                    <td className="py-4 px-5 text-right">
                      {item.status === 'PENDING' ? (
                        <div className="flex items-center justify-end gap-2">
                          <button
                            type="button"
                            onClick={() => handleUpdateTijarahStatus(item.id, 'APPROVED')}
                            className="px-3 py-1.5 bg-[#1A8C4E] hover:bg-[#15703E] text-white rounded-xl text-xs font-bold transition flex items-center gap-1 shadow-xs"
                          >
                            <CheckCircle className="w-3.5 h-3.5" /> Luluskan
                          </button>
                          <button
                            type="button"
                            onClick={() => handleUpdateTijarahStatus(item.id, 'REJECTED')}
                            className="px-3 py-1.5 bg-rose-50 text-rose-600 hover:bg-rose-100 rounded-xl text-xs font-bold transition"
                          >
                            Tolak
                          </button>
                        </div>
                      ) : (
                        <span className="text-[11px] text-slate-400 font-medium">Telah Diproses</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 3: Permohonan Kit Standee QR */}
      {activeTab === 'rakanqr' && (
        <div className="bg-white rounded-3xl border border-slate-100 shadow-xs overflow-hidden">
          <div className="p-5 border-b border-slate-100">
            <h3 className="font-extrabold text-[#0F2028] text-base">Permohonan Standee Fizikal (Duta QR)</h3>
            <p className="text-xs text-slate-400">Pengurusan penghantaran kit standee dan rekod pentauliahan ejen</p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-50/75 border-b border-slate-100 text-[11px] font-extrabold text-slate-500 uppercase tracking-wider">
                  <th className="py-3.5 px-5">Kod Ejen & Nama</th>
                  <th className="py-3.5 px-4">Penempatan</th>
                  <th className="py-3.5 px-4">Alamat Penghantaran Kit</th>
                  <th className="py-3.5 px-4">Status Kit Standee</th>
                  <th className="py-3.5 px-5 text-right">Tindakan Kurier</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-xs font-semibold">
                {rakanQrApps.map((item) => (
                  <tr key={item.agentCode} className="hover:bg-slate-50/50 transition">
                    <td className="py-4 px-5">
                      <span className="px-2 py-0.5 bg-emerald-100 text-emerald-800 rounded font-mono text-[10px] font-bold">
                        {item.agentCode}
                      </span>
                      <p className="font-extrabold text-slate-800 mt-1">{item.fullName}</p>
                      <span className="text-[10px] text-slate-400">{item.phone}</span>
                    </td>
                    <td className="py-4 px-4">
                      <p className="text-slate-800 font-bold">{item.locationName}</p>
                      <span className="text-[10px] text-slate-500 font-normal">{item.placementType}</span>
                    </td>
                    <td className="py-4 px-4 max-w-xs text-slate-600 text-[11px] font-normal leading-relaxed">
                      {item.deliveryAddress}
                    </td>
                    <td className="py-4 px-4">
                      <span className="inline-flex items-center gap-1 text-[10px] font-extrabold px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-800 border border-amber-200 uppercase">
                        <Package className="w-3 h-3 text-amber-600" />
                        {item.physicalKitStatus}
                      </span>
                      <p className="text-[9px] text-slate-400 font-mono mt-0.5">{item.trackingNumber}</p>
                    </td>
                    <td className="py-4 px-5 text-right">
                      <button
                        type="button"
                        onClick={() => handleUpdateQrDispatch(item.agentCode, 'TELAH DIPOS')}
                        className="px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-xl text-xs font-bold transition shadow-xs"
                      >
                        Kemas Kini Tracking Pos
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 4: Pengurusan Staf & Admin Users */}
      {activeTab === 'admins' && (
        <div className="bg-white rounded-3xl border border-slate-100 shadow-xs overflow-hidden">
          <div className="p-5 border-b border-slate-100">
            <h3 className="font-extrabold text-[#0F2028] text-base">Senarai Staf & Pentadbir Sistem</h3>
            <p className="text-xs text-slate-400">Pengesahan akaun pentadbir baharu yang mendaftar melalui portal</p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-50/75 border-b border-slate-100 text-[11px] font-extrabold text-slate-500 uppercase tracking-wider">
                  <th className="py-3.5 px-5">Nama Pentadbir</th>
                  <th className="py-3.5 px-4">Nama Pengguna</th>
                  <th className="py-3.5 px-4">Peranan</th>
                  <th className="py-3.5 px-4">Status Pengesahan</th>
                  <th className="py-3.5 px-5 text-right">Tindakan Kebenaran</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-xs font-semibold">
                {adminUsers.map((admin) => (
                  <tr key={admin.id} className="hover:bg-slate-50/50 transition">
                    <td className="py-4 px-5">
                      <p className="font-extrabold text-slate-800">{admin.fullName}</p>
                      <span className="text-[10px] text-slate-400 font-normal">Didaftar: {admin.registeredAt}</span>
                    </td>
                    <td className="py-4 px-4 font-mono text-[11px] text-slate-700">
                      {admin.username}
                    </td>
                    <td className="py-4 px-4">
                      <span className="px-2.5 py-1 bg-slate-100 text-slate-700 rounded-lg text-[10px] font-extrabold">
                        {admin.role}
                      </span>
                    </td>
                    <td className="py-4 px-4">
                      <span className={`text-[10px] font-black px-2.5 py-0.5 rounded-full uppercase ${
                        admin.status === 'APPROVED'
                          ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                          : 'bg-amber-50 text-amber-700 border border-amber-200'
                      }`}>
                        {admin.status}
                      </span>
                    </td>
                    <td className="py-4 px-5 text-right">
                      <div className="flex items-center justify-end gap-2">
                        {admin.status === 'PENDING' && (
                          <button
                            type="button"
                            onClick={() => handleApproveAdmin(admin.id)}
                            className="px-3 py-1.5 bg-[#1A8C4E] hover:bg-[#15703E] text-white rounded-xl text-xs font-bold transition flex items-center gap-1 shadow-xs"
                          >
                            <UserCheck className="w-3.5 h-3.5" /> Sahkan Akses
                          </button>
                        )}
                        {admin.role !== 'SUPER_ADMIN' && (
                          <button
                            type="button"
                            onClick={() => handleDeleteAdmin(admin.id)}
                            className="p-1.5 text-rose-500 hover:text-rose-700 hover:bg-rose-50 rounded-xl transition"
                            title="Nyahaktifkan Pentadbir"
                          >
                            <XCircle className="w-4 h-4" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Interactive Create Campaign Modal Popup */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 space-y-4 shadow-2xl border border-slate-100 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div>
                <h3 className="font-black text-lg text-[#0F2028]">Cipta Projek Kempen Baharu</h3>
                <p className="text-xs text-slate-400">Terbitkan projek waqaf untuk sumbangan pewakaf</p>
              </div>
              <button
                type="button"
                onClick={() => setIsModalOpen(false)}
                className="p-1.5 text-slate-400 hover:text-slate-700 rounded-xl hover:bg-slate-100 transition"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateCampaign} className="space-y-3.5">
              <div className="space-y-1">
                <label className="text-xs font-bold text-slate-700">Nama Projek Kempen</label>
                <input
                  type="text"
                  required
                  value={newCampaign.title}
                  onChange={(e) => setNewCampaign({ ...newCampaign, title: e.target.value })}
                  placeholder="cth. Pembinaan Pusat Hemodialisis Waqaf"
                  className="w-full h-11 px-3.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1">
                  <label className="text-xs font-bold text-slate-700">Kategori</label>
                  <select
                    value={newCampaign.category}
                    onChange={(e) => setNewCampaign({ ...newCampaign, category: e.target.value })}
                    className="w-full h-11 px-3 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
                  >
                    <option value="Masjid">Masjid & Surau</option>
                    <option value="Pendidikan">Pendidikan & Huffaz</option>
                    <option value="Kesihatan">Kesihatan & Kebajikan</option>
                    <option value="Infrastruktur">Infrastruktur Air / Desa</option>
                  </select>
                </div>

                <div className="space-y-1">
                  <label className="text-xs font-bold text-slate-700">Sasaran Dana (RM)</label>
                  <input
                    type="number"
                    required
                    min="100"
                    value={newCampaign.targetAmount}
                    onChange={(e) => setNewCampaign({ ...newCampaign, targetAmount: e.target.value })}
                    placeholder="50000"
                    className="w-full h-11 px-3.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-bold text-slate-700">Lokasi / Kawasan</label>
                <input
                  type="text"
                  value={newCampaign.location}
                  onChange={(e) => setNewCampaign({ ...newCampaign, location: e.target.value })}
                  placeholder="cth. Cyberjaya, Selangor"
                  className="w-full h-11 px-3.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-bold text-slate-700">Penerangan Ringkas</label>
                <textarea
                  rows={2}
                  value={newCampaign.description}
                  onChange={(e) => setNewCampaign({ ...newCampaign, description: e.target.value })}
                  placeholder="Maklumat dan manfaat kempen kepada masyarakat..."
                  className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold outline-none focus:border-[#1A8C4E] resize-none"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-bold text-slate-700">URL Gambar (Pilihan)</label>
                <input
                  type="url"
                  value={newCampaign.image}
                  onChange={(e) => setNewCampaign({ ...newCampaign, image: e.target.value })}
                  placeholder="https://images.unsplash.com/..."
                  className="w-full h-11 px-3.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold outline-none focus:border-[#1A8C4E]"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-600 text-xs font-bold rounded-xl transition"
                >
                  Batal
                </button>
                <button
                  type="submit"
                  className="px-5 py-2.5 bg-[#1A8C4E] hover:bg-[#15703E] text-white text-xs font-bold rounded-xl shadow-xs transition"
                >
                  Terbitkan Kempen
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
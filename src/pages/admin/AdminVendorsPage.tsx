import React, { useState, useEffect } from 'react';
import { Check, X, ShieldAlert, ShieldCheck, Store, QrCode, MapPin } from 'lucide-react';

interface VendorItem {
  id: string;
  name: string;
  regNo: string;
  owner: string;
  category: string;
  status: 'PENDING' | 'APPROVED';
  qrCodeId: string;
}

interface RakanApplication {
  id: string;
  fullName: string;
  phoneNumber: string;
  organizationType: string;
  placementLocation: string;
  status: 'PENDING' | 'APPROVED';
  agentCode?: string;
}

const DEFAULT_VENDORS: VendorItem[] = [
  {
    id: 'v1',
    name: 'Kafe Mesra Taqwa',
    regNo: '202601004921 (SSM)',
    owner: 'Ahmad Faiz',
    category: 'Makanan & Minuman',
    status: 'PENDING',
    qrCodeId: 'QR-KAFE-01',
  },
  {
    id: 'v2',
    name: 'Kedai Buku Al-Falah',
    regNo: '202503928172 (SSM)',
    owner: 'Nurul Huda',
    category: 'Penerbitan & Runcit',
    status: 'APPROVED',
    qrCodeId: 'QR-FALAH-99',
  },
];

const DEFAULT_RAKAN_APPS: RakanApplication[] = [
  {
    id: 'RAKAN-101',
    fullName: 'Ustaz Hafizuddin Bin Omar',
    phoneNumber: '013-9876543',
    organizationType: 'MASJID',
    placementLocation: 'Masjid Cyberjaya',
    status: 'APPROVED',
    agentCode: 'AGT-7782',
  },
];

export const AdminVendorsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'VENDORS' | 'RAKAN_QR'>('VENDORS');
  const [vendors, setVendors] = useState<VendorItem[]>(DEFAULT_VENDORS);
  const [rakanApps, setRakanApps] = useState<RakanApplication[]>([]);

  useEffect(() => {
    const savedApps = localStorage.getItem('wt_admin_rakan_apps');
    if (savedApps) {
      try {
        const parsed = JSON.parse(savedApps);
        setRakanApps(parsed.length > 0 ? parsed : DEFAULT_RAKAN_APPS);
      } catch {
        setRakanApps(DEFAULT_RAKAN_APPS);
      }
    } else {
      setRakanApps(DEFAULT_RAKAN_APPS);
    }
  }, []);

  const handleApproveVendor = (id: string) => {
    setVendors((prev) =>
      prev.map((v) => (v.id === id ? { ...v, status: 'APPROVED' } : v))
    );
  };

  const handleRejectVendor = (id: string) => {
    setVendors((prev) => prev.filter((v) => v.id !== id));
  };

  const handleApproveRakan = (id: string) => {
    const updated = rakanApps.map((app) =>
      app.id === id
        ? {
            ...app,
            status: 'APPROVED' as const,
            agentCode: app.agentCode || `AGT-${Math.floor(1000 + Math.random() * 9000)}`,
          }
        : app
    );
    setRakanApps(updated);
    localStorage.setItem('wt_admin_rakan_apps', JSON.stringify(updated));
  };

  const handleRejectRakan = (id: string) => {
    const updated = rakanApps.filter((app) => app.id !== id);
    setRakanApps(updated);
    localStorage.setItem('wt_admin_rakan_apps', JSON.stringify(updated));
  };

  return (
    <div className="space-y-6">
      {/* Tab Switcher */}
      <div className="flex items-center gap-2 bg-slate-100/80 p-1.5 rounded-2xl w-fit">
        <button
          onClick={() => setActiveTab('VENDORS')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${
            activeTab === 'VENDORS'
              ? 'bg-white text-[#0F2028] shadow-xs'
              : 'text-slate-500 hover:text-slate-800'
          }`}
        >
          <Store className="w-4 h-4" />
          <span>Verifikasi Peniaga ({vendors.length})</span>
        </button>
        <button
          onClick={() => setActiveTab('RAKAN_QR')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${
            activeTab === 'RAKAN_QR'
              ? 'bg-white text-[#0F2028] shadow-xs'
              : 'text-slate-500 hover:text-slate-800'
          }`}
        >
          <QrCode className="w-4 h-4" />
          <span>Permohonan Rakan QR ({rakanApps.length})</span>
        </button>
      </div>

      {/* Tab 1: Peniaga Table */}
      {activeTab === 'VENDORS' && (
        <div className="bg-white rounded-3xl border border-slate-100 shadow-xs overflow-hidden space-y-4 p-5">
          <div>
            <h2 className="text-base font-extrabold text-[#0F2028]">Permohonan & Verifikasi Peniaga</h2>
            <p className="text-xs text-slate-400">Semak pendaftaran rakan peniaga bagi pengaktifan mod SoftPOS & DuitNow</p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-50/75 border-b border-slate-100 text-[11px] font-extrabold text-slate-500 uppercase">
                  <th className="py-3.5 px-4">Nama Premis / Peniaga</th>
                  <th className="py-3.5 px-4">No. SSM</th>
                  <th className="py-3.5 px-4">Pemilik</th>
                  <th className="py-3.5 px-4">Kategori</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">Tindakan</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-xs font-semibold">
                {vendors.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50/50 transition">
                    <td className="py-4 px-4 font-extrabold text-slate-800">{item.name}</td>
                    <td className="py-4 px-4 text-slate-500">{item.regNo}</td>
                    <td className="py-4 px-4 text-slate-700">{item.owner}</td>
                    <td className="py-4 px-4">{item.category}</td>
                    <td className="py-4 px-4">
                      {item.status === 'APPROVED' ? (
                        <span className="inline-flex items-center gap-1 text-[10px] font-extrabold px-2.5 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
                          <ShieldCheck className="w-3 h-3" /> Disahkan
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1 text-[10px] font-extrabold px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-700 border border-amber-200">
                          <ShieldAlert className="w-3 h-3" /> Menunggu
                        </span>
                      )}
                    </td>
                    <td className="py-4 px-4 text-right">
                      {item.status === 'PENDING' ? (
                        <div className="flex items-center justify-end gap-1.5">
                          <button
                            onClick={() => handleApproveVendor(item.id)}
                            className="p-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 rounded-xl transition"
                            title="Luluskan"
                          >
                            <Check className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => handleRejectVendor(item.id)}
                            className="p-1.5 bg-red-50 hover:bg-red-100 text-red-600 rounded-xl transition"
                            title="Tolak"
                          >
                            <X className="w-4 h-4" />
                          </button>
                        </div>
                      ) : (
                        <span className="text-[10px] font-bold text-slate-400">Terminal Aktif</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab 2: Rakan QR Table */}
      {activeTab === 'RAKAN_QR' && (
        <div className="bg-white rounded-3xl border border-slate-100 shadow-xs overflow-hidden space-y-4 p-5">
          <div>
            <h2 className="text-base font-extrabold text-[#0F2028]">Senarai Duta & Ejen Rakan QR</h2>
            <p className="text-xs text-slate-400">Pengurusan permohonan standee QR fizikal dan kelulusan duta komuniti</p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-50/75 border-b border-slate-100 text-[11px] font-extrabold text-slate-500 uppercase">
                  <th className="py-3.5 px-4">Nama Ejen</th>
                  <th className="py-3.5 px-4">Kategori</th>
                  <th className="py-3.5 px-4">Lokasi Standee</th>
                  <th className="py-3.5 px-4">Kod Ejen</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">Tindakan</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-xs font-semibold">
                {rakanApps.map((app) => (
                  <tr key={app.id} className="hover:bg-slate-50/50 transition">
                    <td className="py-4 px-4">
                      <p className="font-extrabold text-slate-800">{app.fullName}</p>
                      <span className="text-[10px] text-slate-400 font-normal">{app.phoneNumber}</span>
                    </td>
                    <td className="py-4 px-4">
                      <span className="px-2.5 py-1 bg-slate-100 text-slate-600 rounded-lg text-[10px] font-bold">
                        {app.organizationType}
                      </span>
                    </td>
                    <td className="py-4 px-4">
                      <span className="flex items-center gap-1 text-slate-600">
                        <MapPin className="w-3.5 h-3.5 text-slate-400" />
                        {app.placementLocation}
                      </span>
                    </td>
                    <td className="py-4 px-4 font-mono font-bold text-[#1A8C4E]">
                      {app.agentCode || 'Belum Dijana'}
                    </td>
                    <td className="py-4 px-4">
                      {app.status === 'APPROVED' ? (
                        <span className="inline-flex items-center gap-1 text-[10px] font-extrabold px-2.5 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
                          <ShieldCheck className="w-3 h-3" /> Diluluskan
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1 text-[10px] font-extrabold px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-700 border border-amber-200">
                          <ShieldAlert className="w-3 h-3" /> Menunggu
                        </span>
                      )}
                    </td>
                    <td className="py-4 px-4 text-right">
                      {app.status === 'PENDING' ? (
                        <div className="flex items-center justify-end gap-1.5">
                          <button
                            onClick={() => handleApproveRakan(app.id)}
                            className="p-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 rounded-xl transition"
                            title="Luluskan & Jana Kod"
                          >
                            <Check className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => handleRejectRakan(app.id)}
                            className="p-1.5 bg-red-50 hover:bg-red-100 text-red-600 rounded-xl transition"
                            title="Tolak"
                          >
                            <X className="w-4 h-4" />
                          </button>
                        </div>
                      ) : (
                        <span className="text-[10px] font-bold text-slate-400">Kit Aktif</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};
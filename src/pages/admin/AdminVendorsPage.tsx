import React, { useState } from 'react';
import {Check, X, ShieldAlert, ShieldCheck } from 'lucide-react';

export const AdminVendorsPage: React.FC = () => {
  const [vendors, setVendors] = useState([
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
  ]);

  const handleApprove = (id: string) => {
    setVendors(vendors.map((v) => (v.id === id ? { ...v, status: 'APPROVED' } : v)));
  };

  const handleReject = (id: string) => {
    setVendors(vendors.filter((v) => v.id !== id));
  };

  return (
    <div className="bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden space-y-4 p-5">
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
                        onClick={() => handleApprove(item.id)}
                        className="p-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 rounded-xl transition"
                        title="Luluskan"
                      >
                        <Check className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleReject(item.id)}
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
  );
};
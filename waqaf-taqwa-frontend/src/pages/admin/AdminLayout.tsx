import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { LayoutDashboard, FolderPlus, Users, ReceiptText, Settings, ArrowLeft } from 'lucide-react';

export const AdminLayout: React.FC = () => {
  const navigate = useNavigate();

  const navLinks = [
    { label: 'Ringkasan', icon: LayoutDashboard, path: '/admin', end: true },
    { label: 'Urus Kempen', icon: FolderPlus, path: '/admin/kempen' },
    { label: 'Kelulusan Peniaga', icon: Users, path: '/admin/peniaga' },
    { label: 'Audit Transaksi', icon: ReceiptText, path: '/admin/transaksi' },
    { label: 'Tetapan Sistem', icon: Settings, path: '/admin/tetapan' },
  ];

  return (
    <div className="space-y-6">
      {/* Top Breadcrumb Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-5 rounded-3xl border border-slate-100 shadow-sm">
        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate('/')}
            className="p-2.5 bg-slate-50 hover:bg-slate-100 text-slate-600 rounded-2xl transition"
            title="Kembali ke Laman Utama"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
          <div>
            <h1 className="text-xl font-extrabold text-[#0F2028]">Panel Admin Taqwa</h1>
            <p className="text-xs text-slate-400">Penyelarasan data masa nyata, kempen & pedagang</p>
          </div>
        </div>

        {/* Sub Navigation Bar */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0">
          {navLinks.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                end={item.end}
                className={({ isActive }) =>
                  `flex items-center gap-2 px-3.5 py-2 rounded-2xl text-xs font-bold transition-all whitespace-nowrap ${
                    isActive
                      ? 'bg-[#1A8C4E] text-white shadow-[0_4px_12px_rgba(26,140,78,0.25)]'
                      : 'bg-slate-50 text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  }`
                }
              >
                <Icon className="w-3.5 h-3.5" />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </div>
      </div>

      {/* Admin Subview Viewport */}
      <Outlet />
    </div>
  );
};
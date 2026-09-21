import React from 'react';
import { Routes, Route, Navigate, Link, useLocation } from 'react-router-dom';
import { 
  Home, 
  Layers, 
  QrCode, 
  Receipt, 
  User 
} from 'lucide-react';

import { HomePage } from './pages/user/HomePage';
import { ProjectsExplorePage } from './pages/user/ProjectsExplorePage';
import { ScanDonatePage } from './pages/user/ScanDonatePage';
import { TransactionHistoryPage } from './pages/user/TransactionHistoryPage';
import { ApplyTijarahPage } from './pages/user/ApplyTijarahPage';
import { ProfilePage } from './pages/user/ProfilePage';
import { RakanQrPage } from './pages/user/RakanQrPage';
import { LoginPage } from './pages/auth/LoginPage';
import { SignUpPage } from './pages/auth/SignUpPage';
import { AdminDashboardPage } from './pages/admin/AdminDashboardPage';

// Layout shell for Pewakaf (Standard User) pages only
const UserShell: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const location = useLocation();

  const navItems = [
    { path: '/', label: 'Utama', icon: Home },
    { path: '/projek', label: 'Projek', icon: Layers },
    { path: '/imbas', label: 'Waqaf', icon: QrCode },
    { path: '/transaksi', label: 'Transaksi', icon: Receipt },
    { path: '/profil', label: 'Profil', icon: User },
  ];

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-between">
      {/* User Header */}
      <header className="bg-[#1A8C4E] text-white px-4 md:px-8 py-3.5 shadow-sm sticky top-0 z-40">
        <div className="max-w-6xl mx-auto flex items-center justify-between">
          <Link to="/" className="flex items-center gap-2">
            <span className="font-black text-xl tracking-tight text-white">Waqaf Taqwa</span>
          </Link>

          <nav className="flex items-center gap-1 sm:gap-3">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-bold transition ${
                    isActive 
                      ? 'bg-white/20 text-white shadow-2xs' 
                      : 'text-emerald-100 hover:text-white hover:bg-white/10'
                  }`}
                >
                  <Icon className="w-3.5 h-3.5" />
                  <span>{item.label}</span>
                </Link>
              );
            })}
          </nav>
        </div>
      </header>

      {/* Page Body */}
      <main className="max-w-6xl w-full mx-auto p-4 md:p-6 flex-1">
        {children}
      </main>
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <Routes>
      {/* 1. Normal User Pages (with user navigation bar) */}
      <Route path="/" element={<UserShell><HomePage /></UserShell>} />
      <Route path="/projek" element={<UserShell><ProjectsExplorePage /></UserShell>} />
      <Route path="/imbas" element={<UserShell><ScanDonatePage /></UserShell>} />
      <Route path="/transaksi" element={<UserShell><TransactionHistoryPage /></UserShell>} />
      <Route path="/profil" element={<UserShell><ProfilePage /></UserShell>} />
      <Route path="/rakan-qr" element={<UserShell><RakanQrPage /></UserShell>} />
      <Route path="/apply-tijarah" element={<UserShell><ApplyTijarahPage /></UserShell>} />

      {/* 2. Authentication Pages */}
      <Route path="/auth/login" element={<LoginPage />} />
      <Route path="/auth/signup" element={<SignUpPage />} />

      {/* 3. Pure Admin Portal (NO user navbar) */}
      <Route path="/admin" element={<AdminDashboardPage />} />

      {/* Fallback to user home */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default App;
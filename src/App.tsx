import React, { useState } from 'react';
import { Routes, Route, Navigate, Link, useLocation } from 'react-router-dom';
import { 
  Home, 
  Layers, 
  QrCode, 
  Receipt, 
  User, 
  Menu, 
  X 
} from 'lucide-react';

import { HomePage } from './pages/user/HomePage';
import { ProjectsExplorePage } from './pages/user/ProjectsExplorePage';
import { ScanDonatePage } from './pages/user/ScanDonatePage';
import { TransactionHistoryPage } from './pages/user/TransactionHistoryPage';
import { ProfilePage } from './pages/user/ProfilePage';
import { RakanQrPage } from './pages/user/RakanQrPage';
import { ApplyTijarahPage } from './pages/user/ApplyTijarahPage';
import { LoginPage } from './pages/auth/LoginPage';
import { SignUpPage } from './pages/auth/SignUpPage';
import { AdminDashboardPage } from './pages/admin/AdminDashboardPage';

// Responsive User Layout Shell (Mobile Hamburger + Desktop Tabs)
const UserShell: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const location = useLocation();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const navItems = [
    { path: '/', label: 'Utama', icon: Home },
    { path: '/projek', label: 'Projek', icon: Layers },
    { path: '/imbas', label: 'Waqaf', icon: QrCode },
    { path: '/transaksi', label: 'Transaksi', icon: Receipt },
    { path: '/profil', label: 'Profil', icon: User },
  ];

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-between">
      {/* Top Navbar */}
      <header className="bg-[#1A8C4E] text-white px-4 md:px-8 py-3.5 shadow-sm sticky top-0 z-40">
        <div className="max-w-6xl mx-auto flex items-center justify-between">
          {/* Logo */}
          <Link 
            to="/" 
            onClick={() => setIsMobileMenuOpen(false)}
            className="flex items-center gap-2"
          >
            <span className="font-black text-xl tracking-tight text-white">Waqaf Taqwa</span>
          </Link>

          {/* Desktop Navigation Links (md and up) */}
          <nav className="hidden md:flex items-center gap-2">
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

          {/* Mobile Hamburger Toggle Button */}
          <button
            type="button"
            onClick={() => setIsMobileMenuOpen((prev) => !prev)}
            aria-label="Toggle navigation menu"
            className="md:hidden p-2 rounded-xl text-emerald-100 hover:text-white hover:bg-white/10 transition active:scale-95"
          >
            {isMobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>

        {/* Mobile Dropdown Menu */}
        {isMobileMenuOpen && (
          <div className="md:hidden mt-3 pt-3 border-t border-white/15 animate-in fade-in slide-in-from-top-2 duration-200">
            <nav className="flex flex-col gap-1">
              {navItems.map((item) => {
                const Icon = item.icon;
                const isActive = location.pathname === item.path;
                return (
                  <Link
                    key={item.path}
                    to={item.path}
                    onClick={() => setIsMobileMenuOpen(false)}
                    className={`flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-bold transition ${
                      isActive 
                        ? 'bg-white/20 text-white shadow-2xs' 
                        : 'text-emerald-100 hover:text-white hover:bg-white/10'
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span>{item.label}</span>
                  </Link>
                );
              })}
            </nav>
          </div>
        )}
      </header>

      {/* Main Content Area */}
      <main className="max-w-6xl w-full mx-auto p-4 md:p-6 flex-1">
        {children}
      </main>
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <Routes>
      {/* 1. Public User Pages (wrapped in UserShell with responsive nav) */}
      <Route path="/" element={<UserShell><HomePage /></UserShell>} />
      <Route path="/projek" element={<UserShell><ProjectsExplorePage /></UserShell>} />
      <Route path="/imbas" element={<UserShell><ScanDonatePage /></UserShell>} />
      <Route path="/transaksi" element={<UserShell><TransactionHistoryPage /></UserShell>} />
      <Route path="/profil" element={<UserShell><ProfilePage /></UserShell>} />
      <Route path="/rakan-qr" element={<UserShell><RakanQrPage /></UserShell>} />
      <Route path="/tijarah" element={<UserShell><ApplyTijarahPage /></UserShell>} />

      {/* 2. Authentication Pages */}
      <Route path="/auth/login" element={<LoginPage />} />
      <Route path="/auth/signup" element={<SignUpPage />} />

      {/* 3. Pure Admin Portal (isolated, no user nav) */}
      <Route path="/admin" element={<AdminDashboardPage />} />

      {/* Catch-all fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default App;
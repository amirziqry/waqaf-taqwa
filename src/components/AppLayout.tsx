import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Home, LayoutGrid, QrCode, ReceiptText, User } from 'lucide-react';

interface LayoutProps {
  children: React.ReactNode;
  hideNav?: boolean;
}

export const AppLayout: React.FC<LayoutProps> = ({ children, hideNav = false }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const navItems = [
    { label: 'Utama', icon: Home, path: '/' },
    { label: 'Projek', icon: LayoutGrid, path: '/projek' },
    { label: 'Imbas QR', icon: QrCode, path: '/imbas' },
    { label: 'Transaksi', icon: ReceiptText, path: '/transaksi' },
    { label: 'Profil', icon: User, path: '/profil' },
  ];

  return (
    <div className="min-h-screen bg-[#F8FAFC] flex flex-col font-sans antialiased text-slate-800">
      {!hideNav && (
        <header className="hidden md:flex bg-[#1A8C4E] text-white px-8 py-3.5 items-center justify-between sticky top-0 z-40 shadow-sm">
          <div className="flex items-center gap-3 cursor-pointer" onClick={() => navigate('/')}>
            <span className="font-extrabold text-xl tracking-tight">Waqaf Taqwa</span>
          </div>

          <nav className="flex items-center gap-2">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              return (
                <button
                  key={item.path}
                  onClick={() => navigate(item.path)}
                  className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                    isActive
                      ? 'bg-white/20 text-white shadow-inner'
                      : 'text-emerald-100 hover:text-white hover:bg-white/10'
                  }`}
                >
                  {Icon && <Icon className="w-4 h-4" />}
                  <span>{item.label}</span>
                </button>
              );
            })}
          </nav>
        </header>
      )}

      <main className="flex-1 w-full max-w-6xl mx-auto md:px-6 md:py-8 pb-24 md:pb-12">
        {children}
      </main>

      {!hideNav && (
        <nav className="md:hidden fixed bottom-0 inset-x-0 bg-white/95 backdrop-blur-md border-t border-slate-100 px-3 py-2 flex justify-around items-center z-30 shadow-lg">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path;
            return (
              <button
                key={item.path}
                onClick={() => navigate(item.path)}
                className={`flex flex-col items-center gap-0.5 py-1 px-3 transition-colors ${
                  isActive ? 'text-[#1A8C4E] font-bold' : 'text-slate-400 hover:text-slate-600'
                }`}
              >
                {Icon && <Icon className={`w-5 h-5 ${isActive ? 'stroke-[2.5]' : 'stroke-2'}`} />}
                <span className="text-[10px] tracking-tight">{item.label}</span>
              </button>
            );
          })}
        </nav>
      )}
    </div>
  );
};
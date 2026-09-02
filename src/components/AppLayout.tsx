import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Home, LayoutGrid, QrCode, ReceiptText, User, ShieldCheck } from 'lucide-react';

interface LayoutProps {
  children: React.ReactNode;
  hideNav?: boolean;
}

export const AppLayout: React.FC<LayoutProps> = ({ children, hideNav = false }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const userRole = localStorage.getItem('wt_user_role');
  const isAdmin = userRole === 'member';

  const navItems = [
    { label: 'Utama', icon: Home, path: '/' },
    { label: 'Projek', icon: LayoutGrid, path: '/projek' },
    { label: 'Waqaf', icon: QrCode, path: '/imbas' },
    { label: 'Transaksi', icon: ReceiptText, path: '/transaksi' },
    { label: 'Profil', icon: User, path: '/profil' },
  ];

  if (isAdmin) {
    navItems.push({ label: 'Admin', icon: ShieldCheck, path: '/admin' });
  }

  return (
    <div className="min-h-screen bg-[#F8FAFC] flex flex-col font-sans antialiased text-slate-800">
      {!hideNav && (
        <header className="hidden md:flex bg-[#1A8C4E] text-white px-8 lg:px-12 py-3.5 items-center justify-between sticky top-0 z-40 shadow-sm">
          <div
            className="flex items-center gap-3 cursor-pointer select-none"
            onClick={() => navigate(isAdmin ? '/admin' : '/')}
          >
            <span className="font-extrabold text-xl tracking-tight">Waqaf Taqwa</span>
            {isAdmin && (
              <span className="bg-emerald-800 text-emerald-100 text-[10px] font-bold px-2.5 py-0.5 rounded-full border border-emerald-600">
                Admin
              </span>
            )}
          </div>

          <nav className="flex items-center gap-2">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path || (item.path !== '/' && location.pathname.startsWith(item.path));
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

      {/* Responsive Main Viewport: Scales from Mobile (w-full) to Desktop (max-w-7xl) */}
      <main className="flex-1 w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8 pb-24 md:pb-12">
        {children}
      </main>

      {!hideNav && (
        <nav className="md:hidden fixed bottom-0 inset-x-0 bg-white/95 backdrop-blur-md border-t border-slate-100 px-3 py-2 flex justify-around items-center z-30 shadow-lg">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path || (item.path !== '/' && location.pathname.startsWith(item.path));
            return (
              <button
                key={item.path}
                onClick={() => navigate(item.path)}
                className={`flex flex-col items-center gap-0.5 py-1 px-2 transition-colors ${
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
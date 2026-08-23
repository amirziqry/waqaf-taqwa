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
    <div className="min-h-screen bg-slate-100 flex justify-center items-center py-0 md:py-8 font-sans antialiased text-slate-800">
      {/* Mobile container centered on Desktop */}
      <div className="w-full max-w-[420px] bg-[#FAFAF9] min-h-screen md:min-h-[860px] md:max-h-[92vh] md:rounded-[40px] md:shadow-2xl flex flex-col relative overflow-hidden border border-slate-200/80">
        
        {/* Main Content Area */}
        <div className="flex-1 overflow-y-auto no-scrollbar pb-20">
          {children}
        </div>

        {/* Bottom Tab Navigation */}
        {!hideNav && (
          <nav className="absolute bottom-0 inset-x-0 bg-white/95 backdrop-blur-md border-t border-slate-100 px-3 py-2 flex justify-around items-center z-30">
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
                  <Icon className={`w-5 h-5 ${isActive ? 'stroke-[2.5]' : 'stroke-2'}`} />
                  <span className="text-[10px] tracking-tight">{item.label}</span>
                </button>
              );
            })}
          </nav>
        )}
      </div>
    </div>
  );
};
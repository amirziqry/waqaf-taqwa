import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, ShieldCheck, CreditCard, LogOut, ChevronRight, Bell, HeartHandshake, LayoutDashboard } from 'lucide-react';
import api from '../../api/client';

export const ProfilePage: React.FC = () => {
  const navigate = useNavigate();
  const [profile, setProfile] = useState<{ username: string; email?: string } | null>(null);
  const [totalDonated, setTotalDonated] = useState<number>(0.0);
  const [role, setRole] = useState<string>('donator');

  useEffect(() => {
    const activeRole = localStorage.getItem('wt_user_role') || 'donator';
    const storedUsername = localStorage.getItem('wt_user_name') || 'Pengguna';
    setRole(activeRole);

    // Initial state matching stored local session
    setProfile({
      username: storedUsername,
      email: `${storedUsername}@taqwa.com`,
    });

    // 1. Fetch live authenticated user details
    const userEndpoint =
      activeRole === 'member'
        ? '/member/auth/me'
        : activeRole === 'vendor'
        ? '/vendor/auth/me'
        : '/donator/auth/me';

    api.get(userEndpoint)
      .then((res) => {
        if (res.data?.username) {
          setProfile(res.data);
          localStorage.setItem('wt_user_name', res.data.username);
        }
      })
      .catch(() => {
        // Fallback remains the logged-in username stored during login
      });

    // 2. Fetch live real-time donation total (Defaults to 0.00 if new user)
    const donationEndpoint =
      activeRole === 'vendor'
        ? '/vendor/transactions/sum'
        : '/donator/donation/sum';

    api.get(donationEndpoint)
      .then((res) => {
        const sum = typeof res.data === 'number' ? res.data : res.data?.total || 0.0;
        setTotalDonated(Number(sum));
      })
      .catch(() => {
        // Look up local transaction records or default strictly to 0
        try {
          const localTransactions = JSON.parse(localStorage.getItem('wt_transactions') || '[]');
          const sum = localTransactions.reduce((acc: number, item: any) => acc + Number(item.amount || 0), 0);
          setTotalDonated(sum);
        } catch {
          setTotalDonated(0.0);
        }
      });
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('wt_user_role');
    localStorage.removeItem('wt_user_name');
    navigate('/auth/login');
  };

  const getRoleLabel = () => {
    if (role === 'member') return 'Pentadbir';
    if (role === 'vendor') return 'Peniaga';
    return 'Pewakaf';
  };

  return (
    <div className="p-4 space-y-6 max-w-2xl mx-auto pb-12">
      {/* Profile Header Card */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200/80 shadow-xs flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-emerald-50 border border-emerald-200 flex items-center justify-center text-[#1A8C4E]">
            <User className="w-8 h-8" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h2 className="font-extrabold text-base text-[#0F2028]">
                {profile?.username || 'Pengguna'}
              </h2>
              <span className="text-[10px] uppercase font-black px-2 py-0.5 bg-[#EBF7F0] text-[#1A8C4E] rounded-md">
                {getRoleLabel()}
              </span>
            </div>
            <p className="text-xs text-slate-400 mt-0.5">{profile?.email || 'Akaun Terpelihara'}</p>
          </div>
        </div>

        {role === 'member' && (
          <button
            onClick={() => navigate('/admin')}
            className="flex items-center gap-1.5 px-3.5 py-2 bg-[#1A8C4E] hover:bg-[#15703E] text-white text-xs font-bold rounded-xl shadow-xs transition"
          >
            <LayoutDashboard className="w-3.5 h-3.5" />
            <span>Portal Admin</span>
          </button>
        )}
      </div>

      {/* Dynamic Summary Stat Box */}
      <div className="bg-gradient-to-r from-[#1A8C4E] to-[#146C3C] text-white rounded-3xl p-5 shadow-xs space-y-3">
        <div className="flex items-center justify-between">
          <span className="text-xs font-semibold text-emerald-100">
            {role === 'vendor' ? 'Jumlah Kutipan SoftPOS Anda' : 'Jumlah Infaq & Waqaf Anda'}
          </span>
          <HeartHandshake className="w-5 h-5 text-emerald-200" />
        </div>
        <p className="text-3xl font-black">RM {totalDonated.toFixed(2)}</p>
        <p className="text-[11px] text-emerald-100">Semua transaksi dilindungi rekod kriptografi LHDN.</p>
      </div>

      {/* Menu Settings Group */}
      <div className="bg-white rounded-3xl border border-slate-200/80 overflow-hidden divide-y divide-slate-100 shadow-xs">
        <div
          onClick={() => navigate('/transaksi')}
          className="p-4 flex items-center justify-between hover:bg-slate-50 cursor-pointer transition"
        >
          <div className="flex items-center gap-3">
            <CreditCard className="w-5 h-5 text-slate-400" />
            <span className="text-xs font-bold text-slate-800">Sejarah Transaksi</span>
          </div>
          <ChevronRight className="w-4 h-4 text-slate-400" />
        </div>

        {role !== 'vendor' && (
          <div
            onClick={() => navigate('/auto-waqaf')}
            className="p-4 flex items-center justify-between hover:bg-slate-50 cursor-pointer transition"
          >
            <div className="flex items-center gap-3">
              <ShieldCheck className="w-5 h-5 text-slate-400" />
              <span className="text-xs font-bold text-slate-800">Tetapan Auto Waqaf Subuh</span>
            </div>
            <ChevronRight className="w-4 h-4 text-slate-400" />
          </div>
        )}

        <div className="p-4 flex items-center justify-between hover:bg-slate-50 cursor-pointer transition">
          <div className="flex items-center gap-3">
            <Bell className="w-5 h-5 text-slate-400" />
            <span className="text-xs font-bold text-slate-800">Pemberitahuan & Laporan Projek</span>
          </div>
          <ChevronRight className="w-4 h-4 text-slate-400" />
        </div>
      </div>

      {/* Sign Out Button */}
      <button
        onClick={handleLogout}
        className="w-full h-12 bg-rose-50 hover:bg-rose-100 text-rose-600 font-bold rounded-2xl text-xs flex items-center justify-center gap-2 border border-rose-200/60 transition active:scale-[0.99]"
      >
        <LogOut className="w-4 h-4" /> Log Keluar
      </button>
    </div>
  );
};
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, ShieldCheck, CreditCard, LogOut, ChevronRight, Bell, HeartHandshake } from 'lucide-react';
import api from '../../api/client';

export const ProfilePage: React.FC = () => {
  const navigate = useNavigate();
  const [profile, setProfile] = useState<{ username: string; email?: string } | null>(null);
  const [totalDonated, setTotalDonated] = useState<number>(0);
  const [role, setRole] = useState<string>('donator');
  const [, setLoading] = useState(true);

  useEffect(() => {
    const fetchUserData = async () => {
      const activeRole = localStorage.getItem('wt_user_role') || 'donator';
      setRole(activeRole);

      try {
        const endpoint = activeRole === 'member' ? '/member/me' : '/donator/auth/me';
        const userRes = await api.get(endpoint);
        setProfile(userRes.data);

        // Fetch user donation sum placeholder
        const sumRes = await api.get('/donator/donation/sum');
        setTotalDonated(sumRes.data?.total || 150.0);
      } catch (err) {
        // Fallback placeholder display if session not active
        setProfile({
          username: localStorage.getItem('wt_user_name') || 'Ahmad Faiz',
          email: 'ahmad.faiz@example.com',
        });
        setTotalDonated(150.0);
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('wt_user_role');
    localStorage.removeItem('wt_user_name');
    navigate('/auth/login');
  };

  return (
    <div className="p-4 md:p-0 space-y-6 max-w-2xl mx-auto">
      {/* Profile Header Card */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-emerald-50 border border-emerald-200 flex items-center justify-center text-[#1A8C4E]">
            <User className="w-8 h-8" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h2 className="font-extrabold text-base text-[#0F2028]">{profile?.username || 'Pewakaf Taqwa'}</h2>
              <span className="text-[10px] uppercase font-black px-2 py-0.5 bg-[#EBF7F0] text-[#1A8C4E] rounded-md">
                {role}
              </span>
            </div>
            <p className="text-xs text-slate-400 mt-0.5">{profile?.email || 'Akaun Terpelihara'}</p>
          </div>
        </div>

        {role === 'member' && (
          <button
            onClick={() => navigate('/admin')}
            className="px-3 py-1.5 bg-[#1A8C4E] text-white text-xs font-bold rounded-xl shadow-sm hover:bg-[#15703E]"
          >
            Portal Admin
          </button>
        )}
      </div>

      {/* Summary Stat Box */}
      <div className="bg-linear-to-r from-[#1A8C4E] to-[#146C3C] text-white rounded-3xl p-5 shadow-sm space-y-3">
        <div className="flex items-center justify-between">
          <span className="text-xs font-semibold text-emerald-100">Jumlah Infaq & Waqaf Anda</span>
          <HeartHandshake className="w-5 h-5 text-emerald-200" />
        </div>
        <p className="text-3xl font-black">RM {totalDonated.toFixed(2)}</p>
        <p className="text-[11px] text-emerald-100">Semua transaksi dilindungi rekod kriptografi LHDN.</p>
      </div>

      {/* Menu Settings Group */}
      <div className="bg-white rounded-3xl border border-slate-200/80 overflow-hidden divide-y divide-slate-100">
        <div
          onClick={() => navigate('/transaksi')}
          className="p-4 flex items-center justify-between hover:bg-slate-50 cursor-pointer transition"
        >
          <div className="flex items-center gap-3">
            <CreditCard className="w-5 h-5 text-slate-400" />
            <span className="text-xs font-bold text-slate-800">Sejarah Transaksi & Resit LHDN</span>
          </div>
          <ChevronRight className="w-4 h-4 text-slate-400" />
        </div>

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
        className="w-full h-12 bg-rose-50 hover:bg-rose-100 text-rose-600 font-bold rounded-2xl text-xs flex items-center justify-center gap-2 border border-rose-200/60 transition"
      >
        <LogOut className="w-4 h-4" /> Log Keluar
      </button>
    </div>
  );
};
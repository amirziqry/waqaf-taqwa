import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  User, 
  ShieldCheck, 
  CreditCard, 
  LogOut, 
  ChevronRight, 
  HeartHandshake, 
  LayoutDashboard,
  Award,
  Copy,
  Check,
  ExternalLink,
  QrCode,
  Clock
} from 'lucide-react';
import api from '../../api/client';

interface DutaApplication {
  id: string;
  username?: string;
  membershipTier: 'BIASA' | 'DUTA';
  fullName: string;
  agentCode: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  placementLocation?: string;
}

export const ProfilePage: React.FC = () => {
  const navigate = useNavigate();
  const [profile, setProfile] = useState<{ username: string; email?: string } | null>(null);
  const [totalDonated, setTotalDonated] = useState<number>(0.0);
  const [role, setRole] = useState<string>('donator');
  const [dutaData, setDutaData] = useState<DutaApplication | null>(null);
  const [copiedCode, setCopiedCode] = useState(false);
  const [copiedLink, setCopiedLink] = useState(false);

  useEffect(() => {
    const activeRole = localStorage.getItem('wt_user_role') || 'donator';
    const storedUsername = localStorage.getItem('wt_user_name') || 'Pengguna';
    setRole(activeRole);

    setProfile({
      username: storedUsername,
      email: `${storedUsername}@taqwa.com`,
    });

    // Check application specific ONLY to the logged-in user
    const userAppKey = `wt_rakan_qr_app_${storedUsername}`;
    const userApp = localStorage.getItem(userAppKey);

    if (userApp) {
      try {
        const parsed: DutaApplication = JSON.parse(userApp);
        if (parsed.membershipTier === 'DUTA') {
          setDutaData(parsed);
        }
      } catch {}
    } else {
      // Fallback check against global admin list for this exact username
      const adminList: any[] = JSON.parse(localStorage.getItem('wt_admin_rakan_apps') || '[]');
      const userMatch = adminList.find((app) => app.username === storedUsername);
      if (userMatch && userMatch.membershipTier === 'DUTA') {
        setDutaData(userMatch);
        localStorage.setItem(userAppKey, JSON.stringify(userMatch));
      } else {
        setDutaData(null);
      }
    }

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
      .catch(() => {});

    // 2. Fetch live donation total
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

  const paymentLink = dutaData 
    ? `${window.location.origin}/imbas?ref=${dutaData.agentCode}` 
    : '';

  const copyToClipboard = (text: string, type: 'code' | 'link') => {
    navigator.clipboard.writeText(text);
    if (type === 'code') {
      setCopiedCode(true);
      setTimeout(() => setCopiedCode(false), 2000);
    } else {
      setCopiedLink(true);
      setTimeout(() => setCopiedLink(false), 2000);
    }
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

      {/* Ahli Duta Dynamic Card (Scoped to applicant only) */}
      {dutaData && (
        dutaData.status === 'APPROVED' ? (
          /* UNLOCKED: Approved State */
          <div className="bg-gradient-to-b from-amber-50/70 via-white to-amber-50/30 rounded-3xl p-5 border-2 border-amber-300 shadow-xs space-y-4">
            <div className="flex items-center justify-between border-b border-amber-200/60 pb-3">
              <div className="flex items-center gap-2.5">
                <div className="p-2 bg-amber-100 text-amber-700 rounded-xl">
                  <Award className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-xs font-black text-amber-950 uppercase tracking-wide">
                    Status: Duta Waqaf Komuniti (Disahkan)
                  </h3>
                  <p className="text-[10px] text-amber-700/80 font-semibold">
                    Lokasi Premis: {dutaData.placementLocation || 'Kaunter Rasmi'}
                  </p>
                </div>
              </div>
              <button
                onClick={() => navigate('/rakan-qr')}
                className="text-[11px] font-bold text-amber-800 hover:text-amber-950 flex items-center gap-1 transition"
              >
                <QrCode className="w-3.5 h-3.5" />
                <span>Lihat Standee</span>
              </button>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {/* Referral Code Box */}
              <div className="bg-white p-3.5 rounded-2xl border border-amber-200/70 flex flex-col justify-between shadow-2xs">
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Kod Rujukan Duta</span>
                <div className="flex items-center justify-between mt-1">
                  <span className="text-sm font-black text-[#0F2028] tracking-wide font-mono">
                    {dutaData.agentCode}
                  </span>
                  <button
                    onClick={() => copyToClipboard(dutaData.agentCode, 'code')}
                    className="flex items-center gap-1 px-2.5 py-1 bg-amber-50 hover:bg-amber-100 text-amber-800 font-bold text-[10px] rounded-lg transition"
                  >
                    {copiedCode ? <Check className="w-3 h-3 text-emerald-600" /> : <Copy className="w-3 h-3" />}
                    <span>{copiedCode ? 'Disalin' : 'Salin'}</span>
                  </button>
                </div>
              </div>

              {/* Direct Payment Link Box */}
              <div className="bg-white p-3.5 rounded-2xl border border-amber-200/70 flex flex-col justify-between shadow-2xs">
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Pautan Bayaran Terus</span>
                <div className="flex items-center justify-between mt-1">
                  <span className="text-[11px] font-semibold text-slate-600 truncate max-w-[130px]" title={paymentLink}>
                    {paymentLink}
                  </span>
                  <button
                    onClick={() => copyToClipboard(paymentLink, 'link')}
                    className="flex items-center gap-1 px-2.5 py-1 bg-amber-600 hover:bg-amber-700 text-white font-bold text-[10px] rounded-lg shadow-xs transition"
                  >
                    {copiedLink ? <Check className="w-3 h-3 text-white" /> : <ExternalLink className="w-3 h-3" />}
                    <span>{copiedLink ? 'Disalin' : 'Salin Pautan'}</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        ) : (
          /* LOCKED: Pending Review Notice */
          <div className="bg-gradient-to-r from-amber-50 to-orange-50 rounded-3xl p-5 border border-amber-200/80 shadow-xs flex items-start gap-3.5">
            <div className="p-2.5 bg-amber-100 text-amber-700 rounded-2xl shrink-0 mt-0.5">
              <Clock className="w-5 h-5 animate-pulse" />
            </div>
            <div className="space-y-1">
              <div className="flex items-center gap-2">
                <h4 className="text-xs font-black text-amber-950 uppercase tracking-wide">
                  Permohonan Duta Sedang Disemak
                </h4>
                <span className="text-[9px] font-extrabold px-2 py-0.5 bg-amber-200/70 text-amber-900 rounded-full">
                  Menunggu Kelulusan
                </span>
              </div>
              <p className="text-[11px] text-amber-900/80 leading-relaxed">
                Permohonan standee fizikal anda ({dutaData.agentCode}) sedang dinilai oleh pentadbir. Pautan bayaran terus dan kod rujukan aktif akan dipaparkan di sini selepas kelulusan diberikan.
              </p>
            </div>
          </div>
        )
      )}

      {/* Dynamic Summary Stat Box */}
      <div className="bg-gradient-to-r from-[#1A8C4E] to-[#146C3C] text-white rounded-3xl p-5 shadow-xs space-y-3">
        <div className="flex items-center justify-between">
          <span className="text-xs font-semibold text-emerald-100">
            {role === 'vendor' ? 'Jumlah Kutipan SoftPOS Anda' : 'Jumlah Infaq & Waqaf Anda'}
          </span>
          <HeartHandshake className="w-5 h-5 text-emerald-200" />
        </div>
        <p className="text-3xl font-black">RM {totalDonated.toFixed(2)}</p>
        <p className="text-[11px] text-emerald-100">Semua transaksi dilindungi rekod berpusat.</p>
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
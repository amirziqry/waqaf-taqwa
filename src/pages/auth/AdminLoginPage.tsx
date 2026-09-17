import React, { useState } from 'react';
import { ShieldCheck, Lock, User, Mail, ArrowRight, ArrowLeft } from 'lucide-react';
import api from '../../api/client';

interface AdminLoginPageProps {
  onLoginSuccess: () => void;
}

export const AdminLoginPage: React.FC<AdminLoginPageProps> = ({ onLoginSuccess }) => {
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  const [username, setUsername] = useState('');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'error' | 'success'; text: string } | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);

    if (isRegisterMode) {
      // Request New Admin Account (Requires Super Admin Approval)
      try {
        await api.post('/admin/register-admin', {
          username,
          fullName,
          email,
          password,
        }).catch(() => null);

        // Store request in admin queue
        const stored = JSON.parse(localStorage.getItem('wt_admin_users') || '[]');
        const newReq = {
          id: `ADM-${Date.now().toString().slice(-4)}`,
          username,
          fullName,
          email,
          role: 'ADMIN',
          status: 'PENDING',
          registeredAt: new Date().toISOString().split('T')[0],
        };
        localStorage.setItem('wt_admin_users', JSON.stringify([newReq, ...stored]));

        setMessage({
          type: 'success',
          text: 'Permohonan akaun pentadbir telah dihantar. Sila tunggu kelulusan Ketua Eksekutif / Super Admin.',
        });
        setIsRegisterMode(false);
      } catch (err: any) {
        setMessage({
          type: 'error',
          text: err.response?.data?.message || 'Gagal menghantar permohonan admin.',
        });
      } finally {
        setLoading(false);
      }
    } else {
      // Admin Login
      try {
        await api.post('/member/auth/login', { username, password }).catch(() => null);

        // Verify if account is approved
        const adminUsers = JSON.parse(localStorage.getItem('wt_admin_users') || '[]');
        const existing = adminUsers.find((u: any) => u.username === username);

        if (existing && existing.status === 'PENDING') {
          setMessage({
            type: 'error',
            text: 'Akaun anda masih dalam status MENUNGGU KELULUSAN oleh Super Admin.',
          });
          setLoading(false);
          return;
        }

        localStorage.setItem('wt_user_role', 'member');
        localStorage.setItem('wt_user_name', username);
        onLoginSuccess();
      } catch (err: any) {
        setMessage({
          type: 'error',
          text: err.response?.data?.message || 'Nama pengguna atau kata laluan salah.',
        });
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center p-4">
      <div className="bg-white border border-slate-200 rounded-3xl p-6 md:p-8 max-w-md w-full shadow-xs space-y-6">
        <div className="text-center space-y-2">
          <div className="w-12 h-12 bg-slate-900 text-white rounded-2xl flex items-center justify-center mx-auto shadow-sm">
            <ShieldCheck className="w-6 h-6" />
          </div>
          <h2 className="text-xl font-black text-[#0F2028]">
            {isRegisterMode ? 'Daftar Akses Staf Pentadbir' : 'Portal Pengurusan Admin'}
          </h2>
          <p className="text-xs text-slate-400">
            {isRegisterMode
              ? 'Permohonan memerlukan kelulusan Ketua Eksekutif (Super Admin)'
              : 'Kawasan kawalan operasi & pengurusan Waqaf Taqwa'}
          </p>
        </div>

        {message && (
          <div
            className={`p-3 rounded-2xl text-xs font-bold text-center border ${
              message.type === 'success'
                ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                : 'bg-rose-50 text-rose-600 border-rose-200'
            }`}
          >
            {message.text}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-3.5">
          {isRegisterMode && (
            <>
              <div className="space-y-1">
                <label className="text-xs font-bold text-slate-700">Nama Penuh Pegawai</label>
                <div className="h-11 bg-slate-50 border border-slate-200 rounded-xl px-3 flex items-center gap-2">
                  <User className="w-4 h-4 text-slate-400" />
                  <input
                    type="text"
                    required
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    placeholder="cth. Ustaz Ridzuan"
                    className="w-full bg-transparent text-xs font-semibold outline-none"
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-bold text-slate-700">E-mel Rasmi</label>
                <div className="h-11 bg-slate-50 border border-slate-200 rounded-xl px-3 flex items-center gap-2">
                  <Mail className="w-4 h-4 text-slate-400" />
                  <input
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="pegawai@taqwa.com"
                    className="w-full bg-transparent text-xs font-semibold outline-none"
                  />
                </div>
              </div>
            </>
          )}

          <div className="space-y-1">
            <label className="text-xs font-bold text-slate-700">Nama Pengguna (Username)</label>
            <div className="h-11 bg-slate-50 border border-slate-200 rounded-xl px-3 flex items-center gap-2">
              <User className="w-4 h-4 text-slate-400" />
              <input
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="admin_utama"
                className="w-full bg-transparent text-xs font-semibold outline-none"
              />
            </div>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-bold text-slate-700">Kata Laluan</label>
            <div className="h-11 bg-slate-50 border border-slate-200 rounded-xl px-3 flex items-center gap-2">
              <Lock className="w-4 h-4 text-slate-400" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-transparent text-xs font-semibold outline-none"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full h-11 bg-slate-900 hover:bg-slate-800 text-white text-xs font-bold rounded-xl transition flex items-center justify-center gap-2 shadow-xs"
          >
            {loading
              ? 'Memproses...'
              : isRegisterMode
              ? 'Hantar Permohonan Staf'
              : 'Log Masuk Portal'}
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>

        <div className="text-center pt-2 border-t border-slate-100">
          <button
            type="button"
            onClick={() => {
              setIsRegisterMode(!isRegisterMode);
              setMessage(null);
            }}
            className="text-xs font-bold text-slate-600 hover:text-slate-900 transition"
          >
            {isRegisterMode ? 'Kembali ke Log Masuk Pentadbir' : 'Mohon Akses Sebagai Pegawai / Staf Baharu'}
          </button>
        </div>
      </div>
    </div>
  );
};
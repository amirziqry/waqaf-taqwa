import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Lock, User, Store, Shield, ArrowRight, Eye, EyeOff } from 'lucide-react';
import api from '../../api/client';

type RoleType = 'donator' | 'vendor' | 'member';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const [role, setRole] = useState<RoleType>('donator');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setErrorMessage('');

    let endpoint = '/donator/auth/login';
    if (role === 'vendor') endpoint = '/vendor/auth/login';
    if (role === 'member') endpoint = '/member/auth/login';

    try {
      const response = await api.post(endpoint, { username, password });
      
      // Store current user metadata locally
      localStorage.setItem('wt_user_role', role);
      localStorage.setItem('wt_user_name', response.data.username || username);
      
      if (role === 'member') {
        navigate('/admin');
      } else {
        navigate('/profil');
      }
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Log masuk gagal. Sila semak nama pengguna dan kata laluan.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-5 md:p-10 max-w-md mx-auto space-y-6">
      {/* Header */}
      <div className="text-center space-y-1.5">
        <h1 className="text-2xl font-black text-[#0F2028]">Log Masuk</h1>
        <p className="text-xs text-slate-500">Pilih peranan akaun anda untuk meneruskan</p>
      </div>

      {/* Role Switcher Tabs */}
      <div className="grid grid-cols-3 gap-2 p-1 bg-slate-100 rounded-2xl border border-slate-200">
        <button
          type="button"
          onClick={() => { setRole('donator'); setErrorMessage(''); }}
          className={`py-2 px-2 rounded-xl text-xs font-bold flex items-center justify-center gap-1.5 transition ${
            role === 'donator' ? 'bg-[#1A8C4E] text-white shadow-sm' : 'text-slate-600 hover:text-slate-900'
          }`}
        >
          <User className="w-3.5 h-3.5" /> Pewakaf
        </button>
        <button
          type="button"
          onClick={() => { setRole('vendor'); setErrorMessage(''); }}
          className={`py-2 px-2 rounded-xl text-xs font-bold flex items-center justify-center gap-1.5 transition ${
            role === 'vendor' ? 'bg-[#1A8C4E] text-white shadow-sm' : 'text-slate-600 hover:text-slate-900'
          }`}
        >
          <Store className="w-3.5 h-3.5" /> Peniaga
        </button>
        <button
          type="button"
          onClick={() => { setRole('member'); setErrorMessage(''); }}
          className={`py-2 px-2 rounded-xl text-xs font-bold flex items-center justify-center gap-1.5 transition ${
            role === 'member' ? 'bg-[#1A8C4E] text-white shadow-sm' : 'text-slate-600 hover:text-slate-900'
          }`}
        >
          <Shield className="w-3.5 h-3.5" /> Pentadbir
        </button>
      </div>

      {/* Error Alert */}
      {errorMessage && (
        <div className="p-3 bg-red-50 border border-red-200 rounded-2xl text-[11px] font-semibold text-red-600 text-center">
          {errorMessage}
        </div>
      )}

      {/* Login Form */}
      <form onSubmit={handleLogin} className="space-y-4">
        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Nama Pengguna</label>
          <div className="h-12 bg-white border border-slate-200 rounded-2xl px-4 flex items-center gap-2.5 focus-within:border-[#1A8C4E] transition">
            <User className="w-4 h-4 text-slate-400" />
            <input
              type="text"
              required
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Contoh: ahmad_taqwa"
              className="w-full bg-transparent text-xs font-semibold outline-none text-slate-800"
            />
          </div>
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Kata Laluan</label>
          <div className="h-12 bg-white border border-slate-200 rounded-2xl px-4 flex items-center gap-2.5 focus-within:border-[#1A8C4E] transition">
            <Lock className="w-4 h-4 text-slate-400" />
            <input
              type={showPassword ? 'text' : 'password'}
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              className="w-full bg-transparent text-xs font-semibold outline-none text-slate-800"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="text-slate-400 hover:text-slate-600"
            >
              {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] disabled:bg-slate-300 text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99] mt-2"
        >
          {loading ? 'Sedang Memproses...' : 'Log Masuk'}
          {!loading && <ArrowRight className="w-4 h-4" />}
        </button>
      </form>

      <div className="text-center pt-2">
        <p className="text-xs text-slate-500">
          Belum mempunyai akaun?{' '}
          <Link to="/auth" className="text-[#1A8C4E] font-bold hover:underline">
            Daftar Sekarang
          </Link>
        </p>
      </div>
    </div>
  );
};
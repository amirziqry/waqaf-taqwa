import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Lock, User, ArrowRight, Eye, EyeOff } from 'lucide-react';
import api from '../../api/client';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setErrorMessage('');

    try {
      await api.post('/personal/auth/login', { username, password });
      
      localStorage.setItem('wt_user_role', 'donator');
      localStorage.setItem('wt_user_name', username);
      navigate('/profil');
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Log masuk gagal. Sila semak nama pengguna dan kata laluan.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-5 md:p-10 max-w-md mx-auto space-y-6">
      <div className="text-center space-y-1.5">
        <h1 className="text-2xl font-black text-[#0F2028]">Log Masuk</h1>
        <p className="text-xs text-slate-500">Selamat kembali ke portal Waqaf Taqwa</p>
      </div>

      {errorMessage && (
        <div className="p-3 bg-red-50 border border-red-200 rounded-2xl text-[11px] font-semibold text-red-600 text-center">
          {errorMessage}
        </div>
      )}

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
              type="password"
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
          <button
            type="button"
            onClick={() => navigate('/auth/signup')}
            className="text-[#1A8C4E] font-bold hover:underline cursor-pointer ml-1 inline-block"
          >
            Daftar Sekarang
          </button>
        </p>
      </div>
    </div>
  );
};
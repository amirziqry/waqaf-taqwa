import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { User, Store, Shield, Lock, Mail, ArrowRight, } from 'lucide-react';
import api from '../../api/client';

export const SignUpPage: React.FC = () => {
  const navigate = useNavigate();
  const [accountType, setAccountType] = useState<'personal' | 'merchant' | 'admin'>('admin');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setErrorMsg('');

    let endpoint = '/donator/register';
    if (accountType === 'merchant') endpoint = '/vendor/register';
    if (accountType === 'admin') endpoint = '/member/register-admin';

    try {
      await api.post(endpoint, {
        username,
        email,
        password,
      });

      localStorage.setItem('wt_user_role', accountType === 'admin' ? 'member' : accountType === 'merchant' ? 'vendor' : 'donator');
      localStorage.setItem('wt_user_name', username);
      navigate('/auth/login');
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Pendaftaran gagal. Sila cuba lagi.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 space-y-6 bg-white min-h-full max-w-md mx-auto">
      <div className="text-center space-y-1">
        <h1 className="text-2xl font-black text-[#0F2028]">Daftar Akaun</h1>
        <p className="text-xs text-slate-500">Sertai platform Waqaf Taqwa digital hari ini</p>
      </div>

      {errorMsg && (
        <div className="p-3 bg-red-50 border border-red-200 rounded-2xl text-xs font-bold text-red-600 text-center">
          {errorMsg}
        </div>
      )}

      {/* Account Type Selection */}
      <div className="space-y-2">
        <label className="text-xs font-extrabold text-[#0F2028]">Pilih Jenis Akaun</label>
        <div className="grid grid-cols-3 gap-2">
          <div
            onClick={() => setAccountType('personal')}
            className={`p-3 rounded-2xl border-2 cursor-pointer transition text-center ${
              accountType === 'personal' ? 'border-[#1A8C4E] bg-emerald-50/40' : 'border-slate-200 bg-white'
            }`}
          >
            <User className="w-4 h-4 mx-auto mb-1 text-[#1A8C4E]" />
            <h4 className="font-extrabold text-[11px] text-[#0F2028]">Pewakaf</h4>
          </div>

          <div
            onClick={() => setAccountType('merchant')}
            className={`p-3 rounded-2xl border-2 cursor-pointer transition text-center ${
              accountType === 'merchant' ? 'border-[#1A8C4E] bg-emerald-50/40' : 'border-slate-200 bg-white'
            }`}
          >
            <Store className="w-4 h-4 mx-auto mb-1 text-slate-700" />
            <h4 className="font-extrabold text-[11px] text-[#0F2028]">Peniaga</h4>
          </div>

          <div
            onClick={() => setAccountType('admin')}
            className={`p-3 rounded-2xl border-2 cursor-pointer transition text-center ${
              accountType === 'admin' ? 'border-[#1A8C4E] bg-emerald-50/40' : 'border-slate-200 bg-white'
            }`}
          >
            <Shield className="w-4 h-4 mx-auto mb-1 text-[#1A8C4E]" />
            <h4 className="font-extrabold text-[11px] text-[#0F2028]">Admin</h4>
          </div>
        </div>
      </div>

      {/* Account Details Form */}
      <form onSubmit={handleRegister} className="space-y-4">
        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">Nama Pengguna (Username)</label>
          <div className="h-12 bg-white border border-slate-200 rounded-2xl px-4 flex items-center gap-2.5 focus-within:border-[#1A8C4E] transition">
            <User className="w-4 h-4 text-slate-400" />
            <input
              type="text"
              required
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="admin_baru"
              className="w-full bg-transparent text-xs font-semibold outline-none text-slate-800"
            />
          </div>
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">E-mel</label>
          <div className="h-12 bg-white border border-slate-200 rounded-2xl px-4 flex items-center gap-2.5 focus-within:border-[#1A8C4E] transition">
            <Mail className="w-4 h-4 text-slate-400" />
            <input
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="admin@taqwa.com"
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
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] disabled:bg-slate-300 text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99] mt-2"
        >
          {loading ? 'Mendaftar...' : 'Lengkapkan Pendaftaran'}
          {!loading && <ArrowRight className="w-4 h-4" />}
        </button>
      </form>

      <div className="text-center pt-1">
        <p className="text-xs text-slate-500">
          Sudah mempunyai akaun?{' '}
          <Link to="/auth/login" className="text-[#1A8C4E] font-bold hover:underline">
            Log Masuk
          </Link>
        </p>
      </div>
    </div>
  );
};
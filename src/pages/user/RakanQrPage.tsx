import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  QrCode, 
  CheckCircle2, 
  User, 
  Phone, 
  Mail, 
  ArrowRight, 
  Building2, 
  Truck, 
  ArrowLeft,
  Award,
  Users
} from 'lucide-react';

export const RakanQrPage: React.FC = () => {
  const navigate = useNavigate();
  const [currentUsername, setCurrentUsername] = useState('pemohon_baru');
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [generatedAgentCode, setGeneratedAgentCode] = useState('');
  const [membershipTier, setMembershipTier] = useState<'BIASA' | 'DUTA'>('DUTA');

  const [form, setForm] = useState({
    fullName: '',
    phone: '',
    email: '',
    locationName: '',
    placementType: 'Premis Perniagaan / Restoran',
    deliveryAddress: '',
  });

  useEffect(() => {
    const user = localStorage.getItem('wt_user_name') || 'pemohon_baru';
    setCurrentUsername(user);

    // Check if THIS specific user has already submitted
    const savedUserApp = localStorage.getItem(`wt_rakan_qr_app_${user}`);
    if (savedUserApp) {
      try {
        const parsed = JSON.parse(savedUserApp);
        if (parsed.agentCode) {
          setGeneratedAgentCode(parsed.agentCode);
          setMembershipTier(parsed.membershipTier || 'DUTA');
          setIsSubmitted(true);
        }
      } catch {}
    }
  }, []);

  const handleSubmitApplication = (e: React.FormEvent) => {
    e.preventDefault();

    const prefix = membershipTier === 'DUTA' ? 'DUTA' : 'AHLI';
    const agentCode = `${prefix}-${Math.floor(1000 + Math.random() * 9000)}`;

    const applicationData = {
      id: `REQ-${Date.now()}`,
      agentCode,
      username: currentUsername,
      fullName: form.fullName,
      email: form.email || `${currentUsername}@taqwa.com`,
      phone: form.phone,
      placementLocation: form.locationName,
      placementType: form.placementType,
      shippingAddress: form.deliveryAddress,
      membershipTier,
      status: 'PENDING' as const,
      createdAt: new Date().toISOString().split('T')[0],
    };

    // 1. Save strictly under this specific user's key
    localStorage.setItem(`wt_rakan_qr_app_${currentUsername}`, JSON.stringify(applicationData));

    // 2. Add to the admin approval queue
    const existingApps = JSON.parse(localStorage.getItem('wt_admin_rakan_apps') || '[]');
    localStorage.setItem('wt_admin_rakan_apps', JSON.stringify([applicationData, ...existingApps]));

    setGeneratedAgentCode(agentCode);
    setIsSubmitted(true);
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6 pb-20 p-4">
      {/* Top Navigation */}
      <button
        type="button"
        onClick={() => navigate('/profil')}
        className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-500 hover:text-slate-900 transition"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Kembali ke Profil</span>
      </button>

      {/* Hero Banner */}
      <div className="bg-gradient-to-br from-[#0F2028] to-[#1A3340] rounded-3xl p-6 text-white space-y-3 relative overflow-hidden shadow-sm">
        <div className="flex items-center gap-2">
          <span className="px-2.5 py-0.5 bg-amber-400 text-slate-900 rounded-full text-[10px] font-black uppercase tracking-wider">
            Inisiatif Komuniti
          </span>
        </div>
        <h1 className="text-2xl font-black text-white">Pendaftaran Keahlian & Standee QR</h1>
        <p className="text-xs text-slate-300 leading-relaxed max-w-lg">
          Pilih kategori keahlian anda dan dapatkan kit standee QR rasmi untuk penempatan di kaunter premis atau surau komuniti.
        </p>
      </div>

      {isSubmitted ? (
        /* Success Screen */
        <div className="bg-white rounded-3xl p-8 border border-slate-200 text-center space-y-4 shadow-xs">
          <div className="w-14 h-14 bg-emerald-50 text-[#1A8C4E] rounded-2xl flex items-center justify-center mx-auto border border-emerald-200">
            <CheckCircle2 className="w-8 h-8" />
          </div>
          <div className="space-y-1">
            <h2 className="text-lg font-black text-[#0F2028]">Permohonan Berjaya Dihantar</h2>
            <p className="text-xs text-slate-500 max-w-md mx-auto">
              Permohonan keahlian ({membershipTier === 'DUTA' ? 'Ahli Duta' : 'Ahli Biasa'}) anda sedang disemak oleh pihak pentadbir.
            </p>
          </div>

          <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200/80 inline-block text-left min-w-[260px]">
            <div className="flex items-center justify-between gap-4">
              <span className="text-[10px] uppercase font-bold text-slate-400">Kategori</span>
              <span className="text-[10px] font-extrabold px-2 py-0.5 bg-emerald-100 text-emerald-800 rounded-full">
                {membershipTier === 'DUTA' ? 'AHLI DUTA' : 'AHLI BIASA'}
              </span>
            </div>
            <span className="text-[10px] uppercase font-bold text-slate-400 block mt-2">Kod Rujukan</span>
            <p className="text-base font-black text-slate-800 font-mono mt-0.5">{generatedAgentCode}</p>
            <span className="text-[10px] font-bold text-amber-600 mt-1 block">Status: MENUNGGU KELULUSAN</span>
          </div>

          <div className="pt-2">
            <button
              type="button"
              onClick={() => navigate('/profil')}
              className="px-6 py-2.5 bg-[#1A8C4E] hover:bg-[#15703E] text-white text-xs font-bold rounded-2xl shadow-xs transition"
            >
              Lihat di Halaman Profil
            </button>
          </div>
        </div>
      ) : (
        /* Application Form */
        <form onSubmit={handleSubmitApplication} className="bg-white rounded-3xl p-6 border border-slate-200 shadow-xs space-y-5">
          
          {/* Tier Selection Radio Cards */}
          <div className="space-y-2">
            <label className="text-xs font-extrabold text-[#0F2028] block">Pilih Kategori Keahlian</label>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {/* Option 1: Ahli Duta */}
              <div 
                onClick={() => setMembershipTier('DUTA')}
                className={`p-4 rounded-2xl border-2 cursor-pointer transition flex items-start gap-3 ${
                  membershipTier === 'DUTA'
                    ? 'border-[#1A8C4E] bg-emerald-50/40 shadow-xs'
                    : 'border-slate-200 hover:border-slate-300 bg-white'
                }`}
              >
                <div className={`p-2 rounded-xl mt-0.5 ${
                  membershipTier === 'DUTA' ? 'bg-[#1A8C4E] text-white' : 'bg-slate-100 text-slate-500'
                }`}>
                  <Award className="w-4 h-4" />
                </div>
                <div>
                  <h4 className="text-xs font-black text-slate-900">Ahli Duta Waqaf</h4>
                  <p className="text-[11px] text-slate-500 mt-0.5 leading-snug">
                    Dapatkan kit standee fizikal rasmi, pautan bayaran rujukan peribadi & akses portal duta.
                  </p>
                </div>
              </div>

              {/* Option 2: Ahli Biasa */}
              <div 
                onClick={() => setMembershipTier('BIASA')}
                className={`p-4 rounded-2xl border-2 cursor-pointer transition flex items-start gap-3 ${
                  membershipTier === 'BIASA'
                    ? 'border-[#1A8C4E] bg-emerald-50/40 shadow-xs'
                    : 'border-slate-200 hover:border-slate-300 bg-white'
                }`}
              >
                <div className={`p-2 rounded-xl mt-0.5 ${
                  membershipTier === 'BIASA' ? 'bg-[#1A8C4E] text-white' : 'bg-slate-100 text-slate-500'
                }`}>
                  <Users className="w-4 h-4" />
                </div>
                <div>
                  <h4 className="text-xs font-black text-slate-900">Ahli Biasa</h4>
                  <p className="text-[11px] text-slate-500 mt-0.5 leading-snug">
                    Daftar penempatan standee QR tanpa pautan rujukan duta.
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div className="border-t border-slate-100 pt-4 space-y-4">
            <div className="border-b border-slate-100 pb-2">
              <h3 className="text-sm font-extrabold text-[#0F2028]">Maklumat Pemohon & Penempatan Standee</h3>
              <p className="text-[11px] text-slate-400">Semua maklumat digunakan untuk penghantaran kit QR</p>
            </div>

            {/* Full Name */}
            <div className="space-y-1.5">
              <label className="text-xs font-extrabold text-[#0F2028]">Nama Penuh Pemohon</label>
              <div className="h-11 bg-slate-50 border border-slate-200 rounded-xl px-3.5 flex items-center gap-2.5 focus-within:border-[#1A8C4E] transition">
                <User className="w-4 h-4 text-slate-400 shrink-0" />
                <input
                  type="text"
                  required
                  value={form.fullName}
                  onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                  placeholder="cth. Muhammad Amir"
                  className="w-full bg-transparent text-xs font-semibold outline-none text-slate-800"
                />
              </div>
            </div>

            {/* Contact Details */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <label className="text-xs font-extrabold text-[#0F2028]">No. Telefon (WhatsApp)</label>
                <div className="h-11 bg-slate-50 border border-slate-200 rounded-xl px-3.5 flex items-center gap-2.5 focus-within:border-[#1A8C4E] transition">
                  <Phone className="w-4 h-4 text-slate-400 shrink-0" />
                  <input
                    type="tel"
                    required
                    value={form.phone}
                    onChange={(e) => setForm({ ...form, phone: e.target.value })}
                    placeholder="012-3456789"
                    className="w-full bg-transparent text-xs font-semibold outline-none text-slate-800"
                  />
                </div>
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-extrabold text-[#0F2028]">E-mel Pemohon</label>
                <div className="h-11 bg-slate-50 border border-slate-200 rounded-xl px-3.5 flex items-center gap-2.5 focus-within:border-[#1A8C4E] transition">
                  <Mail className="w-4 h-4 text-slate-400 shrink-0" />
                  <input
                    type="email"
                    required
                    value={form.email}
                    onChange={(e) => setForm({ ...form, email: e.target.value })}
                    placeholder="amir@example.com"
                    className="w-full bg-transparent text-xs font-semibold outline-none text-slate-800"
                  />
                </div>
              </div>
            </div>

            {/* Location & Placement Type */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <label className="text-xs font-extrabold text-[#0F2028]">Nama Premis / Lokasi Standee</label>
                <div className="h-11 bg-slate-50 border border-slate-200 rounded-xl px-3.5 flex items-center gap-2.5 focus-within:border-[#1A8C4E] transition">
                  <Building2 className="w-4 h-4 text-slate-400 shrink-0" />
                  <input
                    type="text"
                    required
                    value={form.locationName}
                    onChange={(e) => setForm({ ...form, locationName: e.target.value })}
                    placeholder="cth. Kaunter Cafe Taqwa"
                    className="w-full bg-transparent text-xs font-semibold outline-none text-slate-800"
                  />
                </div>
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-extrabold text-[#0F2028]">Jenis Penempatan</label>
                <select
                  value={form.placementType}
                  onChange={(e) => setForm({ ...form, placementType: e.target.value })}
                  className="w-full h-11 bg-slate-50 border border-slate-200 rounded-xl px-3 text-xs font-semibold outline-none focus:border-[#1A8C4E]"
                >
                  <option value="Premis Perniagaan / Restoran">Premis Perniagaan / Restoran</option>
                  <option value="Masjid / Surau">Masjid / Surau Komuniti</option>
                  <option value="Pejabat / Institusi">Pejabat / Institusi</option>
                  <option value="Acara / Program Khas">Acara / Gerai Jualan</option>
                </select>
              </div>
            </div>

            {/* Delivery Address */}
            <div className="space-y-1.5">
              <label className="text-xs font-extrabold text-[#0F2028]">Alamat Lengkap Pengeposan Kit Standee</label>
              <div className="bg-slate-50 border border-slate-200 rounded-xl p-3 focus-within:border-[#1A8C4E] transition flex items-start gap-2.5">
                <Truck className="w-4 h-4 text-slate-400 shrink-0 mt-0.5" />
                <textarea
                  rows={3}
                  required
                  value={form.deliveryAddress}
                  onChange={(e) => setForm({ ...form, deliveryAddress: e.target.value })}
                  placeholder="No unit, nama jalan, poskod, bandar, dan negeri untuk penghantaran..."
                  className="w-full bg-transparent text-xs font-semibold outline-none text-slate-800 resize-none"
                />
              </div>
            </div>

            {/* Submission Button */}
            <button
              type="submit"
              className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99] mt-2"
            >
              <span>Hantar Permohonan {membershipTier === 'DUTA' ? 'Ahli Duta' : 'Ahli Biasa'}</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        </form>
      )}
    </div>
  );
};
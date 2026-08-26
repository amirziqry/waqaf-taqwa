import React, { useEffect, useState } from 'react';
import { Plus, Trash2, FolderKanban, Users, Coins, CheckCircle2 } from 'lucide-react';
import api from '../../api/client';
import type { ProjectDTO } from '../../types/api';

export const AdminDashboardPage: React.FC = () => {
  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [summary, setSummary] = useState({ total: 0, donatorTotal: 0, vendorTotal: 0 });
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [, setLoading] = useState(true);

  // New Project Form State
  const [name, setName] = useState('');
  const [targetAmount, setTargetAmount] = useState('');
  const [location, setLocation] = useState('');
  const [summaryText, setSummaryText] = useState('');

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const [sumRes, projRes] = await Promise.all([
        api.get('/organization/donation/sum'),
        api.get('/organization/project/all/get'),
      ]);
      setSummary(sumRes.data);
      setProjects(projRes.data);
    } catch (err) {
      console.warn('Backend using mock/placeholder for admin data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  const handleCreateProject = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/organization/project/create', {
        name,
        slugUrl: name.toLowerCase().replace(/\s+/g, '-'),
        collectedAmount: 0,
        targetAmount: Number(targetAmount),
        location,
        category: { id: 'cat-1', name: 'Pembangunan' },
        tags: [{ id: 't-1', name: 'Waqaf' }],
        summary: summaryText,
        contentHtml: `<p>${summaryText}</p>`,
        status: 'PUBLISHED',
        imageUploadRequests: [],
      });
      setIsModalOpen(false);
      setName('');
      setTargetAmount('');
      setLocation('');
      setSummaryText('');
      loadDashboardData();
    } catch (err) {
      alert('Gagal menerbitkan projek. Sila cuba lagi.');
    }
  };

  const handleDeleteProject = async (id: string) => {
    if (!window.confirm('Adakah anda pasti untuk memadam projek ini?')) return;
    try {
      await api.delete(`/organization/project/${id}/delete`);
      setProjects(projects.filter((p) => p.id !== id));
    } catch (err) {
      alert('Gagal memadam projek.');
    }
  };

  return (
    <div className="p-4 md:p-0 space-y-6">
      {/* Top Bar */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-black text-[#0F2028]">Panel Kawalan Pentadbir</h1>
          <p className="text-xs text-slate-500">Pengurusan kempen wakaf, dana organisasi & laporan audit</p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="h-11 bg-[#1A8C4E] hover:bg-[#15703E] text-white px-4 rounded-xl text-xs font-bold flex items-center justify-center gap-2 shadow-sm transition"
        >
          <Plus className="w-4 h-4" /> Cipta Kempen Baharu
        </button>
      </div>

      {/* 3 Overview Stat Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-3xl border border-slate-200/80 shadow-sm space-y-2">
          <div className="flex justify-between items-center text-slate-400">
            <span className="text-xs font-bold">Jumlah Dana Keseluruhan</span>
            <Coins className="w-5 h-5 text-[#1A8C4E]" />
          </div>
          <p className="text-2xl font-black text-[#1A8C4E]">RM {(summary?.total || 428500).toLocaleString()}</p>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-200/80 shadow-sm space-y-2">
          <div className="flex justify-between items-center text-slate-400">
            <span className="text-xs font-bold">Sumbangan Pewakaf Awam</span>
            <Users className="w-5 h-5 text-blue-600" />
          </div>
          <p className="text-2xl font-black text-slate-800">RM {(summary?.donatorTotal || 310000).toLocaleString()}</p>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-200/80 shadow-sm space-y-2">
          <div className="flex justify-between items-center text-slate-400">
            <span className="text-xs font-bold">Kutipan SoftPOS Peniaga</span>
            <FolderKanban className="w-5 h-5 text-amber-600" />
          </div>
          <p className="text-2xl font-black text-slate-800">RM {(summary?.vendorTotal || 118500).toLocaleString()}</p>
        </div>
      </div>

      {/* Project Management Table */}
      <div className="bg-white rounded-3xl border border-slate-200/80 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between">
          <h3 className="font-extrabold text-sm text-[#0F2028]">Senarai Kempen & Projek Aktif</h3>
          <span className="text-xs text-slate-400 font-bold">{projects.length} Projek</span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 text-slate-500 font-bold border-b border-slate-100">
              <tr>
                <th className="p-3.5 pl-5">Nama Projek</th>
                <th className="p-3.5">Lokasi</th>
                <th className="p-3.5">Sasaran (RM)</th>
                <th className="p-3.5">Terkumpul (RM)</th>
                <th className="p-3.5">Status</th>
                <th className="p-3.5 pr-5 text-right">Tindakan</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 font-semibold text-slate-700">
              {projects.map((proj) => (
                <tr key={proj.id} className="hover:bg-slate-50/80">
                  <td className="p-3.5 pl-5 font-bold text-slate-900">{proj.name}</td>
                  <td className="p-3.5">{proj.location || 'Kuala Lumpur'}</td>
                  <td className="p-3.5">RM {Number(proj.targetAmount || 0).toLocaleString()}</td>
                  <td className="p-3.5 text-[#1A8C4E]">RM {Number(proj.collectedAmount || 0).toLocaleString()}</td>
                  <td className="p-3.5">
                    <span className="inline-flex items-center gap-1 text-[10px] font-bold text-emerald-700 bg-emerald-50 px-2.5 py-0.5 rounded-full">
                      <CheckCircle2 className="w-3 h-3" /> {proj.status || 'PUBLISHED'}
                    </span>
                  </td>
                  <td className="p-3.5 pr-5 text-right space-x-2">
                    <button
                      onClick={() => handleDeleteProject(proj.id)}
                      className="p-1.5 text-red-500 hover:bg-red-50 rounded-lg transition"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create Project Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl p-6 w-full max-w-lg space-y-4 shadow-2xl">
            <h3 className="font-extrabold text-base text-[#0F2028]">Terbitkan Kempen Wakaf Baharu</h3>
            <form onSubmit={handleCreateProject} className="space-y-3">
              <div>
                <label className="text-xs font-bold text-slate-700">Nama Projek</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Contoh: Pembinaan Dewan Solat Al-Falah"
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs outline-none focus:border-[#1A8C4E]"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-bold text-slate-700">Sasaran Dana (RM)</label>
                  <input
                    type="number"
                    required
                    value={targetAmount}
                    onChange={(e) => setTargetAmount(e.target.value)}
                    placeholder="100000"
                    className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs outline-none focus:border-[#1A8C4E]"
                  />
                </div>
                <div>
                  <label className="text-xs font-bold text-slate-700">Lokasi</label>
                  <input
                    type="text"
                    required
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                    placeholder="Kluang, Johor"
                    className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs outline-none focus:border-[#1A8C4E]"
                  />
                </div>
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700">Ringkasan Projek</label>
                <textarea
                  rows={3}
                  required
                  value={summaryText}
                  onChange={(e) => setSummaryText(e.target.value)}
                  placeholder="Penerangan ringkas mengenai objektif dan manfaat wakaf ini..."
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs outline-none focus:border-[#1A8C4E]"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-xs font-bold text-slate-500 hover:bg-slate-100 rounded-xl"
                >
                  Batal
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 text-xs font-bold bg-[#1A8C4E] hover:bg-[#15703E] text-white rounded-xl shadow-sm"
                >
                  Terbitkan Projek
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
import React from 'react';
import { Routes, Route, useLocation } from 'react-router-dom';
import { AppLayout } from './components/AppLayout';
import { HomePage } from './pages/user/HomePage';
import { ProjectsExplorePage } from './pages/user/ProjectsExplorePage';
import { ScanDonatePage } from './pages/user/ScanDonatePage';
import { VendorPosPage } from './pages/user/VendorPosPage';
import { ApplyTijarahPage } from './pages/user/ApplyTijarahPage';
import { AutoWaqafPage } from './pages/user/AutoWaqafPage';
import { TransactionHistoryPage } from './pages/user/TransactionHistoryPage';
import { ProfilePage } from './pages/user/ProfilePage';
import { ReceiptPage } from './pages/user/ReceiptPage';
import { CampaignDetailPage } from './pages/user/CampaignDetailPage';
import { LoginPage } from './pages/auth/LoginPage';
import { AdminDashboardPage } from './pages/admin/AdminDashboardPage';
import { AdminCampaignsPage } from './pages/admin/AdminCampaignsPage';
import { AdminLayout } from './pages/admin/AdminLayout';
import { AdminVendorsPage } from './pages/admin/AdminVendorsPage';
import { AdminSettingsPage } from './pages/admin/AdminSettingsPage';

export const App: React.FC = () => {
  const location = useLocation();
  const isAuthPage = location.pathname.startsWith('/auth');

  return (
    <AppLayout hideNav={isAuthPage}>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/projek" element={<ProjectsExplorePage />} />
        <Route path="/projek/:id" element={<CampaignDetailPage />} />
        <Route path="/imbas" element={<ScanDonatePage />} />
        <Route path="/transaksi" element={<TransactionHistoryPage />} />
        <Route path="/resit/:id" element={<ReceiptPage />} />
        <Route path="/profil" element={<ProfilePage />} />
        <Route path="/rakan-qr" element={<VendorPosPage />} />
        <Route path="/pos" element={<VendorPosPage />} />
        <Route path="/apply-tijarah" element={<ApplyTijarahPage />} />
        <Route path="/auto-waqaf" element={<AutoWaqafPage />} />
        <Route path="/admin" element={<AdminDashboardPage />} />
        <Route path="/admin/campaigns" element={<AdminCampaignsPage />} />
        <Route path="/admin/layout" element={<AdminLayout />} />
        <Route path="/admin/vendors" element={<AdminVendorsPage />} />
        <Route path="/admin/settings" element={<AdminSettingsPage />} />
        <Route path="/auth/login" element={<LoginPage />} />
        <Route path="/auth" element={<LoginPage />} />
      </Routes>
    </AppLayout>
  );
};

export default App;
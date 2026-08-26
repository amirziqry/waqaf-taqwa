import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AppLayout } from './components/AppLayout';

// Auth Pages
import { LoginPage } from './pages/auth/LoginPage';
import { SignUpPage } from './pages/auth/SignUpPage';

// User Pages
import { HomePage } from './pages/user/HomePage';
import { ProjectsExplorePage } from './pages/user/ProjectsExplorePage';
import { CampaignDetailPage } from './pages/user/CampaignDetailPage';
import { ScanDonatePage } from './pages/user/ScanDonatePage';
import { AutoWaqafPage } from './pages/user/AutoWaqafPage';
import { TransactionHistoryPage } from './pages/user/TransactionHistoryPage';
import { ReceiptPage } from './pages/user/ReceiptPage';
import { ProfilePage } from './pages/user/ProfilePage';

// Admin Pages
import { AdminLayout } from './pages/admin/AdminLayout';
import { AdminDashboardPage } from './pages/admin/AdminDashboardPage';
import { AdminCampaignsPage } from './pages/admin/AdminCampaignsPage';
import { AdminVendorsPage } from './pages/admin/AdminVendorsPage';
import { AdminSettingsPage } from './pages/admin/AdminSettingsPage';

export const App: React.FC = () => {
  return (
    <Routes>
      {/* Auth Pages (No App Navbar) */}
      <Route
        path="/auth"
        element={
          <AppLayout hideNav>
            <SignUpPage />
          </AppLayout>
        }
      />
      <Route
        path="/auth/login"
        element={
          <AppLayout hideNav>
            <LoginPage />
          </AppLayout>
        }
      />

      {/* User / Public Routes */}
      <Route
        path="/"
        element={
          <AppLayout>
            <HomePage />
          </AppLayout>
        }
      />
      <Route
        path="/projek"
        element={
          <AppLayout>
            <ProjectsExplorePage />
          </AppLayout>
        }
      />
      <Route
        path="/projek/:id"
        element={
          <AppLayout>
            <CampaignDetailPage />
          </AppLayout>
        }
      />
      <Route
        path="/imbas"
        element={
          <AppLayout>
            <ScanDonatePage />
          </AppLayout>
        }
      />
      <Route
        path="/auto-waqaf"
        element={
          <AppLayout>
            <AutoWaqafPage />
          </AppLayout>
        }
      />
      <Route
        path="/transaksi"
        element={
          <AppLayout>
            <TransactionHistoryPage />
          </AppLayout>
        }
      />
      <Route
        path="/resit/:id"
        element={
          <AppLayout>
            <ReceiptPage />
          </AppLayout>
        }
      />
      <Route
        path="/profil"
        element={
          <AppLayout>
            <ProfilePage />
          </AppLayout>
        }
      />

      {/* Admin Subsystem Routes */}
      <Route
        path="/admin"
        element={
          <AppLayout>
            <AdminLayout />
          </AppLayout>
        }
      >
        <Route index element={<AdminDashboardPage />} />
        <Route path="kempen" element={<AdminCampaignsPage />} />
        <Route path="peniaga" element={<AdminVendorsPage />} />
        <Route path="transaksi" element={<TransactionHistoryPage />} />
        <Route path="tetapan" element={<AdminSettingsPage />} />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default App;
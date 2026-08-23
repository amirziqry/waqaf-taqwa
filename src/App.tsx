import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AppLayout } from './components/AppLayout';
import { HomePage } from './pages/user/HomePage';
import { SignUpPage } from './pages/auth/SignUpPage';
import { ProjectsExplorePage } from './pages/user/ProjectsExplorePage';
import { CampaignDetailPage } from './pages/user/CampaignDetailPage';
import { ScanDonatePage } from './pages/user/ScanDonatePage';
import { ReceiptPage } from './pages/user/ReceiptPage';
import { AutoWaqafPage } from './pages/user/AutoWaqafPage';
import { TransactionHistoryPage } from './pages/user/TransactionHistoryPage';

export const App: React.FC = () => {
  return (
    <Routes>
      <Route path="/auth" element={<AppLayout hideNav><SignUpPage /></AppLayout>} />
      <Route path="/" element={<AppLayout><HomePage /></AppLayout>} />
      <Route path="/projek" element={<AppLayout><ProjectsExplorePage /></AppLayout>} />
      <Route path="/projek/:id" element={<AppLayout hideNav><CampaignDetailPage /></AppLayout>} />
      <Route path="/imbas" element={<AppLayout hideNav><ScanDonatePage /></AppLayout>} />
      <Route path="/resit" element={<AppLayout hideNav><ReceiptPage /></AppLayout>} />
      <Route path="/auto-waqaf" element={<AppLayout hideNav><AutoWaqafPage /></AppLayout>} />
      <Route path="/transaksi" element={<AppLayout><TransactionHistoryPage /></AppLayout>} />
    </Routes>
  );
};

export default App;
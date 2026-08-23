export type WaqafCategory = 'semua' | 'pendidikan' | 'kesihatan' | 'infrastruktur' | 'komuniti';

export interface Campaign {
  id: string;
  title: string;
  category: WaqafCategory;
  description: string;
  organizer: string;
  targetAmount: number;
  collectedAmount: number;
  donorCount: number;
  daysRemaining: number;
  imageUrl: string;
  isTaxExempt: boolean;
  createdAt: string;
}

export interface DonationPayload {
  campaignId: string;
  amount: number;
  paymentMethod: 'fpx' | 'duitnow' | 'card';
  akadAgreed: boolean;
  isAnonymous: boolean;
  notes?: string;
}

export interface DonationResponse {
  transactionId: string;
  paymentUrl: string;
  status: 'pending' | 'success' | 'failed';
  hashId: string;
}
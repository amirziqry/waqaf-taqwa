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

export interface Category {
  id: string;
  name: string;
}

export interface Tag {
  id: string;
  name: string;
}

export interface ProjectImage {
  id: string;
  url: string;
}

export interface ProjectDTO {
  id: string;
  name: string;
  slugUrl: string;
  collectedAmount: number;
  targetAmount: number;
  location: string;
  date?: string;
  category: Category;
  tags: Tag[];
  summary: string;
  contentHtml: string;
  status: 'DRAFT' | 'PUBLISHED';
  images: ProjectImage[];
}

export interface NewsItemDTO {
  id: string;
  title: string;
  slugUrl: string;
  author: string;
  date: string;
  category: Category;
  tags: Tag[];
  summary: string;
  contentHtml: string;
  status: string;
  images: { id: string; url: string }[];
}

export interface PaymentGatewayResponse {
  id: string;
  billingCode: string;
  status: string;
  paymentUrl: string;
}
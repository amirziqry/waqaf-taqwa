export type WaqafCategory = 'semua' | 'pendidikan' | 'kesihatan' | 'infrastruktur' | 'komuniti' | 'masjid';

export type UserRole = 'DONATOR' | 'VENDOR' | 'MEMBER' | 'ADMIN' | 'EDITOR';

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

// 1. Core Project & Campaign DTOs
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
  status: 'DRAFT' | 'PUBLISHED' | 'ACTIVE' | 'COMPLETED' | 'INACTIVE';
  images: ProjectImage[];
}

export interface Campaign {
  id: string;
  title: string;
  category: WaqafCategory | string;
  description: string;
  organizer?: string;
  targetAmount: number;
  collectedAmount: number;
  donorCount?: number;
  daysRemaining?: number;
  imageUrl?: string;
  location?: string;
  isTaxExempt?: boolean;
  status?: 'ACTIVE' | 'COMPLETED' | 'INACTIVE' | 'PENDING';
  createdAt?: string;
}

// 2. Authentication & User Profile DTOs
export interface AuthUserDTO {
  id?: string;
  username: string;
  email?: string;
  roles?: string[];
  role?: string;
  phoneNumber?: string;
  businessName?: string;
}

export interface AuthResponseDTO {
  token?: string;
  username: string;
  roles: string[];
  message?: string;
}

// 3. Donation & Payment DTOs
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
  paymentUrl?: string;
  status: 'pending' | 'success' | 'failed';
  hashId: string;
}

export interface PaymentGatewayResponse {
  id: string;
  billingCode: string;
  status: string;
  paymentUrl: string;
}

// 4. Transaction History & LHDN Receipt DTOs
export interface TransactionRecordDTO {
  id: string;
  referenceNo: string;
  amount: number;
  donorName: string;
  campaignTitle: string;
  paymentMethod: string;
  taxDeductible: boolean;
  taxExemptionRef?: string;
  verificationHash: string;
  createdAt: string;
  status: 'SUCCESS' | 'PENDING' | 'FAILED';
}

// 5. Merchant / SoftPOS Terminal DTOs
export interface VendorTerminalDTO {
  id: string;
  vendorId: string;
  businessName: string;
  registrationNumber: string;
  qrPayload: string;
  totalCollected: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}

// 6. News & Announcements DTOs
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
  images: ProjectImage[];
}
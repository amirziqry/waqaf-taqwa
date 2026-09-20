import api from "../../client";

// RakanQr Dashboard
export const getRakanQrDashboard = async () => {
  const response = await api.get("/rakan-qr/dashboard");

  return {
    rakanQrInfo: {
      // User's rakan qr's info.
      id: response.data.info.id,
      fullName: response.data.info.name,
      email: response.data.info.email,
      phoneNumber: response.data.info.phone,
      agentCode: response.data.info.code, // RakanQr Code
      membershipTier: response.data.info.type === "STANDARD" ? "AHLI" : "DUTA", // STANDARD/AMBASSADOR
      status: response.data.info.status, // ACTIVE/PENDING/INACTIVE
      organizationType: response.data.info.establishmentType,
      organizationName: response.data.info.establishmentName,
      shippingAddress: response.data.info.address,
      bankName: response.data.info.bankName,
      bankAccountNumber: response.data.info.bankAccountNumber,
      collectedAmount: response.data.info.collectedAmount, // All time collected donations
      commison: response.data.info.commission, // All time commission receive.
    },
    collectedAmount: {
      // Current month collected donations.
      collectedAmount: response.data.collectedAmount.total,
    },
    transactionList: response.data.donations.map((object: any) => ({
      id: object.id,
      billingCode: object.billingCode,
      transactionId: object.transactionId,
      amount: object.amount,
      paidAt: object.paidAt,
      status: object.status,
      rakanQrCode: object.rakanQrCode,
    })),
  };
};

// RakanQr application
export const applyRakanQr = async (rakanQrApp: any) => {
  const response = await api.post("/rakan-qr/apply", rakanQrApp);
};

// Update rakanQr status (Approve/Disapprove application)
export const updateRakanQrStatus = async (id: string, status: string) => {
  const response = await api.patch(`/rakan-qr/${id}/status`, {
    status,
  });

  return response.data;
};

// Get all rakanQr.
export const getAllRakanQrs = async (
  page: number, // Page index.
  size: number, // Number of items per page.
  status?: string, // ACTIVE/PENDING/INACTIVE
  type?: string, // STANDARD/AMBASSADOR
) => {
  const response = await api.get("/rakan-qr", {
    params: {
      page,
      size,
      status,
      type,
    },
  });

  return {
    rakanQrList: response.data.content.map((object: any) => ({
      id: object.id,
      agentCode: object.code,
      fullName: object.name,
      icNumber: object.icNumber,
      phone: object.phone,
      placementType: object.representativeType,
      locationName: object.establishmentName,
      placementLocation: object.qrSpot,
      deliveryAddress: object.postalAddress,
      bankName: object.bankName, // Ignore for non-Duta
      bankAccountNumber: object.bankAccountNumber, // Ignore for non-Duta
      rakanQrType: object.type,
      rakanQrStatus: object.status, // ACTIVE/PENDING/INACTIVE
      collectedAmount: object.collectedAmount,
      commission: object.commission, // Ignore for non-Duta
    })),
    pageIndex: response.data.page.number,
    pageSize: response.data.page.size,
    totalElements: response.data.page.totalElements,
    totalPages: response.data.page.totalPages,
  };
};

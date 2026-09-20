import api from "../../client";

// User Register.
export const register = async (
  username: string,
  fullName: string,
  email: string,
  phone: string,
  password: string,
) => {
  const requestBody = {
    username,
    accountHolderName: fullName,
    email,
    phone,
    password,
    modMesra: false,
  };

  const response = await api.post("/user/register", requestBody);

  return {
    username: response.data.username,
    role: response.data.roles?.[0],
  };
};

// User logout
export const logout = async () => {
  await api.post("/user/auth/logout");
};

// User auth status
export const accountMe = async () => {
  const response = await api.get("/account/auth/me");

  return {
    username: response.data.username,
    role: response.data.role,
  };
};

// User donation contributions (Sum)
export const getContribution = async (startDate?: string, endDate?: string) => {
  const filter = {
    startDate, // dd-MM-yyyy
    endDate, // dd-MM-yyyy
  };

  const response = await api.get("/user/donations/contributions", {
    params: filter, // Optional
  });

  return {
    total: response.data.total,
  };
};

// User request donation
export const requestPayment = async (amount: number, redirectUrl?: string) => {
  const requestBody = {
    amount,
    redirectUrl,
  };

  const response = await api.post(
    "/user/donations/payment-request",
    requestBody, // Optional
  );

  return {
    id: response.data.id, // Donation id
    billingCode: response.data.billingCode,
    amount: response.data.amount,
    status: response.data.status,
    paymentUrl: response.data.paymentUrl, // NexGen payment page.
  };
};

// User get donation history
export const getAllDonations = async (page: number, size: number) => {
  const response = await api.get("/user/donations", {
    params: {
      page,
      size,
    },
  });

  return {
    donationsList: response.data.content.map((object: any) => ({
      id: object.id,
      billingCode: object.billingCode,
      referenceNo: object.transactionId,
      amount: Number(object.amount),
      paidAt: object.paidAt,
      status: object.status,
      paymentMethod: object.paymentMethod,
      taxExemptionRef: object.receiptHashId,
      campaignId: object.projectId, // null for direct donation.
      campaignTitle: object.projectName, // null for direct donation.
      taxDeductible: null,
      donorName: null,
      createdAt: null,
    })),
    pageIndex: response.data.page.number,
    pageSize: response.data.page.size,
    totalElements: response.data.page.totalElements,
    totalPages: response.data.page.totalPages,
  };
};

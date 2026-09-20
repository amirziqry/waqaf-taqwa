import api from "../../client";

// Admin Register.
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

export const logout = async () => {
  await api.post("/user/auth/logout");
};

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

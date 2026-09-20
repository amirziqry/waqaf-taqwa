import api from "../../client";

// Admin Login.
export const adminLogin = async (username: string, password: string) => {
  const requestBody = {
    username,
    password,
  };

  const response = await api.post("/admin/auth/login", requestBody);

  return {
    username: response.data.username,
    role: response.data.role,
  };
};

// Admin Register.
export const adminRegister = async (
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
  };

  const response = await api.post("/admin/register/admin", requestBody);

  return {
    username: response.data.username,
    role: response.data.role,
  };
};

// Get all admins, list.
export const getAllAdmins = async () => {
  const response = await api.get("/admin/users");

  return response.data.map((object: any) => ({
    id: object.id,
    username: object.username,
    fullName: object.accountHolderName ?? object.username,
    email: object.email,
    phone: object.phone,
    role: object.role, // ADMIN/EDITOR
    registeredAt: object.createdAt,
  }));
};

export const getDonationCollections = async (
  startDate?: string,
  endDate?: string,
) => {
  const response = await api.get("/organization/donation/collections", {
    params: {
      startDate,
      endDate,
    },
  });

  return {
    directTotal: response.data.directTotal,
    recurringTotal: response.data.recurringTotal,
    projectTotal: response.data.projectTotal,
    merchantTotal: response.data.merchantTotal,
    rakanQrTotal: response.data.rakanQrTotal,
  };
};

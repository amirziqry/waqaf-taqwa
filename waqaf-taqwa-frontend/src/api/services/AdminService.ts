import api from "../client";

// Admin Login.
export const accountMe = async () => {
  const response = await api.get("/account/auth/me");

  return {
    username: response.data.username,
    role: response.data.role,
  };
};

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
    role: object.role,
    registeredAt: object.createdAt,
  }));
};

// Get all admins, list.
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
      email: object.email,
      phone: object.phone,
      placementType: object.establishmentType,
      locationName: object.establishmentName,
      deliveryAddress: object.address,
      bankName: object.bankName, // Ignore for non-Duta
      bankAccountNumber: object.bankAccountNumber, // Ignore for non-Duta
      rakanQrType: object.type,
      rakanQrStatus: object.status,
      collectedAmount: object.collectedAmount,
      commission: object.commission, // Ignore for non-Duta
    })),
    pageIndex: response.data.page.number,
    pageSize: response.data.page.size,
    totalElements: response.data.page.totalElements,
    totalPages: response.data.page.totalPages,
  };
};

export const updateRakanQrStatus = async (id: string, status: string) => {
  const response = await api.patch(`/rakan-qr/${id}/status`, {
    status,
  });

  return response.data;
};

export const createProject = async (project: any) => {
  const response = await api.post(`/organization/projects`, project);

  return {
    projectId: response.data.id, // ID for update
    imageUploadList: (response.data.uploadUrl ?? []).map((upload: any) => ({
      uploadUrl: upload.uploadUrl, // Object storage upload link
      imageKey: upload.imageKey, // Update project image key after successful storage save.
    })),
  };
};

export const updateProjectImageKeys = async (
  projectId: string,
  imageKeys: string[],
) => {
  const request = imageKeys.map((imageKey) => ({
    id: null,
    imageKey,
  }));

  await api.put(`/organization/projects/${projectId}/image-keys`, request);
};

export const getAllProjects = async (page: number, size: number) => {
  const response = await api.get("/organization/projects", {
    params: {
      page,
      size,
    },
  });

  return {
    projectList: response.data.content.map((object: any) => ({
      id: object.id,
      title: object.name,
      slugUrl: object.slugUrl,
      collectedAmount: Number(object.collectedAmount),
      targetAmount: Number(object.targetAmount),
      date: object.date,
      category: object.category?.name ?? "",
      tags: object.tags,
      location: object.location,
      description: object.summary,
      contentHtml: object.contentHtml,
      status: object.status,
      images:
        object.images?.map((image: any) => ({
          id: image.id,
          url: image.url,
        })) ?? [],
    })),
    pageIndex: response.data.page.number,
    pageSize: response.data.page.size,
    totalElements: response.data.page.totalElements,
    totalPages: response.data.page.totalPages,
  };
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

import api from "../client";

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

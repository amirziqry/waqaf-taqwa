import api from '../../client';

// Get all admins, list.
export const getRakanQrDashboard = async () => {
    const response = await api.get(
        '/rakan-qr/dashboard'
    );

    return {
        rakanQrInfo: { // User's rakan qr's info.
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
            commison: response.data.info.commission // All time commission receive. 
        },
        collectedAmount: { // Current month collected donations.
            collectedAmount: response.data.collectedAmount.total
        },
        transactionList: response.data.donations.map((object: any) => ({
            id: object.id,
            billingCode: object.billingCode,
            transactionId: object.transactionId,
            amount: object.amount,
            paidAt: object.paidAt,
            status: object.status,
            rakanQrCode: object.rakanQrCode
        }))
    }
};

export const applyRakanQr = async (rakanQrApp: any) => {
    const response = await api.post(
        '/rakan-qr/apply',
        rakanQrApp,
    );
};

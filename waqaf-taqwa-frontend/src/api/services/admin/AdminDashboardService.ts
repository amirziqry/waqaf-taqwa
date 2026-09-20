import api from "../../client";

// Get admin dashboard endpoint.
export const getAdminDashboard = async () => {
    const response = await api.get(
        "/admin/dashboard"
    );

    // Rename frontend variable if needed.
    return {
        donationCollections: { // Collection Data
            directTotal: response.data.collections.directTotal,
            recurringTotal: response.data.collections.recurringTotal,
            projectTotal: response.data.collections.projectTotal,
            merchantTotal: response.data.collections.merchantTotal,
            rakanQrTotal: response.data.collections.rakanQrTotal
        },
        orgAbout: { // Org Information
            id: response.data.orgAbout.id,
            name: response.data.orgAbout.name,
            phone: response.data.orgAbout.phone,
            email: response.data.orgAbout.email,

            address: { // Org Address
                addressLine1: response.data.orgAbout.address.addressLine1,
                addressLine2: response.data.orgAbout.address.addressLine2,
                addressLine3: response.data.orgAbout.address.addressLine3,
                postcode: response.data.orgAbout.address.postcode,
                city: response.data.orgAbout.address.city,
                state: response.data.orgAbout.address.state,
                country: response.data.orgAbout.address.country
            },
            contentHtml: response.data.orgAbout.contentHtml, // About us content.
            logoUrl: response.data.orgAbout.logoUrl, // Image URL from storage.
            heroUrl: response.data.orgAbout.heroUrl // Image URL from storage.
        },
        // Projects
        projects: response.data.projects.map((object: any) => ({
            id: object.id, // Primary identifier.
            name: object.name,
            slugUrl: object.slugUrl,
            collectedAmount: object.collectedAmount,
            targetAmount: object.targetAmount,
            location: object.location,
            date: object.date, // dd-MM-yyyy
            // Category
            category: {
                id: object.category.id, // Primary identifier.
                name: object.category.name
            },
            // Tags
            tags: object.tags.map((tag: any) => ({
                id: tag.id, // Primary identifier.
                name: tag.name
            })),
            summary: object.summary,
            contentHtml: object.contentHtml,
            status: object.status,
            // Images URL
            images: object.images.map((image: any) => ({
                id: image.id, // Primary identifier.
                url: image.url // Image URL from storage.
            }))
        })),
        // News
        news: response.data.news.map((object: any) => ({
            id: object.id, // Primary identifier.
            title: object.title,
            slugUrl: object.slugUrl,
            author: object.author,
            date: object.date, // dd-MM-yyyy
            // Category
            category: {
                id: object.category.id, // Primary identifier.
                name: object.category.name
            },
            // Tags
            tags: object.tags.map((tag: any) => ({
                id: tag.id, // Primary identifier.
                name: tag.name
            })),
            summary: object.summary,
            contentHtml: object.contentHtml,
            status: object.status,
            // Images URL
            images: object.images.map((image: any) => ({
                id: image.id, // Primary identifier.
                url: image.url // Image URL from storage.
            }))
        })),
        // Campaigns
        campaigns: response.data.campaigns.map((object: any) => ({
            id: object.id, // Primary identifier.
            name: object.name,
            slugUrl: object.slugUrl,
            dateStart: object.dateStart, // dd-MM-yyyy
            dateEnd: object.dateEnd, // dd-MM-yyyy
            // Category
            category: {
                id: object.category.id, // Primary identifier.
                name: object.category.name
            },
            // Tags
            tags: object.tags.map((tag: any) => ({
                id: tag.id, // Primary identifier.
                name: tag.name
            })),
            summary: object.summary,
            contentHtml: object.contentHtml,
            status: object.status,
            // Images URL
            images: object.images.map((image: any) => ({
                id: image.id, // Primary identifier.
                url: image.url // Image URL from storage.
            }))
        })),
        // RakanQrs
        rakanqrs: response.data.rakanQrs.map((agent: any) => ({
            id: agent.id, // Primary identifier.
            name: agent.name,
            email: agent.email,
            phone: agent.phone,
            code: agent.code,
            type: agent.type, // AHLI/DUTA
            status: agent.status, // ACTIVE/PENDING/INACTIVE
            collectedAmount: agent.collectedAmount,
            commission: agent.commission
        }))
    };
}

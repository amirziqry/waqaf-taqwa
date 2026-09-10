# Run with Docker

## Prerequisites

Install:

- Docker Desktop
- ngrok — required for payment gateway callbacks/webhooks

## Clone Repository
- After pulling/cloning the project, navigate to the project root

- The project root should contain the docker-compose.yml and a .env.example file.

## Create the Environment File
- Create .env file in the project root.

- Open .env and fill in the values based on .env.example.

```bash
NEXGEN_API_KEY=PLACE_API_KEY_HERE
NEXGEN_API_SECRET=PLACE_API_SECRET_HERE
WEBHOOK_BASE_URL=PLACE_NGROK_URL_HERE
```

Important: Do not commit the .env to Git as it contains environment-specific configuration and secrets. The .env.example file should be used as the template instead.

## Install ngrok
Ngrok is needed for routing localhost to a live domain, which is needed for NexGen API to communicate with our webhooks, so the backend can update the payment information.

- ngrok can be installed through the Microsoft Store.
- Create an ngrok account and log in.
- Open the ngrok dashboard and find your Auth Token.

### Configure ngrok in CMD

```bash
ngrok config add-authtoken YOUR_AUTH_TOKEN
ngrok http 8080 
```

ngrok will display something similar to:

Forwarding    https://example-1234.ngrok-free.app -> http://localhost:8080

Paste the ngrok URL to WEBHOOK_BASE_URL in .env.

## Start Application

From the project root (where docker-compose.yml is located):

```bash
docker compose up --build
```

This will:

- Build the Spring Boot backend image
- Create the backend container
- Create the PostgreSQL database container
- Start both services together

After the containers start, the backend will be available at:

```
http://localhost:8080
```

## Stop Application

Stop and remove containers:

```bash
docker compose down
```

This keeps the data volume.

## Reset
Start with fresh database.

To remove containers and delete all data:

```bash
docker compose down -v
```


# Axios Setup

Create one Axios instance for communicating with the backend.

```javascript
import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
    withCredentials: true
});

export default api;
```

# REST API Endpoints

## Admin (Org User Account)

### Admin — Get Dashboard (200) (Testing)
- Admin-only. Retrieve preview of monthly donation collection across all channels, organization's information, fixed amount of projects, news, campaigns, & RakanQrs records.

- Pending: Payout report.

```javascript
const response = await api.get(
    "/admin/dashboard"
);

const frontend.object = {
    frontend.object: { // Collection Data
        frontend.var: response.data.collections.directTotal,
        frontend.var: response.data.collections.recurringTotal,
        frontend.var: response.data.collections.projectTotal,
        frontend.var: response.data.collections.merchantTotal,
        frontend.var: response.data.collections.rakanQrTotal
    },
    frontend.object: { // Org Information
        frontend.var: response.data.orgAbout.id,
        frontend.var: response.data.orgAbout.name,
        frontend.var: response.data.orgAbout.phone,
        frontend.var: response.data.orgAbout.email,

        frontend.object: { // Org Address
            frontend.var: response.data.orgAbout.address.addressLine1,
            frontend.var: response.data.orgAbout.address.addressLine2,
            frontend.var: response.data.orgAbout.address.addressLine3,
            frontend.var: response.data.orgAbout.address.postcode,
            frontend.var: response.data.orgAbout.address.city,
            frontend.var: response.data.orgAbout.address.state,
            frontend.var: response.data.orgAbout.address.country
        },
        frontend.var: response.data.orgAbout.contentHtml, // About us content.
        frontend.var: response.data.orgAbout.logoUrl, // Image URL from storage.
        frontend.var: response.data.orgAbout.heroUrl // Image URL from storage.
    },
    // Projects
    frontend.list = response.data.projects.map(object => ({
        frontend.var: object.id, // Primary identifier.
        frontend.var: object.name,
        frontend.var: object.slugUrl,
        frontend.var: object.collectedAmount,
        frontend.var: object.targetAmount,
        frontend.var: object.location,
        frontend.var: object.date, // dd-MM-yyyy
        // Category
        frontend.var: {
            frontend.var: object.category.id, // Primary identifier.
            frontend.var: object.category.name
        },
        // Tags
        frontend.list: object.tags.map(tag => ({
            frontend.var: tag.id, // Primary identifier.
            frontend.var: tag.name
        })),
        frontend.var: object.summary,
        frontend.var: object.contentHtml,
        frontend.var: object.status,
        // Images URL
        frontend.list: object.images.map(image => ({
            frontend.var: image.id, // Primary identifier.
            frontend.var: image.url // Image URL from storage.
        }))
    })),
    // News
    frontend.list : response.data.news.map(object => ({
        frontend.var: object.id, // Primary identifier.
        frontend.var: object.title,
        frontend.var: object.slugUrl,
        frontend.var: object.author,
        frontend.var: object.date, // dd-MM-yyyy
        // Category
        frontend.var: {
            frontend.var: object.category.id, // Primary identifier.
            frontend.var: object.category.name
        },
        // Tags
        frontend.list: object.tags.map(tag => ({
            frontend.var: tag.id, // Primary identifier.
            frontend.var: tag.name
        })),
        frontend.var: object.summary,
        frontend.var: object.contentHtml,
        frontend.var: object.status,
        // Images URL
        frontend.list: object.images.map(image => ({
            frontend.var: image.id, // Primary identifier.
            frontend.var: image.url // Image URL from storage.
        }))
    })),
    // Campaigns
    frontend.list : response.data.campaigns.map(object => ({
        frontend.var: object.id, // Primary identifier.
        frontend.var: object.name,
        frontend.var: object.slugUrl,
        frontend.var: object.dateStart, // dd-MM-yyyy
        frontend.var: object.dateEnd, // dd-MM-yyyy
        // Category
        frontend.var: {
            frontend.var: object.category.id, // Primary identifier.
            frontend.var: object.category.name
        },
        // Tags
        frontend.list: object.tags.map(tag => ({
            frontend.var: tag.id, // Primary identifier.
            frontend.var: tag.name
        })),
        frontend.var: object.summary,
        frontend.var: object.contentHtml,
        frontend.var: object.status,
        // Images URL
        frontend.list: object.images.map(image => ({
            frontend.var: image.id, // Primary identifier.
            frontend.var: image.url // Image URL from storage.
        }))
    })),
    // RakanQrs
    frontend.object: response.data.rakanQrs.map(agent => ({
        frontend.var: agent.id, // Primary identifier.
        frontend.var: agent.name,
        frontend.var: agent.email,
        frontend.var: agent.phone,
        frontend.var: agent.code,
        frontend.var: agent.type, // STANDARD/AMBASADDOR
        frontend.var: agent.status, // ACTIVE/PENDING/INACTIVE
        frontend.var: agent.collectedAmount,
        frontend.var: agent.commission
    }))
};
```

### Admin — Register Admin (201) (Testing)
- Pending third party verification

```javascript
const requestBody = {
    username: frontend.username,
    email: frontend.email,
    password: frontend.password
    phone: frontend.phone, // Optional
    modMesra: frontend.modMesra // Optional, Boolean
};

const response = await api.post(
    "/admin/register/admin",
    requestBody
);

const frontend.object = {
    frontend.var: response.data.username,
    frontend.var: response.data.email,
    frontend.var: response.data.phone,
    frontend.var: response.data.roles // ADMIN/EDITOR
};
```

### Admin — Register Editor (201) (Testing)
- Pending third party verification

```javascript
const requestBody = {
    username: frontend.username,
    email: frontend.email,
    password: frontend.password
    phone: frontend.phone, // Optional
    modMesra: frontend.modMesra // Optional, Boolean
};

const response = await api.post(
    "/admin/register/editor",
    requestBody
);

const frontend.object = {
    frontend.var: response.data.username,
    frontend.var: response.data.email,
    frontend.var: response.data.phone,
    frontend.var: response.data.roles // ADMIN/EDITOR
};
```

### Admin — Get By Username (200)

```javascript
const response = await api.get(
    `/admin/users/${frontend.username}`
);

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.username,
    frontend.var: response.data.email,
    frontend.var: object.phone,
    frontend.var: object.roles,
    frontend.var: object.modMesra, // Boolean
};
```

### Admin — Get All (200)

```javascript
const response = await api.get(
    "/admin/users"
);

const frontend.list = response.data.map(object => ({
    frontend.var: object.id,
    frontend.var: object.username,
    frontend.var: object.email,
    frontend.var: object.phone,
    frontend.var: object.roles,
    frontend.var: object.modMesra, // Boolean
}));
```

### Admin — Update Role (200)
- Switch a user from admin to editor, vice versa.

```javascript
const requestBody = {
    role: frontend.role // "ADMIN" / "EDITOR"
};

const response = await api.patch(
    `/admin/users/${frontend.username}/account/role`,
    requestBody
);
```

### Admin — Update Password (200)
- Change logged in user's password (Standard method).

```javascript
const requestBody = {
    currentPassword: frontend.currentPassword,
    newPassword: frontend.newPassword
};

const response = await api.patch(
    `/admin/users/account/password`,
    requestBody
);
```

### Admin — Delete (200)

```javascript
const response = await api.delete(
    `/admin/users/${frontend.username}`
);
```

### Admin — Login (200)
- Successful response will store JWT access token in browser's cookie for subsequent authenticated request.

```javascript
const requestBody = {
    username: frontend.username,
    password: frontend.password
};

const response = await api.post(
    "/admin/login",
    requestBody
);

const frontend.object = {
    frontend.var: response.data.username,
    frontend.var: response.data.email,
    frontend.var: response.data.roles
};
```

### Admin — Logout (200)
- Clear cookie from browser's cookie.

```javascript
const response = await api.post(
    "/admin/logout"
);
```

### Admin — Get User Authentication Status (200)

```javascript
const response = await api.get(
    "/admin/me"
);
```

## Merchant (Vendor Account)

### Merchant — Register

```javascript
const response = await api.post(
    "/merchant/register",
    requestBody
);

const requestBody = {
    username: frontend.username,
    email: frontend.email,
    password: frontend.password
};

const frontend.object = {
    frontend.var: response.data.username,
    frontend.var: response.data.email
};
```

### Merchant — Login

```javascript
const response = await api.post(
    "/merchant/auth/login",
    requestBody
);

const requestBody = {
    username: frontend.username,
    password: frontend.password
};

const frontend.object = {
    frontend.var: response.data.username,
    frontend.var: response.data.email
};
```

### Merchant — Get Current User Auth Status

```javascript
const response = await api.get(
    "/merchant/auth/me"
);
```

### Merchant — Get Donation Sum (Placeholder)

```javascript
const response = await api.get(
    "/merchant/donation/sum"
);

const frontend.object = {
    frontend.var: response.data.total
};
```

## Personal (Donor Account)

### Personal Dashboard

#### Personal — Get Dashboard (200) (Testing)
- Personal-only. Retrieve preview of monthly donation collection across all channels, organization's information, fixed amount of projects, news, campaigns, & RakanQrs records.

- Pending: Project donation and transaction inclusion.

```javascript
const response = await api.get(
    "/personal/dashboard"
);

const frontend.object = {
    frontend.object: { // Contributions
        frontend.var: response.data.contributions.total
    },
    frontend.object: { // Account Information
        frontend.var: response.data.accountInfo.id
        frontend.var: response.data.accountInfo.username
        frontend.var: response.data.accountInfo.accountHolderName
        frontend.var: response.data.accountInfo.email
        frontend.var: response.data.accountInfo.phone
        frontend.var: response.data.accountInfo.modMesra
    },
    frontend.object: { // Org Information
        frontend.var: response.data.orgAbout.id,
        frontend.var: response.data.orgAbout.name,
        frontend.var: response.data.orgAbout.phone,
        frontend.var: response.data.orgAbout.email,

        frontend.object: { // Org Address
            frontend.var: response.data.orgAbout.address.addressLine1,
            frontend.var: response.data.orgAbout.address.addressLine2,
            frontend.var: response.data.orgAbout.address.addressLine3,
            frontend.var: response.data.orgAbout.address.postcode,
            frontend.var: response.data.orgAbout.address.city,
            frontend.var: response.data.orgAbout.address.state,
            frontend.var: response.data.orgAbout.address.country
        },
        frontend.var: response.data.orgAbout.contentHtml, // About us content.
        frontend.var: response.data.orgAbout.logoUrl, // Image URL from storage.
        frontend.var: response.data.orgAbout.heroUrl // Image URL from storage.
    },
    // Projects
    frontend.list = response.data.projects.map(object => ({
        frontend.var: object.id, // Primary identifier.
        frontend.var: object.name,
        frontend.var: object.slugUrl,
        frontend.var: object.collectedAmount,
        frontend.var: object.targetAmount,
        frontend.var: object.location,
        frontend.var: object.date, // dd-MM-yyyy
        // Category
        frontend.var: {
            frontend.var: object.category.id, // Primary identifier.
            frontend.var: object.category.name
        },
        // Tags
        frontend.list: object.tags.map(tag => ({
            frontend.var: tag.id, // Primary identifier.
            frontend.var: tag.name
        })),
        frontend.var: object.summary,
        frontend.var: object.contentHtml,
        frontend.var: object.status,
        // Images URL
        frontend.list: object.images.map(image => ({
            frontend.var: image.id, // Primary identifier.
            frontend.var: image.url // Image URL from storage.
        }))
    })),
    // News
    frontend.list : response.data.news.map(object => ({
        frontend.var: object.id, // Primary identifier.
        frontend.var: object.title,
        frontend.var: object.slugUrl,
        frontend.var: object.author,
        frontend.var: object.date, // dd-MM-yyyy
        // Category
        frontend.var: {
            frontend.var: object.category.id, // Primary identifier.
            frontend.var: object.category.name
        },
        // Tags
        frontend.list: object.tags.map(tag => ({
            frontend.var: tag.id, // Primary identifier.
            frontend.var: tag.name
        })),
        frontend.var: object.summary,
        frontend.var: object.contentHtml,
        frontend.var: object.status,
        // Images URL
        frontend.list: object.images.map(image => ({
            frontend.var: image.id, // Primary identifier.
            frontend.var: image.url // Image URL from storage.
        }))
    })),
    // Campaigns
    frontend.list : response.data.campaigns.map(object => ({
        frontend.var: object.id, // Primary identifier.
        frontend.var: object.name,
        frontend.var: object.slugUrl,
        frontend.var: object.dateStart, // dd-MM-yyyy
        frontend.var: object.dateEnd, // dd-MM-yyyy
        // Category
        frontend.var: {
            frontend.var: object.category.id, // Primary identifier.
            frontend.var: object.category.name
        },
        // Tags
        frontend.list: object.tags.map(tag => ({
            frontend.var: tag.id, // Primary identifier.
            frontend.var: tag.name
        })),
        frontend.var: object.summary,
        frontend.var: object.contentHtml,
        frontend.var: object.status,
        // Images URL
        frontend.list: object.images.map(image => ({
            frontend.var: image.id, // Primary identifier.
            frontend.var: image.url // Image URL from storage.
        }))
    })),
    // Donations/Transactions
    frontend.object: response.data.donations.map(item => ({
        frontend.var: item.id, // Primary identifier.
        frontend.var: item.billingCode,
        frontend.var: item.transactionId,
        frontend.var: item.amount,
        frontend.var: item.paidAt,
        frontend.var: item.status,
        frontend.var: item.receiptHashId
    }))
};
```

### Personal Account Management

#### Personal — Register (201)

```javascript
const requestBody = {
    username: frontend.username, 
    password: frontend.password, 
    email: frontend.email, 
    phone: frontend.phone, // Optional
    modMesra: frontend.modMesra // Optional, Boolean
};

const response = await api.post(
    "/personal/register",
    requestBody
);

const frontend.object = {
    frontend.var: response.data.username,
    frontend.var: response.data.email,
    frontend.var: response.data.phone
};
```

#### Personal — Update Account Info (200)

```javascript
const requestBody = {
    accountHolderName: frontend.accountHolderName, 
    phone: frontend.phone, 
    email: frontend.email, 
    modMesra: frontend.modMesra
};

const response = await api.put( 
    "/personal/users/account", 
    requestBody 
);
```

#### Personal — Change Password (200)

```javascript
const requestBody = {
    currentPassword: frontend.currentPassword, 
    newPassword: frontend.newPassword
};

const response = await api.patch( 
    "/personal/users/account/password", 
    requestBody 
);
```

#### Personal — Get Account Info (200)

```javascript
const response = await api.get( 
    "/personal/users/account" 
); 

const frontend.object = { 
    id: response.data.id, 
    username: response.data.username, 
    accountHolderName: response.data.accountHolderName, 
    email: response.data.email, 
    phone: response.data.phone, 
    modMesra: response.data.modMesra };
```

#### Personal — Login (200)
- This will attached JWT accessToken to the browser's cookie for subsequent authenticated request.

```javascript
const requestBody = {
    username: frontend.username,
    password: frontend.password
};

const response = await api.post(
    "/personal/auth/login",
    requestBody
);

const frontend.object = {
    frontend.var: response.data.username,
    frontend.var: response.data.email
};
```

#### Personal — Logout (200)
- Cookie will be removed from browser.

```javascript
const response = await api.post(
    "/personal/auth/logout"
);
```

#### Personal — Get User Auth Status (200)

```javascript
const response = await api.get(
    "/personal/auth/me"
);
```

### Personal Donation

#### Personal — Request Payment URL (201) (Beta)
- Direct donations only, not include project donations.
User accountHolderName, email, phone is required for payment request. Update user account info first before requesting.

```javascript
const requestBody = { 
    amount: frontend.amount, 
    taxExempt: frontend.taxExempt, // Boolean
    redirectUrl: frontend.redirectUrl // Optional, redirect to UI after payment (non localhost)
};

const response = await api.post(
    "/personal/donations/payment-request",
    requestBody
);

const frontend.object = {
    frontend.var: response.data.id, // Donation id
    frontend.var: response.data.billingCode,
    frontend.var: response.data.amount,
    frontend.var: response.data.status,
    frontend.var: response.data.paymentUrl // NexGen payment page.
};
```

#### Personal — Get Donation Details (200)
- Frontend needs to poll this request at least every +1s since web socket is not used yet.

```javascript
const response = await api.get(
    `/personal/donations/${frontend.id}`
); // Pass donation id

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.billingCode,
    frontend.var: response.data.transactionId,
    frontend.var: response.data.amount,
    frontend.var: response.data.paidAt, // dd-MM-yyyy
    frontend.var: response.data.status,
    frontend.var: response.data.receiptHashId
};
```

#### Personal — Get Contributions (200) (Testing)
- Get total amount of donations, with optional filter for date range.

- Pending: Project donation inclusion.

```javascript
const filter = {
    startDate: frontend.startDate, // dd-MM-yyyy
    endDate: frontend.endDate // dd-MM-yyyy
};

const response = await api.get(
    "/personal/donations/contributions",
    {
        params: filter // Optional
    }
);

const frontend.object = {
    frontend.var: response.data.total
};
```

#### Personal — Get Donations Transactions (200)
- Returns the user's transactions. This response returns Pagination.

- Pending: Project donation transactions inclusion.

```javascript
const params = {
    page: frontend.page, // 0 = First page
    size: frontend.size, // Number of elements per page
    // Default sorting createdAt, DESC
};

const response = await api.get(
    "/personal/donation/get", 
    { 
        params // Optional
    } 
);

const frontend.object = { 
    frontend.list: response.data.content.map(donation => ({ 
        frontend.var: donation.id, 
        frontend.var: donation.billingCode, 
        frontend.var: donation.transactionId, 
        frontend.var: donation.amount, 
        frontend.var: donation.paidAt, 
        frontend.var: donation.status, 
        frontend.var: donation.receiptHashId 
    })),
    frontend.var: response.data.page.totalElements, // Total elements available.
    frontend.var: response.data.page.totalPages, 
    frontend.var: response.data.page.size, 
    frontend.var: response.data.page.number // Current page index.
};
```

## Organization

### Organization — Get Donation Collections (200)
- Get donation collections amount across all channels.

```javascript
const filter = {
    startDate: frontend.startDate, // dd-MM-yyyy
    endDate: frontend.endDate // dd-MM-yyyy
};

const response = await api.get(
    "/organization/donation/collections",
    {
        params: filter // Optional
    }
);

const frontend.object = {
    frontend.var: response.data.directTotal,
    frontend.var: response.data.recurringTotal,
    frontend.var: response.data.projectTotal,
    frontend.var: response.data.merchantTotal,
    frontend.var: response.data.rakanQrTotal
};
```

### Project

#### Project Donation

##### Project Donation — Request Payment URL (201) (Beta)

```javascript
const requestBody = {
    amount: frontend.amount,
    taxExempt: frontend.taxExemptFlag, // Boolean
    redirectUrl: frontend.redirectUrl
};

const response = await api.post(
    `/projects/${frontend.projectId}/donations/payment-request`,
    requestBody
);

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.billingCode,
    frontend.var: response.data.amount,
    frontend.var: response.data.status,
    frontend.var: response.data.paymentUrl
};
```

##### Project Donation — Get Donation Details (200)
- Donation/Payment details

```javascript
const response = await api.get(
    `/projects/donations/${frontend.id}`
);

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.billingCode,
    frontend.var: response.data.transactionId,
    frontend.var: response.data.amount,
    frontend.var: response.data.paidAt,
    frontend.var: response.data.status,
    frontend.var: response.data.receiptHashId,
    frontend.var: response.data.projectId,
    frontend.var: response.data.projectName
};
```

##### Project Donation — Get Donation Collection By Id (200)

```javascript
const filter = {
    startDate: frontend.startDate, // dd-MM-yyyy
    endDate: frontend.endDate       // dd-MM-yyyy
};

const response = await api.get(
    `/projects/${frontend.projectId}/donations/collection`,
    {
        params: filter // Optional
    }
);

const frontend.object = {
    frontend.var: response.data.total
};
```

#### Project Management

##### Project — Create (201)
- Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

```javascript
const response = await api.post(
    "/organization/projects",
    requestBody
);

const requestBody = {
    name: frontend.name,
    slugUrl: frontend.slugUrl,
    targetAmount: frontend.targetAmount,
    location: frontend.location,
    category: {
        id: frontend.categoryId,
        name: frontend.categoryName
    },
    tags: frontend.tags.map(tag => ({
        id: tag.frontend.id,
        name: tag.frontend.name
    })),
    summary: frontend.summary,
    contentHtml: frontend.contentHtml,
    status: frontend.status, // Uppercase: DRAFT/PUBLISHED
    imageUploadRequests: frontend.imageUploadRequests.map(file => ({
        filename: file.frontend.filename, // EG: image.jpg
        contentType: file.frontend.contentType, // EG: "image/jpeg"
        path: file.frontend.path // Set null
    }))
};

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.uploadUrl.map(upload => ({
        frontend.uploadUrl: upload.uploadUrl, // Object storage upload link
        frontend.imageKey: upload.imageKey
    })) 
};
```

##### Project — Update (200)
- Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

```javascript
const response = await api.put(
    `/organization/projects/${frontend.projectId}`,
    requestBody
);

const requestBody = {
    name: frontend.name,
    slugUrl: frontend.slugUrl,
    targetAmount: frontend.targetAmount,
    location: frontend.location,
    category: {
        id: frontend.categoryId,
        name: frontend.categoryName
    },
    tags: frontend.tags.map(tag => ({
        id: tag.frontend.id,
        name: tag.frontend.name
    })),
    summary: frontend.summary,
    contentHtml: frontend.contentHtml,
    status: frontend.status,
    imageUploadRequests: frontend.imageUploadRequests.map(file => ({
        filename: file.frontend.filename,
        contentType: file.frontend.contentType,
        path: file.frontend.path
    }))
};

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.uploadUrl.map(upload => ({
        frontend.uploadUrl: upload.uploadUrl, // Object storage upload link
        frontend.imageKey: upload.imageKey
    })) 
};
```

##### Project — Update Image Keys (200)
- After a successful storage upload, the frontend needs to send the returned image keys to the backend to save them to the content record.

```javascript
const response = await api.put(
    `/organization/projects/${frontend.projectId}/image-keys`,
    requestBody
);

const requestBody = frontend.images.map(image => ({
    id: image.frontend.id, // Null: New, Existing: Keep, Missing: Remove
    key: image.frontend.key
}));
```

##### Project — Get By ID (200)

```javascript
const response = await api.get(
    `/organization/project/${frontend.projectId}`
);

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.name,
    frontend.var: response.data.slugUrl,
    frontend.var: response.data.collectedAmount,
    frontend.var: response.data.targetAmount,
    frontend.var: response.data.location,
    frontend.var: response.data.date,
    frontend.var: {
        frontend.id: response.data.category.id,
        frontend.name: response.data.category.name
    },
    frontend.var: response.data.tags.map(tag => ({
        frontend.id: tag.id,
        frontend.name: tag.name
    })),
    frontend.var: response.data.summary,
    frontend.var: response.data.contentHtml,
    frontend.var: response.data.status,
    frontend.var: response.data.images.map(image => ({
        frontend.id: image.id,
        frontend.url: image.url
    }))
};
```

##### Project — Get All (200)
- Returns pagination.

```javascript
const response = await api.get(
    "/organization/projects"
);

const frontend.list = response.data.content.map(object => ({
    frontend.var: object.id,
    frontend.var: object.name,
    frontend.var: object.slugUrl,
    frontend.var: object.collectedAmount,
    frontend.var: object.targetAmount,
    frontend.var: object.location,
    frontend.var: object.date,
    frontend.var: {
        frontend.id: response.data.category.id,
        frontend.name: response.data.category.name
    },
    frontend.var: frontend.var: response.data.tags.map(tag => ({
        frontend.id: tag.id,
        frontend.name: tag.name
    })),
    frontend.var: object.summary,
    frontend.var: object.contentHtml,
    frontend.var: object.status,
    frontend.var: response.data.images.map(image => ({
        frontend.id: image.id,
        frontend.url: image.url
    }))
}));

const frontend.object = {
    frontend.var: response.data.page.number,
    frontend.var: response.data.page.size,
    frontend.var: response.data.page.totalElements,
    frontend.var: response.data.page.totalPages
};
```

##### Project — Delete (200)

```javascript
const response = await api.delete(
    `/organization/project/${frontend.projectId}`
);
```

### News

#### News — Create (201)
- Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

```javascript
const response = await api.post(
    "/organization/news",
    requestBody
);

const requestBody = {
    title: frontend.title,
    slugUrl: frontend.slugUrl,
    author: frontend.author,
    date: frontend.date,
    category: {
        id: frontend.categoryId,
        name: frontend.categoryName
    },
    tags: frontend.tags.map(tag => ({
        id: tag.id,
        name: tag.name
    })),
    summary: frontend.summary,
    contentHtml: frontend.contentHtml,
    status: frontend.status,
    imageUploadRequests: frontend.images.map(image => ({
        filename: image.filename,
        contentType: image.contentType,
        path: image.path
    }))
};

const frontend.object = {
    frontend.id: response.data.id,
    frontend.uploadUrls: response.data.uploadUrls.map(object => ({
        frontend.uploadUrl: object.uploadUrl,
        frontend.imageKey: object.imageKey
    }))
};
```

#### News — Update (200)
- Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

```javascript
const response = await api.put(
    `/organization/news/${frontend.newsId}`,
    requestBody
);

const requestBody = {
    title: frontend.title,
    slugUrl: frontend.slugUrl,
    author: frontend.author,
    date: frontend.date,
    category: {
        id: frontend.categoryId,
        name: frontend.categoryName
    },
    tags: frontend.tags.map(tag => ({
        id: tag.id,
        name: tag.name
    })),
    summary: frontend.summary,
    contentHtml: frontend.contentHtml,
    status: frontend.status,
    imageUploadRequests: frontend.images.map(image => ({
        filename: image.filename,
        contentType: image.contentType,
        path: image.path
    }))
};

const frontend.object = {
    frontend.id: response.data.id,
    frontend.uploadUrls: response.data.uploadUrls.map(object => ({
        frontend.uploadUrl: object.uploadUrl,
        frontend.imageKey: object.imageKey
    }))
};
```

#### News — Upload Image Keys (200)

```javascript
const response = await api.put(
    `/organization/news/${frontend.newsId}/image-keys`,
    requestBody
);

const requestBody = frontend.images.map(image => ({
    id: image.id,
    key: image.imageKey
}));
```

#### News — Get By ID

```javascript
const response = await api.get(
    `/organization/news/${frontend.newsId}`
);

const frontend.object = {
    frontend.id: response.data.id,
    frontend.title: response.data.title,
    frontend.slugUrl: response.data.slugUrl,
    frontend.author: response.data.author,
    frontend.date: response.data.date,

    frontend.category: {
        frontend.id: response.data.category.id,
        frontend.name: response.data.category.name
    },

    frontend.tags: response.data.tags.map(object => ({
        frontend.id: object.id,
        frontend.name: object.name
    })),

    frontend.summary: response.data.summary,
    frontend.contentHtml: response.data.contentHtml,
    frontend.status: response.data.status,

    frontend.images: response.data.images.map(object => ({
        frontend.id: object.id,
        frontend.url: object.url
    }))
};
```

#### News — Get All

```javascript
const response = await api.get(
    "/organization/news"
);

const frontend.list = response.data.map(object => ({
    frontend.id: object.id,
    frontend.title: object.title,
    frontend.slugUrl: object.slugUrl,
    frontend.author: object.author,
    frontend.date: object.date,

    frontend.category: {
        frontend.id: object.category.id,
        frontend.name: object.category.name
    },

    frontend.tags: object.tags.map(tag => ({
        frontend.id: tag.id,
        frontend.name: tag.name
    })),

    frontend.summary: object.summary,
    frontend.contentHtml: object.contentHtml,
    frontend.status: object.status,

    frontend.images: object.images.map(image => ({
        frontend.id: image.id,
        frontend.url: image.url
    }))
}));
```

#### News — Delete

```javascript
const response = await api.delete(
    `/organization/news/${frontend.newsId}`
);
```

### Campaign

#### Campaign — Create (201)
- Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

```javascript
const requestBody = {
    name: frontend.name,
    slugUrl: frontend.slugUrl,
    dateStart: frontend.dateStart, // "dd-MM-yyyy"
    dateEnd: frontend.dateEnd, // "dd-MM-yyyy"

    category: {
        id: frontend.categoryId,
        name: frontend.categoryName
    },

    tags: frontend.tags.map(tag => ({
        id: tag.id,
        name: tag.name
    })),

    summary: frontend.summary,
    contentHtml: frontend.contentHtml,
    status: frontend.status,

    imageUploadRequests: frontend.images.map(image => ({
        filename: image.filename,
        contentType: image.contentType,
        path: image.path
    }))
};

const response = await api.post(
    "/organization/campaigns",
    requestBody
);

const frontend.object = {
    frontend.id: response.data.id,

    frontend.uploadUrls: response.data.uploadUrls.map(object => ({
        frontend.uploadUrl: object.uploadUrl,
        frontend.imageKey: object.imageKey
    }))
};
```

#### Campaign — Update (200)
- Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

```javascript
const requestBody = {
    name: frontend.name,
    slugUrl: frontend.slugUrl,
    dateStart: frontend.dateStart, // "dd-MM-yyyy"
    dateEnd: frontend.dateEnd, // "dd-MM-yyyy"

    category: {
        id: frontend.categoryId,
        name: frontend.categoryName
    },

    tags: frontend.tags.map(tag => ({
        id: tag.id,
        name: tag.name
    })),

    summary: frontend.summary,
    contentHtml: frontend.contentHtml,
    status: frontend.status,

    imageUploadRequests: frontend.images.map(image => ({
        filename: image.filename,
        contentType: image.contentType,
        path: image.path
    }))
};

const response = await api.put(
    `/organization/campaigns/${frontend.campaignId}`,
    requestBody
);

const frontend.object = {
    frontend.id: response.data.id,

    frontend.uploadUrls: response.data.uploadUrls.map(object => ({
        frontend.uploadUrl: object.uploadUrl,
        frontend.imageKey: object.imageKey
    }))
};
```

#### Campaign — Upload Image Keys

```javascript
const requestBody = frontend.images.map(image => ({
    id: image.id,
    key: image.imageKey
}));

const response = await api.put(
    `/organization/campaigns/${frontend.campaignId}/image-keys`,
    requestBody
);
```

#### Campaign — Get By ID

```javascript
const response = await api.get(
    `/organization/campaigns/${frontend.campaignId}`
);

const frontend.object = {
    frontend.id: response.data.id,
    frontend.name: response.data.name,
    frontend.slugUrl: response.data.slugUrl,
    frontend.dateStart: response.data.dateStart,
    frontend.dateEnd: response.data.dateEnd,

    frontend.category: {
        frontend.id: response.data.category.id,
        frontend.name: response.data.category.name
    },

    frontend.tags: response.data.tags.map(object => ({
        frontend.id: object.id,
        frontend.name: object.name
    })),

    frontend.summary: response.data.summary,
    frontend.contentHtml: response.data.contentHtml,
    frontend.status: response.data.status,

    frontend.images: response.data.images.map(object => ({
        frontend.id: object.id,
        frontend.url: object.url
    }))
};
```

#### Campaign — Get All

```javascript
const response = await api.get(
    "/organization/campaigns"
);

const frontend.list = response.data.map(object => ({
    frontend.id: object.id,
    frontend.name: object.name,
    frontend.slugUrl: object.slugUrl,
    frontend.dateStart: object.dateStart,
    frontend.dateEnd: object.dateEnd,

    frontend.category: {
        frontend.id: object.category.id,
        frontend.name: object.category.name
    },

    frontend.tags: object.tags.map(tag => ({
        frontend.id: tag.id,
        frontend.name: tag.name
    })),

    frontend.summary: object.summary,
    frontend.contentHtml: object.contentHtml,
    frontend.status: object.status,

    frontend.images: object.images.map(image => ({
        frontend.id: image.id,
        frontend.url: image.url
    }))
}));
```

#### Campaign — Delete

```javascript
const response = await api.delete(
    `/organization/campaigns/${frontend.campaignId}`
);
```

### Category (Project, News, Campaign)
- Required for Project, News, Campaign categories and tags for dropdown selection instead of text input.

- contentType: "project", "news", "campaign"
- /organization/project/category/...
- /organization/news/category/...
- /organization/campaign/category/...

#### Category - Create (201)

```javascript
const response = await api.post( 
    `/organization/${contentType}/category`, 
    requestBody   
); 

const requestBody = { 
    name: frontend.categoryName 
}; 

const frontend.object = { 
    frontend.categoryId: response.data.id, 
    frontend.categoryName: response.data.name 
};
```

#### Category - Get (200)

```javascript
const response = await api.get(
    `/organization/${contentType}/category/${frontend.categoryId}`
);

const frontend.object = {
    frontend.categoryId: response.data.id,
    frontend.categoryName: response.data.name
};
```

#### Category - Get All (200)

```javascript
const response = await api.get(
    `/organization/${contentType}/category`
);

const frontend.list = response.data.map(object => ({
    frontend.categoryId: object.id,
    frontend.categoryName: object.name
}));
```

#### Category - Delete (200)

```javascript
const response = await api.delete(
    `/organization/${contentType}/category/${frontend.categoryId}`
);
```

### Tag (Project, News, Campaign)

#### Tag - Create (201) 

```javascript
const response = await api.post(
    `/organization/${contentType}/tag`,
    requestBody
);

const requestBody = {
    name: frontend.tagName
};

const frontend.object = {
    frontend.tagId: response.data.id,
    frontend.tagName: response.data.name
};
```

#### Tag - Get (200)

```javascript
const response = await api.get(
    `/organization/${contentType}/tag/${frontend.tagId}`
);

const frontend.object = {
    frontend.tagId: response.data.id,
    frontend.tagName: response.data.name
};
```

#### Get All Tags

```javascript
const response = await api.get(
    `/organization/${contentType}/tag`
);

const frontend.list = response.data.map(object => ({
    frontend.tagId: object.id,
    frontend.tagName: object.name
}));
```

#### Delete Tag

```javascript
const response = await api.delete(
    `/organization/${contentType}/tag/${frontend.tagId}`
);
```

### Organization About

#### About — Update (200)

```javascript
const requestBody = {
    id: frontend.id,
    name: frontend.name,
    phone: frontend.phone,
    email: frontend.email,

    address: {
        addressLine1: frontend.addressLine1,
        addressLine2: frontend.addressLine2,
        addressLine3: frontend.addressLine3,
        postcode: frontend.postcode,
        city: frontend.city,
        state: frontend.state,
        country: frontend.country
    },

    contentHtml: frontend.contentHtml,

    logoUploadRequest: {
        filename: frontend.logo.filename,
        contentType: frontend.logo.contentType,
        path: null
    },

    heroUploadRequest: {
        filename: frontend.hero.filename,
        contentType: frontend.hero.contentType,
        path: null
    }
};

const response = await api.put(
    "/organization/about",
    requestBody
);

const frontend.object = {
    frontend.logoUploadUrl: response.data.logoUploadUrl,
    frontend.heroUploadUrl: response.data.heroUploadUrl
};
```

#### About — Upload Image Keys (200)

```javascript
const requestBody = {
    logoKey: frontend.logoKey,
    heroKey: frontend.heroKey
};

const response = await api.put(
    "/organization/about/image-keys",
    requestBody
);
```

#### About — Get (200)

```javascript
const response = await api.get(
    "/organization/about"
);

const frontend.object = {
    frontend.id: response.data.id,
    frontend.name: response.data.name,
    frontend.phone: response.data.phone,
    frontend.email: response.data.email,

    frontend.address: {
        frontend.addressLine1: response.data.address.addressLine1,
        frontend.addressLine2: response.data.address.addressLine2,
        frontend.addressLine3: response.data.address.addressLine3,
        frontend.postcode: response.data.address.postcode,
        frontend.city: response.data.address.city,
        frontend.state: response.data.address.state,
        frontend.country: response.data.address.country
    },

    frontend.contentHtml: response.data.contentHtml,
    frontend.logoUrl: response.data.logoUrl,
    frontend.heroUrl: response.data.heroUrl
};
```

## RakanQr

### RakanQr Dashboard

#### Rakan QR — Get Dashboard (200)
- Personal/Merchant, Active RakanQr-only. Retrieve preview of the user's Rakan Qr details, monthly collection, and fixed amount of clients donation transactions record.

```javascript
const response = await api.get(
    "/rakan-qr/dashboard"
);

const frontend.object = {
    frontend.object: { // User's rakan qr's info.
        frontend.var: response.data.info.id, 
        frontend.var: response.data.info.name, 
        frontend.var: response.data.info.email, 
        frontend.var: response.data.info.phone, 
        frontend.var: response.data.info.code, // RakanQr Code
        frontend.var: response.data.info.type, // STANDARD/AMBASSADOR 
        frontend.var: response.data.info.status, // ACTIVE/PENDING/INACTIVE 
        frontend.var: response.data.info.collectedAmount, // All time collected donations
        frontend.var: response.data.info.commission // All time commission receive. 
    },
    frontend.object: { // Current month collected donations.
        frontend.var: response.data.collectedAmount.total 
    },
    frontend.list: response.data.donations.map(object => ({  
        frontend.var: object.id, 
        frontend.var: object.billingCode, 
        frontend.var: object.transactionId, 
        frontend.var: object.amount, 
        frontend.var: object.paidAt, 
        frontend.var: object.status, 
        frontend.var: object.rakanQrCode 
    }))
}
```

### RakanQr Management

#### Rakan QR — Apply (201)
- Personal & Merchant only.

```javascript
const requestBody = {
    type: frontend.type // STANDARD/AMBASSADOR
};

const response = await api.post(
    "/rakan-qr/apply",
    requestBody
);

const frontend.object = {
    frontend.var: response.data.id, // Primary identifier
    frontend.var: response.data.name,
    frontend.var: response.data.email,
    frontend.var: response.data.phone,
    frontend.var: response.data.code, // RakanQr Code
    frontend.var: response.data.type, // STANDARD/AMBASSADOR
    frontend.var: response.data.status // ACTIVE/PENDING/INACTIVE
```

#### Rakan QR — Update Status (200)
- Admin-only to approve application.

```javascript
const requestBody = {
    status: frontend.status // ACTIVE/PENDING/INACTIVE
};

const response = await api.patch(
    `/rakan-qr/${frontend.rakanQrId}/status`,
    requestBody
);
```

#### Rakan QR — Get All (200)
Admin-only to get all RakanQr users, with optional filters for application request management, and type of RakanQr. NOTE: Returns a pagination.

```javascript
const params = { // Each are optional
    type: frontend.agentType, // STANDARD/AMBASSADOR
    status: frontend.status // ACTIVE/PENDING/INACTIVE
    page: frontend.page, // 0 (First page)
    size: frontend.size // Number of records per page
};

const response = await api.get(
    "/rakan-qr",
    {
        params // Optional
    }
);

const frontend.list = response.data.content.map(item => ({     
    frontend.var: item.id, // Primary identifier
    frontend.var: item.code, 
    frontend.var: item.name, 
    frontend.var: item.email, 
    frontend.var: item.phone, 
    frontend.var: item.type, // STANDARD/AMBASSADOR
    frontend.var: item.status,// ACTIVE/PENDING/INACTIVE
    frontend.var: item.collectedAmount, 
    frontend.var: item.commission 
}));

const frontend.pagination = { // Optional
    frontend.var: response.data.page.number, 
    frontend.var: response.data.page.size, 
    frontend.var: response.data.page.totalElements, 
    frontend.var: response.data.page.totalPages
};
```

### RakanQr Donation

#### Rakan QR (Public) — Request Payment (201)
- For RakanQr shared payment page to handle client donations. RakanQr code is used here instead of id.

```javascript
const requestBody = { 
    amount: frontend.amount, 
    redirectUrl: frontend.redirectUrl // URL to redirect back to UI.
}; 

const response = await api.post( 
    `/public/rakan-qr/${frontend.rakanQrCode}/donations/payment-request`, 
    request 
);

const frontend.object = { 
    frontend.var: response.data.id, // Donation ID
    frontend.var: response.data.billingCode, 
    frontend.var: response.data.amount, 
    frontend.var: response.data.status, 
    frontend.var: response.data.paymentUrl // Redirect user to this URL for payment.
};
```

#### Rakan QR (Public) — Get Donation Details (200) (Testing)
- For RakanQr shared payment page to retrive a payment/transaction/donation details to display payment status to client. (Needs polling)

```javascript
const response = await api.post( 
    `/public/rakan-qr/donations/${frontend.donationId}` 
);

const frontend.object = { 
    frontend.var: response.data.id, 
    frontend.var: response.data.billingCode, 
    frontend.var: response.data.transactionId,
    frontend.var: response.data.amount, 
    frontend.var: response.data.paidAt,
    frontend.var: response.data.status, 
    frontend.var: response.data.paymentUrl // Redirect user to this URL for payment
    frontend.var: response.data.rakanQrCode
};
```

#### Rakan QR — Get Collected Amount (200) 
- Personal/Merchant, Active RakanQr-only to get their respective collected amount, with optional filter for date range, for dynamic amount of daily, weekly, monthly, etc. 

```javascript
const filter = {
    startDate: frontend.startDate, // dd-MM-yyyy
    endDate: frontend.endDate // dd-MM-yyyy
};

const response = await api.get(
    "/rakan-qr/donations/collection",
    {
        params: filter // Optional
    }
);

const frontend.object = {
    frontend.var: response.data.total
};
```

#### Rakan QR — Get Donation Transaction Records (200) 
- Personal/Merchant, Active RakanQr-only to get their respective donations/transactions record. Returns a pagination.

```javascript
const params = { // Optional
    page: frontend.page, // 0 (First page)
    size: frontend.size // Number of records per page
    // Default sorting: createdAt, Desc
};

const response = await api.get(
    "/rakan-qr/donations",
    {   
        params // Optional
    }
);

const frontend.list = response.data.content.map(object = ({    
    frontend.var: object.id, // Primary identifier
    frontend.var: object.billingCode, 
    frontend.var: object.transactionId, 
    frontend.var: object.amount, 
    frontend.var: object.paidAt, 
    frontend.var: object.status, 
    frontend.var: object.rakanQrCode 
}));

const frontend.pagination = { // Optional
    frontend.var: response.data.page.number, 
    frontend.var: response.data.page.size, 
    frontend.var: response.data.page.totalElements, 
    frontend.var: response.data.page.totalPages
};
```

## Verification (Placeholder)

### Verification - OTP Request (Placeholder)

```javascript
const request = {
    phone: frontend.phone
};

const response = await api.post(
    "/rakan-qr/register/otp/request",
    request
);
```

### Verification - OTP Verification (Placeholder)

```javascript
const request = {
    phone: frontend.phone,
    otp: frontend.otp
};

const response = await api.post(
    "/rakan-qr/register/otp/verify",
    request
);
```

### Verification - eKYC Submission (Placeholder)

```javascript
const request = {
    request: frontend.ekycRequest
};

const response = await api.post(
    "/rakan-qr/register/ekyc",
    request
);
```

### Verification - Face ID (Placeholder)

```javascript
const request = {
    request: frontend.faceIdRequest
};

const response = await api.post(
    "/rakan-qr/register/face-id",
    request
);
```
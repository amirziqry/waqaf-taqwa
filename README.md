# Run with Docker

## Prerequisites

Install:

- Docker Desktop

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

### Admin — Register Editor

```javascript
const response = await api.post(
    "/admin/register-editor",
    requestBody
);

const requestBody = {
    username: frontend.username,
    email: frontend.email,
    password: frontend.password
};

const frontend.object = {
    frontend.var: response.data.username,
    frontend.var: response.data.email,
    frontend.var: response.data.roles
};
```

### Admin — Register Admin

```javascript
const response = await api.post(
    "/admin/register-admin",
    requestBody
);

const requestBody = {
    username: frontend.username,
    email: frontend.email,
    password: frontend.password
};

const frontend.object = {
    frontend.var: response.data.username,
    frontend.var: response.data.email,
    frontend.var: response.data.roles
};
```

### Admin — Get By Username

```javascript
const response = await api.get(
    `/admin/get/${frontend.username}`
);

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.username,
    frontend.var: response.data.email,
    frontend.var: response.data.roles
};
```

### Admin — Get All

```javascript
const response = await api.get(
    "/admin/get/all"
);

const frontend.list = response.data.map(object => ({
    frontend.var: object.id,
    frontend.var: object.username,
    frontend.var: object.email,
    frontend.var: object.roles
}));
```

### Admin — Update Role

```javascript
const response = await api.patch(
    `/admin/update/${frontend.username}/role`,
    requestBody
);

const requestBody = {
    role: frontend.role // "ADMIN" / "EDITOR"
};
```

### Admin — Delete

```javascript
const response = await api.delete(
    `/admin/delete/${frontend.username}`
);
```

### Admin — Login

```javascript
const response = await api.post(
    "/admin/login",
    requestBody
);

const requestBody = {
    username: frontend.username,
    password: frontend.password
};

const frontend.object = {
    frontend.var: response.data.username,
    frontend.var: response.data.email,
    frontend.var: response.data.roles
};
```

### Admin — Get Current User Authentication Status

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

### Personal — Register

```javascript
const response = await api.post(
    "/personal/register",
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

### Personal — Login

```javascript
const response = await api.post(
    "/personal/auth/login",
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

### Personal — Get Current User Auth Status

```javascript
const response = await api.get(
    "/personal/auth/me"
);
```

### Personal — Request Payment Gateway URL (Placeholder)

```javascript
const response = await api.post(
    "/personal/donation/payment/request-gateway-url",
    requestBody
);

const requestBody = {
    amount: frontend.amount,
    taxExempt: frontend.taxExemptFlag // true/false
};

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.billingCode,
    frontend.var: response.data.status,
    frontend.var: response.data.paymentUrl
};
```

### Personal — Get Payment Status
Frontend needs to poll this request at least every +1s since web socket is not used yet.

```javascript
const response = await api.get(
    `/personal/donation/payment/${frontend.id}/status`
); // Donation/transaction id

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.billingCode,
    frontend.var: response.data.amount,
    frontend.var: response.data.paidAt, // dd-MM-yyyy
    frontend.var: response.data.status,
    frontend.var: response.data.receiptHashId
};
```

### Personal — Get Donation Sum

```javascript
const filter = {
    startDate: frontend.startDate, // dd-MM-yyyy
    endDate: frontend.endDate // dd-MM-yyyy
};

const response = await api.get(
    "/personal/donation/sum",
    {
        params: filter // Optional
    }
);

const frontend.object = {
    frontend.var: response.data.total
};
```

## Organization

### Organization — Get Collection Summary (Placeholder)

```javascript
const filter = {
    startDate: frontend.startDate, // dd-MM-yyyy
    endDate: frontend.endDate // dd-MM-yyyy
};

const response = await api.get(
    "/organization/collection/sum",
    {
        params: filter // Optional
    }
);

const frontend.object = {
    frontend.var: response.data.personalDirectSum,
    frontend.var: response.data.personalRecurringSum,
    frontend.var: response.data.projectSum,
    frontend.var: response.data.merchantDirectSum,
    frontend.var: response.data.rakanQrSum,
    frontend.var: response.data.total
};
```

### Project

#### Project Donation — Request Payment Gateway URL (Placeholder)

```javascript
const response = await api.post(
    `/project/donation/${frontend.projectId}/payment/request-gateway-url`,
    requestBody
);

const requestBody = {
    amount: frontend.amount,
    taxExempt: frontend.taxExemptFlag // true/false
};

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.billingCode,
    frontend.var: response.data.status,
    frontend.var: response.data.paymentUrl
};
```

#### Project Donation — Get Payment Status

```javascript
const response = await api.get(
    `/project/donation/payment/${frontend.id}/status`
);

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.billingCode,
    frontend.var: response.data.amount,
    frontend.var: response.data.paidAt,
    frontend.var: response.data.status,
    frontend.var: response.data.receiptHashId,
    frontend.var: response.data.projectId,
    frontend.var: response.data.projectName
};
```

#### Project Donation — Get Project Donation Collection (Placeholder)

```javascript
const filter = {
    startDate: frontend.startDate, // dd-MM-yyyy
    endDate: frontend.endDate       // dd-MM-yyyy
};

const response = await api.get(
    `/project/donation/${frontend.projectId}/collection`,
    {
        params: filter // Optional
    }
);

const frontend.object = {
    frontend.var: response.data.total
};
```

#### Project — Create
Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

```javascript
const response = await api.post(
    "/organization/project/create",
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

#### Project — Update
Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

```javascript
const response = await api.put(
    `/organization/project/${frontend.projectId}/update`,
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

#### Project — Update Image Keys
After a successful storage upload, the frontend sends the returned image keys to the backend to save them to the content record.

```javascript
const response = await api.put(
    `/organization/project/${frontend.projectId}/image-keys/upload`,
    requestBody
);

const requestBody = frontend.images.map(image => ({
    id: image.frontend.id, // Null: New, Existing: Keep, Missing: Remove
    key: image.frontend.key
}));
```

#### Project — Get By ID

```javascript
const response = await api.get(
    `/organization/project/${frontend.projectId}/get`
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

#### Project — Get All

```javascript
const response = await api.get(
    "/organization/project/all/get"
);

const frontend.list = response.data.map(object => ({
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
```

#### Project — Delete

```javascript
const response = await api.delete(
    `/organization/project/${frontend.projectId}/delete`
);
```

### News

#### News — Create
Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

```javascript
const response = await api.post(
    "/organization/news/create",
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

#### News — Update
Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

```javascript
const response = await api.put(
    `/organization/news/${frontend.newsId}/update`,
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

#### News — Upload Image Keys

```javascript
const response = await api.put(
    `/organization/news/${frontend.newsId}/images/upload`,
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
    `/organization/news/${frontend.newsId}/get`
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
    "/organization/news/all/get"
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
    `/organization/news/${frontend.newsId}/delete`
);
```

### Campaign

#### Campaign — Create
Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

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
    "/organization/campaign/create",
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

#### Campaign — Update
Content images are uploaded directly to storage using the upload URL provided by the backend, rather than sending the image file through the backend.

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
    `/organization/campaign/${frontend.campaignId}/update`,
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
    `/organization/campaign/${frontend.campaignId}/image-keys/upload`,
    requestBody
);
```

#### Campaign — Get By ID

```javascript
const response = await api.get(
    `/organization/campaign/${frontend.campaignId}/get`
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
    "/organization/campaign/all/get"
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
    `/organization/campaign/${frontend.campaignId}/delete`
);
```

### Profile

#### Organization Profile — Update

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
    "/organization/profile/update-request",
    requestBody
);

const frontend.object = {
    frontend.logoUploadUrl: response.data.logoUploadUrl,
    frontend.heroUploadUrl: response.data.heroUploadUrl
};
```

#### Organization Profile — Upload Image Keys

```javascript
const requestBody = {
    logoKey: frontend.logoKey,
    heroKey: frontend.heroKey
};

const response = await api.put(
    "/organization/profile/image-keys/upload",
    requestBody
);
```

#### Organization Profile — Get

```javascript
const response = await api.get(
    "/organization/profile/get"
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

### Category (Project, News, Campaign)

- contentType: "project", "news", "campaign"
- /organization/project/category/...
- /organization/news/category/...
- /organization/campaign/category/...

#### Create Category

```javascript
const response = await api.post( 
    `/organization/${contentType}/category/create`, 
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

#### Get Category

```javascript
const response = await api.get(
    `/organization/${contentType}/category/${frontend.categoryId}/get`
);

const frontend.object = {
    frontend.categoryId: response.data.id,
    frontend.categoryName: response.data.name
};
```

#### Get All Categories

```javascript
const response = await api.get(
    `/organization/${contentType}/category/all/get`
);

const frontend.list = response.data.map(object => ({
    frontend.categoryId: object.id,
    frontend.categoryName: object.name
}));
```

#### Delete Category

```javascript
const response = await api.delete(
    `/organization/${contentType}/category/${frontend.categoryId}/delete`
);
```

### Tag (Project, News, Campaign)

#### Create Tag

```javascript
const response = await api.post(
    `/organization/${contentType}/tag/create`,
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

#### Get Tag

```javascript
const response = await api.get(
    `/organization/${contentType}/tag/${frontend.tagId}/get`
);

const frontend.object = {
    frontend.tagId: response.data.id,
    frontend.tagName: response.data.name
};
```

#### Get All Tags


```javascript
const response = await api.get(
    `/organization/${contentType}/tag/all/get`
);

const frontend.list = response.data.map(object => ({
    frontend.tagId: object.id,
    frontend.tagName: object.name
}));
```

#### Delete Tag

```javascript
const response = await api.delete(
    `/organization/${contentType}/tag/${frontend.tagId}/delete`
);
```

## RakanQr

## RakanQr Management

### Rakan QR Agent — Apply

```javascript
const response = await api.post(
    "/rakan-qr-agent/apply"
);

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.code,
    frontend.var: response.data.type, // PERSONAL / MERCHANT
    frontend.var: response.data.status, // ACTIVE / PENDING / INACTIVE
    frontend.var: response.data.email
```

### Rakan QR Agent — Get All

```javascript
const filter = {
    type: frontend.agentType, // PERSONAL / MERCHANT
    status: frontend.status // ACTIVE / PENDING / INACTIVE
};

const response = await api.get(
    "/rakan-qr-agent/get/all",
    {
        params: filter // Optional
    }
);

const frontend.object = response.data.map(item => ({
    frontend.var: item.id,
    frontend.var: item.code,
    frontend.var: item.type,  // PERSONAL / MERCHANT
    frontend.var: item.status // ACTIVE / PENDING / INACTIVE
}));
```

### Rakan QR Agent — Update Status

```javascript
const response = await api.patch(
    `/rakan-qr-agent/${frontend.id}/status`,
    {
        status: frontend.status // ACTIVE / PENDING / INACTIVE
    }
);
```

### Rakan QR — Get All Agents With Sum

```javascript
const filter = {
    startDate: frontend.startDate, // dd-MM-yyyy
    endDate: frontend.endDate // dd-MM-yyyy
};

const response = await api.get(
    "/rakan-qr-agent/get/sum/all",
    {
        params: filter // Optional
    }
);

const frontend.object = response.data.map(agent => ({
    id: agent.id,
    code: agent.code,
    type: agent.type,
    status: agent.status,
    totalCollected: agent.totalCollected
}));
```

## RakanQr Donation

### Rakan QR Donation — Get Sum

```javascript
const filter = {
    startDate: frontend.startDate, // dd-MM-yyyy
    endDate: frontend.endDate // dd-MM-yyyy
};

const response = await api.get(
    "/rakan-qr-agent/donation/sum",
    {
        params: filter // Optional
    }
);

const frontend.object = {
    frontend.var: response.data.total
};
```

### Verification - OTP Request (Placeholder)

```javascript
const request = {
    phone: frontend.phone
};

const response = await api.post(
    "/rakan-qr-agent/register/otp/request",
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
    "/rakan-qr-agent/register/otp/verify",
    request
);
```

### Verification - eKYC Submission (Placeholder)

```javascript
const request = {
    request: frontend.ekycRequest
};

const response = await api.post(
    "/rakan-qr-agent/register/ekyc",
    request
);
```

### Verification - Face ID (Placeholder)

```javascript
const request = {
    request: frontend.faceIdRequest
};

const response = await api.post(
    "/rakan-qr-agent/register/face-id",
    request
);
```

## Dashboard

### Admin — Get

```javascript
const response = await api.get(
    "/admin/dashboard/get"
);

const frontend.object = {
    frontend.collectionSum: {
        frontend.var: response.data.collectionSum.personalDirectSum,
        frontend.var: response.data.collectionSum.personalRecurringSum,
        frontend.var: response.data.collectionSum.projectSum,
        frontend.var: response.data.collectionSum.merchantDirectSum,
        frontend.var: response.data.collectionSum.rakanQrSum,
        frontend.var: response.data.collectionSum.total
    },

    frontend.projects = response.data.projects.map(object => ({
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
    })),

    frontend.news : response.data.news.map(object => ({
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
    })),

    frontend.list : response.data.campaigns.map(object => ({
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
    })),

    frontend.profile: {
        frontend.id: response.data.organizationProfile.id,
        frontend.name: response.data.organizationProfile.name,
        frontend.phone: response.data.organizationProfile.phone,
        frontend.email: response.data.organizationProfile.email,

        frontend.address: {
            frontend.addressLine1: response.data.organizationProfile.address.addressLine1,
            frontend.addressLine2: response.data.organizationProfile.address.addressLine2,
            frontend.addressLine3: response.data.organizationProfile.address.addressLine3,
            frontend.postcode: response.data.organizationProfile.address.postcode,
            frontend.city: response.data.organizationProfile.address.city,
            frontend.state: response.data.organizationProfile.address.state,
            frontend.country: response.data.organizationProfile.address.country
        },

        frontend.contentHtml: response.data.organizationProfile.contentHtml,
        frontend.logoUrl: response.data.organizationProfile.logoUrl,
        frontend.heroUrl: response.data.organizationProfile.heroUrl
    },

    frontend.rakanQrs: response.data.rakanQrSummary.map(agent => ({
        frontend.var: agent.id,
        frontend.var: agent.code,
        frontend.var: agent.type,
        frontend.var: agent.status,
        frontend.var: agent.totalCollected
    }))
};
```


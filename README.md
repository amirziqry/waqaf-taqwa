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

## Member

### Member — Register Editor

```javascript
const response = await api.post(
    "/member/register-editor",
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

### Member — Register Admin

```javascript
const response = await api.post(
    "/member/register-admin",
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

### Member — Get By Username

```javascript
const response = await api.get(
    `/member/get/${frontend.username}`
);

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.username,
    frontend.var: response.data.email,
    frontend.var: response.data.roles
};
```

### Member — Get All

```javascript
const response = await api.get(
    "/member/get/all"
);

const frontend.list = response.data.map(object => ({
    frontend.var: object.id,
    frontend.var: object.username,
    frontend.var: object.email,
    frontend.var: object.roles
}));
```

### Member — Update Role

```javascript
const response = await api.patch(
    `/member/update/${frontend.username}/role`,
    requestBody
);

const requestBody = {
    role: frontend.role // "ADMIN" / "EDITOR"
};
```

### Member — Delete

```javascript
const response = await api.delete(
    `/member/delete/${frontend.username}`
);
```

### Member — Login

```javascript
const response = await api.post(
    "/member/login",
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

### Member — Get Current User Authentication Status

```javascript
const response = await api.get(
    "/member/me"
);
```

## Vendor

### Vendor — Register

```javascript
const response = await api.post(
    "/vendor/register",
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

### Vendor — Login

```javascript
const response = await api.post(
    "/vendor/auth/login",
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

### Vendor — Get Current User Auth Status

```javascript
const response = await api.get(
    "/vendor/auth/me"
);
```

### Vendor — Get Donation Sum (Placeholder)

```javascript
const response = await api.get(
    "/vendor/donation/sum"
);

const frontend.object = {
    frontend.var: response.data.total
};
```

## Donator

### Donator — Register

```javascript
const response = await api.post(
    "/donator/register",
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

### Donator — Login

```javascript
const response = await api.post(
    "/donator/auth/login",
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

### Donator — Get Current User Auth Status

```javascript
const response = await api.get(
    "/donator/auth/me"
);
```

### Donator — Request Payment Gateway URL (Placeholder)

```javascript
const response = await api.post(
    "/donator/donation/payment/request-gateway-url",
    requestBody
);

const requestBody = {
    amount: frontend.amount
};

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.billingCode,
    frontend.var: response.data.status,
    frontend.var: response.data.paymentUrl
};
```

### Donator — Get Donation Sum (Placeholder)

```javascript
const response = await api.get(
    "/donator/donation/sum"
);

const frontend.object = {
    frontend.var: response.data.total
};
```

## Organization

### Organization — Get Donation Summary (Placeholder)

```javascript
const response = await api.get(
    "/organization/donation/sum"
);

const frontend.object = {
    frontend.var: response.data.donatorTotal,
    frontend.var: response.data.vendorTotal,
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
    amount: frontend.amount
};

const frontend.object = {
    frontend.var: response.data.id,
    frontend.var: response.data.billingCode,
    frontend.var: response.data.status,
    frontend.var: response.data.paymentUrl
};
```

#### Project Donation — Get Project Donation Sum (Placeholder)

```javascript
const response = await api.get(
    `/project/donation/${frontend.projectId}/sum`
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
    collectedAmount: frontend.collectedAmount,
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
    collectedAmount: frontend.collectedAmount,
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

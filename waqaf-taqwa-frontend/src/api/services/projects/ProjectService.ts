import api from "../../client";

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

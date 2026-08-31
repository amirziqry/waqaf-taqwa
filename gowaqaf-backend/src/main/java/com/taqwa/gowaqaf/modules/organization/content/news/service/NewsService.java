package com.taqwa.gowaqaf.modules.organization.content.news.service;

import java.util.List;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.organization.content.news.component.image.dto.NewsImageKey;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsUploadResponse;

public interface NewsService {

	NewsUploadResponse createNews(NewsUploadRequest request);

	NewsUploadResponse updateNewsById(UUID id, NewsUploadRequest dto);

	void uploadNewsImageKeysById(UUID id, List<NewsImageKey> request);

	NewsDetails getNewsDetailsById(UUID id);

	List<NewsDetails> getAllNews();

	void deleteNewsById(UUID id);

}

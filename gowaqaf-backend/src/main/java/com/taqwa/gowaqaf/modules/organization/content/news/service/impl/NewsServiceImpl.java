package com.taqwa.gowaqaf.modules.organization.content.news.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.external.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.external.storage.service.StorageService;
import com.taqwa.gowaqaf.modules.organization.content.component.category.service.ContentCategoryService;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.service.ContentTagService;
import com.taqwa.gowaqaf.modules.organization.content.news.component.image.dto.NewsImageKey;
import com.taqwa.gowaqaf.modules.organization.content.news.component.image.dto.NewsImageUrl;
import com.taqwa.gowaqaf.modules.organization.content.news.component.image.entity.NewsImage;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsUploadResponse;
import com.taqwa.gowaqaf.modules.organization.content.news.entity.News;
import com.taqwa.gowaqaf.modules.organization.content.news.mapper.NewsMapper;
import com.taqwa.gowaqaf.modules.organization.content.news.repository.NewsRepository;
import com.taqwa.gowaqaf.modules.organization.content.news.service.NewsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

	private final NewsRepository newsRepository;
	private final ContentCategoryService categoryService;
	private final ContentTagService tagService;
	private final StorageService storageService;

	@Override
	public NewsUploadResponse createNews(NewsUploadRequest dto) {
		News news = new News();

		news.setTitle(dto.getTitle());
		news.setSlugUrl(dto.getSlugUrl());
		news.setAuthor(dto.getAuthor());
		news.setDate(dto.getDate());

		if (dto.getCategory() != null)
			news.setCategory(categoryService.getCategoryById(ContentType.NEWS, dto.getCategory().getId()));

		if (dto.getTags() != null) {
			Set<Long> tagIds = dto.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet());
			Set<ContentTag> tags = new HashSet<>(tagService.getAllTagById(ContentType.NEWS, tagIds));

			news.setTags(tags);
		}

		news.setSummary(dto.getSummary());
		news.setContentHtml(dto.getContentHtml());
		news.setStatus(dto.getStatus());
		news.setImages(new ArrayList<>());

		News saved = newsRepository.save(news);

		List<UploadUrl> uploadUrls = null;
		if (dto.getImageUploadRequests() != null)
			uploadUrls = generateImageUploadUrls(saved.getId(), dto.getImageUploadRequests());

		return new NewsUploadResponse(saved.getId(), uploadUrls);
	}

	@Override
	public NewsUploadResponse updateNewsById(UUID id, NewsUploadRequest dto) {
		News news = newsRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.A001, String.format("News ID: %s not found.", id)));

		news.setTitle(dto.getTitle());
		news.setSlugUrl(dto.getSlugUrl());
		news.setAuthor(dto.getAuthor());
		news.setDate(dto.getDate());

		if (dto.getCategory() != null)
			news.setCategory(categoryService.getCategoryById(ContentType.NEWS, dto.getCategory().getId()));

		if (dto.getTags() != null) {
			Set<Long> tagIds = dto.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet());
			Set<ContentTag> tags = new HashSet<>(tagService.getAllTagById(ContentType.NEWS, tagIds));

			news.setTags(tags);
		}

		news.setSummary(dto.getSummary());
		news.setContentHtml(dto.getContentHtml());
		news.setStatus(dto.getStatus());

		News saved = newsRepository.save(news);

		List<UploadUrl> uploadUrls = generateImageUploadUrls(saved.getId(), dto.getImageUploadRequests());

		return new NewsUploadResponse(saved.getId(), uploadUrls);
	}

	private List<UploadUrl> generateImageUploadUrls(UUID id, List<FileUploadRequest> files) {
		files.forEach(file -> file.setPath(String.format("campaign/%s/images/", id.toString())));

		List<UploadUrl> response = storageService.generateUploadUrls(files);

		return response;
	}

	@Override
	public void uploadNewsImageKeysById(UUID id, List<NewsImageKey> request) {
		News news = newsRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.A001, String.format("News ID: %s not found.", id)));

		List<NewsImage> existingImages = news.getImages();

		// IDs that are still present in the frontend's new image stack
		Set<Long> requestedIds = request.stream().map(NewsImageKey::getId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		// Remove images that no longer exist in the new stack
		existingImages.removeIf(image -> {

			if (!requestedIds.contains(image.getId())) {
				storageService.deleteFile(image.getFileKey());
				return true;
			}

			return false;
		});

		// Add new images
		List<NewsImage> newImages = request.stream().filter(image -> image.getId() == null).map(imageRequest -> {

			NewsImage image = new NewsImage();

			image.setFileKey(imageRequest.getKey());
			image.setNews(news);

			return image;
		}).toList();

		existingImages.addAll(newImages);

		newsRepository.save(news);
	}

	@Override
	public NewsDetails getNewsDetailsById(UUID id) {
		News news = newsRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.A001, String.format("News ID: %s not found.", id)));

		NewsDetails dto = NewsMapper.mapToNewsDetails(news);

		dto.setImages(news.getImages().stream().map(image -> {
			NewsImageUrl imageUrl = new NewsImageUrl();

			imageUrl.setId(image.getId());
			imageUrl.setUrl(storageService.generateAccessUrl(image.getFileKey()));

			return imageUrl;
		}).toList());

		return dto;
	}

	@Override
	public List<NewsDetails> getAllNews() {
		List<News> news = newsRepository.findAll();
		if (news.size() == 0)
			throw new BadRequestException(ErrorCode.A001, "No news found.");

		List<NewsDetails> dtos = news.stream().map(n -> {
			NewsDetails dto = NewsMapper.mapToNewsDetails(n);

			dto.setImages(n.getImages().stream().map(image -> {
				NewsImageUrl imageUrl = new NewsImageUrl();

				imageUrl.setId(image.getId());
				imageUrl.setUrl(storageService.generateAccessUrl(image.getFileKey()));

				return imageUrl;
			}).toList());

			return dto;
		}).toList();

		return dtos;
	}

	@Override
	public void deleteNewsById(UUID id) {
		News news = newsRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.A001, String.format("News ID: %s not found.", id)));

		// Delete image files from object storage
		for (NewsImage image : news.getImages()) {
			storageService.deleteFile(image.getFileKey());
		}

		// Delete project and its ProjectImage records
		newsRepository.delete(news);
	}

}

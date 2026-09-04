package com.taqwa.gowaqaf.module.organization.content.news;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taqwa.gowaqaf.common.CommonClass;
import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.modules.organization.content.component.category.entity.ContentCategory;
import com.taqwa.gowaqaf.modules.organization.content.component.category.repository.ContentCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.repository.ContentTagRepository;
import com.taqwa.gowaqaf.modules.organization.content.news.component.image.dto.NewsImageKey;
import com.taqwa.gowaqaf.modules.organization.content.news.component.image.dto.NewsImageUrl;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsUploadResponse;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class NewsFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;
	private final S3Client s3Client;
	private final ContentCategoryRepository categoryRepository;
	private final ContentTagRepository tagRepository;

	@Value("${storage.bucket}")
	private String bucket;

	ContentCategory c1, c2;
	ContentTag t1, t2, t3, t4;

	@BeforeEach
	void setup() {
		this.c1 = CommonClass.createMockCategory(categoryRepository, "Kempen", ContentType.NEWS);
		this.c2 = CommonClass.createMockCategory(categoryRepository, "Ekonomi", ContentType.NEWS);

		this.t1 = CommonClass.createMockTag(tagRepository, "Ramadan", ContentType.NEWS);
		this.t2 = CommonClass.createMockTag(tagRepository, "Wakaf Tunai", ContentType.NEWS);
		this.t3 = CommonClass.createMockTag(tagRepository, "Kebajikan", ContentType.NEWS);
		this.t4 = CommonClass.createMockTag(tagRepository, "Usahawan", ContentType.NEWS);
	}

	@AfterAll
	void cleanupBucket() {
		s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).build()).contents()
				.forEach(object -> s3Client
						.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(object.key()).build()));
	}

	@Test
	@WithMockAdmin(username = "member", roles = { "EDITOR" })
	void endToEndNewsTest() throws Exception {
		HttpClient httpClient = HttpClient.newHttpClient();

		// Dump image files.
		byte[] dump1Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump1.jpg"));
		byte[] dump2Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump2.jpg"));
		byte[] dump3Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump3.jpg"));

		// /////////
		// CREATE //
		// /////////
		MvcResult result = createNewsRequest(mockNews1(), 1);

		// Extract object
		String response = result.getResponse().getContentAsString();
		NewsUploadResponse objectResponse = objectMapper.readValue(response, NewsUploadResponse.class);
		assertNotNull(objectResponse);

		UUID newsId = objectResponse.getId();
		List<UploadUrl> uploadUrls = objectResponse.getUploadUrls();

		// Extract urls and save to storage.
		List<String> urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		CommonClass.testSaveFileToStorage(httpClient, urls.get(0), dump1Bytes);

		// Extract keys and save to DB.
		List<String> imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		List<NewsImageKey> imageKeyRequests = imageKeys.stream().map(key -> new NewsImageKey(null, key)).toList();

		String imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		updateImageKeys(newsId.toString(), imagesJson);

		// Test get.
		result = mockMvc
				.perform(get("/api/organization/news/{newsId}/get", newsId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(newsId.toString()))
				.andExpect(jsonPath("$.title").value("Program Wakaf Tunai Ramadan Kumpul RM120,000"))
				.andExpect(jsonPath("$.slugUrl").value("program-wakaf-tunai-ramadan"))
				.andExpect(jsonPath("$.author").value("Unit Komunikasi"))
				.andExpect(jsonPath("$.date").value("17-07-2026"))
				.andExpect(jsonPath("$.category.name").value("Kempen")).andExpect(jsonPath("$.tags.length()").value(2))
				.andExpect(jsonPath("$.tags[*].name", Matchers.containsInAnyOrder("Ramadan", "Wakaf Tunai")))
				.andExpect(jsonPath("$.summary").value(
						"Kempen Wakaf Tunai sepanjang Ramadan berjaya mengumpul RM120,000 hasil sumbangan jemaah Masjid At-Taqwa dan orang ramai."))
				.andExpect(jsonPath("$.contentHtml").value("<p>Place content here</p>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images").isArray())
				.andExpect(jsonPath("$.images.length()").value(1)).andReturn();

		// Extract get urls.
		JsonNode profile = objectMapper.readTree(result.getResponse().getContentAsString());

		List<NewsImageUrl> urlList = objectMapper.convertValue(profile.get("images"),
				new TypeReference<List<NewsImageUrl>>() {
				});

		List<String> imagesList = urlList.stream().map(NewsImageUrl::getUrl).toList();

		// Test storage call.
		CommonClass.testGetImagesFromStorage(httpClient, imagesList);

		// /////////
		// UPDATE //
		// /////////
		result = updateNewsRequest(newsId.toString(), mockNews2(), 2);

		// Extract object
		response = result.getResponse().getContentAsString();
		objectResponse = objectMapper.readValue(response, NewsUploadResponse.class);
		assertNotNull(objectResponse);

		uploadUrls = objectResponse.getUploadUrls();
		assertEquals(2, uploadUrls.size());

		// Extract urls and save to storage.
		urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		CommonClass.testSaveFileToStorage(httpClient, urls.get(0), dump2Bytes);
		CommonClass.testSaveFileToStorage(httpClient, urls.get(1), dump1Bytes);

		// Extract keys and save to DB.
		imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		imageKeyRequests = imageKeys.stream().map(key -> new NewsImageKey(null, key)).toList();
		assertEquals(2, imageKeyRequests.size());

		imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		updateImageKeys(newsId.toString(), imagesJson);

		// Test get.
		result = mockMvc
				.perform(get("/api/organization/news/{newsId}/get", newsId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(newsId.toString()))
				.andExpect(jsonPath("$.title").value("Program Wakaf Tunai Ramadan Kumpul RM120,000"))
				.andExpect(jsonPath("$.slugUrl").value("program-wakaf-tunai-ramadan"))
				.andExpect(jsonPath("$.author").value("Unit Komunikasi"))
				.andExpect(jsonPath("$.date").value("17-07-2026"))
				.andExpect(jsonPath("$.category.name").value("Kempen")).andExpect(jsonPath("$.tags.length()").value(1))
				.andExpect(jsonPath("$.tags[*].name", Matchers.containsInAnyOrder("Ramadan")))
				.andExpect(jsonPath("$.summary").value(
						"Kempen Wakaf Tunai sepanjang Ramadan berjaya mengumpul RM120,000 hasil sumbangan jemaah Masjid At-Taqwa dan orang ramai."))
				.andExpect(jsonPath("$.contentHtml").value("<p>Place content here</p>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images").isArray())
				.andExpect(jsonPath("$.images.length()").value(2)).andReturn();

		// Extract get urls.
		profile = objectMapper.readTree(result.getResponse().getContentAsString());

		urlList = objectMapper.convertValue(profile.get("images"), new TypeReference<List<NewsImageUrl>>() {
		});

		imagesList = urlList.stream().map(NewsImageUrl::getUrl).toList();

		// Test storage call.
		CommonClass.testGetImagesFromStorage(httpClient, imagesList);

		// /////////////////
		// CREATE ///////////
		// ////////////////
		result = createNewsRequest(mockNews3(), 2);

		// Extract object
		response = result.getResponse().getContentAsString();
		objectResponse = objectMapper.readValue(response, NewsUploadResponse.class);
		assertNotNull(objectResponse);

		newsId = objectResponse.getId();
		uploadUrls = objectResponse.getUploadUrls();

		// Extract urls and save to storage.
		urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		CommonClass.testSaveFileToStorage(httpClient, urls.get(0), dump3Bytes);
		CommonClass.testSaveFileToStorage(httpClient, urls.get(1), dump2Bytes);

		// Extract keys and save to DB.
		imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		imageKeyRequests = imageKeys.stream().map(key -> new NewsImageKey(null, key)).toList();

		imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		updateImageKeys(newsId.toString(), imagesJson);

		// Test get.
		result = mockMvc
				.perform(get("/api/organization/news/{newsId}/get", newsId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(newsId.toString()))
				.andExpect(jsonPath("$.title").value("Program Ekonomi"))
				.andExpect(jsonPath("$.slugUrl").value("program-ekonomi"))
				.andExpect(jsonPath("$.author").value("Unit Ekonomi")).andExpect(jsonPath("$.date").value("17-07-2026"))
				.andExpect(jsonPath("$.category.name").value("Ekonomi")).andExpect(jsonPath("$.tags.length()").value(2))
				.andExpect(jsonPath("$.tags[*].name", Matchers.containsInAnyOrder("Kebajikan", "Usahawan")))
				.andExpect(jsonPath("$.summary").value("Program usahawan sepanjang Ramadan"))
				.andExpect(jsonPath("$.contentHtml").value("<p>Place content here</p>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images").isArray())
				.andExpect(jsonPath("$.images.length()").value(2)).andReturn();

		// Extract get urls.
		profile = objectMapper.readTree(result.getResponse().getContentAsString());

		urlList = objectMapper.convertValue(profile.get("images"), new TypeReference<List<NewsImageUrl>>() {
		});

		imagesList = urlList.stream().map(NewsImageUrl::getUrl).toList();

		// Test storage call.
		CommonClass.testGetImagesFromStorage(httpClient, imagesList);

		// Test get all
		mockMvc.perform(get("/api/organization/news/all/get")).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));

		// Test delete.
		mockMvc.perform(delete("/api/organization/news/{newsId}/delete", newsId)).andExpect(status().isOk());

		// Test get
		mockMvc.perform(get("/api/organization/news/{newsId}/get", newsId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	// ///////////////////////////
	// Endpoints /////////////////
	// ///////////////////////////

	private MvcResult createNewsRequest(String requestBody, int length) throws Exception {
		MvcResult result = mockMvc
				.perform(post("/api/organization/news/create").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.uploadUrls.length()").value(length)).andReturn();

		return result;
	}

	private MvcResult updateNewsRequest(String id, String requestBody, int length) throws Exception {
		MvcResult result = mockMvc
				.perform(put("/api/organization/news/" + id + "/update").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.uploadUrls.length()").value(length)).andReturn();

		return result;
	}

	private void updateImageKeys(String newsId, String requestBody) throws Exception {
		mockMvc.perform(put("/api/organization/news/" + newsId + "/image-keys/upload")
				.contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk());
	}

	// /////////////////////////////////
	// Mock News ///////////////////////
	// /////////////////////////////////

	private String mockNews1() {
		return """
				{
				  "title": "Program Wakaf Tunai Ramadan Kumpul RM120,000",
				  "slugUrl": "program-wakaf-tunai-ramadan",
				  "author": "Unit Komunikasi",
				  "date": "17-07-2026",
				  "category": {
				    "id": "%s", "name": "Kempen"
				  },
				  "tags": [
				    {
				      "id": "%s", "name": "Ramadan"
				    },
				    {
				      "id": "%s", "name": "Wakaf Tunai"
				    }
				  ],
				  "summary": "Kempen Wakaf Tunai sepanjang Ramadan berjaya mengumpul RM120,000 hasil sumbangan jemaah Masjid At-Taqwa dan orang ramai.",
				  "contentHtml": "<p>Place content here</p>",
				  "status": "PUBLISHED",
				  "imageUploadRequests": [
				    {
				      "filename": "news.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c1.getId(), t1.getId(), t2.getId());
	}

	private String mockNews2() {
		return """
				{
				  "title": "Program Wakaf Tunai Ramadan Kumpul RM120,000",
				  "slugUrl": "program-wakaf-tunai-ramadan",
				  "author": "Unit Komunikasi",
				  "date": "17-07-2026",
				  "category": {
				    "id": "%s", "name": "Kempen"
				  },
				  "tags": [
				    {
				      "id": "%s", "name": "Ramadan"
				    }
				  ],
				  "summary": "Kempen Wakaf Tunai sepanjang Ramadan berjaya mengumpul RM120,000 hasil sumbangan jemaah Masjid At-Taqwa dan orang ramai.",
				  "contentHtml": "<p>Place content here</p>",
				  "status": "PUBLISHED",
				  "imageUploadRequests": [
				    {
				      "filename": "news.jpg", "contentType": "image/jpeg"
				    },
				    {
				      "filename": "news.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c1.getId(), t1.getId());
	}

	private String mockNews3() {
		return """
				{
				  "title": "Program Ekonomi",
				  "slugUrl": "program-ekonomi",
				  "author": "Unit Ekonomi",
				  "date": "17-07-2026",
				  "category": {
				    "id": "%s", "name": "Ekonomi"
				  },
				  "tags": [
				    {
				      "id": "%s", "name": "Kebajikan"
				    },
				    {
				      "id": "%s", "name": "Usahawan"
				    }
				  ],
				  "summary": "Program usahawan sepanjang Ramadan",
				  "contentHtml": "<p>Place content here</p>",
				  "status": "PUBLISHED",
				  "imageUploadRequests": [
				    {
				      "filename": "news.jpg", "contentType": "image/jpeg"
				    },
				    {
				      "filename": "news.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				""".formatted(c2.getId(), t3.getId(), t4.getId());
	}

}

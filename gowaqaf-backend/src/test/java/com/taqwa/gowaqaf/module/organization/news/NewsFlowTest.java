package com.taqwa.gowaqaf.module.organization.news;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

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
import com.taqwa.gowaqaf.mockuser.member.WithMockMember;
import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategory;
import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.news.component.image.dto.NewsImageKey;
import com.taqwa.gowaqaf.modules.organization.news.component.image.dto.NewsImageUrl;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTag;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTagRepository;
import com.taqwa.gowaqaf.modules.organization.news.dto.NewsUploadResponse;
import com.taqwa.gowaqaf.storage.dto.UploadUrl;

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
	private final NewsCategoryRepository categoryRepository;
	private final NewsTagRepository tagRepository;

	@Value("${storage.bucket}")
	private String bucket;

	NewsCategory c1;
	NewsTag t1, t2;

	@BeforeEach
	void setup() {
		this.c1 = creatMockCategory("Kempen");

		this.t1 = creatMockTag("Ramadan");
		this.t2 = creatMockTag("Wakaf Tunai");
	}

	private NewsCategory creatMockCategory(String name) {
		NewsCategory category = new NewsCategory();
		category.setName(name);

		return categoryRepository.save(category);
	}

	private NewsTag creatMockTag(String name) {
		NewsTag tag = new NewsTag();
		tag.setName(name);

		return tagRepository.save(tag);
	}

	private String getContentHtml() {
		return "<p>Kempen Wakaf Tunai Ramadan yang dijalankan sepanjang bulan mulia berjaya mengumpul sebanyak RM120,000.</p><p>Dana ini akan disalurkan kepada projek naik taraf ruang solat serta tabung pendidikan anak asnaf.</p><p>Pihak pengurusan merakamkan setinggi-tinggi penghargaan kepada semua pewakaf.</p>";
	}

	@Test
	@WithMockMember(username = "member", roles = { "EDITOR" })
	void creatAndGetNewsTest() throws Exception {

		String request = """
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
				  "contentHtml": "%s",
				  "status": "PUBLISHED",
				  "imageUploadRequests": [
				    {
				      "filename": "building.jpg", "contentType": "image/jpeg"
				    },
				    {
				      "filename": "office.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c1.getId(), t1.getId(), t2.getId(), getContentHtml());

		MvcResult result = mockMvc
				.perform(post("/api/organization/news/create").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isCreated()).andReturn();

		String response = result.getResponse().getContentAsString();

		NewsUploadResponse objectResponse = objectMapper.readValue(response, NewsUploadResponse.class);

		UUID newsId = objectResponse.getId();
		Assertions.assertNotNull(newsId);

		Assertions.assertNotNull(objectResponse);
		Assertions.assertNotNull(objectResponse.getUploadUrls());
		Assertions.assertEquals(2, objectResponse.getUploadUrls().size());

		List<UploadUrl> uploadUrls = objectResponse.getUploadUrls();

		List<String> imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		List<String> uploadUrlsList = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		uploadUrlsList.forEach(Assertions::assertNotNull);

		byte[] dumpBytes1 = Files.readAllBytes(Paths.get("src/test/resources/dump1.jpg"));

		byte[] dumpBytes2 = Files.readAllBytes(Paths.get("src/test/resources/dump2.jpg"));

		HttpClient httpClient = HttpClient.newHttpClient();

		HttpRequest dump1Request = HttpRequest.newBuilder().uri(URI.create(uploadUrlsList.get(0)))
				.header("Content-Type", "image/jpeg").PUT(HttpRequest.BodyPublishers.ofByteArray(dumpBytes1)).build();

		HttpResponse<byte[]> dump1Response = httpClient.send(dump1Request, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, dump1Response.statusCode());

		HttpRequest dump2Request = HttpRequest.newBuilder().uri(URI.create(uploadUrlsList.get(1)))
				.header("Content-Type", "image/jpeg").PUT(HttpRequest.BodyPublishers.ofByteArray(dumpBytes2)).build();

		HttpResponse<byte[]> dump2Response = httpClient.send(dump2Request, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, dump2Response.statusCode());

		// Both images are new, therefore their IDs are null.
		List<NewsImageKey> imageKeyRequests = imageKeys.stream().map(key -> new NewsImageKey(null, key)).toList();

		// ============================================================
		// 5. Save image keys
		// ============================================================
		String imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		mockMvc.perform(put("/api/organization/news/" + newsId + "/images/upload")
				.contentType(MediaType.APPLICATION_JSON).content(imagesJson)).andExpect(status().isOk());

		result = mockMvc
				.perform(get("/api/organization/news/" + newsId + "/get").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(newsId.toString()))
				.andExpect(jsonPath("$.title").value("Program Wakaf Tunai Ramadan Kumpul RM120,000"))
				.andExpect(jsonPath("$.slugUrl").value("program-wakaf-tunai-ramadan"))
				.andExpect(jsonPath("$.author").value("Unit Komunikasi"))
				.andExpect(jsonPath("$.date").value("17-07-2026"))
				.andExpect(jsonPath("$.category.name").value("Kempen")).andExpect(jsonPath("$.tags.length()").value(2))
				.andExpect(jsonPath("$.summary").value(
						"Kempen Wakaf Tunai sepanjang Ramadan berjaya mengumpul RM120,000 hasil sumbangan jemaah Masjid At-Taqwa dan orang ramai."))
				.andExpect(jsonPath("$.contentHtml").value(getContentHtml()))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images").isArray())
				.andExpect(jsonPath("$.images.length()").value(2)).andReturn();

		JsonNode profile = objectMapper.readTree(result.getResponse().getContentAsString());

		List<NewsImageUrl> urls = objectMapper.convertValue(profile.get("images"),
				new TypeReference<List<NewsImageUrl>>() {
				});

		urls.forEach(url -> {
			try {
				testGetImage(httpClient, url.getUrl());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		imageKeys.forEach(key -> {
			deleteFileFromStorage(key);
		});

		testDeletedImage(httpClient, urls.get(0).getUrl());
	}

	private void testGetImage(HttpClient httpClient, String url) throws Exception {
		HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<byte[]> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, getResponse.statusCode());
	}

	private void testDeletedImage(HttpClient httpClient, String url) throws Exception {
		HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<byte[]> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(404, getResponse.statusCode());
	}

	private void deleteFileFromStorage(String fileKey) {
		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(fileKey).build());
	}

	@AfterAll
	void cleanupBucket() {
		s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).build()).contents()
				.forEach(object -> s3Client
						.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(object.key()).build()));
	}

}

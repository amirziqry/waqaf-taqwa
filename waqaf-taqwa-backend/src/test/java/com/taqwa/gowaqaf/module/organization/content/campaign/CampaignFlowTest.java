package com.taqwa.gowaqaf.module.organization.content.campaign;

import static org.hamcrest.CoreMatchers.hasItems;
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
import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.dto.CampaignImageKey;
import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.dto.CampaignImageUrl;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignUploadResponse;
import com.taqwa.gowaqaf.modules.organization.content.component.category.entity.ContentCategory;
import com.taqwa.gowaqaf.modules.organization.content.component.category.repository.ContentCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.repository.ContentTagRepository;

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
public class CampaignFlowTest {

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
		this.c1 = CommonClass.createMockCategory(categoryRepository, "Korporat", ContentType.CAMPAIGN);
		this.c2 = CommonClass.createMockCategory(categoryRepository, "Kempen", ContentType.CAMPAIGN);

		this.t1 = CommonClass.createMockTag(tagRepository, "CSR", ContentType.CAMPAIGN);
		this.t2 = CommonClass.createMockTag(tagRepository, "Korporat", ContentType.CAMPAIGN);
		this.t3 = CommonClass.createMockTag(tagRepository, "Wakaf Jariah", ContentType.CAMPAIGN);
		this.t4 = CommonClass.createMockTag(tagRepository, "Berkala", ContentType.CAMPAIGN);
	}

	@AfterAll
	void cleanupBucket() {
		s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).build()).contents()
				.forEach(object -> s3Client
						.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(object.key()).build()));
	}

	@Test
	@WithMockAdmin(username = "member", roles = { "EDITOR" })
	void endToEndCampaignTest() throws Exception {
		HttpClient httpClient = HttpClient.newHttpClient();

		// Dump image files.
		byte[] dump1Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump1.jpg"));
		byte[] dump2Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump2.jpg"));
		byte[] dump3Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump3.jpg"));

		// /////////
		// CREATE //
		// /////////
		MvcResult result = createCampaignRequest(mockCampaign1(), 1);

		// Extract object
		String response = result.getResponse().getContentAsString();
		CampaignUploadResponse objectResponse = objectMapper.readValue(response, CampaignUploadResponse.class);
		assertNotNull(objectResponse);

		UUID campaignId = objectResponse.getId();
		List<UploadUrl> uploadUrls = objectResponse.getUploadUrls();

		// Extract urls and save to storage.
		List<String> urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		CommonClass.testSaveFileToStorage(httpClient, urls.get(0), dump1Bytes);

		// Extract keys and save to DB.
		List<String> imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		List<CampaignImageKey> imageKeyRequests = imageKeys.stream().map(key -> new CampaignImageKey(null, key))
				.toList();

		String imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		updateImageKeys(campaignId.toString(), imagesJson);

		result = mockMvc
				.perform(get("/api/organization/campaign/" + campaignId + "/get")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(campaignId.toString()))
				.andExpect(jsonPath("$.name").value("Wakaf Korporat & Padanan Sumbangan"))
				.andExpect(jsonPath("$.slugUrl").value("wakaf-korporat-padanan-sumbangan"))
				.andExpect(jsonPath("$.dateStart").value("22-07-2026"))
				.andExpect(jsonPath("$.dateEnd").value("25-09-2026"))
				.andExpect(jsonPath("$.category.name").value("Korporat"))
				.andExpect(jsonPath("$.tags.length()").value(2))
				.andExpect(jsonPath("$.tags[*].name", hasItems("Korporat", "CSR")))
				.andExpect(jsonPath("$.summary").value(
						"Program padanan sumbangan untuk syarikat yang ingin menyalurkan dana CSR kepada projek wakaf berimpak tinggi."))
				.andExpect(jsonPath("$.contentHtml").value("<p>Place content here</p>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images.length()").value(1))
				.andReturn();

		// Extract get urls.
		JsonNode profile = objectMapper.readTree(result.getResponse().getContentAsString());

		List<CampaignImageUrl> urlList = objectMapper.convertValue(profile.get("images"),
				new TypeReference<List<CampaignImageUrl>>() {
				});

		List<String> imagesList = urlList.stream().map(CampaignImageUrl::getUrl).toList();

		// Test storage call.
		CommonClass.testGetImagesFromStorage(httpClient, imagesList);

		// /////////
		// UPDATE //
		// /////////
		result = updateCampaignRequest(campaignId.toString(), mockCampaign2(), 3);

		// Extract object
		response = result.getResponse().getContentAsString();
		objectResponse = objectMapper.readValue(response, CampaignUploadResponse.class);
		assertNotNull(objectResponse);

		uploadUrls = objectResponse.getUploadUrls();
		assertEquals(3, uploadUrls.size());

		// Extract urls and save to storage.
		urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		CommonClass.testSaveFileToStorage(httpClient, urls.get(0), dump2Bytes);
		CommonClass.testSaveFileToStorage(httpClient, urls.get(1), dump1Bytes);
		CommonClass.testSaveFileToStorage(httpClient, urls.get(2), dump3Bytes);

		// Extract keys and save to DB.
		imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		imageKeyRequests = imageKeys.stream().map(key -> new CampaignImageKey(null, key)).toList();
		assertEquals(3, imageKeyRequests.size());

		imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		updateImageKeys(campaignId.toString(), imagesJson);

		result = mockMvc
				.perform(get("/api/organization/campaign/" + campaignId + "/get")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(campaignId.toString()))
				.andExpect(jsonPath("$.name").value("Wakaf Korporat & Padanan Sumbangan"))
				.andExpect(jsonPath("$.slugUrl").value("wakaf-korporat-padanan-sumbangan"))
				.andExpect(jsonPath("$.dateStart").value("22-07-2026"))
				.andExpect(jsonPath("$.dateEnd").value("25-09-2026"))
				.andExpect(jsonPath("$.category.name").value("Korporat"))
				.andExpect(jsonPath("$.tags.length()").value(0))
				.andExpect(jsonPath("$.summary").value(
						"Program padanan sumbangan untuk syarikat yang ingin menyalurkan dana CSR kepada projek wakaf berimpak tinggi."))
				.andExpect(jsonPath("$.contentHtml").value("<p>Place content here</p>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images.length()").value(3))
				.andReturn();

		// Extract get urls.
		profile = objectMapper.readTree(result.getResponse().getContentAsString());

		urlList = objectMapper.convertValue(profile.get("images"), new TypeReference<List<CampaignImageUrl>>() {
		});

		imagesList = urlList.stream().map(CampaignImageUrl::getUrl).toList();

		// Test storage call.
		CommonClass.testGetImagesFromStorage(httpClient, imagesList);

		// /////////////////
		// CREATE ///////////
		// ////////////////
		result = createCampaignRequest(mockCampaign3(), 1);

		// Extract object
		response = result.getResponse().getContentAsString();
		objectResponse = objectMapper.readValue(response, CampaignUploadResponse.class);
		assertNotNull(objectResponse);

		campaignId = objectResponse.getId();
		uploadUrls = objectResponse.getUploadUrls();

		// Extract urls and save to storage.
		urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		CommonClass.testSaveFileToStorage(httpClient, urls.get(0), dump3Bytes);

		// Extract keys and save to DB.
		imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		imageKeyRequests = imageKeys.stream().map(key -> new CampaignImageKey(null, key)).toList();

		imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		updateImageKeys(campaignId.toString(), imagesJson);

		result = mockMvc
				.perform(get("/api/organization/campaign/" + campaignId + "/get")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(campaignId.toString()))
				.andExpect(jsonPath("$.name").value("Kempen Wakaf Jariah 30 Hari"))
				.andExpect(jsonPath("$.slugUrl").value("kempen-wakaf-jariah-30-hari"))
				.andExpect(jsonPath("$.dateStart").value("17-07-2026"))
				.andExpect(jsonPath("$.dateEnd").value("16-08-2026"))
				.andExpect(jsonPath("$.category.name").value("Kempen")).andExpect(jsonPath("$.tags.length()").value(2))
				.andExpect(jsonPath("$.tags[*].name", hasItems("Wakaf Jariah", "Berkala")))

				.andExpect(jsonPath("$.summary").value(
						"Sertai kempen 30 hari berwakaf serendah RM10 sehari dan raih pahala berterusan untuk diri serta keluarga."))
				.andExpect(jsonPath("$.contentHtml").value("<p>Place content here</p>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images.length()").value(1))
				.andReturn();

		// Extract get urls.
		profile = objectMapper.readTree(result.getResponse().getContentAsString());

		urlList = objectMapper.convertValue(profile.get("images"), new TypeReference<List<CampaignImageUrl>>() {
		});

		imagesList = urlList.stream().map(CampaignImageUrl::getUrl).toList();

		// Test storage call.
		CommonClass.testGetImagesFromStorage(httpClient, imagesList);

		// Test get all
		mockMvc.perform(get("/api/organization/campaign/all/get")).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));

		// Test delete.
		mockMvc.perform(delete("/api/organization/campaign/{campaignId}/delete", campaignId))
				.andExpect(status().isOk());

		// Test get
		mockMvc.perform(
				get("/api/organization/campaign/{campaignId}/get", campaignId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	// ///////////////////////////
	// Endpoints /////////////////
	// ///////////////////////////

	private MvcResult createCampaignRequest(String requestBody, int length) throws Exception {
		MvcResult result = mockMvc
				.perform(post("/api/organization/campaign/create").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.uploadUrls.length()").value(length)).andReturn();

		return result;
	}

	private MvcResult updateCampaignRequest(String campaignId, String requestBody, int length) throws Exception {
		MvcResult result = mockMvc
				.perform(put("/api/organization/campaign/" + campaignId + "/update")
						.contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(campaignId))
				.andExpect(jsonPath("$.uploadUrls.length()").value(length)).andReturn();

		return result;
	}

	private void updateImageKeys(String campaignId, String requestBody) throws Exception {
		mockMvc.perform(put("/api/organization/campaign/" + campaignId + "/image-keys/upload")
				.contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk());
	}

	// /////////////////////////////////
	// Mock Campaign ///////////////////
	// /////////////////////////////////

	private String mockCampaign1() {
		return """
				{
				  "name": "Wakaf Korporat & Padanan Sumbangan",
				  "slugUrl": "wakaf-korporat-padanan-sumbangan",
				  "dateStart": "22-07-2026",
				  "dateEnd": "25-09-2026",
				  "category": {
				    "id": "%s", "name": "Korporat"
				  },
				  "tags": [
				    {
				      "id": "%s", "name": "CSR"
				    },
				    {
				      "id": "%s", "name": "Korporat"
				    }
				  ],
				  "summary": "Program padanan sumbangan untuk syarikat yang ingin menyalurkan dana CSR kepada projek wakaf berimpak tinggi.",
				  "contentHtml": "<p>Place content here</p>",
				  "status": "PUBLISHED",
				  "imageUploadRequests": [
				    {
				      "filename": "campaign1.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c1.getId(), t1.getId(), t2.getId());
	}

	private String mockCampaign2() {
		return """
				{
				  "name": "Wakaf Korporat & Padanan Sumbangan",
				  "slugUrl": "wakaf-korporat-padanan-sumbangan",
				  "dateStart": "22-07-2026",
				  "dateEnd": "25-09-2026",
				  "category": {
				    "id": "%s", "name": "Korporat"
				  },
				  "tags": [],
				  "summary": "Program padanan sumbangan untuk syarikat yang ingin menyalurkan dana CSR kepada projek wakaf berimpak tinggi.",
				  "contentHtml": "<p>Place content here</p>",
				  "status": "PUBLISHED",
				  "imageUploadRequests": [
				    {
				      "filename": "campaign1.jpg", "contentType": "image/jpeg"
				    },
				    {
				      "filename": "campaign1.jpg", "contentType": "image/jpeg"
				    },
				    {
				      "filename": "campaign1.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c1.getId(), t1.getId(), t2.getId());
	}

	private String mockCampaign3() {
		return """
				{
				  "name": "Kempen Wakaf Jariah 30 Hari",
				  "slugUrl": "kempen-wakaf-jariah-30-hari",
				  "dateStart": "17-07-2026",
				  "dateEnd": "16-08-2026",
				  "category": {
				    "id": "%s", "name": "Kempen"
				  },
				  "tags": [
				    {
				      "id": "%s", "name": "Wakaf Jariah"
				    },
				    {
				      "id": "%s", "name": "Berkala"
				    }
				  ],
				  "summary": "Sertai kempen 30 hari berwakaf serendah RM10 sehari dan raih pahala berterusan untuk diri serta keluarga.",
				  "contentHtml": "<p>Place content here</p>",
				  "status": "PUBLISHED",
				  "imageUploadRequests": [
				    {
				      "filename": "campaign1.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c2.getId(), t3.getId(), t4.getId());
	}

}

package com.taqwa.gowaqaf.module.organization.campaign;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.junit.jupiter.api.Disabled;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.dto.CampaignImageKey;
import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.dto.CampaignImageUrl;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignUploadResponse;
import com.taqwa.gowaqaf.modules.organization.content.component.category.entity.ContentCategory;
import com.taqwa.gowaqaf.modules.organization.content.component.category.repository.ContentCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.repository.ContentTagRepository;
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
		this.c1 = creatMockCategory("Korporat");
		this.c2 = creatMockCategory("Kempen");

		this.t1 = creatMockTag("CSR");
		this.t2 = creatMockTag("Korporat");
		this.t3 = creatMockTag("Wakaf Jariah");
		this.t4 = creatMockTag("Berkala");
	}

	private ContentCategory creatMockCategory(String name) {
		ContentCategory category = new ContentCategory();
		category.setName(name);
		category.setType(ContentType.CAMPAIGN);

		return categoryRepository.save(category);
	}

	private ContentTag creatMockTag(String name) {
		ContentTag tag = new ContentTag();
		tag.setName(name);
		tag.setType(ContentType.CAMPAIGN);

		return tagRepository.save(tag);
	}

	private MvcResult createCampaignRequest() throws Exception {
		String jsonBody = """
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
				  "contentHtml": "<p>Waqaf Taqwa Berhad menawarkan program wakaf korporat di mana syarikat boleh menaja keseluruhan projek atau menyertai skim padanan sumbangan bersama pewakaf individu.</p><p>Laporan impak suku tahunan disediakan untuk setiap penaja korporat.</p>",
				  "status": "DRAFT",
				  "imageUploadRequests": [
				    {
				      "filename": "promosi1.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c1.getId(), t1.getId(), t2.getId());

		MvcResult result = mockMvc.perform(
				post("/api/organization/campaign/create").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
				.andExpect(status().isCreated()).andReturn();

		return result;
	}

	@SuppressWarnings("unused")
	private MvcResult updateCampaignRequest() throws Exception {
		String jsonBody = """
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
				  "contentHtml": "<p>Kempen Wakaf Jariah 30 Hari menggalakkan amalan berwakaf secara berkala. Dengan hanya RM10 sehari, anda menyumbang kepada projek pembangunan masjid, pendidikan dan kesihatan komuniti.</p><h3>Cara Sertai</h3><ol><li>Imbas kod QR DuitNow di Masjid At-Taqwa</li><li>Tetapkan sumbangan berkala melalui perbankan internet</li><li>Hubungi kami untuk borang wakaf berkala</li></ol>",
				  "status": "DRAFT",
				  "files": [
				    {
				      "filename": "promosi1.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c1, t1, t2);

		MvcResult result = mockMvc
				.perform(post("/api/campaign/update").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		return result;
	}

	@Test
	@WithMockAdmin(username = "member", roles = { "ADMIN" })
	void createAndGetCampaignTest() throws Exception {
		// ============================================================
		// Create project + get response
		// ============================================================
		MvcResult result = createCampaignRequest();

		String response = result.getResponse().getContentAsString();

		CampaignUploadResponse objectResponse = objectMapper.readValue(response, CampaignUploadResponse.class);
		UUID campaignId = objectResponse.getId();

		assertNotNull(campaignId);
		assertNotNull(objectResponse);
		assertNotNull(objectResponse.getUploadUrls());
		assertEquals(1, objectResponse.getUploadUrls().size());

		List<UploadUrl> uploadUrls = objectResponse.getUploadUrls();

		List<String> imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		List<String> urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		urls.forEach(Assertions::assertNotNull);

		byte[] dumpBytes = Files.readAllBytes(Paths.get("src/test/resources/dump1.jpg"));

		HttpClient httpClient = HttpClient.newHttpClient();

		testSaveFileToStorage(httpClient, urls.getFirst(), dumpBytes);

		// Both images are new, therefore their IDs are null.
		List<CampaignImageKey> imageKeyRequests = imageKeys.stream().map(key -> new CampaignImageKey(null, key))
				.toList();

		String imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		mockMvc.perform(put("/api/organization/campaign/" + campaignId + "/image-keys/upload")
				.contentType(MediaType.APPLICATION_JSON).content(imagesJson)).andExpect(status().isOk());

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
				.andExpect(jsonPath("$.contentHtml").value(
						"<p>Waqaf Taqwa Berhad menawarkan program wakaf korporat di mana syarikat boleh menaja keseluruhan projek atau menyertai skim padanan sumbangan bersama pewakaf individu.</p><p>Laporan impak suku tahunan disediakan untuk setiap penaja korporat.</p>"))
				.andExpect(jsonPath("$.status").value("DRAFT")).andExpect(jsonPath("$.images").isArray())
				.andExpect(jsonPath("$.images.length()").value(1)).andReturn();

		JsonNode profile = objectMapper.readTree(result.getResponse().getContentAsString());

		List<CampaignImageUrl> urlList = objectMapper.convertValue(profile.get("images"),
				new TypeReference<List<CampaignImageUrl>>() {
				});

		testGetImageFromStorage(httpClient, urlList.get(0).getUrl());

		imageKeys.forEach(key -> {
			deleteFileFromStorage(key);
		});

		testDeletedImageFromStorage(httpClient, urlList.get(0).getUrl());
	}

	private void testSaveFileToStorage(HttpClient httpClient, String url, byte[] fileBytes) throws Exception {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "image/jpeg")
				.PUT(HttpRequest.BodyPublishers.ofByteArray(fileBytes)).build();

		HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, response.statusCode());
	}

	private void testGetImageFromStorage(HttpClient httpClient, String url) throws Exception {
		HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<byte[]> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, getResponse.statusCode());
	}

	private void testDeletedImageFromStorage(HttpClient httpClient, String url) throws Exception {
		HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<byte[]> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(404, getResponse.statusCode());
	}

	private void deleteFileFromStorage(String fileKey) {
		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(fileKey).build());
	}

	@Disabled
	@Test
	@WithMockAdmin(username = "member", roles = { "ADMIN" })
	void getCampaignNonExistTest() throws Exception {
		String id = "79caa3f1-54b5-44d9-906f-104a14648af9";

		mockMvc.perform(get("/api/campaign/details/" + id).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andDo(print());
	}

	@AfterAll
	void cleanupBucket() {
		s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).build()).contents()
				.forEach(object -> s3Client
						.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(object.key()).build()));
	}

}

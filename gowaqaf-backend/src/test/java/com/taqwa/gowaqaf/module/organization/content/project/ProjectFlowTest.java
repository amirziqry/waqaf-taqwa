package com.taqwa.gowaqaf.module.organization.content.project;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.taqwa.gowaqaf.modules.organization.content.project.component.image.dto.ProjectImageKey;
import com.taqwa.gowaqaf.modules.organization.content.project.component.image.dto.ProjectImageUrl;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectUploadResponse;
import com.taqwa.gowaqaf.modules.organization.content.project.repository.ProjectRepository;

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
public class ProjectFlowTest {

	@Value("${storage.bucket}")
	private String bucket;
	private final S3Client s3Client;

	private final MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@SuppressWarnings("unused")
	private final ProjectRepository projectRepository;
	private final ContentCategoryRepository categoryRepository;
	private final ContentTagRepository tagRepository;

	ContentCategory c1, c2;
	ContentTag t1, t2, t3, t4, t5;

	@BeforeEach
	void setup() {
		this.c1 = CommonClass.createMockCategory(categoryRepository, "Kelestarian Alam Sekitar", ContentType.PROJECT);
		this.c2 = CommonClass.createMockCategory(categoryRepository, "Ekonomi & Kebajikan", ContentType.PROJECT);

		this.t1 = CommonClass.createMockTag(tagRepository, "Alam Sekitar", ContentType.PROJECT);
		this.t2 = CommonClass.createMockTag(tagRepository, "Masjid", ContentType.PROJECT);
		this.t3 = CommonClass.createMockTag(tagRepository, "Ekonomi", ContentType.PROJECT);
		this.t4 = CommonClass.createMockTag(tagRepository, "Usahawan", ContentType.PROJECT);
		this.t5 = CommonClass.createMockTag(tagRepository, "Kebajikan", ContentType.PROJECT);
	}

	@AfterAll
	void cleanupBucket() {
		s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).build()).contents()
				.forEach(object -> s3Client
						.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(object.key()).build()));
	}

	@Test
	@WithMockAdmin(username = "member", roles = { "ADMIN" })
	void endToEndProjectTest() throws Exception {
		HttpClient httpClient = HttpClient.newHttpClient();

		// Dump image files.
		byte[] dump1Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump1.jpg"));
		byte[] dump2Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump2.jpg"));
		byte[] dump3Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump3.jpg"));

		//
		// CREATE
		//
		MvcResult result = createProjectRequest(mockProject1(), 2);

		// Extract object
		String response = result.getResponse().getContentAsString();
		ProjectUploadResponse objectResponse = objectMapper.readValue(response, ProjectUploadResponse.class);
		assertNotNull(objectResponse);

		UUID projectId = objectResponse.getId();
		List<UploadUrl> uploadUrls = objectResponse.getUploadUrl();

		// Extract urls and save to storage.
		List<String> urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		CommonClass.testSaveFileToStorage(httpClient, urls.get(0), dump1Bytes);
		CommonClass.testSaveFileToStorage(httpClient, urls.get(1), dump2Bytes);

		// Extract keys and save to DB.
		List<String> imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		List<ProjectImageKey> imageKeyRequests = imageKeys.stream().map(key -> new ProjectImageKey(null, key)).toList();

		String imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		updateImageKeys(projectId.toString(), imagesJson);

		// Test get.
		result = mockMvc
				.perform(get("/api/organization/project/" + projectId + "/get").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(projectId.toString()))
				.andExpect(jsonPath("$.name").value("Wakaf Penghijauan Perkarangan Masjid"))
				.andExpect(jsonPath("$.slugUrl").value("wakaf-penghijauan-perkarangan-masjid"))
				.andExpect(jsonPath("$.collectedAmount").value(0.0)).andExpect(jsonPath("$.targetAmount").value(80000))
				.andExpect(jsonPath("$.location").value("Taman Tun Dr Ismail, Kuala Lumpur"))
				.andExpect(jsonPath("$.date").value(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
				.andExpect(jsonPath("$.category.name").value("Kelestarian Alam Sekitar"))
				.andExpect(jsonPath("$.tags.length()").value(2))
				.andExpect(jsonPath("$.tags[*].name", Matchers.containsInAnyOrder("Alam Sekitar", "Masjid")))
				.andExpect(jsonPath("$.summary").value(
						"Menanam pokok teduhan dan taman herba di perkarangan masjid demi kelestarian alam sekitar dan keselesaan jemaah."))
				.andExpect(jsonPath("$.contentHtml").value(
						"<p>Projek penghijauan ini menanam pokok teduhan, taman herba komuniti dan sistem penuaian air hujan di perkarangan Masjid At-Taqwa.</p>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images").isArray())
				.andExpect(jsonPath("$.images.length()").value(2)).andReturn();

		// Extract get urls.
		JsonNode profile = objectMapper.readTree(result.getResponse().getContentAsString());

		List<ProjectImageUrl> urlList = objectMapper.convertValue(profile.get("images"),
				new TypeReference<List<ProjectImageUrl>>() {
				});

		List<String> imagesList = urlList.stream().map(ProjectImageUrl::getUrl).toList();

		// Test storage call.
		CommonClass.testGetImagesFromStorage(httpClient, imagesList);

		// /////////
		// UPDATE //
		// /////////
		result = updateProjectRequest(projectId.toString(), mockProject2(), 1);

		// Extract object
		response = result.getResponse().getContentAsString();
		objectResponse = objectMapper.readValue(response, ProjectUploadResponse.class);
		assertNotNull(objectResponse);

		uploadUrls = objectResponse.getUploadUrl();
		assertEquals(1, uploadUrls.size());

		// Extract urls and save to storage.
		urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		CommonClass.testSaveFileToStorage(httpClient, urls.get(0), dump1Bytes);

		// Extract keys and save to DB.
		imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		imageKeyRequests = imageKeys.stream().map(key -> new ProjectImageKey(null, key)).toList();
		assertEquals(1, imageKeyRequests.size());

		imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		updateImageKeys(projectId.toString(), imagesJson);

		// Test get.
		result = mockMvc
				.perform(get("/api/organization/project/" + projectId + "/get").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(projectId.toString()))
				.andExpect(jsonPath("$.name").value("Wakaf Penghijauan Perkarangan Masjid"))
				.andExpect(jsonPath("$.slugUrl").value("wakaf-penghijauan-perkarangan-masjid"))
				.andExpect(jsonPath("$.collectedAmount").value(0.0)).andExpect(jsonPath("$.targetAmount").value(80000))
				.andExpect(jsonPath("$.location").value("Bandar Utama, Kuala Lumpur"))
				.andExpect(jsonPath("$.date").value(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
				.andExpect(jsonPath("$.category.name").value("Kelestarian Alam Sekitar"))
				.andExpect(jsonPath("$.tags.length()").value(2))
				.andExpect(jsonPath("$.tags[*].name", Matchers.containsInAnyOrder("Alam Sekitar", "Usahawan")))
				.andExpect(jsonPath("$.summary").value(
						"Menanam pokok teduhan dan taman herba di perkarangan masjid demi kelestarian alam sekitar dan keselesaan jemaah."))
				.andExpect(jsonPath("$.contentHtml").value(
						"<p>Projek penghijauan ini menanam pokok teduhan, taman herba komuniti dan sistem penuaian air hujan di perkarangan Masjid At-Taqwa.</p>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images").isArray())
				.andExpect(jsonPath("$.images.length()").value(1)).andReturn();

		// Extract get urls.
		profile = objectMapper.readTree(result.getResponse().getContentAsString());

		urlList = objectMapper.convertValue(profile.get("images"), new TypeReference<List<ProjectImageUrl>>() {
		});

		imagesList = urlList.stream().map(ProjectImageUrl::getUrl).toList();

		// Test storage call.
		CommonClass.testGetImagesFromStorage(httpClient, imagesList);

		//
		// CREATE
		//
		result = createProjectRequest(mockProject3(), 1);

		// Extract object
		response = result.getResponse().getContentAsString();
		objectResponse = objectMapper.readValue(response, ProjectUploadResponse.class);
		assertNotNull(objectResponse);

		projectId = objectResponse.getId();
		uploadUrls = objectResponse.getUploadUrl();

		// Extract urls and save to storage.
		urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		CommonClass.testSaveFileToStorage(httpClient, urls.get(0), dump3Bytes);

		// Extract keys and save to DB.
		imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		imageKeyRequests = imageKeys.stream().map(key -> new ProjectImageKey(null, key)).toList();

		imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		updateImageKeys(projectId.toString(), imagesJson);

		// Get
		result = mockMvc
				.perform(get("/api/organization/project/" + projectId + "/get").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(projectId.toString()))
				.andExpect(jsonPath("$.name").value("Wakaf Tunai Pembangunan Ekonomi Ummah"))
				.andExpect(jsonPath("$.slugUrl").value("wakaf-tunai-pembangunan-ekonomi-ummah"))
				.andExpect(jsonPath("$.collectedAmount").value(0.0)).andExpect(jsonPath("$.targetAmount").value(250000))
				.andExpect(jsonPath("$.location").value("Lembah Klang"))
				.andExpect(jsonPath("$.date").value(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
				.andExpect(jsonPath("$.category.name").value("Ekonomi & Kebajikan"))
				.andExpect(jsonPath("$.tags.length()").value(3))
				.andExpect(jsonPath("$.tags[*].name", Matchers.containsInAnyOrder("Ekonomi", "Usahawan", "Kebajikan")))
				.andExpect(jsonPath("$.summary").value(
						"Dana pusingan untuk usahawan mikro asnaf bagi menjana pendapatan lestari dan keluar daripada kepompong kemiskinan."))
				.andExpect(jsonPath("$.contentHtml").value(
						"<p>Dana wakaf tunai ini disalurkan sebagai modal pusingan dan latihan keusahawanan kepada usahawan mikro daripada golongan asnaf.</p><h3>Komponen Program</h3><ul><li>Modal permulaan perniagaan mikro</li><li>Latihan pengurusan kewangan dan pemasaran digital</li><li>Bimbingan mentor selama 12 bulan</li></ul>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images").isArray())
				.andExpect(jsonPath("$.images.length()").value(1)).andReturn();

		// Extract get urls.
		profile = objectMapper.readTree(result.getResponse().getContentAsString());

		urlList = objectMapper.convertValue(profile.get("images"), new TypeReference<List<ProjectImageUrl>>() {
		});

		imagesList = urlList.stream().map(ProjectImageUrl::getUrl).toList();

		// Test storage call.
		CommonClass.testGetImagesFromStorage(httpClient, imagesList);

		// Test get all
		mockMvc.perform(get("/api/organization/project/all/get")).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));

		// Test delete.
		mockMvc.perform(delete("/api/organization/project/{projectId}/delete", projectId)).andExpect(status().isOk());

		// Test get
		mockMvc.perform(get("/api/organization/project/" + projectId + "/get").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	// /////////////////////////////////
	// Endpoints ///////////////////////
	// /////////////////////////////////

	private MvcResult createProjectRequest(String requestBody, int length) throws Exception {
		MvcResult result = mockMvc
				.perform(post("/api/organization/project/create").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.uploadUrl.length()").value(length)).andReturn();

		return result;
	}

	private MvcResult updateProjectRequest(String id, String requestBody, int length) throws Exception {
		MvcResult result = mockMvc
				.perform(put("/api/organization/project/" + id + "/update").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.uploadUrl.length()").value(length)).andReturn();

		return result;
	}

	private void updateImageKeys(String projectId, String requestBody) throws Exception {
		mockMvc.perform(put("/api/organization/project/" + projectId + "/image-keys/upload")
				.contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk());
	}

	// /////////////////////////////////
	// Mock Projects ///////////////////
	// /////////////////////////////////

	private String mockProject1() {
		return """
				{
				  "name": "Wakaf Penghijauan Perkarangan Masjid",
				  "slugUrl": "wakaf-penghijauan-perkarangan-masjid",
				  "targetAmount": 80000,
				  "location": "Taman Tun Dr Ismail, Kuala Lumpur",
				  "category": {
				    "id": "%s", "name": "Alam Sekitar"
				  },
				  "tags": [
				    {
				      "id": "%s", "name": "Alam Sekitar"
				    },
				    {
				      "id": "%s", "name": "Masjid"
				    }
				  ],
				  "summary": "Menanam pokok teduhan dan taman herba di perkarangan masjid demi kelestarian alam sekitar dan keselesaan jemaah.",
				  "contentHtml": "<p>Projek penghijauan ini menanam pokok teduhan, taman herba komuniti dan sistem penuaian air hujan di perkarangan Masjid At-Taqwa.</p>",
				  "status": "PUBLISHED",
				  "imageUploadRequests": [
				    {
				      "filename": "project.jpg", "contentType": "image/jpeg"
				    },
				    {
				      "filename": "project.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c1.getId(), t1.getId(), t2.getId());
	}

	private String mockProject2() {
		return """
				{
				  "name": "Wakaf Penghijauan Perkarangan Masjid",
				  "slugUrl": "wakaf-penghijauan-perkarangan-masjid",
				  "targetAmount": 80000,
				  "location": "Bandar Utama, Kuala Lumpur",
				  "category": {
				    "id": "%s", "name": "Alam Sekitar"
				  },
				  "tags": [
				    {
				      "id": "%s", "name": "Alam Sekitar"
				    },
				    {
				      "id": "%s", "name": "Usahawan"
				    }
				  ],
				  "summary": "Menanam pokok teduhan dan taman herba di perkarangan masjid demi kelestarian alam sekitar dan keselesaan jemaah.",
				  "contentHtml": "<p>Projek penghijauan ini menanam pokok teduhan, taman herba komuniti dan sistem penuaian air hujan di perkarangan Masjid At-Taqwa.</p>",
				  "status": "PUBLISHED",
				  "imageUploadRequests": [
				    {
				      "filename": "project.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c1.getId(), t1.getId(), t4.getId());
	}

	private String mockProject3() {
		return """
				{
					"name": "Wakaf Tunai Pembangunan Ekonomi Ummah",
					"slugUrl": "wakaf-tunai-pembangunan-ekonomi-ummah",
					  "targetAmount": 250000,
					  "location": "Lembah Klang",
					  "category": {
					    "id": "%s", "name": "Ekonomi & Kebajikan"
					  },
				  "tags": [
				    {
				      "id": "%s", "name": "Ekonomi"
				    },
				    {
				      "id": "%s", "name": "Usahawan"
				    },
				    {
				      "id": "%s", "name": "Kebajikan"
				    }
				  ],
				  "summary": "Dana pusingan untuk usahawan mikro asnaf bagi menjana pendapatan lestari dan keluar daripada kepompong kemiskinan.",
				  "contentHtml": "<p>Dana wakaf tunai ini disalurkan sebagai modal pusingan dan latihan keusahawanan kepada usahawan mikro daripada golongan asnaf.</p><h3>Komponen Program</h3><ul><li>Modal permulaan perniagaan mikro</li><li>Latihan pengurusan kewangan dan pemasaran digital</li><li>Bimbingan mentor selama 12 bulan</li></ul>",
				  "status": "PUBLISHED",
				  "imageUploadRequests": [
				    {
				      "filename": "project.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c2.getId(), t3.getId(), t4.getId(), t5.getId());
	}

}

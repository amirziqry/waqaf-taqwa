package com.taqwa.gowaqaf.module.organization.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taqwa.gowaqaf.mockuser.member.WithMockAdmin;
import com.taqwa.gowaqaf.modules.organization.project.component.category.entity.ProjectCategory;
import com.taqwa.gowaqaf.modules.organization.project.component.category.repository.ProjectCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.project.component.image.dto.ProjectImageKey;
import com.taqwa.gowaqaf.modules.organization.project.component.image.dto.ProjectImageUrl;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.entity.ProjectTag;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.repository.ProjectTagRepository;
import com.taqwa.gowaqaf.modules.organization.project.dto.ProjectUploadResponse;
import com.taqwa.gowaqaf.modules.organization.project.repository.ProjectRepository;
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
public class ProjectFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;
	private final S3Client s3Client;
	private final ProjectRepository projectRepository;
	private final ProjectCategoryRepository categoryRepository;
	private final ProjectTagRepository tagRepository;

	@Value("${storage.bucket}")
	private String bucket;

	ProjectCategory c1, c2;
	ProjectTag t1, t2, t3, t4, t5;

	@BeforeEach
	void setup() {
		this.c1 = creatMockCategory("Kelestarian Alam Sekitar");
		this.c2 = creatMockCategory("Ekonomi & Kebajikan");

		this.t1 = creatMockTag("Alam Sekitar");
		this.t2 = creatMockTag("Masjid");
		this.t3 = creatMockTag("Ekonomi");
		this.t4 = creatMockTag("Usahawan");
		this.t5 = creatMockTag("Kebajikan");
	}

	private ProjectCategory creatMockCategory(String name) {
		ProjectCategory category = new ProjectCategory();
		category.setName(name);

		return categoryRepository.save(category);
	}

	private ProjectTag creatMockTag(String name) {
		ProjectTag tag = new ProjectTag();
		tag.setName(name);

		return tagRepository.save(tag);
	}

	private MvcResult createProjectRequest() throws Exception {
		String jsonCreateRequest = """
				{
				  "name": "Wakaf Penghijauan Perkarangan Masjid",
				  "slugUrl": "wakaf-penghijauan-perkarangan-masjid",
				  "collectedAmount": 12300,
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
				      "filename": "building.jpg", "contentType": "image/jpeg"
				    },
				    {
				      "filename": "office.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(c1.getId(), t1.getId(), t2.getId());

		MvcResult result = mockMvc.perform(post("/api/organization/project/create")
				.contentType(MediaType.APPLICATION_JSON).content(jsonCreateRequest)).andExpect(status().isCreated())
				.andReturn();

		return result;
	}

	private MvcResult updateProjectRequest(UUID id) throws Exception {
		String jsonCreateRequest = """
				{
					"id": "%s",
					"name": "Wakaf Tunai Pembangunan Ekonomi Ummah",
					"slugUrl": "wakaf-tunai-pembangunan-ekonomi-ummah",
					  "collectedAmount": 58900,
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
				  "files": [
				    {
				      "filename": "project4.jpg", "contentType": "image/jpeg"
				    }
				]
				}
				"""
				.formatted(id, c2.getId(), t3.getId(), t4.getId(), t5.getId());

		MvcResult result = mockMvc.perform(
				put("/api/project/update/" + id).contentType(MediaType.APPLICATION_JSON).content(jsonCreateRequest))
				.andExpect(status().isOk()).andReturn();

		return result;
	}

	@Test
	@WithMockAdmin(username = "member", roles = { "ADMIN" })
	void createAndGetProjectTest() throws Exception {
		// ============================================================
		// 1. Create project + get response
		// ============================================================
		MvcResult result = createProjectRequest();

		String response = result.getResponse().getContentAsString();

		ProjectUploadResponse objectResponse = objectMapper.readValue(response, ProjectUploadResponse.class);
		UUID projectId = objectResponse.getId();
		List<UploadUrl> uploadUrls = objectResponse.getUploadUrl();

		assertNotNull(projectId);
		assertNotNull(objectResponse);
		assertNotNull(objectResponse.getUploadUrl());
		assertEquals(2, objectResponse.getUploadUrl().size());

		List<String> imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		List<String> urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		urls.forEach(Assertions::assertNotNull);

		byte[] dump1Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump1.jpg"));
		byte[] dump2Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump2.jpg"));

		HttpClient httpClient = HttpClient.newHttpClient();

		testSaveFileToStorage(httpClient, urls.get(0), dump1Bytes);
		testSaveFileToStorage(httpClient, urls.get(1), dump2Bytes);

		List<ProjectImageKey> imageKeyRequests = imageKeys.stream().map(key -> new ProjectImageKey(null, key)).toList();

		String imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		mockMvc.perform(put("/api/organization/project/" + projectId + "/image-keys/upload")
				.contentType(MediaType.APPLICATION_JSON).content(imagesJson)).andExpect(status().isOk());

		result = mockMvc
				.perform(get("/api/organization/project/" + projectId + "/get").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(projectId.toString()))
				.andExpect(jsonPath("$.name").value("Wakaf Penghijauan Perkarangan Masjid"))
				.andExpect(jsonPath("$.slugUrl").value("wakaf-penghijauan-perkarangan-masjid"))
				.andExpect(jsonPath("$.collectedAmount").value(12300))
				.andExpect(jsonPath("$.targetAmount").value(80000))
				.andExpect(jsonPath("$.location").value("Taman Tun Dr Ismail, Kuala Lumpur"))
				.andExpect(jsonPath("$.date").value(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
				.andExpect(jsonPath("$.category.name").value("Kelestarian Alam Sekitar"))
				.andExpect(jsonPath("$.tags.length()").value(2))
				.andExpect(jsonPath("$.summary").value(
						"Menanam pokok teduhan dan taman herba di perkarangan masjid demi kelestarian alam sekitar dan keselesaan jemaah."))
				.andExpect(jsonPath("$.contentHtml").value(
						"<p>Projek penghijauan ini menanam pokok teduhan, taman herba komuniti dan sistem penuaian air hujan di perkarangan Masjid At-Taqwa.</p>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images").isArray())
				.andExpect(jsonPath("$.images.length()").value(2)).andReturn();

		JsonNode profile = objectMapper.readTree(result.getResponse().getContentAsString());

		List<ProjectImageUrl> urlList = objectMapper.convertValue(profile.get("images"),
				new TypeReference<List<ProjectImageUrl>>() {
				});

		urlList.forEach(url -> {
			try {
				testGetImageFromStorage(httpClient, url.getUrl());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		imageKeys.forEach(key -> {
			deleteFileFromStorage(key);
		});

		testDeletedImageFromStorage(httpClient, urlList.get(0).getUrl());
	}

	@Disabled
	@Test
	@WithMockAdmin(username = "member", roles = { "ADMIN" })
	void updateProjectTest() throws Exception {
		// ============================================================
		// 1. Create project + save
		// ============================================================
		MvcResult result = createProjectRequest();

		String response = result.getResponse().getContentAsString();

		ProjectUploadResponse objectResponse = objectMapper.readValue(response, ProjectUploadResponse.class);
		UUID projectId = objectResponse.getId();
		List<UploadUrl> uploadUrls = objectResponse.getUploadUrl();

		assertNotNull(projectId);
		assertNotNull(objectResponse);
		assertNotNull(objectResponse.getUploadUrl());
		assertEquals(2, objectResponse.getUploadUrl().size());

		List<String> imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		List<String> urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		urls.forEach(Assertions::assertNotNull);

		byte[] dump1Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump1.jpg"));
		byte[] dump2Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump2.jpg"));

		HttpClient httpClient = HttpClient.newHttpClient();

		testSaveFileToStorage(httpClient, urls.getFirst(), dump1Bytes);
		testSaveFileToStorage(httpClient, urls.getFirst(), dump2Bytes);

		List<ProjectImageKey> imageKeyRequests = imageKeys.stream().map(key -> new ProjectImageKey(null, key)).toList();

		String imagesJson = objectMapper.writeValueAsString(imageKeyRequests);
		mockMvc.perform(put("/api/project/" + projectId + "/images/upload").contentType(MediaType.APPLICATION_JSON)
				.content(imagesJson)).andExpect(status().isOk());

		// ============================================================
		// 2. Update project + Save + Details
		// ============================================================
		result = updateProjectRequest(projectId);

		response = result.getResponse().getContentAsString();

		objectResponse = objectMapper.readValue(response, ProjectUploadResponse.class);
		projectId = objectResponse.getId();
		uploadUrls = objectResponse.getUploadUrl();

		assertNotNull(projectId);
		assertNotNull(objectResponse);
		assertNotNull(objectResponse.getUploadUrl());
		assertEquals(2, objectResponse.getUploadUrl().size());

		imageKeys = uploadUrls.stream().map(UploadUrl::getImageKey).toList();
		imageKeys.forEach(Assertions::assertNotNull);

		urls = uploadUrls.stream().map(UploadUrl::getUploadUrl).toList();
		urls.forEach(Assertions::assertNotNull);

		dump1Bytes = Files.readAllBytes(Paths.get("src/test/resources/dump1.jpg"));

		testSaveFileToStorage(httpClient, urls.getFirst(), dump1Bytes);

		imageKeyRequests = imageKeys.stream().map(key -> new ProjectImageKey(null, key)).toList();

		mockMvc.perform(put("/api/project/" + projectId + "/images/upload").contentType(MediaType.APPLICATION_JSON)
				.content(imagesJson)).andExpect(status().isOk());

		// ============================================================
		// 6. Get project details
		// ============================================================
		mockMvc.perform(get("/api/project/details/" + projectId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(projectId.toString()))
				.andExpect(jsonPath("$.name").value("Wakaf Tunai Pembangunan Ekonomi Ummah"))
				.andExpect(jsonPath("$.slugUrl").value("wakaf-tunai-pembangunan-ekonomi-ummah"))
				.andExpect(jsonPath("$.collectedAmount").value(58900))
				.andExpect(jsonPath("$.targetAmount").value(250000))
				.andExpect(jsonPath("$.location").value("Lembah Klang"))
				.andExpect(jsonPath("$.date").value(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
				.andExpect(jsonPath("$.category.name").value("Ekonomi & Kebajikan"))
				.andExpect(jsonPath("$.tags.length()").value(3))
				.andExpect(jsonPath("$.summary").value(
						"Dana pusingan untuk usahawan mikro asnaf bagi menjana pendapatan lestari dan keluar daripada kepompong kemiskinan."))
				.andExpect(jsonPath("$.contentHtml").value(
						"<p>Dana wakaf tunai ini disalurkan sebagai modal pusingan dan latihan keusahawanan kepada usahawan mikro daripada golongan asnaf.</p><h3>Komponen Program</h3><ul><li>Modal permulaan perniagaan mikro</li><li>Latihan pengurusan kewangan dan pemasaran digital</li><li>Bimbingan mentor selama 12 bulan</li></ul>"))
				.andExpect(jsonPath("$.status").value("PUBLISHED")).andExpect(jsonPath("$.images").isArray())
				.andExpect(jsonPath("$.images.length()").value(1))
				.andExpect(jsonPath("$.images[0].imageUrl").value("https://placeholder-storage/" + imageKeys.get(0)));
	}

	@Disabled
	@Test
	@WithMockAdmin(username = "member", roles = { "ADMIN" })
	void deleteProjectTest() throws Exception {
		MvcResult result = createProjectRequest();

		String apiResponse = result.getResponse().getContentAsString();
		ProjectUploadResponse objectResponse = objectMapper.readValue(apiResponse, ProjectUploadResponse.class);

		UUID id = objectResponse.getId();

		mockMvc.perform(delete("/api/project/delete/{id}", id)).andExpect(status().isOk());

		assertThat(projectRepository.findById(id)).isEmpty();
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

	@AfterAll
	void cleanupBucket() {
		s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).build()).contents()
				.forEach(object -> s3Client
						.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(object.key()).build()));
	}

}

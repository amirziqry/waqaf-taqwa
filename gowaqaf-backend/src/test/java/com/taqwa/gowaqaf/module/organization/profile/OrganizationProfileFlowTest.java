package com.taqwa.gowaqaf.module.organization.profile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileUploadUrlsResponse;
import com.taqwa.gowaqaf.modules.organization.profile.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class OrganizationProfileFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final S3Client s3Client;
	private final MockMvc mockMvc;

	@SuppressWarnings("unused")
	private final OrganizationRepository repository;

	@Value("${storage.bucket}")
	private String bucket;

	private String logoImageKey, heroImageKey;

	@BeforeEach
	void setup() {

	}

	@Test
	@WithMockAdmin(username = "mock_member", roles = { "EDITOR" })
	void uploadAndGetProfileTest() throws Exception {
		String profileUpdateRequest = """
				{
				    "name": "Taqwa",
				    "phone": "011-5432 6360",
				    "email": "info@waqaftaqwa.com",
				    "address": {
				        "addressLine1": "Tingkat 1, Anjung Niaga",
				        "addressLine2": "Masjid At-Taqwa, Jalan Dato' Sulaiman",
				        "addressLine3": "Taman Tun Dr Ismail",
				        "postcode": 60000,
				        "city": "Kuala Lumpur",
				        "state": "Wilayah Persekutuan",
				        "country": "Malaysia"
				    },
				    "contentHtml": "<h2>Mengenai Waqaf Taqwa Berhad</h2><p>Penubuhan Waqaf Taqwa Berhad ditaja oleh Masjid At-Taqwa, Taman Tun Dr Ismail, Kuala Lumpur. Ia adalah sebuah <strong>Syarikat Berhad Menurut Jaminan (Company Limited by Guarantee - CLBG)</strong> yang diperbadankan di bawah Akta Syarikat 2016 bagi mengurus dan melaksanakan inisiatif wakaf untuk manfaat masyarakat.</p><p>Majlis Agama Islam Wilayah Persekutuan (MAIWP) telah meluluskan pelantikan dan memberi tauliah kepada Waqaf Taqwa Berhad sebagai <strong>Mutawalli Wakaf</strong> untuk menerima, mengurus, membangunkan dan mengagihkan manfaat wakaf secara profesional.</p><h3>Fungsi Utama</h3><ul><li>Menerima dan mengumpul Dana Wakaf</li><li>Mengurus dan membangunkan Dana Wakaf</li><li>Mengembangkan dana wakaf secara mampan</li><li>Mengurus wakaf hartanah (tertakluk kepada kelulusan MAIWP)</li><li>Mengagihkan manfaat kepada masyarakat</li></ul><h3>Visi</h3><p>Meningkatkan kesedaran, memupuk dan mengembangkan amalan berwakaf dalam kalangan masyarakat.</p><h3>Misi</h3><ul><li>Mendidik masyarakat mengenai kepentingan wakaf.</li><li>Menggalakkan amalan wakaf secara langsung, berkala dan bertangguh.</li><li>Memudahkan urusan berwakaf melalui program dan platform yang sistematik.</li></ul><h3>Objektif</h3><p>Berusaha mengembangkan Dana Wakaf secara mampan bagi memaksimumkan manfaat yang diagihkan selaras dengan hukum Syarak.</p><h3>Agihan Manfaat Wakaf</h3><ul><li>Agama</li><li>Pendidikan</li><li>Ekonomi &amp; Kebajikan</li><li>Kesihatan</li><li>Kelestarian alam sekitar</li></ul><h3>Kaedah Berwaqaf</h3><p>Tabung-tabung khas Wakaf Tunai disediakan di dalam Masjid At-Taqwa. Sumbangan Wakaf Tunai boleh juga dibuat melalui imbasan kod QR DuitNow.</p><blockquote><strong>Lafaz Pewakaf:</strong> Saya mewakilkan dan mengamanahkan kepada Waqaf Taqwa Berhad untuk menguruskan dana wakaf saya yang saya sumbangkan ini demi kebajikan dan kepentingan agama Islam dan umatnya kerana Allah Taala. Saya membenarkan 10% daripada amaun sumbangan saya sebagai upah pengurusan kepada Waqaf Taqwa Berhad yang bertindak sebagai Mutawalli Waqaf yang dilantik dan diberi tauliah oleh Majlis Agama Islam Wilayah Persekutuan (MAIWP).</blockquote><h3>Fi Pengurusan</h3><p>Selaras dengan terma pelantikan Mutawalli oleh MAIWP, fi pengurusan sebanyak 10% daripada kutipan dana wakaf digunakan bagi membiayai kos pengurusan syarikat untuk memastikan pelaksanaan Wakaf Tunai diurus secara profesional, telus dan mampan.</p>",
					"logoUploadRequest" : {
							"filename": "dump1.jpg",
							"contentType": "image/jpeg"
						},
					"heroUploadRequest" : {
							"filename": "dump2.jpg",
							"contentType": "image/jpeg"
						}
				}
				""";

		MvcResult result = mockMvc.perform(put("/api/organization/profile/update-request")
				.contentType(MediaType.APPLICATION_JSON).content(profileUpdateRequest)).andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		OrganizationProfileUploadUrlsResponse objectResponse = objectMapper.readValue(response,
				OrganizationProfileUploadUrlsResponse.class);

		Assertions.assertNotNull(objectResponse);
		Assertions.assertNotNull(objectResponse.getLogoUploadUrl());
		Assertions.assertNotNull(objectResponse.getHeroUploadUrl());

		UploadUrl logoUpload = objectResponse.getLogoUploadUrl();

		UploadUrl heroUpload = objectResponse.getHeroUploadUrl();

		String logoUploadUrl = logoUpload.getUploadUrl();
		String heroUploadUrl = heroUpload.getUploadUrl();
		String logoImageKey = logoUpload.getImageKey();
		String heroImageKey = heroUpload.getImageKey();
		this.logoImageKey = logoImageKey;
		this.heroImageKey = heroImageKey;

		byte[] logoBytes = Files.readAllBytes(Paths.get("src/test/resources/dump1.jpg"));

		byte[] heroBytes = Files.readAllBytes(Paths.get("src/test/resources/dump2.jpg"));

		Assertions.assertNotNull(logoUploadUrl);
		Assertions.assertNotNull(heroUploadUrl);
		Assertions.assertNotNull(logoImageKey);
		Assertions.assertNotNull(heroImageKey);
		Assertions.assertTrue(logoImageKey.contains("organization/images/logo"));
		Assertions.assertTrue(heroImageKey.contains("organization/images/hero"));

		HttpClient httpClient = HttpClient.newHttpClient();

		HttpRequest logoRequest = HttpRequest.newBuilder().uri(URI.create(logoUpload.getUploadUrl()))
				.header("Content-Type", "image/jpeg").PUT(HttpRequest.BodyPublishers.ofByteArray(logoBytes)).build();

		HttpResponse<byte[]> logoResponse = httpClient.send(logoRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, logoResponse.statusCode());

		HttpRequest heroRequest = HttpRequest.newBuilder().uri(URI.create(heroUpload.getUploadUrl()))
				.header("Content-Type", "image/jpeg").PUT(HttpRequest.BodyPublishers.ofByteArray(heroBytes)).build();

		HttpResponse<byte[]> heroResponse = httpClient.send(heroRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, heroResponse.statusCode());

		String imageKeysJson = objectMapper
				.writeValueAsString(Map.of("logoKey", logoImageKey, "heroKey", heroImageKey));

		mockMvc.perform(put("/api/organization/profile/image-keys/upload").contentType(MediaType.APPLICATION_JSON)
				.content(imageKeysJson)).andExpect(status().isOk());

		result = mockMvc.perform(MockMvcRequestBuilders.get("/api/organization/profile/get"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Taqwa"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("011-5432 6360"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("info@waqaftaqwa.com"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address.addressLine1").value("Tingkat 1, Anjung Niaga"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address.addressLine2")
						.value("Masjid At-Taqwa, Jalan Dato' Sulaiman"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address.addressLine3").value("Taman Tun Dr Ismail"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address.postcode").value(60000))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address.city").value("Kuala Lumpur"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address.state").value("Wilayah Persekutuan"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address.country").value("Malaysia"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.contentHtml").value(
						"<h2>Mengenai Waqaf Taqwa Berhad</h2><p>Penubuhan Waqaf Taqwa Berhad ditaja oleh Masjid At-Taqwa, Taman Tun Dr Ismail, Kuala Lumpur. Ia adalah sebuah <strong>Syarikat Berhad Menurut Jaminan (Company Limited by Guarantee - CLBG)</strong> yang diperbadankan di bawah Akta Syarikat 2016 bagi mengurus dan melaksanakan inisiatif wakaf untuk manfaat masyarakat.</p><p>Majlis Agama Islam Wilayah Persekutuan (MAIWP) telah meluluskan pelantikan dan memberi tauliah kepada Waqaf Taqwa Berhad sebagai <strong>Mutawalli Wakaf</strong> untuk menerima, mengurus, membangunkan dan mengagihkan manfaat wakaf secara profesional.</p><h3>Fungsi Utama</h3><ul><li>Menerima dan mengumpul Dana Wakaf</li><li>Mengurus dan membangunkan Dana Wakaf</li><li>Mengembangkan dana wakaf secara mampan</li><li>Mengurus wakaf hartanah (tertakluk kepada kelulusan MAIWP)</li><li>Mengagihkan manfaat kepada masyarakat</li></ul><h3>Visi</h3><p>Meningkatkan kesedaran, memupuk dan mengembangkan amalan berwakaf dalam kalangan masyarakat.</p><h3>Misi</h3><ul><li>Mendidik masyarakat mengenai kepentingan wakaf.</li><li>Menggalakkan amalan wakaf secara langsung, berkala dan bertangguh.</li><li>Memudahkan urusan berwakaf melalui program dan platform yang sistematik.</li></ul><h3>Objektif</h3><p>Berusaha mengembangkan Dana Wakaf secara mampan bagi memaksimumkan manfaat yang diagihkan selaras dengan hukum Syarak.</p><h3>Agihan Manfaat Wakaf</h3><ul><li>Agama</li><li>Pendidikan</li><li>Ekonomi &amp; Kebajikan</li><li>Kesihatan</li><li>Kelestarian alam sekitar</li></ul><h3>Kaedah Berwaqaf</h3><p>Tabung-tabung khas Wakaf Tunai disediakan di dalam Masjid At-Taqwa. Sumbangan Wakaf Tunai boleh juga dibuat melalui imbasan kod QR DuitNow.</p><blockquote><strong>Lafaz Pewakaf:</strong> Saya mewakilkan dan mengamanahkan kepada Waqaf Taqwa Berhad untuk menguruskan dana wakaf saya yang saya sumbangkan ini demi kebajikan dan kepentingan agama Islam dan umatnya kerana Allah Taala. Saya membenarkan 10% daripada amaun sumbangan saya sebagai upah pengurusan kepada Waqaf Taqwa Berhad yang bertindak sebagai Mutawalli Waqaf yang dilantik dan diberi tauliah oleh Majlis Agama Islam Wilayah Persekutuan (MAIWP).</blockquote><h3>Fi Pengurusan</h3><p>Selaras dengan terma pelantikan Mutawalli oleh MAIWP, fi pengurusan sebanyak 10% daripada kutipan dana wakaf digunakan bagi membiayai kos pengurusan syarikat untuk memastikan pelaksanaan Wakaf Tunai diurus secara profesional, telus dan mampan.</p>"
								+ ""))
				.andReturn();

		JsonNode profile = objectMapper.readTree(result.getResponse().getContentAsString());

		String logoUrl = profile.get("logoUrl").asText();
		String heroUrl = profile.get("heroUrl").asText();

		Assertions.assertNotNull(logoUrl);
		Assertions.assertNotNull(heroUrl);

		HttpRequest logoGetRequest = HttpRequest.newBuilder().uri(URI.create(logoUrl)).GET().build();

		HttpResponse<byte[]> logoGetResponse = httpClient.send(logoGetRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, logoGetResponse.statusCode());
		Assertions.assertArrayEquals(logoBytes, logoGetResponse.body());

		HttpRequest heroGetRequest = HttpRequest.newBuilder().uri(URI.create(heroUrl)).GET().build();

		HttpResponse<byte[]> heroGetResponse = httpClient.send(heroGetRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, heroGetResponse.statusCode());
		Assertions.assertArrayEquals(heroBytes, heroGetResponse.body());

		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(logoImageKey).build());

		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(heroImageKey).build());

		HttpResponse<byte[]> deletedResponse = httpClient.send(logoGetRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(404, deletedResponse.statusCode());
	}

	@Disabled
	@AfterEach
	void cleanup() {
		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(logoImageKey).build());

		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(heroImageKey).build());
	}

}

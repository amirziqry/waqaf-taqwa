package com.taqwa.gowaqaf.external.payment.client.nexgen.client;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing.NexGenBillingResponse;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing.NexGenCreateBillingRequest;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.qr.NexGenCreateQrRequest;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.qr.NexGenQrResponse;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * ============================================================================
 * NexGen Payment API Integration
 * ============================================================================
 *
 * This client is the integration layer for the NexGen Payment API.
 *
 * Replace the current mock implementations with actual API requests when NexGen
 * integration is enabled. Payment-related services should communicate with
 * NexGen through this client rather than calling the external API directly.
 *
 * Current implementation: Mock / TODO
 * ============================================================================
 */

@Component
@RequiredArgsConstructor
public class NexGenPaymentClient {

	private final RestClient nexGenRestClient;
	private final ObjectMapper objectMapper;

	@Value("${nexgen.api.secret}")
	private String apiSecret;

	public NexGenBillingResponse createBilling(String collectionCode, NexGenCreateBillingRequest request) {
		request.setFieldDueDate(LocalDateTime.now(ZoneId.of("Asia/Kuala_Lumpur")).plusMinutes(5)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

		// NexGen API request handler method.
		NexGenBillingResponse billing = requestNexGenBilling(collectionCode, request);

		return billing;
	}

	// NexGen Live API call.
	private NexGenBillingResponse requestNexGenBilling(String collectionCode, NexGenCreateBillingRequest request) {
		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();

		Map<String, Object> fields = objectMapper.convertValue(request, new TypeReference<Map<String, Object>>() {
		});

		fields.forEach(formData::add);

		return nexGenRestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/v1/billing/create/{collectionCode}")
						.queryParam("ApiSecret", apiSecret).build(collectionCode))
				.contentType(MediaType.MULTIPART_FORM_DATA).body(formData).retrieve().body(NexGenBillingResponse.class);
	}

	// NexGen Live API call.
	@SuppressWarnings("unused")
	private NexGenQrResponse requestNexGenQr(String collectionCode, NexGenCreateQrRequest request) {
		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();

		Map<String, Object> fields = objectMapper.convertValue(request, new TypeReference<Map<String, Object>>() {
		});

		fields.forEach(formData::add);

		return nexGenRestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/").queryParam("ApiSecret", apiSecret).build(collectionCode))
				.contentType(MediaType.MULTIPART_FORM_DATA).body(formData).retrieve().body(NexGenQrResponse.class);
	}

}

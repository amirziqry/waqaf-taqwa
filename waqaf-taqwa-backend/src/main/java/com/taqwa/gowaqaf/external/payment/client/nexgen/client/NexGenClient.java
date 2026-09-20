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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.InternalServerErrorException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing.NexGenBillingObject;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing.NexGenBillingResponse;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing.NexGenCreateBillingRequest;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.collection.NexGenCollectionResponse;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.collection.NexGenCreateCollectionRequest;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.qr.NexGenCreateQrRequest;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.qr.NexGenQrResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * Current implementation: Sandbox
 * ============================================================================
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class NexGenClient {

	private final RestClient nexGenRestClient;
	private final ObjectMapper objectMapper;

	@Value("${nexgen.api.secret}")
	private String apiSecret;

	/**
	 * Create billing request for payment (URL) method handler.
	 * 
	 * @param collectionCode
	 * @param request
	 * @return
	 */
	public NexGenBillingResponse createBilling(String collectionCode, NexGenCreateBillingRequest request) {
		request.setFieldDueDate(LocalDateTime.now(ZoneId.of("Asia/Kuala_Lumpur")).plusMinutes(5)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

		// NexGen API request handler method.
		NexGenBillingResponse billing = requestNexGenCreateBilling(collectionCode, request);

		return billing;
	}

	/**
	 * Get billing data method handler.
	 * 
	 * @param collectionCode
	 * @param billingCode
	 * @return
	 */
	public NexGenBillingObject getBilling(String collectionCode, String billingCode) {

		// NexGen API request handler method.
		NexGenBillingObject billing = requestNexGenGetBilling(collectionCode, billingCode);

		return billing;
	}

	/**
	 * NexGen create billing API call.
	 * 
	 * @param collectionCode
	 * @param request
	 * @return
	 */
	private NexGenBillingResponse requestNexGenCreateBilling(String collectionCode,
			NexGenCreateBillingRequest request) {
		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();

		Map<String, Object> fields = objectMapper.convertValue(request, new TypeReference<Map<String, Object>>() {
		});

		fields.forEach(formData::add);

		try {
			return nexGenRestClient.post()
					.uri(uriBuilder -> uriBuilder.path("/api/v1/billing/create/{collectionCode}")
							.queryParam("ApiSecret", apiSecret).build(collectionCode))
					.contentType(MediaType.MULTIPART_FORM_DATA).body(formData).retrieve()
					.body(NexGenBillingResponse.class);

		} catch (HttpClientErrorException.BadRequest e) {
			log.error("NexGen create billing failed. Please review input values.", e);
			throw new ResourceNotFoundException(ErrorCode.NPP001, "Payment provider error.");

		} catch (HttpServerErrorException.InternalServerError e) {
			log.error("NexGen create billing failed. Please try again.", e);
			throw new InternalServerErrorException(ErrorCode.NPP001, "Payment provider error.");

		} catch (Exception e) {
			log.error("Error connecting to payment provider error", e);
			throw new InternalServerErrorException(ErrorCode.NPP001, "Payment provider error.");
		}
	}

	/**
	 * NexGen get billing API call.
	 * 
	 * @param collectionCode
	 * @param billingCode
	 * @return
	 */
	private NexGenBillingObject requestNexGenGetBilling(String collectionCode, String billingCode) {
		try {
			return nexGenRestClient.get()
					.uri(uriBuilder -> uriBuilder.path("/api/v1/billing/get/data/{collectionCode}/{billingCode}")
							.queryParam("ApiSecret", apiSecret).build(collectionCode, billingCode))
					.retrieve().body(NexGenBillingObject.class);

		} catch (HttpClientErrorException.NotFound e) {
			log.error("NexGen billing/collection not found", billingCode, e);
			throw new ResourceNotFoundException(ErrorCode.NPP001, "Billing not found.");

		} catch (Exception e) {
			log.error("NexGen billing request failed. Billing code: {}", billingCode, e);
			throw new InternalServerErrorException(ErrorCode.NPP001, "Payment provider error.");
		}
	}

	public NexGenCollectionResponse requestNexGenCreateCollection(NexGenCreateCollectionRequest request) {

		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();

		Map<String, Object> fields = objectMapper.convertValue(request, new TypeReference<Map<String, Object>>() {
		});

		fields.forEach(formData::add);

		try {
			return nexGenRestClient.post()
					.uri(uriBuilder -> uriBuilder.path("/api/v1/collection/create").queryParam("ApiSecret", apiSecret).build())
					.contentType(MediaType.MULTIPART_FORM_DATA).body(formData).retrieve()
					.body(NexGenCollectionResponse.class);

		} catch (HttpClientErrorException.BadRequest e) {
			log.error("NexGen create collection failed. Please review input values.", e);
			throw new ResourceNotFoundException(ErrorCode.NPP001, "Payment provider error.");

		} catch (HttpServerErrorException.InternalServerError e) {
			log.error("NexGen create collection failed. Please try again.", e);
			throw new InternalServerErrorException(ErrorCode.NPP001, "Payment provider error.");

		} catch (Exception e) {
			log.error("Error connecting to payment provider.", e);
			throw new InternalServerErrorException(ErrorCode.NPP001, "Payment provider error.");
		}
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

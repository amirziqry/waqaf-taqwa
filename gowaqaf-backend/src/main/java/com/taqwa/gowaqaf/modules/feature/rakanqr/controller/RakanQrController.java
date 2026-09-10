package com.taqwa.gowaqaf.modules.feature.rakanqr.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrApplicationRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrFilter;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithCollection;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithSumFilter;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * General RakanQr agent management handler.
 * </p>
 */
@RestController
@RequestMapping(ApiBasePath.RAKANQR)
@RequiredArgsConstructor
public class RakanQrController {

	private final RakanQrService service;

	/**
	 * FINAL Personal/Merchant-only to apply for RakanQr.
	 * 
	 * @param authentication
	 * @param request
	 * @return
	 */
	@PostMapping("/apply")
	@PreAuthorize("@accountSecurity.isMerchant(authentication) || @accountSecurity.isPersonal(authentication)")
	public ResponseEntity<RakanQrInfo> createRakanQr(Authentication authentication,
			@RequestBody RakanQrApplicationRequest request) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		RakanQrInfo response = service.createRakanQr(principal, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * FINAL Admin-only to update RakanQr agent application.
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@PatchMapping("/{id}/status")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<Void> updateRakanQrStatus(@PathVariable UUID id, @RequestBody RakanQrStatusRequest request) {
		service.updateRakanQrStatus(id, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * FINAL Admin-only to retrieve list of RakanQr agents.
	 * 
	 * @param pageable
	 * @param filter
	 * @return
	 */
	@GetMapping
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<Page<RakanQrInfo>> getAllRakanQr(
			@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			@ModelAttribute RakanQrFilter filter) {
		Page<RakanQrInfo> response = service.getAllRakanQrInfo(pageable, filter);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * TESTING Admin-only to retrieve list of RakanQr agents with their respective
	 * collected donations.
	 * 
	 * @param pageable
	 * @param filter
	 * @return
	 */
	@GetMapping("/with-collection")
	@PreAuthorize("@accountSecurity.isAdmin(authentication) && hasRole('ADMIN')")
	public ResponseEntity<Page<RakanQrWithCollection>> getAllRakanQrWithCollection(
			@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			@ModelAttribute RakanQrWithSumFilter filter) {
		Page<RakanQrWithCollection> response = service.getAllRakanQrWithCollection(pageable, filter.getStartDate(),
				filter.getEndDate());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

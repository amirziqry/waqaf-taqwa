package com.taqwa.gowaqaf.modules.organization.collection.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.organization.collection.dto.DonationCollectionFilter;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrgCollectionInfo;
import com.taqwa.gowaqaf.modules.organization.collection.dto.TransactionDetails;
import com.taqwa.gowaqaf.modules.organization.collection.service.DonationCollectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/donation")
@RequiredArgsConstructor
public class DonationCollectionController {

	private final DonationCollectionService service;

	@GetMapping("/collections")
	@PreAuthorize("@accountSecurity.isAdmin(authentication) && hasRole('ADMIN')")
	public ResponseEntity<OrgCollectionInfo> getCollectionSum(@ModelAttribute DonationCollectionFilter request) {
		OrgCollectionInfo response = service.getDonationCollectionSum(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/transactions")
	@PreAuthorize("@accountSecurity.isAdmin(authentication) && hasRole('ADMIN')")
	public ResponseEntity<Page<TransactionDetails>> getAllDonationTransactions(Authentication authentication,
			@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<TransactionDetails> response = service.getAllTransactions(pageable);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

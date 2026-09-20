package com.taqwa.gowaqaf.modules.feature.recurring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.feature.recurring.dto.RecurringApplicationRequest;
import com.taqwa.gowaqaf.modules.feature.recurring.service.RecurringService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = { "/api/recurring", "/api/auto-waqaf" })
@RequiredArgsConstructor
public class RecurringController {

	// Set auto waqaf
	private final RecurringService recurringService;

	@PostMapping("/apply")
	public ResponseEntity<?> createRecurringApplication(Authentication authentication,
			@RequestBody RecurringApplicationRequest request) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		recurringService.createRecurringByUser(principal, request);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}

package com.taqwa.gowaqaf.modules.agent.controller;

import java.util.List;
import java.util.UUID;

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

import com.taqwa.gowaqaf.modules.agent.dto.RakanQrFilter;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrWithSum;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrWithSumFilter;
import com.taqwa.gowaqaf.modules.agent.service.RakanQrService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rakan-qr-agent")
@RequiredArgsConstructor
public class RakanQrController {

	private final RakanQrService service;

	@PostMapping("/apply")
	public ResponseEntity<RakanQrInfo> createRakanQr(Authentication authentication) {
		RakanQrInfo response = service.createRakanQr(authentication);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get/all")
	@PreAuthorize("@accountSecurity.isAdmin(authentication) && hasRole('ADMIN')")
	public ResponseEntity<List<RakanQrInfo>> getAllRakanQr(@ModelAttribute RakanQrFilter filter) {
		List<RakanQrInfo> response = service.getAllRakanQr(filter);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get/sum/all")
	@PreAuthorize("@accountSecurity.isAdmin(authentication) && hasRole('ADMIN')")
	public ResponseEntity<List<RakanQrWithSum>> getAllRakanQrWithSum(@ModelAttribute RakanQrWithSumFilter filter) {
		List<RakanQrWithSum> response = service.getAllRakanQrWithSum(filter);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("@accountSecurity.isAdmin(authentication) && hasRole('ADMIN')")
	public ResponseEntity<Void> updateRakanQrStatus(@PathVariable UUID id, @RequestBody RakanQrStatusRequest request) {
		service.updateRakanQrStatus(id, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}

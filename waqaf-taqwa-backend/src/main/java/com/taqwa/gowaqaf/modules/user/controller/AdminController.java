package com.taqwa.gowaqaf.modules.user.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.user.dto.AccountInfo;
import com.taqwa.gowaqaf.modules.user.dto.RegisterCredentials;
import com.taqwa.gowaqaf.modules.user.dto.RegisterResponse;
import com.taqwa.gowaqaf.modules.user.dto.admin.UpdateAdminRoleRequest;
import com.taqwa.gowaqaf.modules.user.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.ADMIN)
@RequiredArgsConstructor
public class AdminController {

	private final AdminService service;

	/**
	 * Register admin.
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/register/admin")
	public ResponseEntity<RegisterResponse> registerAdmin(@RequestBody RegisterCredentials request) {
		RegisterResponse response = service.createAdmin(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/register/editor")
	public ResponseEntity<RegisterResponse> registerEditor(@RequestBody RegisterCredentials request) {
		RegisterResponse response = service.createEditor(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Get all admins
	 * 
	 * @return
	 */
	@GetMapping("/users")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
	public ResponseEntity<List<AccountInfo>> getAllAdmin() {
		List<AccountInfo> response = service.getAllAdmins();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Update role.
	 * 
	 * @param username
	 * @param request
	 * @return
	 */
	@PatchMapping("/users/{userId}/account/role")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> updateAdminRole(@PathVariable UUID userId,
			@RequestBody UpdateAdminRoleRequest request) {
		service.updateAdminRoleById(userId, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/users/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteAdminByUsername(@PathVariable UUID userId) {
		service.deleteAdminById(userId);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}

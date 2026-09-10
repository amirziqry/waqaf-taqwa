package com.taqwa.gowaqaf.modules.user.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminInfo;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterResponse;
import com.taqwa.gowaqaf.modules.user.admin.dto.ChangePasswordRequest;
import com.taqwa.gowaqaf.modules.user.admin.dto.UpdateAdminRoleRequest;
import com.taqwa.gowaqaf.modules.user.admin.service.AdminService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.ADMIN)
@RequiredArgsConstructor
public class AdminController {

	private final AdminService service;

	/**
	 * Register admin.
	 * @param request
	 * @return
	 */
	@PostMapping("/register/admin")
	public ResponseEntity<AdminRegisterResponse> registerAdmin(@RequestBody AdminRegisterCredentials request) {
		AdminRegisterResponse response = service.createAdmin(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/register/editor")
	public ResponseEntity<AdminRegisterResponse> registerEditor(@RequestBody AdminRegisterCredentials request) {
		AdminRegisterResponse response = service.createEditor(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Get by username
	 * 
	 * @param username
	 * @return
	 */
	@GetMapping("/users/{username}")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<AdminInfo> getAdminByUsername(@PathVariable String username) {
		AdminInfo response = service.getAdminByUsername(username);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Get all admins
	 * 
	 * @return
	 */
	@GetMapping("/users")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<List<AdminInfo>> getAllAdmin() {
		List<AdminInfo> response = service.getAllAdmins();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Update role.
	 * 
	 * @param username
	 * @param request
	 * @return
	 */
	@PatchMapping("/users/{username}/account/role")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)  && hasRole('ADMIN')")
	public ResponseEntity<Void> updateAdminRole(@PathVariable String username,
			@RequestBody UpdateAdminRoleRequest request) {
		service.updateAdminRole(username, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping("/users/account/password")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<Void> changePassword(Authentication authentication,
			@RequestBody ChangePasswordRequest request) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		service.changePasswordByUsername(principal, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/users/{username}")
	@PreAuthorize("@accountSecurity.isAdmin(authentication) && hasRole('ADMIN')")
	public ResponseEntity<Void> deleteAdminByUsername(@PathVariable String username) {
		service.deleteAdminByUsername(username);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}

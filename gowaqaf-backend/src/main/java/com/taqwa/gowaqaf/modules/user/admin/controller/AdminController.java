package com.taqwa.gowaqaf.modules.user.admin.controller;

import java.util.List;

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
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminInfo;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterResponse;
import com.taqwa.gowaqaf.modules.user.admin.dto.UpdateAdminRoleRequest;
import com.taqwa.gowaqaf.modules.user.admin.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.ADMIN)
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@PostMapping("/register-editor")
	public ResponseEntity<AdminRegisterResponse> registerEditor(@RequestBody AdminRegisterCredentials request) {
		AdminRegisterResponse response = adminService.createEditor(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/register-admin")
	public ResponseEntity<AdminRegisterResponse> registerAdmin(@RequestBody AdminRegisterCredentials request) {
		AdminRegisterResponse response = adminService.createAdmin(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/get/{username}")
	public ResponseEntity<AdminInfo> getMemberByUsername(@PathVariable String username) {
		AdminInfo response = adminService.getAdminByUsername(username);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get/all")
	@PreAuthorize("@accountSecurity.isAdmin(authentication) && hasRole('ADMIN')")
	public ResponseEntity<List<AdminInfo>> getAllMember() {
		List<AdminInfo> response = adminService.getAllAdmins();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PatchMapping("/update/{username}/role")
	public ResponseEntity<Void> updateMemberRole(@PathVariable String username,
			@RequestBody UpdateAdminRoleRequest request) {
		adminService.updateAdminRole(username, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/delete/{username}")
	public ResponseEntity<Void> deleteMemberByUsername(@PathVariable String username) {
		adminService.deleteAdminByUsername(username);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}

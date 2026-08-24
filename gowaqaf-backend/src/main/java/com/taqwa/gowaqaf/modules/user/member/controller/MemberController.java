package com.taqwa.gowaqaf.modules.user.member.controller;

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

import com.taqwa.gowaqaf.modules.user.member.dto.MemberInfo;
import com.taqwa.gowaqaf.modules.user.member.dto.MemberRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.member.dto.MemberRegisterResponse;
import com.taqwa.gowaqaf.modules.user.member.dto.UpdateMemberRoleRequest;
import com.taqwa.gowaqaf.modules.user.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/register-editor")
	public ResponseEntity<MemberRegisterResponse> registerEditor(@RequestBody MemberRegisterCredentials request) {
		MemberRegisterResponse response = memberService.createMemberEditor(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/register-admin")
	public ResponseEntity<MemberRegisterResponse> registerAdmin(@RequestBody MemberRegisterCredentials request) {
		MemberRegisterResponse response = memberService.createMemberAdmin(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get/{username}")
	public ResponseEntity<MemberInfo> getMemberByUsername(@PathVariable String username) {
		MemberInfo response = memberService.getMemberByUsername(username);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get/all")
	@PreAuthorize("@accountSecurity.isMember(authentication) && hasRole('ADMIN')")
	public ResponseEntity<List<MemberInfo>> getAllMember() {
		List<MemberInfo> response = memberService.getAllMembers();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PatchMapping("/update/{username}/role")
	public ResponseEntity<Void> updateMemberRole(@PathVariable String username,
			@RequestBody UpdateMemberRoleRequest request) {
		memberService.updateMemberRole(username, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/delete/{username}")
	public ResponseEntity<Void> deleteMemberByUsername(@PathVariable String username) {
		memberService.deleteMemberByUsername(username);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}

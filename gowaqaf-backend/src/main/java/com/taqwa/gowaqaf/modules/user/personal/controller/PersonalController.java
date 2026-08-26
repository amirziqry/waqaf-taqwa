package com.taqwa.gowaqaf.modules.user.personal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.PERSONAL)
@RequiredArgsConstructor
public class PersonalController {

	private final PersonalService personalService;

	@PostMapping("/register")
	public ResponseEntity<PersonalRegisterResponse> register(@RequestBody PersonalRegisterCredentials request) {
		PersonalRegisterResponse response = personalService.createPersonal(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

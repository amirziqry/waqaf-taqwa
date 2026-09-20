package com.taqwa.gowaqaf.modules.verification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.verification.dto.EkycRequest;
import com.taqwa.gowaqaf.modules.verification.dto.FaceIdRequest;
import com.taqwa.gowaqaf.modules.verification.dto.PhoneOtpRequest;
import com.taqwa.gowaqaf.modules.verification.dto.VerifyOtpRequest;
import com.taqwa.gowaqaf.modules.verification.service.VerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VerificationController {

	private final VerificationService service;

	@PostMapping("/register/otp/request")
	public ResponseEntity<?> requestOtp(@RequestBody PhoneOtpRequest request) {
		service.requestOtp(request.phone());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/register/otp/verify")
	public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
		service.verifyOtp(request.phone(), request.otp());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/register/ekyc")
	public ResponseEntity<?> submitEkyc(@RequestBody EkycRequest request) {
		service.submitEkyc(request.request());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/register/face-id")
	public ResponseEntity<?> verifyFace(@RequestBody FaceIdRequest request) {
		service.verifyFace(request.request());

		return new ResponseEntity<>(HttpStatus.OK);
	}

}

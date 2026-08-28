package com.taqwa.gowaqaf.modules.verification.service;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.verification.ekyc.EkycService;
import com.taqwa.gowaqaf.verification.faceid.FaceIdService;
import com.taqwa.gowaqaf.verification.otp.service.OtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

	private final OtpService otpService;
	private final EkycService ekycService;
	private final FaceIdService faceIdService;

	@Override
	public void requestOtp(String phone) {
		otpService.sendOtp(phone);
	}

	@Override
	public void verifyOtp(String phone, String otp) {
		otpService.verifyOtp(phone, otp);
	}

	@Override
	public void submitEkyc(String request) {
		ekycService.verify(request);
	}

	@Override
	public void verifyFace(String request) {
		faceIdService.verify(request);
	}

}

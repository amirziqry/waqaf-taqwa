package com.taqwa.gowaqaf.modules.verification.service;

public interface VerificationService {

	void requestOtp(String phone);

	void verifyOtp(String phone, String otp);

	void submitEkyc(String request);

	void verifyFace(String request);

}

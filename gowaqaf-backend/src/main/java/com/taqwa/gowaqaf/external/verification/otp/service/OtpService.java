package com.taqwa.gowaqaf.external.verification.otp.service;

public interface OtpService {

	void sendOtp(String phone);

	boolean verifyOtp(String phone, String otp);

}

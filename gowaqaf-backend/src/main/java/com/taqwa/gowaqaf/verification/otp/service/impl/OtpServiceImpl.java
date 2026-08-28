package com.taqwa.gowaqaf.verification.otp.service.impl;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.verification.otp.service.OtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

	@Override
	public void sendOtp(String phone) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean verifyOtp(String phone, String otp) {
		// TODO Auto-generated method stub
		return false;
	}

}

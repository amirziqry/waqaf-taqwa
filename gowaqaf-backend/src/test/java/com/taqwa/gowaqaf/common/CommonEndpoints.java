package com.taqwa.gowaqaf.common;

public class CommonEndpoints {

	public static String personalLogin = "/api/personal/auth/login";
	public static String personalMe = "/api/personal/auth/me";

	public static String personalRegister = "/api/personal/register";
	public static String personalUpdateAccount = "/api/personal/account/update";
	public static String personalGetAccount = "/api/personal/account/get";

	public static String personalDirectDonationRequest = "/api/personal/donation/payment/request-gateway-url";

	public static String personalPaymentStatus(String donationId) {
		return String.format("/api/personal/donation/payment/%s/status", donationId);
	};

}

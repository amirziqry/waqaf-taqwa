package com.taqwa.gowaqaf.common;

public class CommonEndpoints {

	public static String personalLogin = "/api/personal/auth/login";
	public static String personalMe = "/api/personal/auth/me";

	public static String personalRegister = "/api/personal/register";
	public static String personalUpdateAccount = "/api/personal/account/update";
	public static String personalGetAccount = "/api/personal/account/get";

	public static String personalDashboard = "/api/personal/dashboard";

	public static String personalDirectDonationRequest = "/api/personal/donations/payment-request";
	public static String personalGetDonationDetails = "/api/personal/donations/{id}";
	public static String personalGetDonationDetailsByBillingCode = "/api/personal/donations/billing/{code}";

	public static String projectDonationRequest = "/api/projects/{projectId}/donations/payment-request";
	public static String projectGetDonationDetails = "/api/projects/donations/{id}";

	public static String rakanQrDonationRequest = "/api/public/rakan-qr/{code}/donations/payment-request";
	public static String rakanQrGetDonationDetails = "/api/public/rakan-qr/donations/{id}";

	public static String personalPaymentStatus(String donationId) {
		return String.format("/api/personal/donation/payment/%s/status", donationId);
	};

	public static String rakanQrDashboard = "/api/rakan-qr/dashboard";

	public static String adminDashboard = "/api/admin/dashboard";

	public static String adminLogin = "/api/admin/auth/login";
	public static String adminMe = "/api/admin/auth/me";

	public static String adminRegisterAdmin = "/api/admin/register/admin";
	public static String adminRegisterEditor = "/api/admin/register/editor";
	public static String adminGetAccount = "/api/admin/account/get";
	public static String adminGetAll = "/api/admin/users";
	public static String adminDelete = "/api/admin/users/{username}";

}

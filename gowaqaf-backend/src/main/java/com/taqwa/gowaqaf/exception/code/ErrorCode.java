package com.taqwa.gowaqaf.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	// General
	G001("Internal server error"), G002("Invalid request"),

	// Authentication / Authorization
	A001("Authentication required"), A002("Invalid credentials"), A003("Access denied"),

	// Member
	MBR001("Member not found"),

	// Role
	ROL001("Invalid role"),

	// Donator
	DON001("Donator not found"),

	// Personal User
	PER001("Personal not found"),

	// Personal User
	MER001("Merchant not found"),

	// Organization Profile,
	PRO010("Profile not created"),

	// Campaign
	CPG001("Campaign not found"), CPG002("Campaign already exists"),

	// Project
	PRJ001("Project not found"), PRJ002("Project already exists"),

	// Project Category
	PRJ010("Project Category not found"),

	// Project Tag
	PRJ020("Project Tag not found"),

	// Category
	CAT001("Category not found"),

	// Tag
	TAG001("Tag not found"),

	// Organization
	O001("Organization not found"),

	// Collection Gateway
	COL001("Collection creation fail"),

	// Webhook
	WHK001("Webhook token invalid"), WHK002("Billing Code not match"), WHK003("Amount not match");

	private final String defaultMessage;

}

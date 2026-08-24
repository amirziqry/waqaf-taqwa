package com.taqwa.gowaqaf.modules.organization.profile.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationProfileDetails {

	private UUID id;
	private String name;
	private String phone;
	private String email;
	private Address address;
	private String contentHtml;
	private String logoUrl;
	private String heroUrl;

}

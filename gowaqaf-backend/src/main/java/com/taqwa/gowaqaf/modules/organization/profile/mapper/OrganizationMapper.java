package com.taqwa.gowaqaf.modules.organization.profile.mapper;

import com.taqwa.gowaqaf.modules.organization.profile.dto.Address;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileDetails;
import com.taqwa.gowaqaf.modules.organization.profile.entity.OrganizationProfile;

public class OrganizationMapper {

	public static OrganizationProfileDetails mapToOrganizationProfileDetails(OrganizationProfile org) {
		OrganizationProfileDetails dto = new OrganizationProfileDetails();

		dto.setId(org.getId());
		dto.setName(org.getName());
		dto.setEmail(org.getEmail());
		dto.setPhone(org.getPhone());
		dto.setAddress(mapToAddress(org));
		dto.setContentHtml(org.getContentHtml());
		dto.setLogoUrl(org.getLogoKey());
		dto.setHeroUrl(org.getHeroKey());

		return dto;
	}

	private static Address mapToAddress(OrganizationProfile org) {
		Address address = new Address();

		address.setAddressLine1(org.getAddressLine1());
		address.setAddressLine2(org.getAddressLine2());
		address.setAddressLine3(org.getAddressLine3());
		address.setPostcode(org.getPostcode());
		address.setCity(org.getCity());
		address.setState(org.getState());
		address.setCountry(org.getCountry());

		return address;
	}

}

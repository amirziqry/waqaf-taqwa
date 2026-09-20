package com.taqwa.gowaqaf.modules.organization.about.mapper;

import com.taqwa.gowaqaf.modules.organization.about.dto.Address;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrgAboutDetails;
import com.taqwa.gowaqaf.modules.organization.about.entity.OrganizationAbout;

public class OrganizationMapper {

	public static OrgAboutDetails mapToOrganizationProfileDetails(OrganizationAbout org) {
		OrgAboutDetails dto = new OrgAboutDetails();

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

	private static Address mapToAddress(OrganizationAbout org) {
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

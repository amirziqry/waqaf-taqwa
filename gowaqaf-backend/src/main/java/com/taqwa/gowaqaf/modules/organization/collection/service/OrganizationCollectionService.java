package com.taqwa.gowaqaf.modules.organization.collection.service;

import java.time.LocalDate;

import com.taqwa.gowaqaf.modules.organization.collection.dto.OrgCollectionInfo;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSumFilter;

public interface OrganizationCollectionService {

	OrgCollectionInfo getDonationCollectionSum(OrganizationCollectionSumFilter filter);

	OrgCollectionInfo getDonationCollectionSum(LocalDate startDate, LocalDate endDate);

}

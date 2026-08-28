package com.taqwa.gowaqaf.modules.organization.collection.service;

import java.time.LocalDate;

import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSum;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSumFilter;

public interface OrganizationCollectionService {

	OrganizationCollectionSum getAllCollectionSum(OrganizationCollectionSumFilter filter);

	OrganizationCollectionSum getAllCollectionSum(LocalDate startDate, LocalDate endDate);

}

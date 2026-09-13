package com.taqwa.gowaqaf.modules.organization.collection.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taqwa.gowaqaf.modules.organization.collection.dto.DonationCollectionFilter;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrgCollectionInfo;
import com.taqwa.gowaqaf.modules.organization.collection.dto.TransactionDetails;

public interface DonationCollectionService {

	OrgCollectionInfo getDonationCollectionSum(DonationCollectionFilter filter);

	OrgCollectionInfo getDonationCollectionSum(LocalDate startDate, LocalDate endDate);

	Page<TransactionDetails> getAllTransactions(Pageable pageable);

}

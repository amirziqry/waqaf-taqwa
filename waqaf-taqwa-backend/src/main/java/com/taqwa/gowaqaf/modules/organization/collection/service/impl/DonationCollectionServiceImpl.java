package com.taqwa.gowaqaf.modules.organization.collection.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.organization.collection.TransactionMapper;
import com.taqwa.gowaqaf.modules.organization.collection.dto.DonationCollectionFilter;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrgCollectionInfo;
import com.taqwa.gowaqaf.modules.organization.collection.dto.TransactionDetails;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.collection.repository.TransactionRepository;
import com.taqwa.gowaqaf.modules.organization.collection.service.DonationCollectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DonationCollectionServiceImpl implements DonationCollectionService {

	private final TransactionRepository transactionRepository;

	@Override
	public OrgCollectionInfo getDonationCollectionSum(DonationCollectionFilter filter) {
		return getDonationCollectionSum(filter.getStartDate(), filter.getEndDate());
	}

	@Override
	public OrgCollectionInfo getDonationCollectionSum(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		OrgCollectionInfo dto = transactionRepository.getDonationCollectionSum(startDateTime, endDateTime);

		return dto;
	}

	@Override
	public Page<TransactionDetails> getAllTransactions(Pageable pageable) {
		Page<Transaction> page = transactionRepository.findAllByStatus(PaymentStatus.PAID, pageable);

		return page.map(TransactionMapper::mapToDetails);
	}

}

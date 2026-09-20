package com.taqwa.gowaqaf.modules.donation.rakanqr.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrCollection;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationFilter;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;
import com.taqwa.gowaqaf.modules.donation.rakanqr.mapper.RakanQrDonationMapper;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationService;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.collection.repository.TransactionRepository;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RakanQrDonationServiceImpl implements RakanQrDonationService {

	private final TransactionRepository transactionRepository;
	private final RakanQrDonationRepository repository;
	private final RakanQrService rakanQrService;

	@Override
	public void reconstructDonation(UUID rakanQrId, RakanQrDonation rakanQrDonation) {
		RakanQr user = rakanQrService.getRakanQrById(rakanQrId);

		Transaction transaction = transactionRepository.save(rakanQrDonation.getTransaction());

		rakanQrDonation.setId(transaction.getId());
		rakanQrDonation.setTransaction(transaction);
		rakanQrDonation.setRakanQr(user);

		RakanQrDonation saved = repository.save(rakanQrDonation);

		if (saved.getTransaction().getStatus() != PaymentStatus.PAID)
			rakanQrService.updateRakanQrCollectedAmountById(saved.getRakanQr().getId(),
					saved.getTransaction().getAmount());
	}

	@Override
	public RakanQrDonationDetails getDonationDetailsById(UUID donationId) {
		RakanQrDonation donation = repository.findById(donationId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RQD001, "Donation not found"));

		return RakanQrDonationMapper.mapToDetails(donation);
	}

	@Override
	public RakanQrCollection getDonationCollectionByUser(CustomUserDetails principal, RakanQrDonationFilter filter) {
		RakanQr agent = rakanQrService.getRakanQrByUser(principal);

		RakanQrCollection sum = getDonationCollectionByUser(agent.getId(), filter.getStartDate(), filter.getEndDate());

		return sum;
	}

	@Override
	public RakanQrCollection getDonationCollectionByUser(UUID rakanQrId, LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal sum = repository.sumAllPaidDonationsByUser(rakanQrId, startDateTime, endDateTime);

		return new RakanQrCollection(sum);
	}

	@Override
	public Page<RakanQrDonationDetails> getAllDonationDetailsByUser(CustomUserDetails principal, Pageable pageable) {
		RakanQr agent = rakanQrService.getRakanQrByUser(principal);

		return getAllDonationDetailsByUser(agent.getId(), pageable);
	}

	@Override
	public List<RakanQrDonationDetails> getDonationDetailsListByUser(UUID rakanQrId, Pageable pageable) {
		List<RakanQrDonation> donations = repository.findAllByRakanQr_Id(rakanQrId, pageable);

		return donations.stream().map(d -> RakanQrDonationMapper.mapToDetails(d)).toList();
	}

	@Override
	public Page<RakanQrDonationDetails> getAllDonationDetailsByUser(UUID rakanQrId, Pageable pageable) {
		Page<RakanQrDonation> donations = repository.findByRakanQrId(rakanQrId, pageable);

		return donations.map(RakanQrDonationMapper::mapToDetails);
	}

	@Override
	public BigDecimal getTotalCollectionFromRakanQr(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = repository.sumAllPaidDonations(startDateTime, endDateTime);

		return total;
	}

}

package com.taqwa.gowaqaf.modules.dashboard.rakanqr.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.modules.dashboard.rakanqr.dto.RakanQrDashboard;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrCollection;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationService;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.mapper.RakanQrMapper;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RakanQrDashboardServiceImpl implements RakanQrDashboardService {

	private final RakanQrService rakanQrService;
	private final RakanQrDonationService donationService;

	@Override
	public RakanQrDashboard getDashboardByUser(CustomUserDetails principal) {
		RakanQr agent = rakanQrService.getRakanQrByUser(principal);

		// Valid only for active status.
		if (agent.getStatus() != RakanQrStatus.ACTIVE)
			throw new BadRequestException(ErrorCode.A001, "Service not available.");

		RakanQrDashboard dashboard = new RakanQrDashboard();

		// Set object response.
		dashboard.setInfo(RakanQrMapper.mapToInfo(agent));
		dashboard.setCollectedAmount(getRakanQrCollectionByUser(agent.getId()));
		dashboard.setDonations(getAllRakanQrDonationDetailsByUser(agent.getId()));

		return dashboard;
	}

	private RakanQrCollection getRakanQrCollectionByUser(UUID rakanQrId) {
		LocalDate today = LocalDate.now();
		LocalDate startDate = today.withDayOfMonth(1);
		LocalDate endDate = today.plusMonths(1).withDayOfMonth(1);

		return donationService.getDonationCollectionByUser(rakanQrId, startDate, endDate);
	}

	private List<RakanQrDonationDetails> getAllRakanQrDonationDetailsByUser(UUID rakanQrId) {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("transaction.createdAt").descending());

		return donationService.getDonationDetailsListByUser(rakanQrId, pageable);
	}

}

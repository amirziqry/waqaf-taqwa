package com.taqwa.gowaqaf.modules.feature.rakanqr.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.feature.rakanqr.component.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.component.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrFilter;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithSum;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithSumFilter;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.mapper.RakanQrMapper;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.service.MerchantService;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RakanQrServiceImpl implements RakanQrService {

	private final RakanQrRepository agentRepository;
	private final MerchantService merchantService;
	private final PersonalService personalService;

	@Override
	public RakanQrInfo createRakanQr(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		RakanQr agent = new RakanQr();

		if (principal.getAccountType() == AccountType.MERCHANT) {
			Merchant merchant = merchantService.getMerchantByUsername(principal.getUsername());

			agent.setMerchant(merchant);
			agent.setType(RakanQrType.MERCHANT);
		}

		if (principal.getAccountType() == AccountType.PERSONAL) {
			Personal personal = personalService.getPersonalByUsername(principal.getUsername());

			agent.setPersonal(personal);
			agent.setType(RakanQrType.PERSONAL);
		}

		agent.setStatus(RakanQrStatus.PENDING);

		RakanQr saved = agentRepository.save(agent);

		return RakanQrMapper.mapToInfo(saved);
	}

	@Override
	public List<RakanQrInfo> getAllRakanQr(RakanQrFilter filter) {
		List<RakanQr> agents = agentRepository.findAllWithFilters(filter.getType(), filter.getStatus());

		return agents.stream().map(agent -> RakanQrMapper.mapToInfo(agent)).toList();
	}

	@Override
	public void updateRakanQrStatus(UUID id, RakanQrStatusRequest request) {
		RakanQr agent = agentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Rakan QR agent not found"));

		agent.setStatus(request.status());

		agentRepository.save(agent);
	}

	@Override
	public List<RakanQrWithSum> getAllRakanQrWithSum(RakanQrWithSumFilter filter) {
		return getAllRakanQrWithSum(filter.getStartDate(), filter.getEndDate());
	}

	@Override
	public List<RakanQrWithSum> getAllRakanQrWithSum(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		List<RakanQrWithSum> dtos = agentRepository.findAllRakanQrWithSum(startDateTime, endDateTime);

		return dtos;
	}

}

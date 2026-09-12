package com.taqwa.gowaqaf.modules.feature.rakanqr.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrApplicationRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrFilter;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithCollection;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrAccount;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.mapper.RakanQrMapper;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrSpecification;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.service.MerchantService;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * General RakanQr agent management business logic.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RakanQrServiceImpl implements RakanQrService {

	private final RakanQrRepository repository;
	private final MerchantService merchantService;
	private final PersonalService personalService;

	@Override
	public RakanQrInfo createRakanQr(AccountUserDetails principal, RakanQrApplicationRequest request) {
		RakanQr agent = new RakanQr();

		if (principal.getAccountType() == AccountType.PERSONAL) {
			Personal user = personalService.getPersonalByUsername(principal.getUsername());
			agent.setPersonal(user);
			agent.setAccount(RakanQrAccount.PERSONAL);
			validateUser(user.getInfo().getAccountHolderName(), user.getInfo().getEmail(), user.getInfo().getPhone());
		}

		if (principal.getAccountType() == AccountType.MERCHANT) {
			Merchant user = merchantService.getMerchantByUsername(principal.getUsername());
			agent.setMerchant(user);
			agent.setAccount(RakanQrAccount.MERCHANT);
			validateUser(user.getInfo().getAccountHolderName(), user.getInfo().getEmail(), user.getInfo().getPhone());
		}

		agent.setType(request.getType());
		agent.setStatus(RakanQrStatus.PENDING);
		agent.setCode(generateUniqueRakanQrCode());
		agent.setCollectedAmount(new BigDecimal("0.00"));

		if (agent.getType() == RakanQrType.AMBASSADOR)
			agent.setCommission(new BigDecimal("0.00"));

		RakanQr saved = repository.save(agent);

		return RakanQrMapper.mapToInfo(saved);
	}

	private void validateUser(String name, String email, String phone) {
		if (name == null || email == null || phone == null)
			throw new BadRequestException(ErrorCode.PER001, "Please update account name, email, phone.");
	}

	private String generateUniqueRakanQrCode() {
		String code = "RQR" + ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000);

		if (repository.existsByCode(code))
			throw new BadRequestException(ErrorCode.RQA001, "Try again.");

		return code;
	}

	@Override
	public void updateRakanQrStatus(UUID id, RakanQrStatusRequest request) {
		RakanQr agent = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RQA001, "Rakan QR not found"));

		agent.setStatus(request.status());

		repository.save(agent);
	}

	/**
	 * Update RakanQr collected amount by webhook.
	 */
	@Override
	@Transactional
	public void updateRakanQrCollectedAmountById(UUID id, BigDecimal amount) {
		repository.incrementCollectedAmountById(id, amount);
	}

	@Override
	public RakanQr getRakanQrById(UUID rakanQrId) {
		RakanQr agent = repository.findById(rakanQrId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RQA001, "Rakan QR agent not found"));

		return agent;
	}

	/**
	 * Get by principal > username.
	 */
	@Override
	public RakanQr getRakanQrByUser(AccountUserDetails principal) {
		RakanQr agent = null;

		if (principal.getAccountType() == AccountType.MERCHANT)
			agent = repository.findByMerchant_Username(principal.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RQA001, "Rakan QR agent not found"));

		if (principal.getAccountType() == AccountType.PERSONAL)
			agent = repository.findByPersonal_Username(principal.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RQA001, "Rakan QR agent not found"));

		return agent;
	}

	/**
	 * Get by rakanqr code.
	 */
	@Override
	public RakanQr getRakanQrByUser(String agentCode) {
		RakanQr agent = repository.findByCode(agentCode)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RQA001, "Rakan QR agent not found"));

		return agent;
	}

	@Override
	public RakanQrInfo getRakanQrInfoByUser(AccountUserDetails principal) {
		RakanQr agent = getRakanQrByUser(principal);

		return RakanQrMapper.mapToInfo(agent);
	}

	/**
	 * Provide all RakanQrs to controller (Status filter).
	 */
	@Override
	public Page<RakanQrInfo> getAllRakanQrInfo(Pageable pageable, RakanQrFilter filter) {
		Specification<RakanQr> specification = Specification.where(RakanQrSpecification.hasType(filter.getType()))
				.and(RakanQrSpecification.hasStatus(filter.getStatus()));

		Page<RakanQr> rakanqrs = repository.findAll(specification, pageable);

		return rakanqrs.map(agent -> RakanQrMapper.mapToInfo(agent));
	}

	/**
	 * Provide all RakanQrs to dashboard service.
	 */
	@Override
	public List<RakanQrInfo> getRakanQrInfoList(Pageable pageable) {
		List<RakanQr> rakanqrs = repository.findAllBy(pageable);

		List<RakanQrInfo> dtos = rakanqrs.stream().map(r -> RakanQrMapper.mapToInfo(r)).toList();

		return dtos;
	}

	@Override
	public Page<RakanQrWithCollection> getAllRakanQrWithCollection(Pageable pageable, LocalDate startDate,
			LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		Page<RakanQrWithCollection> dtos = repository.findAllRakanQrWithSum(pageable, startDateTime, endDateTime);
		return dtos;
	}

}

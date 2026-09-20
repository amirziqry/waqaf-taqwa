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
import com.taqwa.gowaqaf.external.payment.client.nexgen.client.NexGenClient;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.collection.NexGenCollectionResponse;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.collection.NexGenCreateCollectionRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrApplicationRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrFilter;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithCollection;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.mapper.RakanQrMapper;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrSpecification;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.service.UserService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

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
	private final UserService userService;
	private final NexGenClient nexGenClient;

	@Override
	@Transactional
	public RakanQrInfo createRakanQr(CustomUserDetails principal, RakanQrApplicationRequest request) {
		RakanQr agent = new RakanQr();

		// repository.deleteById(UUID.fromString("d5a46a26-db39-48bf-a4ca-9f571b234637"));

		Account user = userService.getUserById(principal.getId());

		if (repository.existsByAccount_Id(user.getId()))
			throw new BadRequestException(ErrorCode.RQA002, "Rakan QR application already exists.");

		agent.setAccount(user);
		agent.setCode(generateUniqueRakanQrCode());
		agent.setType(request.getType());
		agent.setFullName(request.getFullName());
		agent.setIcNumber(request.getIcNumber());
		agent.setPhone(request.getPhone());

		agent.setRepresentativeType(request.getRepresentativeType());
		agent.setEstablishmentName(request.getEstablishmentName());
		agent.setCollectedAmount(new BigDecimal("0.00"));

		if (agent.getType() == RakanQrType.AHLI) {
			agent.setStatus(RakanQrStatus.ACTIVE);
		}

		if (agent.getType() == RakanQrType.DUTA) {
			agent.setStatus(RakanQrStatus.PENDING);
			agent.setQrSpot(request.getQrSpot());
			agent.setPostalAddress(request.getPostalAddress());
			agent.setBankName(request.getBankName());
			agent.setBankAccountNumber(request.getBankAccountNumber());
			agent.setCommission(new BigDecimal("0.00"));
		}

		RakanQr saved = repository.save(agent);

		return RakanQrMapper.mapToInfo(saved);
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

		String collectionCode = null;
		if (agent.getType() == RakanQrType.DUTA)
			if (agent.getStatus() == RakanQrStatus.ACTIVE)
				if (agent.getCollectionCode() == null)
					collectionCode = nexGenCreateCollection(agent.getAccount().getAccountHolderName() + " Collection",
							"RakanQr Collection", "active");

		agent.setCollectionCode(collectionCode);

		repository.save(agent);
	}

	private String nexGenCreateCollection(String name, String description, String status) {

		NexGenCollectionResponse response = nexGenClient
				.requestNexGenCreateCollection(new NexGenCreateCollectionRequest(name, description, status));

		return response.getCode();
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
	public RakanQr getRakanQrByUser(CustomUserDetails principal) {
		RakanQr agent = null;

		agent = repository.findByAccount_Username(principal.getUsername())
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
	public RakanQrInfo getRakanQrInfoByUser(CustomUserDetails principal) {
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

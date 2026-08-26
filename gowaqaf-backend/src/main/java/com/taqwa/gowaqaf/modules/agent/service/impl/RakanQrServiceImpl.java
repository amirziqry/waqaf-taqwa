package com.taqwa.gowaqaf.modules.agent.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.agent.component.AgentStatus;
import com.taqwa.gowaqaf.modules.agent.component.AgentType;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrFilter;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.agent.entity.RakanQr;
import com.taqwa.gowaqaf.modules.agent.mapper.RakanQrMapper;
import com.taqwa.gowaqaf.modules.agent.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.agent.service.RakanQrService;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RakanQrServiceImpl implements RakanQrService {

	private final RakanQrRepository agentRepository;
	private final MerchantRepository merchantRepository;
	private final PersonalRepository personalRepository;

	@Override
	public RakanQrInfo createRakanQr(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		RakanQr agent = new RakanQr();

		if (principal.getAccountType() == AccountType.MERCHANT) {
			Merchant merchant = merchantRepository.findByUsername(principal.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Merchant account not found"));

			agent.setMerchant(merchant);
			agent.setType(AgentType.MERCHANT);

		} else if (principal.getAccountType() == AccountType.PERSONAL) {
			Personal personal = personalRepository.findByUsername(principal.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Personal account not found"));

			agent.setPersonal(personal);
			agent.setType(AgentType.PERSONAL);

		} else {
			throw new BadRequestException(ErrorCode.A001, "Account type not allowed.");
		}

		agent.setStatus(AgentStatus.PENDING);

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

}

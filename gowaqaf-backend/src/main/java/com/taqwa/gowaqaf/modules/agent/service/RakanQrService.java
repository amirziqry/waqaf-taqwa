package com.taqwa.gowaqaf.modules.agent.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.modules.agent.dto.RakanQrFilter;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrWithSum;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrWithSumFilter;

public interface RakanQrService {

	List<RakanQrInfo> getAllRakanQr(RakanQrFilter filter);

	RakanQrInfo createRakanQr(Authentication authentication);

	void updateRakanQrStatus(UUID id, RakanQrStatusRequest request);

	List<RakanQrWithSum> getAllRakanQrWithSum(RakanQrWithSumFilter filter);

	List<RakanQrWithSum> getAllRakanQrWithSum(LocalDate startDate, LocalDate endDate);

}

package com.taqwa.gowaqaf.modules.agent.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.modules.agent.dto.RakanQrFilter;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrStatusRequest;

public interface RakanQrService {

	List<RakanQrInfo> getAllRakanQr(RakanQrFilter filter);

	RakanQrInfo createRakanQr(Authentication authentication);

	void updateRakanQrStatus(UUID id, RakanQrStatusRequest request);

}

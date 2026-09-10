package com.taqwa.gowaqaf.modules.dashboard.merchant.service.impl;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.dashboard.merchant.dto.MerchantDashboard;
import com.taqwa.gowaqaf.modules.dashboard.merchant.service.MerchantDashboardService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantDashboardServiceImpl implements MerchantDashboardService {
	@Override
	public MerchantDashboard getDashboardByUser(AccountUserDetails principal) {
		// TODO Auto-generated method stub
		return null;
	}

}

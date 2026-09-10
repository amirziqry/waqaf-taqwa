package com.taqwa.gowaqaf.modules.dashboard.merchant.service;

import com.taqwa.gowaqaf.modules.dashboard.merchant.dto.MerchantDashboard;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface MerchantDashboardService {

	MerchantDashboard getDashboardByUser(AccountUserDetails principal);

}

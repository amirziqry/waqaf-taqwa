package com.taqwa.gowaqaf.modules.dashboard.merchant.service;

import com.taqwa.gowaqaf.modules.dashboard.merchant.dto.MerchantDashboard;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

public interface MerchantDashboardService {

	MerchantDashboard getDashboardByUser(CustomUserDetails principal);

}

package com.taqwa.gowaqaf.modules.feature.recurring.service;

import com.taqwa.gowaqaf.modules.feature.recurring.dto.RecurringApplicationRequest;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

public interface RecurringService {

	void createRecurringByUser(CustomUserDetails principal, RecurringApplicationRequest request);

	void processDuePayments();

}

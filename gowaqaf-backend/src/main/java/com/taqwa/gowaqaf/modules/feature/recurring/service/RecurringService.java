package com.taqwa.gowaqaf.modules.feature.recurring.service;

import com.taqwa.gowaqaf.modules.feature.recurring.dto.RecurringApplicationRequest;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface RecurringService {

	void createRecurringByUser(AccountUserDetails principal, RecurringApplicationRequest request);

	void processDuePayments();

}

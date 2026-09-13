package com.taqwa.gowaqaf.modules.user.merchant.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.user.admin.dto.ChangePasswordRequest;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantAccountInfo;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterResponse;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.personal.dto.AccountUploadFields;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface MerchantService {

	MerchantRegisterResponse createMerchant(MerchantRegisterCredentials request);

	Merchant getMerchantByUsername(String username);

	void updateAccountByUser(AccountUserDetails principal, AccountUploadFields request);

	void changePasswordByUsername(AccountUserDetails principal, ChangePasswordRequest request);

	/**
	 * Get personal account info.
	 */
	MerchantAccountInfo getAccountByUser(AccountUserDetails principal);

	Merchant getMerchantById(UUID userId);

}

package com.taqwa.gowaqaf.security.account;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;

public interface AccountUserDetails extends UserDetails {

	UUID getId();

	AccountType getAccountType();

}

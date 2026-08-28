package com.taqwa.gowaqaf.modules.user.account.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.user.account.entity.AccountIdentity;

public interface AccountIdentityRepository extends JpaRepository<AccountIdentity, UUID> {

}

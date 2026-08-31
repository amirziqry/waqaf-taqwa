package com.taqwa.gowaqaf.modules.user.account.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;

public interface AccountInfoRepository extends JpaRepository<AccountInfo, UUID> {

}

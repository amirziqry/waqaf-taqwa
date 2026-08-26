package com.taqwa.gowaqaf.modules.user.merchant.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

	Optional<Merchant> findByUsername(String username);

	Merchant findByEmail(String email);

}

package com.taqwa.gowaqaf.modules.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.enums.Role;

public interface AccountRepository extends JpaRepository<Account, UUID> {

	List<Account> findByRolesIn(List<Role> roles);

	Optional<Account> findByUsername(String username);

	boolean existsByUsername(String username);

}

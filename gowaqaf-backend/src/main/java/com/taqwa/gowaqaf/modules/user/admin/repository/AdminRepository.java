package com.taqwa.gowaqaf.modules.user.admin.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, UUID> {

	Optional<Admin> findByUsername(String username);

	Admin findByEmail(String email);

	Boolean existsByUsername(String username);

	void deleteByUsername(String username);

}

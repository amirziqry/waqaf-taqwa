package com.taqwa.gowaqaf.modules.user.personal.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;

public interface PersonalRepository extends JpaRepository<Personal, UUID> {

	Optional<Personal> findByUsername(String username);

	Personal findByEmail(String email);

}

package com.taqwa.gowaqaf.modules.user.donator.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.user.donator.entity.Donator;

public interface DonatorRepository extends JpaRepository<Donator, UUID> {

	Donator findByUsername(String username);

	Donator findByEmail(String email);

}

package com.taqwa.gowaqaf.modules.user.vendor.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.user.vendor.entity.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {

	Vendor findByUsername(String username);

	Vendor findByEmail(String email);

}

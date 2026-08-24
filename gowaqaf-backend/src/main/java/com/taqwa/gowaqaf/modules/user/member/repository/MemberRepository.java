package com.taqwa.gowaqaf.modules.user.member.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.user.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, UUID> {

	Member findByUsername(String username);

	Member findByEmail(String email);

	Boolean existsByUsername(String username);

	void deleteByUsername(String username);

}

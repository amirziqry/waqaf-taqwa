package com.taqwa.gowaqaf.modules.user.member.dto;

import java.util.Set;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.user.member.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfo {

	private UUID id;

	private String username;

	private String email;

	private Set<Role> roles;
}

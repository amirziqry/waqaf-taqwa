package com.taqwa.gowaqaf.modules.organization.profile.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column
	private String name;

	@Column
	private String phone;

	@Column
	private String email;

	@Column(nullable = true)
	private String addressLine1;

	@Column(nullable = true)
	private String addressLine2;

	@Column(nullable = true)
	private String addressLine3;

	@Column(nullable = true)
	private Long postcode;

	@Column(nullable = true)
	private String city;

	@Column(nullable = true)
	private String state;

	@Column(nullable = true)
	private String country;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String contentHtml;

	@Column(nullable = true)
	private String logoKey;

	@Column(nullable = true)
	private String heroKey;

}

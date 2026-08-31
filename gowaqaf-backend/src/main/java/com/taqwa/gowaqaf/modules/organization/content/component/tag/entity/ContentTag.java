package com.taqwa.gowaqaf.modules.organization.content.component.tag.entity;

import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "content_tag_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@Enumerated(EnumType.STRING)
	private ContentType type;

}

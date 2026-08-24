package com.taqwa.gowaqaf.modules.organization.project.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.organization.project.component.category.entity.ProjectCategory;
import com.taqwa.gowaqaf.modules.organization.project.component.image.entity.ProjectImage;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.entity.ProjectTag;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column
	private String name;

	@Column
	private String slugUrl;

	@Column(nullable = true, precision = 19, scale = 2)
	private BigDecimal collectedAmount;

	@Column(nullable = true, precision = 19, scale = 2)
	private BigDecimal targetAmount;

	@Column
	private String location;

	@Column
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate date;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private ProjectCategory category;

	@ManyToMany
	@JoinTable(name = "project_tag", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<ProjectTag> tags;

	@Column
	private String summary;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String contentHtml;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProjectImage> images;

	@Column(nullable = false, unique = true)
	private String paymentCollectionCode;

}

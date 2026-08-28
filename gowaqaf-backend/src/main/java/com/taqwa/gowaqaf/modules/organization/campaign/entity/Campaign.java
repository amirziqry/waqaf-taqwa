package com.taqwa.gowaqaf.modules.organization.campaign.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.entity.CampaignCategory;
import com.taqwa.gowaqaf.modules.organization.campaign.component.image.CampaignImage;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.entity.CampaignTag;
import com.taqwa.gowaqaf.modules.organization.project.entity.Status;

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
@Table(name = "campaign_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column
	private String name;

	@Column
	private String slugUrl;

	@Column
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate dateStart;

	@Column
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate dateEnd;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "category_id", nullable = true)
	private CampaignCategory category;

	@ManyToMany
	@JoinTable(name = "campaign_tag", joinColumns = @JoinColumn(name = "campaign_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<CampaignTag> tags;

	@Column
	private String summary;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String contentHtml;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CampaignImage> images;

}

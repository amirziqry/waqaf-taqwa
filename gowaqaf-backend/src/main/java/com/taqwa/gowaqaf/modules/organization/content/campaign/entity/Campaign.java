package com.taqwa.gowaqaf.modules.organization.content.campaign.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.entity.CampaignImage;
import com.taqwa.gowaqaf.modules.organization.content.component.category.entity.ContentCategory;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;

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
	private ContentCategory category;

	@ManyToMany
	@JoinTable(name = "campaign_tag", joinColumns = @JoinColumn(name = "campaign_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<ContentTag> tags;

	@Column
	private String summary;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String contentHtml;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ContentStatus status;

	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CampaignImage> images;

}

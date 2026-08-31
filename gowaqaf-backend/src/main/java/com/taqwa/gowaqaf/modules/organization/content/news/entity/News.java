package com.taqwa.gowaqaf.modules.organization.content.news.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.organization.content.component.category.entity.ContentCategory;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;
import com.taqwa.gowaqaf.modules.organization.content.news.component.image.entity.NewsImage;

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
@Table(name = "news_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column
	private String title;

	@Column
	private String slugUrl;

	@Column
	private String author;

	@Column
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate date;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "category_id", nullable = true)
	private ContentCategory category;

	@ManyToMany
	@JoinTable(name = "news_tag", joinColumns = @JoinColumn(name = "news_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<ContentTag> tags;

	@Column
	private String summary;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String contentHtml;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ContentStatus status;

	@OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NewsImage> images;

}

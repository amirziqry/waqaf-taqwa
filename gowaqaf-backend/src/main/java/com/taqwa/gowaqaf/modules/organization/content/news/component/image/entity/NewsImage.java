package com.taqwa.gowaqaf.modules.organization.content.news.component.image.entity;

import com.taqwa.gowaqaf.modules.organization.content.news.entity.News;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class NewsImage {

	@Id
	@GeneratedValue
	private Long id;

	private String fileKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "news_id")
	private News news;

}

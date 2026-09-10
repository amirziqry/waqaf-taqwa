package com.taqwa.gowaqaf.modules.organization.content.news.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.content.news.entity.News;

public interface NewsRepository extends JpaRepository<News, UUID> {

	List<News> findAllBy(Pageable pageable);

}

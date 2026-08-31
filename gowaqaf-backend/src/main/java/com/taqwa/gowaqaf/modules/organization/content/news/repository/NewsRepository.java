package com.taqwa.gowaqaf.modules.organization.content.news.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.content.news.entity.News;

public interface NewsRepository extends JpaRepository<News, UUID> {

}

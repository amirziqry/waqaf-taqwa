package com.taqwa.gowaqaf.modules.organization.content.component.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.content.component.category.entity.ContentCategory;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;

public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {

	Optional<ContentCategory> findByIdAndType(Long id, ContentType type);

	List<ContentCategory> findAllByType(ContentType type);

	boolean existsByIdAndType(ContentType type, Long id);

}

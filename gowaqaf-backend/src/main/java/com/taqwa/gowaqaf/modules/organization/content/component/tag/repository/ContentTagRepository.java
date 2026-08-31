package com.taqwa.gowaqaf.modules.organization.content.component.tag.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;

public interface ContentTagRepository extends JpaRepository<ContentTag, Long> {

	Optional<ContentTag> findByIdAndType(Long id, ContentType type);

	List<ContentTag> findAllByType(ContentType type);

	boolean existsByIdAndType(Long id, ContentType type);

	List<ContentTag> findAllByIdInAndType(Set<Long> ids, ContentType type);

}

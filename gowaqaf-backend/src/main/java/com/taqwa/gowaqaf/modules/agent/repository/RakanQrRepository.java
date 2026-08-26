package com.taqwa.gowaqaf.modules.agent.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.agent.component.AgentStatus;
import com.taqwa.gowaqaf.modules.agent.component.AgentType;
import com.taqwa.gowaqaf.modules.agent.entity.RakanQr;

public interface RakanQrRepository extends JpaRepository<RakanQr, UUID> {

	@Query("""
			SELECT a
			FROM RakanQr a
			WHERE (:type IS NULL OR a.type = :type)
			AND (:status IS NULL OR a.status = :status)
			""")
	List<RakanQr> findAllWithFilters(@Param("type") AgentType type, @Param("status") AgentStatus status);

	Optional<RakanQr> findByMerchant_Username(String username);

	Optional<RakanQr> findByPersonal_Username(String username);

}

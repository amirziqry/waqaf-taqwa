package com.taqwa.gowaqaf.modules.feature.rakanqr.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.feature.rakanqr.component.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.component.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithSum;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;

public interface RakanQrRepository extends JpaRepository<RakanQr, UUID> {

	@Query("""
			SELECT a
			FROM RakanQr a
			WHERE (:type IS NULL OR a.type = :type)
			AND (:status IS NULL OR a.status = :status)
			""")
	List<RakanQr> findAllWithFilters(@Param("type") RakanQrType type, @Param("status") RakanQrStatus status);

	Optional<RakanQr> findByMerchant_Username(String username);

	Optional<RakanQr> findByPersonal_Username(String username);

	@Query("""
			SELECT new com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithSum(
			    a.id,
			    a.code,
			    a.type,
			    a.status,
			    COALESCE(SUM(d.amount), 0)
			)
			FROM RakanQr a
			LEFT JOIN RakanQrDonation d
			    ON d.rakanQr = a
			    AND d.status = 'PAID'
			    AND (:startDate IS NULL OR d.paidAt >= :startDate)
			    AND (:endDate IS NULL OR d.paidAt < :endDate)
			GROUP BY a.id, a.code, a.type, a.status
			""")
	List<RakanQrWithSum> findAllRakanQrWithSum(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

}

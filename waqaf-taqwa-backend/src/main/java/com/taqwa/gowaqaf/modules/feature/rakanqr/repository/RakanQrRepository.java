package com.taqwa.gowaqaf.modules.feature.rakanqr.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithCollection;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;

public interface RakanQrRepository extends JpaRepository<RakanQr, UUID>, JpaSpecificationExecutor<RakanQr> {

	Optional<RakanQr> findByAccount_Username(String username);

	Optional<RakanQr> findByCode(String code);

	boolean existsByCode(String code);

	List<RakanQr> findAllBy(Pageable pageable);

	@Modifying
	@Query("""
			   UPDATE RakanQr a
			   SET a.collectedAmount = a.collectedAmount + :amount
			   WHERE a.id = :id
			""")
	void incrementCollectedAmountById(UUID id, BigDecimal amount);

	@Query(value = """
			SELECT new com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithCollection(
			    a.id,
			    a.code,
			    a.type,
			    a.status,
			    COALESCE(SUM(d.transaction.amount), 0)
			)
			FROM RakanQr a
			LEFT JOIN RakanQrDonation d
			    ON d.rakanQr = a
			    AND d.transaction.status = 'PAID'
			    AND (:startDate IS NULL OR d.transaction.paidAt >= :startDate)
			    AND (:endDate IS NULL OR d.transaction.paidAt < :endDate)
			GROUP BY a.id, a.code, a.type, a.status, a.updatedAt
			""", countQuery = """
			SELECT COUNT(a)
			FROM RakanQr a
			""")
	Page<RakanQrWithCollection> findAllRakanQrWithSum(Pageable pageable, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	boolean existsByAccount_Id(UUID id);

}

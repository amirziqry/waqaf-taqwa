package com.taqwa.gowaqaf.modules.feature.rakanqr.repository;

import org.springframework.data.jpa.domain.Specification;

import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;

public class RakanQrSpecification {

	public static Specification<RakanQr> hasType(RakanQrType type) {
		return (root, _, cb) -> {
			if (type == null) {
				return null;
			}

			return cb.equal(root.get("type"), type);
		};
	}

	public static Specification<RakanQr> hasStatus(RakanQrStatus status) {
		return (root, _, cb) -> {
			if (status == null) {
				return null;
			}

			return cb.equal(root.get("status"), status);
		};
	}

}

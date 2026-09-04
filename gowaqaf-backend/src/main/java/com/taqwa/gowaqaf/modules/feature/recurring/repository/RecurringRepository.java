package com.taqwa.gowaqaf.modules.feature.recurring.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.feature.recurring.entity.Recurring;

public interface RecurringRepository extends JpaRepository<Recurring, UUID> {

}

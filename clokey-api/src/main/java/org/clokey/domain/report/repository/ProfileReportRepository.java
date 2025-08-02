package org.clokey.domain.report.repository;

import org.clokey.report.entity.ProfileReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ProfileReportRepository extends JpaRepository<ProfileReport, Long>, QuerydslPredicateExecutor<ProfileReport> {
}

package org.clokey.domain.report.repository;

import org.clokey.report.entity.HistoryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface HistoryReportRepository
        extends JpaRepository<HistoryReport, Long>, QuerydslPredicateExecutor<HistoryReport> {}

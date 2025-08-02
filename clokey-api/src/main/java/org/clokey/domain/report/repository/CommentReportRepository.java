package org.clokey.domain.report.repository;

import org.clokey.report.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long>, QuerydslPredicateExecutor<CommentReport> {
}

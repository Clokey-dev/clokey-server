package org.clokey.domain.report.repository;

import org.clokey.member.entity.Member;
import org.clokey.report.entity.Report;
import org.clokey.report.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterAndTargetTypeAndTargetId(
            Member reporter, TargetType targetType, Long TargetId);
}

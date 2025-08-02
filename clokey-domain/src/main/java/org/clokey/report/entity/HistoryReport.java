package org.clokey.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;
import org.clokey.history.entity.History;
import org.clokey.history.enums.HistoryReportType;
import org.clokey.member.entity.Member;
import org.clokey.report.enums.ReportStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoryReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryReportType historyReportType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'UNCHECKED'", nullable = false)
    private ReportStatus reportStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 200)
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private HistoryReport(HistoryReportType historyReportType,
                          History history,
                          Member member,
                          String content) {
        this.historyReportType = historyReportType;
        this.history = history;
        this.member = member;
        this.content = content;
        this.reportStatus = ReportStatus.UNCHECKED; // 기본값 지정
    }

    public static HistoryReport createHistoryReport(HistoryReportType historyReportType,
                                       History history,
                                       Member member,
                                       String content) {
        return HistoryReport.builder()
                .historyReportType(historyReportType)
                .history(history)
                .member(member)
                .content(content)
                .build();
    }

}
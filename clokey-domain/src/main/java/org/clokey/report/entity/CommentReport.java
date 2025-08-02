package org.clokey.report.entity;


import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;
import org.clokey.history.entity.Comment;
import org.clokey.history.enums.CommentReportType;
import org.clokey.member.entity.Member;
import org.clokey.report.enums.ReportStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentReportType commentReportType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'UNCHECKED'", nullable = false)
    private ReportStatus reportStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 200)
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private CommentReport(CommentReportType commentReportType,
                          Comment comment,
                          Member member,
                          String content) {
        this.commentReportType = commentReportType;
        this.comment = comment;
        this.member = member;
        this.content = content;
        this.reportStatus = ReportStatus.UNCHECKED; // 기본값
    }

    public static CommentReport createCommentReport(CommentReportType commentReportType,
                                       Comment comment,
                                       Member member,
                                       String content) {
        return CommentReport.builder()
                .commentReportType(commentReportType)
                .comment(comment)
                .member(member)
                .content(content)
                .build();
    }

}
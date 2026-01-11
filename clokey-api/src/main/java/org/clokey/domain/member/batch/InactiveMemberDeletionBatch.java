package org.clokey.domain.member.batch;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.member.entity.Member;
import org.clokey.member.enums.MemberStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InactiveMemberDeletionBatch {

    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 00:00:00
    @Transactional
    public void deleteInactiveMembers() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(15);
        List<Member> inactiveMembers =
                memberRepository.findInactiveMembersBefore(MemberStatus.INACTIVE, cutoffDate);

        log.info("삭제 대상 INACTIVE 회원 수: {}", inactiveMembers.size());
        memberRepository.deleteAll(inactiveMembers);
        log.info("INACTIVE 회원 {}명 삭제 완료", inactiveMembers.size());
    }
}

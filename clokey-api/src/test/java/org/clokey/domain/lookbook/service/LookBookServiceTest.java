package org.clokey.domain.lookbook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.clokey.IntegrationTest;
import org.clokey.domain.lookbook.dto.request.LookBookCreateRequest;
import org.clokey.domain.lookbook.repository.LookBookRepository;
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.global.util.MemberUtil;
import org.clokey.member.entity.Member;
import org.clokey.member.entity.OauthInfo;
import org.clokey.member.enums.OauthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class LookBookServiceTest extends IntegrationTest {

    @Autowired private LookBookService lookBookService;
    @Autowired private LookBookRepository lookBookRepository;
    @Autowired private MemberRepository memberRepository;

    @MockitoBean private MemberUtil memberUtil;

    @Nested
    class 룩북을_생성할_때 {

        @BeforeEach
        void setUp() {
            Member member1 =
                    Member.createMember(
                            "testEmail1",
                            "testClokeyId1",
                            "testNickName1",
                            OauthInfo.createOauthInfo("testOauthId1", OauthProvider.KAKAO));

            memberRepository.save(member1);
            given(memberUtil.getCurrentMember()).willReturn(member1);
        }

        @Test
        void 유효한_요청이면_룩북을_생성한다() {
            // when
            lookBookService.createLookBook(new LookBookCreateRequest("testName"));

            // then
            assertThat(lookBookRepository.findById(1L).orElseThrow())
                    .extracting("name", "member.id")
                    .containsExactly("testName", 1L);
        }
    }
}

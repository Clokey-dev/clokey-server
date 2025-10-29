package org.clokey.domain.lookbook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.clokey.IntegrationTest;
import org.clokey.domain.lookbook.dto.request.LookBookCreateRequest;
import org.clokey.domain.lookbook.dto.request.LookBookUpdateRequest;
import org.clokey.domain.lookbook.exception.LookBookErrorCode;
import org.clokey.domain.lookbook.repository.LookBookRepository;
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.util.MemberUtil;
import org.clokey.lookbook.entity.LookBook;
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

    @Nested
    class 룩북을_수정할_때 {

        @BeforeEach
        void setUp() {
            Member member1 =
                    Member.createMember(
                            "testEmail1",
                            "testClokeyId1",
                            "testNickName1",
                            OauthInfo.createOauthInfo("testOauthId1", OauthProvider.KAKAO));

            Member member2 =
                    Member.createMember(
                            "testEmail2",
                            "testClokeyId2",
                            "testNickName2",
                            OauthInfo.createOauthInfo("testOauthId1", OauthProvider.KAKAO));

            memberRepository.saveAll(List.of(member1, member2));
            given(memberUtil.getCurrentMember()).willReturn(member1);

            LookBook lookBook1 = LookBook.createLookBook("testName1", member1);
            LookBook lookBook2 = LookBook.createLookBook("testName2", member2);
            lookBookRepository.saveAll(List.of(lookBook1, lookBook2));
        }

        @Test
        void 유효한_요청이면_룩북을_수정한다() {
            // when
            lookBookService.updateLookBook(1L, new LookBookUpdateRequest("newName"));

            // then
            assertThat(lookBookRepository.findById(1L).get().getName()).isEqualTo("newName");
        }

        @Test
        void 존재하지_않는_룩북을_입려하면_예외가_발생한다() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    lookBookService.updateLookBook(
                                            999L, new LookBookUpdateRequest("newName")))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(LookBookErrorCode.LOOK_BOOK_NOT_FOUND.getMessage());
        }

        @Test
        void 나의_룩북이_아닌_경우_예외가_발생한다() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    lookBookService.updateLookBook(
                                            2L, new LookBookUpdateRequest("newName")))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(LookBookErrorCode.NOT_LOOK_BOOK_OWNER.getMessage());
        }
    }
}

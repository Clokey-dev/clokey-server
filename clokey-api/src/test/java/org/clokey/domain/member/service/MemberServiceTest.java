package org.clokey.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.clokey.IntegrationTest;
import org.clokey.domain.member.dto.request.ProfileUpdateRequest;
import org.clokey.domain.member.exception.MemberErrorCode;
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.FakeAuthContext;
import org.clokey.member.entity.Member;
import org.clokey.member.entity.OauthInfo;
import org.clokey.member.enums.MemberStatus;
import org.clokey.member.enums.OauthProvider;
import org.clokey.member.enums.RegisterStatus;
import org.clokey.member.enums.Visibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class MemberServiceTest extends IntegrationTest {

    @Autowired private MemberService memberService;
    @Autowired private MemberRepository memberRepository;

    @MockitoBean FakeAuthContext fakeAuthContext;

    @Nested
    class 프로필을_수정할_때 {

        @BeforeEach
        void setUp() {
            Member member =
                    Member.createMember(
                            "testEmail",
                            "oldClokeyId",
                            "oldNickname",
                            OauthInfo.createOauthInfo("testOauthId", OauthProvider.KAKAO),
                            MemberStatus.ACTIVE,
                            RegisterStatus.REGISTERED,
                            Visibility.PRIVATE);

            member.updateProfile(
                    "oldNickname",
                    "oldClokeyId",
                    "oldProfileUrl",
                    "oldBackUrl",
                    "oldBio",
                    Visibility.PRIVATE);

            memberRepository.save(member);
            given(fakeAuthContext.getCurrentMember()).willReturn(member);
        }

        @Test
        void 유효한_요청이면_프로필을_수정한다() {
            // given
            ProfileUpdateRequest request =
                    new ProfileUpdateRequest(
                            "newNickname",
                            "newClokeyId",
                            "newBio",
                            Visibility.PUBLIC,
                            "https://img.example.com/profile.jpg",
                            "https://img.example.com/back.jpg");

            // when
            memberService.updateProfile(request);

            // then
            Member found = memberRepository.findById(1L).orElseThrow();
            assertThat(found)
                    .extracting(
                            "nickname",
                            "clokeyId",
                            "bio",
                            "profileImageUrl",
                            "profileBackImageUrl",
                            "visibility")
                    .containsExactly(
                            "newNickname",
                            "newClokeyId",
                            "newBio",
                            "https://img.example.com/profile.jpg",
                            "https://img.example.com/back.jpg",
                            Visibility.PUBLIC);
        }

        @Test
        void 이미지_URL이_null_또는_공백이면_삭제된다() {
            // given
            ProfileUpdateRequest request =
                    new ProfileUpdateRequest(
                            "testNickname",
                            "testClokeyId",
                            "testBio",
                            Visibility.PRIVATE,
                            null,
                            " ");

            // when
            memberService.updateProfile(request);

            // then
            Member found = memberRepository.findById(1L).orElseThrow();
            assertThat(found)
                    .extracting(
                            "nickname",
                            "clokeyId",
                            "bio",
                            "visibility",
                            "profileImageUrl",
                            "profileBackImageUrl")
                    .containsExactly(
                            "testNickname",
                            "testClokeyId",
                            "testBio",
                            Visibility.PRIVATE,
                            null,
                            null);
        }

        @Test
        void 밴된_회원이_PUBLIC으로_변경하려면_예외가_발생한다() {
            // given
            Member current = fakeAuthContext.getCurrentMember();
            current.updateMemberStatus(MemberStatus.BANNED);
            memberRepository.save(current);

            ProfileUpdateRequest request =
                    new ProfileUpdateRequest(
                            "testNickname",
                            "testClokeyId",
                            "testBio",
                            Visibility.PUBLIC,
                            "profile.jpg",
                            "back.jpg");

            // when & then
            assertThatThrownBy(() -> memberService.updateProfile(request))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(MemberErrorCode.BANNED_MEMBER_TO_PUBLIC.getMessage());
        }
    }

    @Nested
    class 아이디_중복_확인할_때 {

        @BeforeEach
        void setUp() {
            Member member =
                    Member.createMember(
                            "testEmail",
                            "testClokeyId",
                            "testNickname",
                            OauthInfo.createOauthInfo("testOauthId", OauthProvider.KAKAO),
                            MemberStatus.ACTIVE,
                            RegisterStatus.REGISTERED,
                            Visibility.PRIVATE);

            memberRepository.save(member);
            given(fakeAuthContext.getCurrentMember()).willReturn(member);
        }

        private Member otherMember(String clokeyId) {
            Member m =
                    Member.createMember(
                            "testEmail",
                            clokeyId,
                            "testNickname",
                            OauthInfo.createOauthInfo("testOauthId", OauthProvider.KAKAO),
                            MemberStatus.ACTIVE,
                            RegisterStatus.REGISTERED,
                            Visibility.PRIVATE);
            return memberRepository.save(m);
        }

        @Test
        void 내_아이디와_같은_ID를_요청하면_성공한다() {
            // when& then
            assertThatCode(() -> memberService.checkDuplicateClokeyId("testClokeyId"))
                    .doesNotThrowAnyException();
        }

        @Test
        void 다른_사람이_쓰는_ID를_요청하면_예외가_발생한다() {
            otherMember("usedId");

            // when & then
            assertThatThrownBy(() -> memberService.checkDuplicateClokeyId("usedId"))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_CLOKEY_ID.getMessage());
        }

        @Test
        void 다른_사람이_쓰지_않는_ID를_요청하면_성공한다() {
            // when
            memberService.checkDuplicateClokeyId("availableId");

            // then
            assertThat(memberRepository.existsByClokeyId("availableId")).isFalse();
        }

        @Test
        void 클로키아이디가_null이면_예외가_발생한다() {
            assertThatThrownBy(() -> memberService.checkDuplicateClokeyId(null))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(MemberErrorCode.INVALID_CLOKEY_ID.getMessage());
        }

        @Test
        void 클로키아이디가_공백이면_예외가_발생한다() {
            assertThatThrownBy(() -> memberService.checkDuplicateClokeyId(" "))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(MemberErrorCode.INVALID_CLOKEY_ID.getMessage());
        }
    }
}

package org.clokey.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import org.clokey.IntegrationTest;
import org.clokey.domain.member.dto.request.ProfileRequest;
import org.clokey.domain.member.dto.response.ProfileResponse;
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
import org.junit.jupiter.api.Assertions;
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

        private Member member;

        @BeforeEach
        void setUp() {
            // 기본 멤버 생성
            member =
                    Member.createMember(
                            "testEmail",
                            "oldClokeyId",
                            "oldNickname",
                            OauthInfo.createOauthInfo("testOauthId", OauthProvider.KAKAO),
                            MemberStatus.ACTIVE,
                            RegisterStatus.REGISTERED, // 필요시 미등록 상태로 바꿔 검증 가능
                            Visibility.PRIVATE);

            // 기존 프로필 값 세팅 (이미지 fallback 검증용)
            member.profileUpdate(
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
            ProfileRequest request =
                    new ProfileRequest(
                            "newNickname",
                            "newClokeyId",
                            "newBio",
                            Visibility.PUBLIC,
                            "https://img.example.com/profile.jpg",
                            "https://img.example.com/back.jpg");

            // when
            ProfileResponse response = memberService.updateProfile(request);

            // then
            Assertions.assertAll(
                    () -> assertThat(response.id()).isEqualTo(member.getId()),
                    () -> assertThat(response.nickname()).isEqualTo("newNickname"),
                    () -> assertThat(response.clokeyId()).isEqualTo("newClokeyId"),
                    () -> assertThat(response.bio()).isEqualTo("newBio"),
                    () ->
                            assertThat(response.profileImageUrl())
                                    .isEqualTo("https://img.example.com/profile.jpg"),
                    () ->
                            assertThat(response.profileBackImageUrl())
                                    .isEqualTo("https://img.example.com/back.jpg"),
                    () -> assertThat(response.visibility()).isEqualTo(Visibility.PUBLIC),
                    () -> assertThat(response.updatedAt()).isNotNull());

            // 영속 엔티티 재확인
            Member found = memberRepository.findById(member.getId()).orElseThrow();
            Assertions.assertAll(
                    () -> assertThat(found.getNickname()).isEqualTo("newNickname"),
                    () -> assertThat(found.getClokeyId()).isEqualTo("newClokeyId"),
                    () -> assertThat(found.getBio()).isEqualTo("newBio"),
                    () ->
                            assertThat(found.getProfileImageUrl())
                                    .isEqualTo("https://img.example.com/profile.jpg"),
                    () ->
                            assertThat(found.getProfileBackImageUrl())
                                    .isEqualTo("https://img.example.com/back.jpg"),
                    () -> assertThat(found.getVisibility()).isEqualTo(Visibility.PUBLIC),
                    () -> assertThat(found.getUpdatedAt()).isNotNull());
        }

        @Test
        void 이미지_URL이_비어있으면_기존값을_유지한다() {
            // given (null/공백이면 기존 값 유지)
            ProfileRequest request =
                    new ProfileRequest(
                            "keepNickname",
                            "keepClokeyId",
                            "keepBio",
                            Visibility.PRIVATE,
                            null, // profileImageUrl 비움
                            " " // profileBackImageUrl 공백
                            );

            // when
            ProfileResponse response = memberService.updateProfile(request);

            // then (응답/영속 둘 다 기존 값 유지 확인)
            Member found = memberRepository.findById(member.getId()).orElseThrow();
            Assertions.assertAll(
                    () -> assertThat(response.profileImageUrl()).isEqualTo("oldProfileUrl"),
                    () -> assertThat(response.profileBackImageUrl()).isEqualTo("oldBackUrl"),
                    () -> assertThat(found.getProfileImageUrl()).isEqualTo("oldProfileUrl"),
                    () -> assertThat(found.getProfileBackImageUrl()).isEqualTo("oldBackUrl"));
        }

        @Test
        void 밴된_회원이_PUBLIC으로_변경하려면_예외가_발생한다() {
            // given
            member.updateMemberStatus(MemberStatus.BANNED); // 실제 메서드명에 맞게 조정
            memberRepository.save(member);

            ProfileRequest request =
                    new ProfileRequest(
                            "anyNick",
                            "anyId",
                            "anyBio",
                            Visibility.PUBLIC,
                            "profile.jpg",
                            "back.jpg");

            // when & then
            assertThatThrownBy(() -> memberService.updateProfile(request))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(MemberErrorCode.BANNED_MEMBER_TO_PUBLIC.getMessage());
        }
    }
}

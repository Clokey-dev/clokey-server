package org.clokey.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import java.awt.*;
import java.util.List;
import org.clokey.IntegrationTest;
import org.clokey.TransactionUtil;
import org.clokey.domain.member.dto.request.DuplicatedIdCheckRequest;
import org.clokey.domain.member.dto.request.ProfileUpdateRequest;
import org.clokey.domain.member.exception.MemberErrorCode;
import org.clokey.domain.member.repository.FollowRepository;
import org.clokey.domain.member.repository.FollowRequestRepository;
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.util.MemberUtil;
import org.clokey.member.entity.Follow;
import org.clokey.member.entity.FollowRequest;
import org.clokey.member.entity.Member;
import org.clokey.member.entity.OauthInfo;
import org.clokey.member.enums.MemberStatus;
import org.clokey.member.enums.OauthProvider;
import org.clokey.member.enums.Visibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

class MemberServiceTest extends IntegrationTest {

    @Autowired private MemberService memberService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private FollowRepository followRepository;
    @Autowired private FollowRequestRepository followRequestRepository;

    @Autowired private TransactionUtil transactionUtil;
    @MockitoBean private MemberUtil memberUtil;

    @Nested
    class 프로필을_수정할_때 {

        @BeforeEach
        void setUp() {
            Member member =
                    Member.createMember(
                            "testEmail",
                            "oldClokeyId",
                            "oldNickname",
                            OauthInfo.createOauthInfo("testOauthId", OauthProvider.KAKAO));

            memberRepository.save(member);
            given(memberUtil.getCurrentMember()).willReturn(member);
        }

        @Test
        @Transactional
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
            assertThat(memberRepository.findById(1L).orElseThrow())
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
        void 밴된_회원이_PUBLIC으로_변경하려면_예외가_발생한다() {
            // given
            Member current = memberUtil.getCurrentMember();
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
    class 아이디_중복을_확인할_때 {

        @BeforeEach
        void setUp() {
            Member member1 =
                    Member.createMember(
                            "testEmail1",
                            "testClokeyId1",
                            "testNickname1",
                            OauthInfo.createOauthInfo("testOauthId", OauthProvider.KAKAO));
            Member member2 =
                    Member.createMember(
                            "testEmail2",
                            "testClokeyId2",
                            "testNickname2",
                            OauthInfo.createOauthInfo("testOauthId", OauthProvider.KAKAO));

            memberRepository.saveAll(List.of(member1, member2));
            given(memberUtil.getCurrentMember()).willReturn(member1);
        }

        @ParameterizedTest
        @ValueSource(strings = {"testClokeyId1", "distinctId1", "distinctId2"})
        void 현재_ID_또는_중복되지_않는_ID를_입력하면_false를_반환한다(String clokeyId) {
            // given
            DuplicatedIdCheckRequest request = new DuplicatedIdCheckRequest(clokeyId);

            // when& then
            assertThat(memberService.checkDuplicateClokeyId(request).duplicated()).isFalse();
        }

        @Test
        void 중복되는_ID를_입력한_경우_true를_반환한다() {
            // given
            DuplicatedIdCheckRequest request = new DuplicatedIdCheckRequest("testClokeyId2");

            // when& then
            assertThat(memberService.checkDuplicateClokeyId(request).duplicated()).isTrue();
        }
    }

    @Nested
    class 팔로우_언팔로우_할_때 {

        Member me;
        Member publicUser;
        Member privateUser;

        @BeforeEach
        void setUp() {
            me =
                    Member.createMember(
                            "me@test.com",
                            "meId",
                            "me",
                            OauthInfo.createOauthInfo("meOauth", OauthProvider.KAKAO));
            publicUser =
                    Member.createMember(
                            "public@test.com",
                            "publicId",
                            "pub",
                            OauthInfo.createOauthInfo("pubOauth", OauthProvider.KAKAO));

            privateUser =
                    Member.createMember(
                            "private@test.com",
                            "privateId",
                            "pri",
                            OauthInfo.createOauthInfo("priOauth", OauthProvider.KAKAO));
            privateUser.changeVisibility();

            memberRepository.saveAll(List.of(me, publicUser, privateUser));
            given(memberUtil.getCurrentMember()).willReturn(me);
        }

        @Test
        void 공개계정을_팔로우하면_팔로우가_추가된다() {
            // when
            memberService.follow(2L);

            // then
            assertThat(followRepository.existsByFollowFrom_IdAndFollowTo_Id(1L, 2L)).isTrue();
        }

        @Test
        void 공개계정을_이미팔로우중이면_취소된다() {
            // given
            followRepository.save(Follow.createFollow(me, publicUser));

            // when
            memberService.follow(2L);

            // then
            assertThat(followRepository.existsByFollowFrom_IdAndFollowTo_Id(1L, 2L)).isFalse();
        }

        @Test
        void 비공개계정을_팔로우하면_팔로우요청이_추가된다() {
            // when
            memberService.follow(3L);

            // then
            assertThat(followRequestRepository.existsByFollowFrom_IdAndFollowTo_Id(1L, 3L))
                    .isTrue();
        }

        @Test
        void 비공개계정을_이미요청중이면_취소된다() {
            // given
            followRequestRepository.save(FollowRequest.createFollowRequest(me, privateUser));

            // when
            memberService.follow(3L);

            // then
            assertThat(followRequestRepository.existsByFollowFrom_IdAndFollowTo_Id(1L, 3L))
                    .isFalse();
        }

        @Test
        void 비공개계정을_이미팔로우중이면_취소된다() {
            // given
            followRepository.save(Follow.createFollow(me, privateUser));

            // when
            memberService.follow(3L);

            // then
            assertThat(followRepository.existsByFollowFrom_IdAndFollowTo_Id(1L, 3L)).isFalse();
        }

        @Test
        void 자기자신을_팔로우하면_예외가_발생한다() {
            // when & then
            assertThatThrownBy(() -> memberService.follow(1L))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(MemberErrorCode.CANNOT_FOLLOW_MYSELF.getMessage());
        }
    }
}

package org.clokey.domain.member.service;

import static org.springframework.util.StringUtils.hasText;

import lombok.RequiredArgsConstructor;
import org.clokey.domain.member.dto.request.ProfileRequest;
import org.clokey.domain.member.dto.response.ProfileResponse;
import org.clokey.domain.member.exception.MemberErrorCode;
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.FakeAuthContext;
import org.clokey.member.entity.Member;
import org.clokey.member.enums.MemberStatus;
import org.clokey.member.enums.RegisterStatus;
import org.clokey.member.enums.Visibility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final FakeAuthContext fakeAuthContext;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public ProfileResponse updateProfile(ProfileRequest request) {
        // 사용자 확인
        final Member member = fakeAuthContext.getCurrentMember();

        // 사용자 상태 체크 및 유효성 검증
        validateVisualizeBannedMember(member, request);

        String profileImageUrl =
                hasText(request.profileImageUrl())
                        ? request.profileImageUrl()
                        : member.getProfileImageUrl();

        String profileBackImageUrl =
                hasText(request.profileBackImageUrl())
                        ? request.profileBackImageUrl()
                        : member.getProfileBackImageUrl();

        // 프로필 업데이트
        member.profileUpdate(
                request.nickname(),
                request.clokeyId(),
                profileImageUrl,
                profileBackImageUrl,
                request.bio(),
                request.visibility());

        // 등록 상태 업데이트 (약관 동의가 완료된 경우)
        if (member.getRegisterStatus() != RegisterStatus.REGISTERED) {
            member.updateRegisterStatus(RegisterStatus.REGISTERED);
        }

        // 저장
        Member updatedMember = memberRepository.save(member);

        // Elasticsearch 동기화였던 부분 삭제

        // 응답 DTO 반환
        return ProfileResponse.from(updatedMember);
    }

    private void validateVisualizeBannedMember(Member member, ProfileRequest request) {
        boolean banned = member.getMemberStatus().equals(MemberStatus.BANNED);
        boolean changeToPublic = request.visibility().equals(Visibility.PUBLIC);
        if (banned && changeToPublic) {
            throw new BaseCustomException(MemberErrorCode.BANNED_MEMBER_TO_PUBLIC);
        }
    }
}

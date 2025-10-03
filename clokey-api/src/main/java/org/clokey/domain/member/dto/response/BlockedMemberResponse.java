package org.clokey.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.clokey.member.entity.Member;

public record BlockedMemberResponse(
        @Schema(description = "차단된 멤버의 ID", example = "1") Long id,
        @Schema(description = "차단된 멤버의 Codive ID", example = "Codive123") String codiveId,
        @Schema(
                        description = "차단됨 멤버의 프로필 이미지 URL",
                        example = "https://example.com/profile/john.jpg")
                String profileImageUrl) {
    public static BlockedMemberResponse from(Member blockedMember) {
        return new BlockedMemberResponse(
                blockedMember.getId(),
                blockedMember.getClokeyId(),
                blockedMember.getProfileImageUrl());
    }
}

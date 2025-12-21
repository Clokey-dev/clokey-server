package org.clokey.domain.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "좋아요 유저 조회 결과")
public record LikedMembersResponse(
        @Schema(description = "유저 미리보기 목록") List<LikedMemberPreview> memberPreviews,
        @Schema(description = "마지막 페이지 여부", example = "false") boolean isLast) {

    @Schema(description = "유저 미리보기 DTO")
    public record LikedMemberPreview(
            @Schema(description = "유저 ID", example = "30") Long id,
            @Schema(description = "클로키 ID", example = "@Clokey_USER1") String codiveId,
            @Schema(description = "프로필 이미지 URL") String imageUrl,
            @Schema(description = "닉네임") String nickname,
            @Schema(description = "팔로우 여부") boolean followStatus) {}
}

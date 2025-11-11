package org.clokey.domain.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record NewFollowerNotificationResponse(
        @Schema(description = "새 팔로우 알림의 content", example = "동엽님이 회원님의 옷장을 팔로우하기 시작했습니다.")
                String content,
        @Schema(
                        description = "팔로우한 사람의 profileImageUrl",
                        example = "https://example.com/profile/john.jpg")
                String profileImageUrl,
        @Schema(description = "팔로우한 사람의 Member ID", example = "1L") Long memberId,
        @Schema(description = "팔로우한 사람의 Codive ID", example = "codive123") String codiveId) {}

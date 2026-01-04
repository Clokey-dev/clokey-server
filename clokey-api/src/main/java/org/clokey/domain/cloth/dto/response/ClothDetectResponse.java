package org.clokey.domain.cloth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.clokey.cloth.enums.Season;

@Schema(description = "사진에서 옷 탐지 응답")
public record ClothDetectResponse(@Schema(description = "옷 정보 목록") List<Payload> payloads) {

    public static ClothDetectResponse of(List<Payload> payloads) {
        return new ClothDetectResponse(payloads);
    }

    @Schema(name = "ClothDetectResponsePayload", description = "옷 정보")
    public record Payload(
            @Schema(description = "새로운 옷 사진 URL", example = "https://example.com/cloth.jpg")
                    String clothImageUrl,
            @Schema(description = "계절", example = "SPRING") Season season,
            @Schema(description = "카테고리 ID", example = "1") Long categoryId,
            @Schema(description = "카테고리 이름", example = "상의") String categoryName) {}
}

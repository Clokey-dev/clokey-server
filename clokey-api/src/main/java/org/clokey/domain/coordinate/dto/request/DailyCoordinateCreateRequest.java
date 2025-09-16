package org.clokey.domain.coordinate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.clokey.global.annotation.UniqueList;

public record DailyCoordinateCreateRequest(
        @NotBlank(message = "오늘의 코디의 사진은 비워둘 수 없습니다.")
                @Schema(description = "오늘의 코디의 사진", example = "https://example.jpg")
                String coordinateImageUrl,
        @NotBlank(message = "옷들의 ID는 비워둘 수 없습니다.")
                @UniqueList(message = "중복된 옷을 등록할 수 없습니다.")
                @Schema(description = "옷 ID들", example = "[1,2,3,4]")
                List<Long> clothIds) {}

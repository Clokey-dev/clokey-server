package org.clokey.domain.cloth.dto.response;

import java.util.List;
import org.clokey.cloth.enums.Season;

public record ClothInfoExtractAiResponseDTO(List<Payload> payloads) {

    public record Payload(
            String clothImageUrl, Season season, Long categoryId, String categoryName) {}
}

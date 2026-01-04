package org.clokey.domain.cloth.dto.response;

import java.util.List;

public record HistoryStyleInferenceAiResponseDTO(
        Long situationId, String situationName, List<StylePayload> styles) {

    public record StylePayload(Long styleId, String styleName) {}
}

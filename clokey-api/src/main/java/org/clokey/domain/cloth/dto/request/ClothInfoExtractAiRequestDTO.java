package org.clokey.domain.cloth.dto.request;

import java.util.List;

public record ClothInfoExtractAiRequestDTO(
        List<String> clothImageUrls, List<String> presignedUrls) {}

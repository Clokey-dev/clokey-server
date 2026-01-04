package org.clokey.domain.cloth.dto.request;

import java.util.List;

public record ClothDetectAiRequestDTO(String imageUrl, List<String> presignedUrls) {}

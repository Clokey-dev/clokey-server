package org.clokey.domain.cloth.service;

import org.clokey.domain.cloth.dto.request.ClothImagesUploadRequest;
import org.clokey.domain.cloth.dto.response.ClothImagesPresignedUrlResponse;

public interface ClothAiService {

    ClothImagesPresignedUrlResponse getClothUploadPresignedUrls(ClothImagesUploadRequest request);
}

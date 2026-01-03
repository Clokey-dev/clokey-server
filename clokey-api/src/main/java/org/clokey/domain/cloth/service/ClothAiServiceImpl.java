package org.clokey.domain.cloth.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.clokey.domain.cloth.dto.request.ClothImagesUploadRequest;
import org.clokey.domain.cloth.dto.response.ClothImagesPresignedUrlResponse;
import org.clokey.enums.ImageType;
import org.clokey.global.util.MemberUtil;
import org.clokey.member.entity.Member;
import org.clokey.util.S3Util;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClothAiServiceImpl implements ClothAiService {

    private final MemberUtil memberUtil;
    private final S3Util s3Util;

    @Override
    public ClothImagesPresignedUrlResponse getClothUploadPresignedUrls(
            ClothImagesUploadRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();

        // 중요 :  md5 해시로 변조 확인을 하기 때문에 들어온 순서대로 반환해야함!!
        List<String> presignedUrls =
                request.payloads().stream()
                        .map(
                                req ->
                                        s3Util.createPresignedUrl(
                                                ImageType.CLOTH_IMAGE,
                                                currentMember.getId(),
                                                req.fileExtension(),
                                                req.md5Hashes()))
                        .toList();

        return ClothImagesPresignedUrlResponse.of(presignedUrls);
    }
}

package org.clokey.domain.cloth.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.clokey.domain.cloth.dto.request.ClothDetectAiRequestDTO;
import org.clokey.domain.cloth.dto.request.ClothDetectRequest;
import org.clokey.domain.cloth.dto.request.ClothImagesUploadRequest;
import org.clokey.domain.cloth.dto.request.ClothInfoExtractAiRequestDTO;
import org.clokey.domain.cloth.dto.request.ClothInfoExtractRequest;
import org.clokey.domain.cloth.dto.request.HistoryStyleInferenceAiRequestDTO;
import org.clokey.domain.cloth.dto.request.HistoryStyleInferenceRequest;
import org.clokey.domain.cloth.dto.response.ClothDetectAiResponseDTO;
import org.clokey.domain.cloth.dto.response.ClothDetectResponse;
import org.clokey.domain.cloth.dto.response.ClothImagesPresignedUrlResponse;
import org.clokey.domain.cloth.dto.response.ClothInfoExtractAiResponseDTO;
import org.clokey.domain.cloth.dto.response.ClothInfoExtractResponse;
import org.clokey.domain.cloth.dto.response.HistoryStyleInferenceAiResponseDTO;
import org.clokey.domain.cloth.dto.response.HistoryStyleInferenceResponse;
import org.clokey.domain.cloth.exception.ClothErrorCode;
import org.clokey.domain.history.exception.HistoryErrorCode;
import org.clokey.enums.FileExtension;
import org.clokey.enums.ImageType;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.util.MemberUtil;
import org.clokey.member.entity.Member;
import org.clokey.properties.WebClientProperties;
import org.clokey.util.S3Util;
import org.clokey.util.WebClientUtil;
import org.springframework.stereotype.Service;

// FIXME: 외부 API와 연동되는 부분으로 절대로 Transaction을 붙여서 DB Connection pool을 낭비하지 말 것 ! (현재는 필요한 부분에서
// Transaction Util을 사용하세요)
// FIXME: 현재는 Tomcat Thread Pool을 점유하고 있는 비효율적인 구조이기 때문에 나중에 비동기 처리를 통해 트래픽이 생길 경우 최적화가 필요합니다.
@Service
@RequiredArgsConstructor
public class ClothAiServiceImpl implements ClothAiService {

    private final MemberUtil memberUtil;
    private final S3Util s3Util;
    private final WebClientUtil webClientUtil;
    private final WebClientProperties webClientProperties;

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

    @Override
    public ClothInfoExtractResponse extractClothInfo(ClothInfoExtractRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final List<String> clothImageUrls = request.clothImageUrls();

        validateImageUrls(clothImageUrls);

        // AI Server에게 N개의 사진을 전처리한 후 업로드할 수 있는 presignedUrl을 넘겨줍니다.
        List<String> presignedUrls =
                createPresignedUrls(currentMember.getId(), clothImageUrls.size());

        ClothInfoExtractAiResponseDTO aiResponse =
                webClientUtil
                        .postToAiServer(
                                webClientProperties.clothInferencePath(),
                                new ClothInfoExtractAiRequestDTO(clothImageUrls, presignedUrls),
                                ClothInfoExtractAiResponseDTO.class)
                        .block();

        List<ClothInfoExtractResponse.Payload> payloads =
                aiResponse.payloads().stream()
                        .map(
                                payload ->
                                        new ClothInfoExtractResponse.Payload(
                                                payload.clothImageUrl(),
                                                payload.season(),
                                                payload.categoryId(),
                                                payload.categoryName()))
                        .toList();

        return ClothInfoExtractResponse.of(payloads);
    }

    @Override
    public HistoryStyleInferenceResponse inferHistoryStyle(HistoryStyleInferenceRequest request) {
        final String historyImageUrl = request.historyImageUrl();

        validateImageUrl(historyImageUrl);

        HistoryStyleInferenceAiResponseDTO aiResponse =
                webClientUtil
                        .postToAiServer(
                                webClientProperties.styleInferencePath(),
                                new HistoryStyleInferenceAiRequestDTO(historyImageUrl),
                                HistoryStyleInferenceAiResponseDTO.class)
                        .block();

        List<HistoryStyleInferenceResponse.StylePayload> styles =
                aiResponse.styles().stream()
                        .map(
                                style ->
                                        new HistoryStyleInferenceResponse.StylePayload(
                                                style.styleId(), style.styleName()))
                        .toList();

        return HistoryStyleInferenceResponse.of(
                aiResponse.situationId(), aiResponse.situationName(), styles);
    }

    @Override
    public ClothDetectResponse detectClothes(ClothDetectRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final String imageUrl = request.imageUrl();

        validateImageUrl(imageUrl);

        List<String> presignedUrls = createPresignedUrls(currentMember.getId(), 10);

        ClothDetectAiResponseDTO aiResponse =
                webClientUtil
                        .postToAiServer(
                                webClientProperties.clothDetectPath(),
                                new ClothDetectAiRequestDTO(imageUrl, presignedUrls),
                                ClothDetectAiResponseDTO.class)
                        .block();

        List<ClothDetectResponse.Payload> payloads =
                aiResponse.payloads().stream()
                        .map(
                                payload ->
                                        new ClothDetectResponse.Payload(
                                                payload.clothImageUrl(),
                                                payload.season(),
                                                payload.categoryId(),
                                                payload.categoryName()))
                        .toList();

        return ClothDetectResponse.of(payloads);
    }

    private void validateImageUrls(List<String> imageUrls) {
        if (!s3Util.doAllFilesExistByUrls(imageUrls)) {
            throw new BaseCustomException(ClothErrorCode.ClOTH_NOT_FOUND);
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (!s3Util.doesFileExistByUrl(imageUrl)) {
            throw new BaseCustomException(HistoryErrorCode.HISTORY_IMAGE_NOT_FOUND);
        }
    }

    // TODO : 현재 AI 서버와 비동기 처리와 더불어 양방향 통신을 고려하지 않고 있습니다. 따라서, MD5 해시를 통한 무결성 검증이 불가능하며, JPEG로 고정할
    // 것을 요청해야합니다.
    private List<String> createPresignedUrls(Long memberId, int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(
                        i ->
                                s3Util.createPresignedUrlWithoutMd5(
                                        ImageType.CLOTH_IMAGE, memberId, FileExtension.JPEG))
                .toList();
    }
}

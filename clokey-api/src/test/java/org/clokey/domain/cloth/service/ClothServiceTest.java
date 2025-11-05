package org.clokey.domain.cloth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.clokey.IntegrationTest;
import org.clokey.category.entity.Category;
import org.clokey.cloth.entity.Cloth;
import org.clokey.cloth.enums.Season;
import org.clokey.domain.category.exception.CategoryErrorCode;
import org.clokey.domain.category.repository.CategoryRepository;
import org.clokey.domain.cloth.dto.request.ClothCreateRequest;
import org.clokey.domain.cloth.dto.request.ClothCreateRequests;
import org.clokey.domain.cloth.dto.response.ClothRecommendListResponse;
import org.clokey.domain.cloth.repository.ClothRepository;
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.util.MemberUtil;
import org.clokey.member.entity.Member;
import org.clokey.member.entity.OauthInfo;
import org.clokey.member.enums.OauthProvider;
import org.clokey.response.SliceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ClothServiceTest extends IntegrationTest {

    @Autowired private ClothService clothService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ClothRepository clothRepository;
    @Autowired private CategoryRepository categoryRepository;

    @MockitoBean private MemberUtil memberUtil;

    @Nested
    class 옷을_생성할_때 {

        @BeforeEach
        void setUp() {
            Member member =
                    Member.createMember(
                            "testEmail",
                            "testClokeyId",
                            "testNickName",
                            OauthInfo.createOauthInfo("testOauthId", OauthProvider.KAKAO));

            memberRepository.save(member);
            given(memberUtil.getCurrentMember()).willReturn(member);

            Category category = Category.createCategory("testCategory", null);
            categoryRepository.save(category);
        }

        @Test
        void 유효한_요청이면_옷을_생성한다() {
            // given
            ClothCreateRequests request =
                    new ClothCreateRequests(
                            List.of(
                                    new ClothCreateRequest("testClothImageUrl1", 1L, Season.SPRING),
                                    new ClothCreateRequest(
                                            "testClothImageUrl2", 1L, Season.SPRING)));

            // when
            clothService.createClothes(request);

            // then
            Assertions.assertAll(
                    () ->
                            assertThat(clothRepository.findById(1L).orElseThrow())
                                    .extracting("clothImageUrl", "category.id", "member.id")
                                    .containsExactly("testClothImageUrl1", 1L, 1L),
                    () ->
                            assertThat(clothRepository.findById(2L).orElseThrow())
                                    .extracting("clothImageUrl", "category.id", "member.id")
                                    .containsExactly("testClothImageUrl2", 1L, 1L));
        }

        @Test
        void 카테고리가_존재하지_않을_경우_예외가_발생한다() {
            // given
            ClothCreateRequests request =
                    new ClothCreateRequests(
                            List.of(
                                    new ClothCreateRequest("testClothImageUrl1", 1L, Season.SPRING),
                                    new ClothCreateRequest(
                                            "testClothImageUrl2", 999L, Season.SPRING)));

            // when & then
            assertThatThrownBy(() -> clothService.createClothes(request))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(CategoryErrorCode.CATEGORY_IN_BULK_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class 카테고리별_계절_옷을_추천할_때 {

        @BeforeEach
        void setUp() {
            Member member =
                    Member.createMember(
                            "testEmail1",
                            "testClokeyId1",
                            "testNickName1",
                            OauthInfo.createOauthInfo("testOauthId1", OauthProvider.KAKAO));
            memberRepository.save(member);
            given(memberUtil.getCurrentMember()).willReturn(member);

            Category category1 = Category.createCategory("testCategory1", null);
            Category category2 = Category.createCategory("testCategory2", null);
            categoryRepository.saveAll(List.of(category1, category2));

            Cloth cloth1 = Cloth.createCloth("testImageUrl1", category1, Season.SUMMER, member);
            Cloth cloth2 = Cloth.createCloth("testImageUrl2", category1, Season.SPRING, member);
            Cloth cloth3 = Cloth.createCloth("testImageUrl3", category1, Season.SPRING, member);
            Cloth cloth4 = Cloth.createCloth("testImageUrl4", category1, Season.SUMMER, member);
            Cloth cloth5 = Cloth.createCloth("testImageUrl5", category1, Season.WINTER, member);
            Cloth cloth6 = Cloth.createCloth("testImageUrl6", category1, Season.FALL, member);

            clothRepository.saveAll(List.of(cloth1, cloth2, cloth3, cloth4, cloth5, cloth6));
        }

        @Test
        void 유효한_요창이면_가까운_계절순으로_옷을_반환한다() {
            // when
            SliceResponse<ClothRecommendListResponse> response =
                    clothService.recommendCategoryClothes(null, 6, 1L, Season.SPRING);

            // then
            assertThat(response.content())
                    .extracting("clothId")
                    .containsExactly(2L, 3L, 1L, 4L, 5L, 6L);
        }

        @Test
        void lastClothId를_입력하면_다음_Cloth_부터_조회한다() {
            // when
            SliceResponse<ClothRecommendListResponse> response =
                    clothService.recommendCategoryClothes(3L, 4, 1L, Season.SPRING);

            // then
            assertThat(response.content()).extracting("clothId").containsExactly(1L, 4L, 5L, 6L);
        }

        @Test
        void 옷을_모두_조회한_경우_빈_리스트를_반환한다() {
            // when
            SliceResponse<ClothRecommendListResponse> response =
                    clothService.recommendCategoryClothes(6L, 4, 1L, Season.SPRING);

            // then
            assertThat(response.content()).isEmpty();
        }

        @Test
        void 카테고리_옷이_존재하지_않는_경우_빈_리스트를_반환한다() {
            // when
            SliceResponse<ClothRecommendListResponse> response =
                    clothService.recommendCategoryClothes(null, 4, 2L, Season.SPRING);

            // then
            assertThat(response.content()).isEmpty();
        }

        @Test
        void 마지막_페이지인_경우_isLast를_true로_반환한다() {
            // when
            SliceResponse<ClothRecommendListResponse> response =
                    clothService.recommendCategoryClothes(null, 6, 1L, Season.SPRING);

            // then
            assertThat(response.isLast()).isTrue();
        }

        @Test
        void 마지막_페이지가_아닌_경우_isLast를_false로_반환한다() {
            // when
            SliceResponse<ClothRecommendListResponse> response =
                    clothService.recommendCategoryClothes(null, 5, 1L, Season.SPRING);

            // then
            assertThat(response.isLast()).isFalse();
        }
    }
}

package org.clokey.domain.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import org.clokey.IntegrationTest;
import org.clokey.RedisCleaner;
import org.clokey.category.entity.Category;
import org.clokey.cloth.entity.Cloth;
import org.clokey.cloth.enums.Season;
import org.clokey.domain.category.exception.CategoryErrorCode;
import org.clokey.domain.category.repository.CategoryRepository;
import org.clokey.domain.cloth.dto.response.ClothListResponse;
import org.clokey.domain.cloth.repository.ClothRepository;
import org.clokey.domain.history.repository.HashtagRepository;
import org.clokey.domain.history.repository.HistoryClothTagRepository;
import org.clokey.domain.history.repository.HistoryHashtagRepository;
import org.clokey.domain.history.repository.HistoryImageRepository;
import org.clokey.domain.history.repository.HistoryRepository;
import org.clokey.domain.history.repository.HistoryStyleRepository;
import org.clokey.domain.history.repository.SituationRepository;
import org.clokey.domain.history.repository.StyleRepository;
import org.clokey.domain.like.repository.MemberLikeRepository;
import org.clokey.domain.member.repository.BlockRepository;
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.domain.search.dto.response.SearchedHistoryResponse;
import org.clokey.domain.search.dto.response.SearchedMemberResponse;
import org.clokey.domain.search.dto.response.SearchingRecommendResponse;
import org.clokey.domain.search.enums.HistorySearchSortType;
import org.clokey.domain.search.repository.SearchRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.paging.SortDirection;
import org.clokey.global.util.MemberUtil;
import org.clokey.history.entity.Hashtag;
import org.clokey.history.entity.History;
import org.clokey.history.entity.HistoryClothTag;
import org.clokey.history.entity.HistoryHashtag;
import org.clokey.history.entity.HistoryImage;
import org.clokey.history.entity.HistoryStyle;
import org.clokey.history.entity.Situation;
import org.clokey.history.entity.Style;
import org.clokey.like.entity.MemberLike;
import org.clokey.member.entity.Block;
import org.clokey.member.entity.Member;
import org.clokey.member.entity.OauthInfo;
import org.clokey.member.enums.OauthProvider;
import org.clokey.response.SliceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class SearchServiceTest extends IntegrationTest {

    @Autowired private SearchService searchService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ClothRepository clothRepository;
    @Autowired private BlockRepository blockRepository;
    @Autowired private HistoryRepository historyRepository;
    @Autowired private SituationRepository situationRepository;
    @Autowired private StyleRepository styleRepository;
    @Autowired private HistoryStyleRepository historyStyleRepository;
    @Autowired private HistoryHashtagRepository historyHashtagRepository;
    @Autowired private HistoryImageRepository historyImageRepository;
    @Autowired private HistoryClothTagRepository historyClothTagRepository;
    @Autowired private HashtagRepository hashtagRepository;
    @Autowired private MemberLikeRepository memberLikeRepository;
    @Autowired private RedisCleaner redisCleaner;
    @Autowired private RedisTemplate<String, Object> redisTemplate;

    @MockitoBean private MemberUtil memberUtil;
    @MockitoBean private SearchRepository searchRepository;

    private Member currentMember;

    @BeforeEach
    void setUp() {
        redisCleaner.flushAll();
        currentMember =
                memberRepository.save(
                        Member.createMember(
                                "current@test.com",
                                "currentNick",
                                OauthInfo.createOauthInfo("current-oauth", OauthProvider.KAKAO)));
        given(memberUtil.getCurrentMember()).willReturn(currentMember);
    }

    @Nested
    class 옷을_검색할_때 {

        private Category parentCategory;
        private Category category;

        @BeforeEach
        void setUp() {
            parentCategory = categoryRepository.save(Category.createCategory("상의", null));
            category = categoryRepository.save(Category.createCategory("후드티", parentCategory));
        }

        private Cloth createCloth(String name, String brand, Season season) {
            return clothRepository.save(
                    Cloth.createCloth(
                            "testImageUrl",
                            null,
                            name,
                            brand,
                            List.of(season),
                            category,
                            currentMember));
        }

        @Test
        void 이름_키워드로_검색하면_해당_옷을_반환한다() {
            Cloth cloth = createCloth("파란색 후드티", "나이키", Season.SPRING);
            createCloth("빨간색 셔츠", "아디다스", Season.SPRING);

            SliceResponse<ClothListResponse> response =
                    searchService.searchClothes("파란색", null, 10, SortDirection.DESC, null, null);

            assertThat(response.content()).extracting("clothId").containsExactly(cloth.getId());
        }

        @Test
        void 브랜드_키워드로_검색하면_해당_옷을_반환한다() {
            Cloth cloth = createCloth("후드티", "나이키", Season.SPRING);
            createCloth("셔츠", "아디다스", Season.SPRING);

            SliceResponse<ClothListResponse> response =
                    searchService.searchClothes("나이키", null, 10, SortDirection.DESC, null, null);

            assertThat(response.content()).extracting("clothId").containsExactly(cloth.getId());
        }

        @Test
        void 상위_카테고리로_검색하면_하위_카테고리_옷들을_모두_반환한다() {
            Cloth cloth = createCloth("후드티", "나이키", Season.SPRING);

            SliceResponse<ClothListResponse> response =
                    searchService.searchClothes(
                            "후드티", null, 10, SortDirection.DESC, parentCategory.getId(), null);

            assertThat(response.content()).extracting("clothId").containsExactly(cloth.getId());
        }

        @Test
        void 계절_조건으로_검색하면_해당_계절의_옷만_반환한다() {
            createCloth("여름옷", "나이키", Season.SUMMER);
            Cloth springCloth = createCloth("여름옷", "나이키", Season.SPRING);

            SliceResponse<ClothListResponse> response =
                    searchService.searchClothes(
                            "여름옷", null, 10, SortDirection.DESC, null, List.of(Season.SPRING));

            assertThat(response.content())
                    .extracting("clothId")
                    .containsExactly(springCloth.getId());
        }

        @Test
        void 존재하지_않는_카테고리로_검색하면_예외가_발생한다() {
            org.assertj.core.api.Assertions.assertThatThrownBy(
                            () ->
                                    searchService.searchClothes(
                                            "키워드", null, 10, SortDirection.DESC, 999L, null))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(CategoryErrorCode.CATEGORY_NOT_FOUND.getMessage());
        }

        @Test
        void 일치하는_옷이_없으면_빈_목록을_반환한다() {
            createCloth("후드티", "나이키", Season.SPRING);

            SliceResponse<ClothListResponse> response =
                    searchService.searchClothes(
                            "존재하지않는키워드", null, 10, SortDirection.DESC, null, null);

            assertThat(response.content()).isEmpty();
        }
    }

    @Nested
    class 기록을_검색할_때 {

        @Test
        void 차단한_유저를_제외하고_검색엔진에_위임한다() {
            Member blocked =
                    memberRepository.save(
                            Member.createMember(
                                    "blocked@test.com",
                                    "blockedNick",
                                    OauthInfo.createOauthInfo(
                                            "blocked-oauth", OauthProvider.KAKAO)));
            blockRepository.save(Block.createBlock(currentMember, blocked));

            SliceResponse<SearchedHistoryResponse> mockResponse =
                    new SliceResponse<>(List.of(), true);
            given(
                            searchRepository.findHistoriesByKeyword(
                                    eq("키워드"),
                                    eq(0L),
                                    eq(10),
                                    eq(HistorySearchSortType.LATEST),
                                    anyList()))
                    .willReturn(mockResponse);

            SliceResponse<SearchedHistoryResponse> response =
                    searchService.searchHistoryByHashtagsAndCategories(
                            "키워드", 0L, 10, HistorySearchSortType.LATEST);

            assertThat(response).isEqualTo(mockResponse);
            verify(searchRepository)
                    .findHistoriesByKeyword(
                            eq("키워드"),
                            eq(0L),
                            eq(10),
                            eq(HistorySearchSortType.LATEST),
                            eq(List.of(blocked.getId())));
        }
    }

    @Nested
    class 유저를_검색할_때 {

        @Test
        void 차단한_유저와_본인을_제외하고_검색엔진에_위임한다() {
            Member blocked =
                    memberRepository.save(
                            Member.createMember(
                                    "blocked2@test.com",
                                    "blockedNick2",
                                    OauthInfo.createOauthInfo(
                                            "blocked2-oauth", OauthProvider.KAKAO)));
            blockRepository.save(Block.createBlock(currentMember, blocked));

            SliceResponse<SearchedMemberResponse> mockResponse =
                    new SliceResponse<>(List.of(), true);
            given(searchRepository.findUsersByKeyword(eq("닉네임"), eq(0L), eq(10), anyList()))
                    .willReturn(mockResponse);

            SliceResponse<SearchedMemberResponse> response =
                    searchService.searchUserByNickname("닉네임", 0L, 10);

            assertThat(response).isEqualTo(mockResponse);
            verify(searchRepository)
                    .findUsersByKeyword(
                            eq("닉네임"),
                            eq(0L),
                            eq(10),
                            eq(List.of(blocked.getId(), currentMember.getId())));
        }
    }

    @Nested
    class 검색엔진에_기록을_동기화할_때 {

        @Test
        void 기록이_존재하면_검색엔진에_저장한다() {
            Situation situation = situationRepository.save(Situation.createSituation("situation"));
            History history =
                    historyRepository.save(
                            History.createHistory(
                                    LocalDate.of(2026, 1, 1), "content", currentMember, situation));

            searchService.syncAllHistories();

            verify(searchRepository, times(1)).saveAllHistories(anyList());
        }

        @Test
        void 기록이_없으면_검색엔진을_호출하지_않는다() {
            searchService.syncAllHistories();

            verify(searchRepository, never()).saveAllHistories(anyList());
        }
    }

    @Nested
    class 검색엔진에서_기록_동기화를_해제할_때 {

        @Test
        void 기록이_존재하면_모두_삭제한다() {
            Situation situation = situationRepository.save(Situation.createSituation("situation"));
            History history =
                    historyRepository.save(
                            History.createHistory(
                                    LocalDate.of(2026, 1, 1), "content", currentMember, situation));

            searchService.unSyncAllHistories();

            verify(searchRepository, times(1)).deleteHistory(eq(history.getId().toString()));
        }

        @Test
        void 기록이_없으면_검색엔진을_호출하지_않는다() {
            searchService.unSyncAllHistories();

            verify(searchRepository, never()).deleteHistory(any());
        }
    }

    @Nested
    class 검색엔진에_유저를_동기화할_때 {

        @Test
        void 유저가_존재하면_검색엔진에_저장한다() {
            searchService.syncAllMembers();

            verify(searchRepository, times(1)).saveAllMembers(anyList());
        }
    }

    @Nested
    class 검색엔진에서_유저_동기화를_해제할_때 {

        @Test
        void 유저가_존재하면_모두_삭제한다() {
            searchService.unSyncAllMembers();

            verify(searchRepository, times(1)).deleteMember(eq(currentMember.getId().toString()));
        }
    }

    @Nested
    class 검색_탭에서_기록을_추천할_때 {

        private Member candidateMember;
        private Category category;
        private Style usedStyle;
        private Style untriedStyle;
        private Hashtag hashtag;
        private History recommendableHistory;

        @BeforeEach
        void setUp() {
            candidateMember =
                    memberRepository.save(
                            Member.createMember(
                                    "candidate@test.com",
                                    "candidateNick",
                                    OauthInfo.createOauthInfo(
                                            "candidate-oauth", OauthProvider.KAKAO)));

            category = categoryRepository.save(Category.createCategory("상의", null));
            usedStyle = styleRepository.save(Style.createStyle("캐주얼"));
            untriedStyle = styleRepository.save(Style.createStyle("포멀"));
            hashtag = hashtagRepository.save(Hashtag.createHashtag("가을룩"));

            Situation situation = situationRepository.save(Situation.createSituation("situation"));

            // 현재 유저 본인의 기록: 사용한 스타일 / 자주 입은 카테고리 / 최근 해시태그 산출용
            Cloth myCloth =
                    clothRepository.save(
                            Cloth.createCloth(
                                    "myClothImage",
                                    null,
                                    null,
                                    null,
                                    List.of(Season.SPRING),
                                    category,
                                    currentMember));
            History myHistory =
                    historyRepository.save(
                            History.createHistory(
                                    LocalDate.of(2026, 1, 1), "content", currentMember, situation));
            historyStyleRepository.save(HistoryStyle.createHistoryStyle(myHistory, usedStyle));
            historyHashtagRepository.save(HistoryHashtag.createHistoryHashtag(myHistory, hashtag));
            HistoryImage myHistoryImage =
                    historyImageRepository.save(
                            HistoryImage.createHistoryImage("myHistoryImage", myHistory));
            historyClothTagRepository.save(
                    HistoryClothTag.createHistoryClothTag(myHistoryImage, myCloth, 1.0, 1.0));

            // 추천 후보가 될 다른 유저의 기록: untried style + 동일 카테고리 + 동일 해시태그 모두 매칭
            Cloth candidateCloth =
                    clothRepository.save(
                            Cloth.createCloth(
                                    "candidateClothImage",
                                    null,
                                    null,
                                    null,
                                    List.of(Season.SPRING),
                                    category,
                                    candidateMember));
            recommendableHistory =
                    historyRepository.save(
                            History.createHistory(
                                    LocalDate.of(2026, 1, 2),
                                    "candidateContent",
                                    candidateMember,
                                    situation));
            historyStyleRepository.save(
                    HistoryStyle.createHistoryStyle(recommendableHistory, untriedStyle));
            historyHashtagRepository.save(
                    HistoryHashtag.createHistoryHashtag(recommendableHistory, hashtag));
            HistoryImage candidateImage =
                    historyImageRepository.save(
                            HistoryImage.createHistoryImage(
                                    "candidateHistoryImage", recommendableHistory));
            historyClothTagRepository.save(
                    HistoryClothTag.createHistoryClothTag(
                            candidateImage, candidateCloth, 1.0, 1.0));

            // 카테고리/해시태그 조건은 본인 기록도 함께 만족하므로, 후보 기록에 좋아요를 주어
            // "좋아요 많은 순" 정렬에서 결정적으로 후보 기록이 선택되도록 한다.
            memberLikeRepository.save(
                    MemberLike.createMemberLike(candidateMember, recommendableHistory));
        }

        @Test
        void 안읽어본_스타일_자주입은_카테고리_최근_해시태그_기준으로_추천한다() {
            List<SearchingRecommendResponse> response = searchService.recommendInSearching();

            assertThat(response).hasSize(3);
            assertThat(response).extracting("historyId").containsOnly(recommendableHistory.getId());
            assertThat(response)
                    .extracting("recommendType")
                    .containsExactlyInAnyOrder(
                            "UNTRIED_STYLE", "FREQUENTLY_WORN_CATEGORY", "RECENTLY_USED_HASHTAG");
        }

        @Test
        void 추천_결과는_레디스에_캐싱된다() {
            searchService.recommendInSearching();

            Object cached =
                    redisTemplate.opsForValue().get("search:recommend:" + currentMember.getId());
            assertThat(cached).isNotNull();
        }

        @Test
        void 캐시된_추천_결과가_있으면_재계산하지_않고_반환한다() {
            List<SearchingRecommendResponse> first = searchService.recommendInSearching();
            List<SearchingRecommendResponse> second = searchService.recommendInSearching();

            assertThat(second).isEqualTo(first);
        }

        @Test
        void 캐시된_추천에_차단한_유저가_포함되면_재계산한다() {
            searchService.recommendInSearching();
            blockRepository.save(Block.createBlock(currentMember, candidateMember));

            List<SearchingRecommendResponse> response = searchService.recommendInSearching();

            assertThat(response).extracting("memberId").doesNotContain(candidateMember.getId());
        }
    }
}

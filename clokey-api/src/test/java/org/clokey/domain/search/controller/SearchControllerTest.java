package org.clokey.domain.search.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.clokey.domain.cloth.dto.response.ClothListResponse;
import org.clokey.domain.search.dto.response.SearchedHistoryResponse;
import org.clokey.domain.search.dto.response.SearchedMemberResponse;
import org.clokey.domain.search.dto.response.SearchingRecommendResponse;
import org.clokey.domain.search.enums.HistorySearchSortType;
import org.clokey.domain.search.service.SearchService;
import org.clokey.global.paging.SortDirection;
import org.clokey.response.SliceResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchService searchService;

    @Nested
    class 옷_검색_요청_시 {

        @Test
        void 유효한_요청이면_옷_목록을_반환한다() throws Exception {
            List<ClothListResponse> content =
                    List.of(
                            new ClothListResponse(
                                    1L,
                                    "testImageUrl",
                                    "testBrand",
                                    "testName",
                                    "testParent",
                                    "testCategory"));
            given(searchService.searchClothes("키워드", null, 10, SortDirection.DESC, null, null))
                    .willReturn(new SliceResponse<>(content, true));

            ResultActions perform =
                    mockMvc.perform(
                            get("/search/clothes").param("keyword", "키워드").param("size", "10"));

            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON200"))
                    .andExpect(jsonPath("$.result.content[0].clothId").value(1))
                    .andExpect(jsonPath("$.result.isLast").value(true));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 페이지_크기가_0_이하이면_예외가_발생한다(String size) throws Exception {
            ResultActions perform =
                    mockMvc.perform(
                            get("/search/clothes").param("keyword", "키워드").param("size", size));

            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON400"));
        }

        @Test
        void seasons_파라미터에_존재하지_않는_계절값이면_예외가_발생한다() throws Exception {
            ResultActions perform =
                    mockMvc.perform(
                            get("/search/clothes")
                                    .param("keyword", "키워드")
                                    .param("size", "10")
                                    .param("seasons", "SPRINGG"));

            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON400"));
        }
    }

    @Nested
    class 기록_검색_요청_시 {

        @Test
        void 유효한_요청이면_기록_목록을_반환한다() throws Exception {
            List<SearchedHistoryResponse> content =
                    List.of(
                            new SearchedHistoryResponse(
                                    1L, "testImageUrl", "testProfile", "testNick"));
            given(
                            searchService.searchHistoryByHashtagsAndCategories(
                                    "키워드", 0L, 10, HistorySearchSortType.LATEST))
                    .willReturn(new SliceResponse<>(content, true));

            ResultActions perform =
                    mockMvc.perform(
                            get("/search/histories")
                                    .param("keyword", "키워드")
                                    .param("page", "0")
                                    .param("size", "10"));

            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON200"))
                    .andExpect(jsonPath("$.result.content[0].historyId").value(1));
        }

        @Test
        void 정렬조건을_지정하면_해당_정렬로_조회한다() throws Exception {
            given(
                            searchService.searchHistoryByHashtagsAndCategories(
                                    "키워드", 0L, 10, HistorySearchSortType.POPULAR))
                    .willReturn(new SliceResponse<>(List.of(), true));

            ResultActions perform =
                    mockMvc.perform(
                            get("/search/histories")
                                    .param("keyword", "키워드")
                                    .param("page", "0")
                                    .param("size", "10")
                                    .param("sort", "POPULAR"));

            perform.andExpect(status().isOk()).andExpect(jsonPath("$.isSuccess").value(true));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 페이지_크기가_0_이하이면_예외가_발생한다(String size) throws Exception {
            ResultActions perform =
                    mockMvc.perform(
                            get("/search/histories")
                                    .param("keyword", "키워드")
                                    .param("page", "0")
                                    .param("size", size));

            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON400"));
        }
    }

    @Nested
    class 기록_검색엔진_동기화_요청_시 {

        @Test
        void 전체_동기화_요청이면_성공을_반환한다() throws Exception {
            willDoNothing().given(searchService).syncAllHistories();

            ResultActions perform = mockMvc.perform(get("/search/histories/sync-all"));

            perform.andExpect(status().isOk()).andExpect(jsonPath("$.isSuccess").value(true));
        }

        @Test
        void 전체_동기화_해제_요청이면_성공을_반환한다() throws Exception {
            willDoNothing().given(searchService).unSyncAllHistories();

            ResultActions perform = mockMvc.perform(get("/search/histories/unsync-all"));

            perform.andExpect(status().isOk()).andExpect(jsonPath("$.isSuccess").value(true));
        }
    }

    @Nested
    class 유저_검색_요청_시 {

        @Test
        void 유효한_요청이면_유저_목록을_반환한다() throws Exception {
            List<SearchedMemberResponse> content =
                    List.of(new SearchedMemberResponse(1L, "testProfile", "testNick"));
            given(searchService.searchUserByNickname("닉네임", 0L, 10))
                    .willReturn(new SliceResponse<>(content, true));

            ResultActions perform =
                    mockMvc.perform(
                            get("/search/members")
                                    .param("keyword", "닉네임")
                                    .param("page", "0")
                                    .param("size", "10"));

            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON200"))
                    .andExpect(jsonPath("$.result.content[0].memberId").value(1));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 페이지_크기가_0_이하이면_예외가_발생한다(String size) throws Exception {
            ResultActions perform =
                    mockMvc.perform(
                            get("/search/members")
                                    .param("keyword", "닉네임")
                                    .param("page", "0")
                                    .param("size", size));

            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON400"));
        }
    }

    @Nested
    class 유저_검색엔진_동기화_요청_시 {

        @Test
        void 전체_동기화_요청이면_성공을_반환한다() throws Exception {
            willDoNothing().given(searchService).syncAllMembers();

            ResultActions perform = mockMvc.perform(get("/search/members/sync-all"));

            perform.andExpect(status().isOk()).andExpect(jsonPath("$.isSuccess").value(true));
        }

        @Test
        void 전체_동기화_해제_요청이면_성공을_반환한다() throws Exception {
            willDoNothing().given(searchService).unSyncAllMembers();

            ResultActions perform = mockMvc.perform(get("/search/members/unsync-all"));

            perform.andExpect(status().isOk()).andExpect(jsonPath("$.isSuccess").value(true));
        }
    }

    @Nested
    class 검색_탭_기록_추천_요청_시 {

        @Test
        void 유효한_요청이면_추천_기록_목록을_반환한다() throws Exception {
            List<SearchingRecommendResponse> response =
                    List.of(
                            new SearchingRecommendResponse(
                                    1L,
                                    2L,
                                    "UNTRIED_STYLE",
                                    "분위기 전환이 필요할 때",
                                    "포멀",
                                    "testImageUrl"));
            given(searchService.recommendInSearching()).willReturn(response);

            ResultActions perform = mockMvc.perform(get("/search/recommendations"));

            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON200"))
                    .andExpect(jsonPath("$.result[0].historyId").value(1))
                    .andExpect(jsonPath("$.result[0].recommendType").value("UNTRIED_STYLE"));
        }

        @Test
        void 추천할_기록이_없으면_빈_목록을_반환한다() throws Exception {
            given(searchService.recommendInSearching()).willReturn(List.of());

            ResultActions perform = mockMvc.perform(get("/search/recommendations"));

            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.result").isEmpty());
        }
    }
}

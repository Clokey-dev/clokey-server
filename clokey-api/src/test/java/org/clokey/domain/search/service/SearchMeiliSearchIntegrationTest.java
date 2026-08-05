package org.clokey.domain.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;

import io.vanslog.spring.data.meilisearch.core.MeilisearchOperations;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import org.clokey.MeiliSearchIntegrationTest;
import org.clokey.domain.history.repository.HistoryRepository;
import org.clokey.domain.history.repository.HistoryStyleRepository;
import org.clokey.domain.history.repository.SituationRepository;
import org.clokey.domain.history.repository.StyleRepository;
import org.clokey.domain.like.repository.MemberLikeRepository;
import org.clokey.domain.member.repository.BlockRepository;
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.domain.search.document.HistoryDocument;
import org.clokey.domain.search.document.MemberDocument;
import org.clokey.domain.search.dto.response.SearchedHistoryResponse;
import org.clokey.domain.search.dto.response.SearchedMemberResponse;
import org.clokey.domain.search.enums.HistorySearchSortType;
import org.clokey.domain.search.repository.MeilisearchHistoryRepository;
import org.clokey.domain.search.repository.MeilisearchMemberRepository;
import org.clokey.global.util.MemberUtil;
import org.clokey.history.entity.History;
import org.clokey.history.entity.HistoryStyle;
import org.clokey.history.entity.Situation;
import org.clokey.history.entity.Style;
import org.clokey.like.entity.MemberLike;
import org.clokey.member.entity.Block;
import org.clokey.member.entity.Member;
import org.clokey.member.entity.OauthInfo;
import org.clokey.member.enums.MemberStatus;
import org.clokey.member.enums.OauthProvider;
import org.clokey.response.SliceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * 실제 Meilisearch 컨테이너를 대상으로 기록/유저 검색 엔진 동기화 및 검색 기능을 검증하는 통합 테스트입니다. "test" 프로파일에서는 {@link
 * org.clokey.domain.search.repository.NoopSearchRepository} 로 대체되어 검증되지 않는 경로이므로, 여기서 실제
 * Meilisearch 연동 동작을 별도로 검증합니다.
 */
class SearchMeiliSearchIntegrationTest extends MeiliSearchIntegrationTest {

    @Autowired private SearchService searchService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private HistoryRepository historyRepository;
    @Autowired private SituationRepository situationRepository;
    @Autowired private StyleRepository styleRepository;
    @Autowired private HistoryStyleRepository historyStyleRepository;
    @Autowired private MemberLikeRepository memberLikeRepository;
    @Autowired private BlockRepository blockRepository;
    @Autowired private MeilisearchOperations meilisearchOperations;
    @Autowired private MeilisearchHistoryRepository meilisearchHistoryRepository;
    @Autowired private MeilisearchMemberRepository meilisearchMemberRepository;

    @MockitoBean private MemberUtil memberUtil;

    @BeforeEach
    void setUpMeilisearchIndices() {
        meilisearchOperations.applySettings(HistoryDocument.class);
        meilisearchOperations.applySettings(MemberDocument.class);
        meilisearchHistoryRepository.deleteAll();
        meilisearchMemberRepository.deleteAll();
    }

    private Member createMember(String email, String nickname) {
        return Member.createMember(
                email, nickname, OauthInfo.createOauthInfo(email + "-oauth", OauthProvider.KAKAO));
    }

    private void awaitHistorySearch(
            String keyword, HistorySearchSortType sort, Consumer<List<Long>> assertion) {
        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(
                        () -> {
                            SliceResponse<SearchedHistoryResponse> response =
                                    searchService.searchHistoryByHashtagsAndCategories(
                                            keyword, 0L, 10, sort);
                            assertion.accept(
                                    response.content().stream()
                                            .map(SearchedHistoryResponse::historyId)
                                            .toList());
                        });
    }

    private void awaitMemberSearch(String keyword, Consumer<List<Long>> assertion) {
        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(
                        () -> {
                            SliceResponse<SearchedMemberResponse> response =
                                    searchService.searchUserByNickname(keyword, 0L, 10);
                            assertion.accept(
                                    response.content().stream()
                                            .map(SearchedMemberResponse::memberId)
                                            .toList());
                        });
    }

    @Nested
    class 기록을_검색할_때 {

        private Member owner;
        private Situation situation;
        private Style style;

        @BeforeEach
        void setUp() {
            owner = memberRepository.save(createMember("owner@test.com", "ownerNick"));
            situation = situationRepository.save(Situation.createSituation("testSituation"));
            style = styleRepository.save(Style.createStyle("빈티지"));
            given(memberUtil.getCurrentMember()).willReturn(owner);
        }

        private History createTaggedHistory(Member member, LocalDate date, boolean banned) {
            History history =
                    historyRepository.save(
                            History.createHistory(date, "testContent", member, situation));
            historyStyleRepository.save(HistoryStyle.createHistoryStyle(history, style));
            if (banned) {
                history.ban();
                historyRepository.save(history);
            }
            return history;
        }

        @Test
        void 스타일_키워드로_검색하면_해당_기록을_반환한다() {
            History history = createTaggedHistory(owner, LocalDate.of(2026, 1, 1), false);
            searchService.syncAllHistories();

            awaitHistorySearch(
                    "빈티지",
                    HistorySearchSortType.LATEST,
                    historyIds -> assertThat(historyIds).contains(history.getId()));
        }

        @Test
        void 신고된_기록은_검색결과에서_제외된다() {
            History bannedHistory = createTaggedHistory(owner, LocalDate.of(2026, 1, 1), true);
            searchService.syncAllHistories();

            awaitHistorySearch(
                    "빈티지",
                    HistorySearchSortType.LATEST,
                    historyIds -> assertThat(historyIds).doesNotContain(bannedHistory.getId()));
        }

        @Test
        void 차단한_유저의_기록은_검색결과에서_제외된다() {
            Member blocked = memberRepository.save(createMember("blocked@test.com", "blockedNick"));
            blockRepository.save(Block.createBlock(owner, blocked));

            History blockedHistory = createTaggedHistory(blocked, LocalDate.of(2026, 1, 1), false);
            searchService.syncAllHistories();

            awaitHistorySearch(
                    "빈티지",
                    HistorySearchSortType.LATEST,
                    historyIds -> assertThat(historyIds).doesNotContain(blockedHistory.getId()));
        }

        @Test
        void 좋아요가_많은_순으로_인기순_정렬한다() {
            History lessLiked = createTaggedHistory(owner, LocalDate.of(2026, 1, 1), false);
            History moreLiked = createTaggedHistory(owner, LocalDate.of(2026, 1, 2), false);

            Member liker1 = memberRepository.save(createMember("liker1@test.com", "liker1"));
            Member liker2 = memberRepository.save(createMember("liker2@test.com", "liker2"));
            memberLikeRepository.save(MemberLike.createMemberLike(liker1, moreLiked));
            memberLikeRepository.save(MemberLike.createMemberLike(liker2, moreLiked));

            searchService.syncAllHistories();

            awaitHistorySearch(
                    "빈티지",
                    HistorySearchSortType.POPULAR,
                    historyIds -> {
                        assertThat(historyIds).hasSize(2);
                        assertThat(historyIds.get(0)).isEqualTo(moreLiked.getId());
                        assertThat(historyIds.get(1)).isEqualTo(lessLiked.getId());
                    });
        }
    }

    @Nested
    class 유저를_검색할_때 {

        @Test
        void 닉네임_키워드로_검색하면_해당_유저를_반환한다() {
            Member searcher =
                    memberRepository.save(createMember("searcher@test.com", "searcherNick"));
            Member target = memberRepository.save(createMember("target@test.com", "빈티지러버"));
            given(memberUtil.getCurrentMember()).willReturn(searcher);

            searchService.syncAllMembers();

            awaitMemberSearch("빈티지", memberIds -> assertThat(memberIds).contains(target.getId()));
        }

        @Test
        void 본인은_검색결과에서_제외된다() {
            Member searcher = memberRepository.save(createMember("searcher2@test.com", "빈티지매니아"));
            given(memberUtil.getCurrentMember()).willReturn(searcher);

            searchService.syncAllMembers();

            awaitMemberSearch(
                    "빈티지", memberIds -> assertThat(memberIds).doesNotContain(searcher.getId()));
        }

        @Test
        void 차단한_유저는_검색결과에서_제외된다() {
            Member searcher =
                    memberRepository.save(createMember("searcher3@test.com", "searcherNick3"));
            Member blocked = memberRepository.save(createMember("blocked2@test.com", "빈티지블락"));
            blockRepository.save(Block.createBlock(searcher, blocked));
            given(memberUtil.getCurrentMember()).willReturn(searcher);

            searchService.syncAllMembers();

            awaitMemberSearch(
                    "빈티지", memberIds -> assertThat(memberIds).doesNotContain(blocked.getId()));
        }

        @Test
        void 정지된_유저는_검색결과에서_제외된다() {
            Member searcher =
                    memberRepository.save(createMember("searcher4@test.com", "searcherNick4"));
            Member banned = memberRepository.save(createMember("banned@test.com", "빈티지밴"));
            banned.updateMemberStatus(MemberStatus.BANNED);
            memberRepository.save(banned);
            given(memberUtil.getCurrentMember()).willReturn(searcher);

            searchService.syncAllMembers();

            awaitMemberSearch(
                    "빈티지", memberIds -> assertThat(memberIds).doesNotContain(banned.getId()));
        }
    }

    @Nested
    class 검색엔진_동기화_해제를_할_때 {

        @Autowired private SituationRepository situationRepository;
        @Autowired private StyleRepository styleRepository;
        @Autowired private HistoryStyleRepository historyStyleRepository;

        @Test
        void unSyncAllHistories_호출하면_모든_기록이_검색결과에서_제거된다() {
            Member owner = memberRepository.save(createMember("owner2@test.com", "owner2Nick"));
            Situation situation =
                    situationRepository.save(Situation.createSituation("testSituation2"));
            Style style = styleRepository.save(Style.createStyle("빈티지2"));
            History history =
                    historyRepository.save(
                            History.createHistory(
                                    LocalDate.of(2026, 1, 2), "testContent", owner, situation));
            historyStyleRepository.save(HistoryStyle.createHistoryStyle(history, style));
            given(memberUtil.getCurrentMember()).willReturn(owner);

            searchService.syncAllHistories();
            awaitHistorySearch(
                    "빈티지2",
                    HistorySearchSortType.LATEST,
                    historyIds -> assertThat(historyIds).contains(history.getId()));

            searchService.unSyncAllHistories();
            awaitHistorySearch(
                    "빈티지2",
                    HistorySearchSortType.LATEST,
                    historyIds -> assertThat(historyIds).isEmpty());
        }

        @Test
        void unSyncAllMembers_호출하면_모든_유저가_검색결과에서_제거된다() {
            Member searcher =
                    memberRepository.save(createMember("searcher5@test.com", "searcherNick5"));
            Member target = memberRepository.save(createMember("target2@test.com", "빈티지헌터"));
            given(memberUtil.getCurrentMember()).willReturn(searcher);

            searchService.syncAllMembers();
            awaitMemberSearch("빈티지헌터", memberIds -> assertThat(memberIds).contains(target.getId()));

            searchService.unSyncAllMembers();
            awaitMemberSearch("빈티지헌터", memberIds -> assertThat(memberIds).isEmpty());
        }
    }
}

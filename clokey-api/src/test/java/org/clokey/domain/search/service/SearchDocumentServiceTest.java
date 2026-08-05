package org.clokey.domain.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.clokey.IntegrationTest;
import org.clokey.category.entity.Category;
import org.clokey.cloth.entity.Cloth;
import org.clokey.cloth.enums.Season;
import org.clokey.domain.category.repository.CategoryRepository;
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
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.domain.search.document.HistoryDocument;
import org.clokey.domain.search.document.MemberDocument;
import org.clokey.history.entity.Hashtag;
import org.clokey.history.entity.History;
import org.clokey.history.entity.HistoryClothTag;
import org.clokey.history.entity.HistoryHashtag;
import org.clokey.history.entity.HistoryImage;
import org.clokey.history.entity.HistoryStyle;
import org.clokey.history.entity.Situation;
import org.clokey.history.entity.Style;
import org.clokey.like.entity.MemberLike;
import org.clokey.member.entity.Member;
import org.clokey.member.entity.OauthInfo;
import org.clokey.member.enums.OauthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SearchDocumentServiceTest extends IntegrationTest {

    @Autowired private SearchDocumentService searchDocumentService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ClothRepository clothRepository;
    @Autowired private HistoryRepository historyRepository;
    @Autowired private SituationRepository situationRepository;
    @Autowired private StyleRepository styleRepository;
    @Autowired private HistoryStyleRepository historyStyleRepository;
    @Autowired private HashtagRepository hashtagRepository;
    @Autowired private HistoryHashtagRepository historyHashtagRepository;
    @Autowired private HistoryImageRepository historyImageRepository;
    @Autowired private HistoryClothTagRepository historyClothTagRepository;
    @Autowired private MemberLikeRepository memberLikeRepository;

    private Member member;
    private Situation situation;

    @BeforeEach
    void setUp() {
        member =
                memberRepository.save(
                        Member.createMember(
                                "test@test.com",
                                "testNick",
                                OauthInfo.createOauthInfo("test-oauth", OauthProvider.KAKAO)));
        situation = situationRepository.save(Situation.createSituation("situation"));
    }

    @Nested
    class 기록을_문서로_변환할_때 {

        @Test
        void 기록의_모든_정보를_HistoryDocument로_변환한다() {
            Category category = categoryRepository.save(Category.createCategory("상의", null));
            Cloth cloth =
                    clothRepository.save(
                            Cloth.createCloth(
                                    "clothImage",
                                    null,
                                    null,
                                    null,
                                    List.of(Season.SPRING),
                                    category,
                                    member));
            Style style = styleRepository.save(Style.createStyle("캐주얼"));
            Hashtag hashtag = hashtagRepository.save(Hashtag.createHashtag("가을룩"));

            History history =
                    historyRepository.save(
                            History.createHistory(
                                    LocalDate.of(2026, 1, 1), "content", member, situation));
            historyStyleRepository.save(HistoryStyle.createHistoryStyle(history, style));
            historyHashtagRepository.save(HistoryHashtag.createHistoryHashtag(history, hashtag));
            HistoryImage image =
                    historyImageRepository.save(
                            HistoryImage.createHistoryImage("historyImageUrl", history));
            historyClothTagRepository.save(
                    HistoryClothTag.createHistoryClothTag(image, cloth, 1.0, 1.0));

            Member liker =
                    memberRepository.save(
                            Member.createMember(
                                    "liker@test.com",
                                    "likerNick",
                                    OauthInfo.createOauthInfo("liker-oauth", OauthProvider.KAKAO)));
            memberLikeRepository.save(MemberLike.createMemberLike(liker, history));

            HistoryDocument document = searchDocumentService.toHistoryDocument(history.getId());

            assertThat(document.getId()).isEqualTo(history.getId().toString());
            assertThat(document.getMemberId()).isEqualTo(member.getId());
            assertThat(document.getBanned()).isFalse();
            assertThat(document.getLikeCount()).isEqualTo(1L);
            assertThat(document.getCreatedAt())
                    .isEqualTo(
                            history.getCreatedAt()
                                    .atZone(ZoneOffset.UTC)
                                    .toInstant()
                                    .toEpochMilli());
            assertThat(document.getHistoryImageUrl()).isEqualTo("historyImageUrl");
            assertThat(document.getProfileImageUrl()).isEqualTo(member.getProfileImageUrl());
            assertThat(document.getNickname()).isEqualTo("testNick");
            assertThat(document.getStyleNames()).containsExactly("캐주얼");
            assertThat(document.getHashtagNames()).containsExactly("가을룩");
            assertThat(document.getCategoryNames()).containsExactly("상의");
        }

        @Test
        void 신고된_기록은_banned가_true로_변환된다() {
            History history =
                    historyRepository.save(
                            History.createHistory(
                                    LocalDate.of(2026, 1, 1), "content", member, situation));
            history.ban();
            historyRepository.save(history);

            HistoryDocument document = searchDocumentService.toHistoryDocument(history.getId());

            assertThat(document.getBanned()).isTrue();
        }

        @Test
        void 이미지가_없으면_historyImageUrl은_null이다() {
            History history =
                    historyRepository.save(
                            History.createHistory(
                                    LocalDate.of(2026, 1, 1), "content", member, situation));

            HistoryDocument document = searchDocumentService.toHistoryDocument(history.getId());

            assertThat(document.getHistoryImageUrl()).isNull();
        }

        @Test
        void 같은_카테고리가_여러번_태깅되어도_중복없이_반환한다() {
            Category category = categoryRepository.save(Category.createCategory("상의", null));
            Cloth cloth1 =
                    clothRepository.save(
                            Cloth.createCloth(
                                    "clothImage1",
                                    null,
                                    null,
                                    null,
                                    List.of(Season.SPRING),
                                    category,
                                    member));
            Cloth cloth2 =
                    clothRepository.save(
                            Cloth.createCloth(
                                    "clothImage2",
                                    null,
                                    null,
                                    null,
                                    List.of(Season.SPRING),
                                    category,
                                    member));

            History history =
                    historyRepository.save(
                            History.createHistory(
                                    LocalDate.of(2026, 1, 1), "content", member, situation));
            HistoryImage image =
                    historyImageRepository.save(
                            HistoryImage.createHistoryImage("historyImageUrl", history));
            historyClothTagRepository.save(
                    HistoryClothTag.createHistoryClothTag(image, cloth1, 1.0, 1.0));
            historyClothTagRepository.save(
                    HistoryClothTag.createHistoryClothTag(image, cloth2, 2.0, 2.0));

            HistoryDocument document = searchDocumentService.toHistoryDocument(history.getId());

            assertThat(document.getCategoryNames()).containsExactly("상의");
        }

        @Test
        void 존재하지_않는_기록이면_예외가_발생한다() {
            assertThatThrownBy(() -> searchDocumentService.toHistoryDocument(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("History not found: 999");
        }
    }

    @Nested
    class 유저를_문서로_변환할_때 {

        @Test
        void 유저의_모든_정보를_MemberDocument로_변환한다() {
            MemberDocument document = searchDocumentService.toMemberDocument(member.getId());

            assertThat(document.getId()).isEqualTo(member.getId().toString());
            assertThat(document.getMemberStatus()).isEqualTo(member.getMemberStatus().name());
            assertThat(document.getProfileImageUrl()).isEqualTo(member.getProfileImageUrl());
            assertThat(document.getNickname()).isEqualTo("testNick");
        }

        @Test
        void 존재하지_않는_유저면_예외가_발생한다() {
            assertThatThrownBy(() -> searchDocumentService.toMemberDocument(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Member not found: 999");
        }
    }
}

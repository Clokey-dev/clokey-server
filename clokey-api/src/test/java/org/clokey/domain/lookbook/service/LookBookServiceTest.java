package org.clokey.domain.lookbook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Objects;
import org.clokey.IntegrationTest;
import org.clokey.category.entity.Category;
import org.clokey.cloth.entity.Cloth;
import org.clokey.coordinate.entity.Coordinate;
import org.clokey.coordinate.entity.CoordinateCloth;
import org.clokey.domain.category.repository.CategoryRepository;
import org.clokey.domain.cloth.repository.ClothRepository;
import org.clokey.domain.coordinate.repository.CoordinateClothRepository;
import org.clokey.domain.coordinate.repository.CoordinateRepository;
import org.clokey.domain.image.event.ImagesDeleteEvent;
import org.clokey.domain.lookbook.dto.request.LookBookCreateRequest;
import org.clokey.domain.lookbook.dto.request.LookBookUpdateRequest;
import org.clokey.domain.lookbook.exception.LookBookErrorCode;
import org.clokey.domain.lookbook.repository.LookBookRepository;
import org.clokey.domain.member.repository.MemberRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.util.MemberUtil;
import org.clokey.lookbook.entity.LookBook;
import org.clokey.member.entity.Member;
import org.clokey.member.entity.OauthInfo;
import org.clokey.member.enums.OauthProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class LookBookServiceTest extends IntegrationTest {

    @Autowired private LookBookService lookBookService;
    @Autowired private LookBookRepository lookBookRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CoordinateRepository coordinateRepository;
    @Autowired private CoordinateClothRepository coordinateClothRepository;
    @Autowired private ClothRepository clothRepository;

    @Autowired private ApplicationEvents applicationEvents;
    @MockitoBean private MemberUtil memberUtil;

    @Nested
    class 룩북을_생성할_때 {

        @BeforeEach
        void setUp() {
            Member member1 =
                    Member.createMember(
                            "testEmail1",
                            "testClokeyId1",
                            "testNickName1",
                            OauthInfo.createOauthInfo("testOauthId1", OauthProvider.KAKAO));

            memberRepository.save(member1);
            given(memberUtil.getCurrentMember()).willReturn(member1);
        }

        @Test
        void 유효한_요청이면_룩북을_생성한다() {
            // when
            lookBookService.createLookBook(new LookBookCreateRequest("testName"));

            // then
            assertThat(lookBookRepository.findById(1L).orElseThrow())
                    .extracting("name", "member.id")
                    .containsExactly("testName", 1L);
        }
    }

    @Nested
    class 룩북을_수정할_때 {

        @BeforeEach
        void setUp() {
            Member member1 =
                    Member.createMember(
                            "testEmail1",
                            "testClokeyId1",
                            "testNickName1",
                            OauthInfo.createOauthInfo("testOauthId1", OauthProvider.KAKAO));

            Member member2 =
                    Member.createMember(
                            "testEmail2",
                            "testClokeyId2",
                            "testNickName2",
                            OauthInfo.createOauthInfo("testOauthId1", OauthProvider.KAKAO));

            memberRepository.saveAll(List.of(member1, member2));
            given(memberUtil.getCurrentMember()).willReturn(member1);

            LookBook lookBook1 = LookBook.createLookBook("testName1", member1);
            LookBook lookBook2 = LookBook.createLookBook("testName2", member2);
            lookBookRepository.saveAll(List.of(lookBook1, lookBook2));
        }

        @Test
        void 유효한_요청이면_룩북을_수정한다() {
            // when
            lookBookService.updateLookBook(1L, new LookBookUpdateRequest("newName"));

            // then
            assertThat(lookBookRepository.findById(1L).get().getName()).isEqualTo("newName");
        }

        @Test
        void 존재하지_않는_룩북을_입려하면_예외가_발생한다() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    lookBookService.updateLookBook(
                                            999L, new LookBookUpdateRequest("newName")))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(LookBookErrorCode.LOOK_BOOK_NOT_FOUND.getMessage());
        }

        @Test
        void 나의_룩북이_아닌_경우_예외가_발생한다() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    lookBookService.updateLookBook(
                                            2L, new LookBookUpdateRequest("newName")))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(LookBookErrorCode.NOT_LOOK_BOOK_OWNER.getMessage());
        }
    }

    @Nested
    class 룩북을_삭제할_때 {

        @BeforeEach
        void setUp() {
            Member member1 =
                    Member.createMember(
                            "testEmail1",
                            "testClokeyId1",
                            "testNickName1",
                            OauthInfo.createOauthInfo("testOauthId1", OauthProvider.KAKAO));

            Member member2 =
                    Member.createMember(
                            "testEmail2",
                            "testClokeyId2",
                            "testNickName2",
                            OauthInfo.createOauthInfo("testOauthId1", OauthProvider.KAKAO));

            memberRepository.saveAll(List.of(member1, member2));
            given(memberUtil.getCurrentMember()).willReturn(member1);

            LookBook lookBook1 = LookBook.createLookBook("testName1", member1);
            LookBook lookBook2 = LookBook.createLookBook("testName2", member2);
            lookBookRepository.saveAll(List.of(lookBook1, lookBook2));

            Category category = Category.createCategory("testCategory", null);
            categoryRepository.save(category);

            Cloth cloth = Cloth.createCloth("testImageUrl", category, member1);

            clothRepository.saveAll(List.of(cloth));

            Coordinate coordinate1 =
                    Coordinate.createCoordinateManual(
                            "testName1", "testMemo1", "testImageUrl1", member1, lookBook1);
            Coordinate coordinate2 =
                    Coordinate.createCoordinateManual(
                            "testName2", "testMemo2", "testImageUrl2", member1, lookBook1);
            Coordinate coordinate3 = Coordinate.createDailyCoordinate("testImageUrl3", member1);
            Coordinate coordinate4 = Coordinate.createDailyCoordinate("testImageUrl4", member1);
            coordinate3.addToDailyCoordinateToLookBook("testName3", "testMemo3", lookBook1);
            coordinate4.addToDailyCoordinateToLookBook("testNam4e", "testMemo4", lookBook1);
            coordinateRepository.saveAll(
                    List.of(coordinate1, coordinate2, coordinate3, coordinate4));

            CoordinateCloth coordinateCloth1 =
                    CoordinateCloth.createCoordinateCloth(
                            1.0, 1.0, 1.0, 50.0, 1, coordinate1, cloth);
            CoordinateCloth coordinateCloth2 =
                    CoordinateCloth.createCoordinateCloth(
                            2.0, 2.0, 2.0, 60.0, 1, coordinate2, cloth);
            CoordinateCloth coordinateCloth3 =
                    CoordinateCloth.createCoordinateCloth(
                            2.0, 2.0, 2.0, 60.0, 1, coordinate3, cloth);
            CoordinateCloth coordinateCloth4 =
                    CoordinateCloth.createCoordinateCloth(
                            2.0, 2.0, 2.0, 60.0, 1, coordinate4, cloth);
            coordinateClothRepository.saveAll(
                    List.of(
                            coordinateCloth1,
                            coordinateCloth2,
                            coordinateCloth3,
                            coordinateCloth4));
        }

        @Test
        void 유효한_요청이면_룩북을_삭제한다() {
            // when
            lookBookService.deleteLookBook(1L);

            var events = applicationEvents.stream(ImagesDeleteEvent.class).toList();

            // then
            Assertions.assertAll(
                    // 룩북에서 생성된 코디는 삭제, 오늘의 코디는 삭제되지 않고 룩북에서만 제거된다.
                    () -> assertThat(coordinateRepository.findAllById(List.of(1L, 2L))).isEmpty(),
                    () ->
                            assertThat(coordinateRepository.findAllById(List.of(3L, 4L)))
                                    .extracting("lookBook")
                                    .allMatch(Objects::isNull),

                    // 코디-옷 역시 위의 조건에 따라 룩북에서 생성된 코디의 코디-옷만 제거된다.
                    () ->
                            assertThat(coordinateClothRepository.findAllById(List.of(1L, 2L)))
                                    .isEmpty(),
                    () ->
                            assertThat(coordinateRepository.findAllById(List.of(3L, 4L)).size())
                                    .isEqualTo(2),

                    // 룩북에서 생성된 코디의 imageUrl의 삭제 이벤트가 발행된다.
                    () ->
                            assertThat(events)
                                    .singleElement()
                                    .extracting("imageUrls")
                                    .isEqualTo(List.of("testImageUrl1", "testImageUrl2")));
        }

        @Test
        void 존재하지_않는_룩북을_입려하면_예외가_발생한다() {
            // when & then
            assertThatThrownBy(() -> lookBookService.deleteLookBook(999L))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(LookBookErrorCode.LOOK_BOOK_NOT_FOUND.getMessage());
        }

        @Test
        void 나의_룩북이_아닌_경우_예외가_발생한다() {
            // when & then
            assertThatThrownBy(() -> lookBookService.deleteLookBook(2L))
                    .isInstanceOf(BaseCustomException.class)
                    .hasMessage(LookBookErrorCode.NOT_LOOK_BOOK_OWNER.getMessage());
        }
    }
}

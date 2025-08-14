package org.clokey.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clokey.domain.member.dto.request.ProfileRequest;
import org.clokey.domain.member.dto.response.ProfileResponse;
import org.clokey.domain.member.service.MemberService;
import org.clokey.member.enums.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private MemberService memberService;

    @Nested
    class 프로필_수정_요청_시 {

        @Test
        void 유효한_요청이면_프로필을_반환한다() throws Exception {
            // given
            ProfileRequest request =
                    new ProfileRequest(
                            "닉네임",
                            "clokeyId",
                            "바이오",
                            Visibility.PUBLIC,
                            "https://img.example.com/bg.jpg",
                            "https://img.example.com/bg.jpg");

            ProfileResponse response =
                    new ProfileResponse(
                            1L,
                            "바이오",
                            "email@email.com",
                            "닉네임",
                            "clokeyId",
                            "https://img.example.com/bg.jpg",
                            "https://img.example.com/bg.jpg",
                            Visibility.PUBLIC);

            given(memberService.updateProfile(any(ProfileRequest.class))).willReturn(response);
            ;

            // when
            ResultActions perform =
                    mockMvc.perform(
                                    patch("/users")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
                            .andDo(print());

            // then
            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON201"))
                    .andExpect(jsonPath("$.message").value("요청 성공 및 리소스 생성됨"))
                    .andExpect(jsonPath("$.result.id").value(1))
                    .andExpect(jsonPath("$.result.bio").value("바이오"))
                    .andExpect(jsonPath("$.result.email").value("email@email.com"))
                    .andExpect(jsonPath("$.result.nickname").value("닉네임"))
                    .andExpect(jsonPath("$.result.clokeyId").value("clokeyId"))
                    .andExpect(
                            jsonPath("$.result.profileImageUrl")
                                    .value("https://img.example.com/bg.jpg"))
                    .andExpect(
                            jsonPath("$.result.profileBackImageUrl")
                                    .value("https://img.example.com/bg.jpg"))
                    .andExpect(jsonPath("$.result.visibility").value("PUBLIC"))
                    .andExpect(jsonPath("$.result.updatedAt").value("2025-08-11T12:00:00"));
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {" ", "   "})
        void 닉네임_비어있으면_예외가_발생한다(String nickname) throws Exception {
            // given
            Long userId = 1L;
            ProfileRequest request =
                    new ProfileRequest(
                            nickname,
                            "clokeyId",
                            "바이오",
                            Visibility.PRIVATE,
                            "https://img.example.com/bg.jpg",
                            "https://img.example.com/bg.jpg");

            // when
            ResultActions perform =
                    mockMvc.perform(
                                    patch("/users")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
                            .andDo(print());

            // then (Bean Validation → 400)
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON400"))
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.result.nickname").value("닉네임은 비워둘 수 없습니다."));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        @DisplayName("클로키아이디가_null_또는_공백이면_예외가_발생한다")
        void 클로키아이디_비어있으면_예외가_발생한다(String clokeyId) throws Exception {
            // given
            Long userId = 1L;
            ProfileRequest request =
                    new ProfileRequest(
                            "닉네임", // nickname
                            clokeyId, // clokeyId
                            "바이오", // bio
                            Visibility.PRIVATE, // visibility
                            "https://img.example.com/bg.jpg", // profileImageUrl
                            "https://img.example.com/bg.jpg" // profileBackImageUrl
                            );

            // when
            ResultActions perform =
                    mockMvc.perform(
                                    patch("/users")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
                            .andDo(print());

            // then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON400"))
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.result.clokeyId").value("Clokey ID는 비워둘 수 없습니다."));
        }

        @Test
        void 바이오가_100자를_초과하면_예외가_발생한다() throws Exception {
            // given
            String longBio = "a".repeat(101); // 101자
            ProfileRequest request =
                    new ProfileRequest(
                            "닉네임",
                            "clokeyId",
                            longBio,
                            Visibility.PRIVATE,
                            "https://img.example.com/bg.jpg",
                            "https://img.example.com/bg.jpg");

            // when
            ResultActions perform =
                    mockMvc.perform(
                                    patch("/users")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
                            .andDo(print());

            // then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON400"))
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.result.bio").value("바이오는 100자를 넘길 수 없습니다."));
        }
    }
}

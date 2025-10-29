package org.clokey.domain.lookbook.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clokey.domain.lookbook.dto.request.LookBookCreateRequest;
import org.clokey.domain.lookbook.dto.request.LookBookUpdateRequest;
import org.clokey.domain.lookbook.dto.response.LookBookCreateResponse;
import org.clokey.domain.lookbook.service.LookBookService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(LookBookController.class)
@AutoConfigureMockMvc(addFilters = false)
class LookBookControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LookBookService lookBookService;

    @Nested
    class 룩북_생성_요청_시 {

        @Test
        void 유효한_요청이면_룩북을_생성한다() throws Exception {
            // given
            LookBookCreateRequest request = new LookBookCreateRequest("testName");
            LookBookCreateResponse response = new LookBookCreateResponse(1L);
            given(lookBookService.createLookBook(request)).willReturn(response);

            // when & then
            ResultActions perform =
                    mockMvc.perform(
                            post("/lookbooks")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)));

            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON201"))
                    .andExpect(jsonPath("$.message").value("요청 성공 및 리소스 생성됨"))
                    .andExpect(jsonPath("$.result.lookBookId").value(1));
        }

        @Test
        void 룩북의_이름을_비워둔_경우_예외가_발생한다() throws Exception {
            // given
            LookBookCreateRequest request = new LookBookCreateRequest(null);

            // when & then
            ResultActions perform =
                    mockMvc.perform(
                            post("/lookbooks")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)));

            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON400"))
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.result.name").value("룩북의 이름은 비워둘 수 없습니다."));
        }
    }

    @Nested
    class 룩북_수정_요청_시 {

        @Test
        void 유효한_요청이면_룩북을_수정한다() throws Exception {
            // given
            LookBookUpdateRequest request = new LookBookUpdateRequest("testName");
            willDoNothing().given(lookBookService).updateLookBook(1L, request);

            // when & then
            ResultActions perform =
                    mockMvc.perform(
                            patch("/lookbooks/1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)));

            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON204"))
                    .andExpect(jsonPath("$.message").value("요청 성공 및 반환값 없음"));
        }

        @Test
        void 룩북의_이름을_비워둔_경우_예외가_발생한다() throws Exception {
            // given
            LookBookUpdateRequest request = new LookBookUpdateRequest(null);

            // when & then
            ResultActions perform =
                    mockMvc.perform(
                            patch("/lookbooks/1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)));

            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON400"))
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.result.name").value("룩북의 이름은 비워둘 수 없습니다."));
        }
    }

    @Nested
    class 룩북_삭제_요청_시 {

        @Test
        void 유효한_요청이면_룩북을_삭제한다() throws Exception {
            // given
            willDoNothing().given(lookBookService).deleteLookBook(1L);

            // when & then
            ResultActions perform =
                    mockMvc.perform(delete("/lookbooks/1").contentType(MediaType.APPLICATION_JSON));

            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON204"))
                    .andExpect(jsonPath("$.message").value("요청 성공 및 반환값 없음"));
        }
    }
}

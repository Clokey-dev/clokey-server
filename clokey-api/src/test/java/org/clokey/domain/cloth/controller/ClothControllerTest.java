package org.clokey.domain.cloth.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.clokey.domain.cloth.dto.request.ClothCreateRequest;
import org.clokey.domain.cloth.dto.request.ClothCreateRequests;
import org.clokey.domain.cloth.dto.response.ClothCreateResponse;
import org.clokey.domain.cloth.service.ClothService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ClothController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClothControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ClothService clothService;

    @Nested
    class 옷_생성_요청_시 {

        @Test
        void 유효한_요청이면_생성된_옷_ID를_반환한다() throws Exception {
            // given
            ClothCreateRequests request =
                    new ClothCreateRequests(
                            List.of(
                                    new ClothCreateRequest("testClothImageUrl1", 1L),
                                    new ClothCreateRequest("testClothImageUrl2", 1L)));

            ClothCreateResponse response = new ClothCreateResponse(List.of(1L, 2L));

            given(clothService.createCloths(request)).willReturn(response);

            // when & then
            ResultActions perform =
                    mockMvc.perform(
                            post("/cloths")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)));

            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON201"))
                    .andExpect(jsonPath("$.message").value("요청 성공 및 리소스 생성됨"))
                    .andExpect(jsonPath("$.result.clothIds[0]").value(1))
                    .andExpect(jsonPath("$.result.clothIds[1]").value(2));
        }

        @Test
        void 빈_요청이면_예외를_반환한다() throws Exception {
            // given
            ClothCreateRequests request = new ClothCreateRequests(List.of());

            // when & then
            ResultActions perform =
                    mockMvc.perform(
                            post("/cloths")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)));

            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON201"))
                    .andExpect(jsonPath("$.message").value("요청 성공 및 리소스 생성됨"))
                    .andExpect(jsonPath("$.result.clothIds[0]").value(1))
                    .andExpect(jsonPath("$.result.clothIds[1]").value(2));
        }
    }
}

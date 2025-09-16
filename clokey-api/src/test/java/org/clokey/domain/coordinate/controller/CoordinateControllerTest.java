package org.clokey.domain.coordinate.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.clokey.domain.coordinate.dto.request.DailyCoordinateCreateRequest;
import org.clokey.domain.coordinate.dto.response.DailyCoordinateCreateResponse;
import org.clokey.domain.coordinate.service.CoordinateService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CoordinateController.class)
@AutoConfigureMockMvc(addFilters = false)
class CoordinateControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CoordinateService coordinateService;

    @Nested
    class 오늘의_코디_생성_요청_시 {

        @Test
        void 유효한_요청이면_오늘의_코디를_생성하고_ID를_반환한다() throws Exception {
            // given
            DailyCoordinateCreateRequest request =
                    new DailyCoordinateCreateRequest(
                            "testUrl",
                            List.of(
                                    new DailyCoordinateCreateRequest.Payload(
                                            1L, 100.5, 200.25, 1.0, 1)));
            DailyCoordinateCreateResponse response = new DailyCoordinateCreateResponse(1L);
            given(coordinateService.createDailyCoordinate(request)).willReturn(response);

            // when & then
            ResultActions perform =
                    mockMvc.perform(
                            post("/coordinate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)));

            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON201"))
                    .andExpect(jsonPath("$.message").value("요청 성공 및 리소스 생성됨"))
                    .andExpect(jsonPath("$.result.dailyCoordinateId").value(1));
        }
    }
}

package kodanect.domain.logging.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kodanect.domain.logging.dto.ActionLogPayload;
import kodanect.domain.logging.service.ActionLogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ActionLogController.class)
public class ActionLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActionLogService actionLogService;

    @MockBean
    private MessageSourceAccessor messageSource;

    @Captor
    private ArgumentCaptor<List<ActionLogPayload>> payloadCaptor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 테스트 실행 전 Mock 초기화 및 메시지 설정
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(messageSource.getMessage(eq("log.save.success"), any(Object[].class)))
                .thenReturn("로그를 성공적으로 저장했습니다.");
    }

    /**
     * GIVEN: ActionLogPayload 리스트가 요청으로 들어오고
     * WHEN: POST /action-log 호출 시
     * THEN: 서비스가 호출되고, 성공 응답이 반환되어야 한다
     */
    @Test
    @DisplayName("액션 로그 수집 요청 처리 테스트")
    public void shouldCollectActionLogsSuccessfully() throws Exception {
        List<ActionLogPayload> payloads = List.of(
                ActionLogPayload.builder().type("read").target("button1").build(),
                ActionLogPayload.builder().type("create").target("button2").build()
        );

        mockMvc.perform(post("/action-log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloads)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("로그를 성공적으로 저장했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(actionLogService).enqueueLogs(payloadCaptor.capture());

        List<ActionLogPayload> actual = payloadCaptor.getValue();
        assertEquals(payloads.size(), actual.size());

        for (int i = 0; i < payloads.size(); i++) {
            assertEquals(payloads.get(i).getType(), actual.get(i).getType());
            assertEquals(payloads.get(i).getTarget(), actual.get(i).getTarget());
        }
    }

}

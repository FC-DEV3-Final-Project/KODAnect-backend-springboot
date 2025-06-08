package kodanect.domain.logging.controller;

import kodanect.domain.logging.dto.ActionLogPayload;
import kodanect.domain.logging.exception.ActionLogConversionException;
import kodanect.domain.logging.service.ActionLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ActionLogController.class)
@Import(ActionLogConversionException.class)
class ActionLogExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActionLogService actionLogService;

    @MockBean
    private MessageSourceAccessor messageSource;

    /**
     * GIVEN: 클라이언트가 /action-log API에 POST 요청을 보내면
     * WHEN: 내부에서 직렬화 오류가 발생해 ActionLogConversionException이 발생하면
     * THEN: 500 상태 코드와 함께 적절한 에러 메시지가 반환되어야 한다
     */
    @Test
    @DisplayName("로그 직렬화 실패 예외 테스트")
    void handleActionLogConversionException() throws Exception {
        List<ActionLogPayload> payloads = List.of(
                ActionLogPayload.builder().type("read").target("btn1").build()
        );

        doThrow(new ActionLogConversionException())
                .when(actionLogService).enqueueLogs(anyList());

        when(messageSource.getMessage(anyString(), any(Object[].class), anyString()))
                .thenReturn("로그 데이터를 저장하는 도중 오류가 발생했습니다.");

        mockMvc.perform(post("/action-log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        [{"type":"read","target":"btn1"}]
                    """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("로그 데이터를 저장하는 도중 오류가 발생했습니다."));
    }
}

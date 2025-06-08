package kodanect.domain.logging.service.impl;

import kodanect.domain.logging.dto.ActionLogPayload;
import kodanect.domain.logging.model.ActionLogContext;
import kodanect.domain.logging.service.ActionLogBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class ActionLogServiceImplTest {

    private ActionLogBuffer buffer;
    private ActionLogServiceImpl service;

    /**
     * 테스트 실행 전 Buffer를 Mock으로 초기화
     * ServiceImpl은 실제 비즈니스 로직만 테스트
     */
    @BeforeEach
    void setUp() {
        buffer = mock(ActionLogBuffer.class);
        service = new ActionLogServiceImpl(buffer);
    }

    /**
     * GIVEN: ActionLogPayload 리스트가 주어졌을 때
     * WHEN: enqueueLogs()를 호출하면
     * THEN: 각 payload가 ActionLogContext로 변환되어 buffer.enqueue()가 호출되어야 한다
     */
    @Test
    @DisplayName("payload들을 buffer에 enqueue하는지 테스트")
    void shouldEnqueuePayloadsToBuffer() {
        ActionLogPayload payload1 = ActionLogPayload.builder().type("read").target("btn1").build();
        ActionLogPayload payload2 = ActionLogPayload.builder().type("create").target("btn2").build();
        List<ActionLogPayload> payloads = List.of(payload1, payload2);

        service.enqueueLogs(payloads);

        verify(buffer, times(2)).enqueue(any(ActionLogContext.class));
    }

}

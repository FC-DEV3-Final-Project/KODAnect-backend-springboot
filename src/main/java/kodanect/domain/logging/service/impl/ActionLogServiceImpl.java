package kodanect.domain.logging.service.impl;

import kodanect.domain.logging.dto.ActionLogPayload;
import kodanect.domain.logging.model.ActionLogContext;
import kodanect.domain.logging.service.ActionLogBuffer;
import kodanect.domain.logging.service.ActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 액션 로그 서비스 구현체
 *
 * 로그 수집 요청을 큐에 비동기 적재
 */
@Service
@RequiredArgsConstructor
public class ActionLogServiceImpl implements ActionLogService {

    private final ActionLogBuffer actionLogBuffer;

    /**
     * 클라이언트 로그 요청을 큐에 비동기 적재
     *
     * @param payloads 클라이언트 로그 리스트
     */
    @Async("logExecutor")
    public void enqueueLogs(List<ActionLogPayload> payloads) {
        for (ActionLogPayload payload : payloads) {
            actionLogBuffer.enqueue(ActionLogContext.from(payload));
        }
    }

}

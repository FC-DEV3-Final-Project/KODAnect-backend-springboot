package kodanect.domain.logging.service;

import kodanect.domain.logging.dto.ActionLogPayload;

import java.util.List;

/**
 * 액션 로그 처리 서비스 인터페이스
 *
 * 클라이언트에서 수신한 로그를 큐에 적재
 */
public interface ActionLogService {

    /**
     * 액션 로그 리스트를 큐에 비동기 적재
     *
     * @param payloads 클라이언트 요청 로그 리스트
     */
    void enqueueLogs(List<ActionLogPayload> payloads);

}

package kodanect.domain.logging.service;

import kodanect.domain.logging.dto.ActionLogPayload;

import javax.servlet.http.HttpServletRequest;

/**
 * 이용 로그 저장 서비스 인터페이스
 *
 * - 클라이언트 UX 로그 및 메타 정보를 비동기로 기록
 */
public interface ActionLogService {

    /**
     * 클라이언트 UX 로그와 요청 정보를 기반으로 로그 저장
     *
     * @param payload 클라이언트 UX 로그 데이터
     * @param request 요청 메타데이터(IP, User-Agent 등 포함)
     */
    void saveLog(ActionLogPayload payload, HttpServletRequest request);

}

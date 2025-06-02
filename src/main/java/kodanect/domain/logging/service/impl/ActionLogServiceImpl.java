package kodanect.domain.logging.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kodanect.domain.logging.dto.ActionLogPayload;
import kodanect.domain.logging.support.CrudCode;
import kodanect.domain.logging.exception.LogSerializationException;
import kodanect.domain.logging.entity.ActionLog;
import kodanect.domain.logging.repository.ActionLogRepository;
import kodanect.domain.logging.service.ActionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 이용 로그 저장 서비스 구현체
 *
 * - 클라이언트 UX 데이터와 서버 메타데이터를 구조화하여 비동기로 저장
 * - JSON 직렬화 실패 시 사용자 정의 예외 발생
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActionLogServiceImpl implements ActionLogService {

    private final ActionLogRepository actionLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * 클라이언트 UX 로그와 서버 요청 정보를 조합하여 로그 저장
     *
     * - 비동기 실행 (@Async)
     * - JSON 직렬화 실패 시 예외 발생
     *
     * @param payload 클라이언트 UX 로그 데이터 (Map<String, Object>)
     * @param request 요청 메타데이터(IP, Header 등)
     */
    @Override
    @Async("logExecutor")
    public void saveLog(ActionLogPayload payload, HttpServletRequest request) {
        try {
            Map<String, Object> serverMetadata = extractServerMetadata(request);
            String structuredLogJson = toStructuredLogJson(payload.getData(), serverMetadata);

            ActionLog log = ActionLog.builder()
                    .urlName(request.getRequestURI())
                    .crudCode(CrudCode.fromHttpMethod(request.getMethod()).name())
                    .ipAddr(extractClientIp(request))
                    .logText(structuredLogJson)
                    .build();

            actionLogRepository.save(log);
        } catch (Exception e) {
            log.error("액션 로그 저장 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * 서버 요청 메타데이터 추출
     *
     * - 요청 헤더 및 시스템 정보를 조합
     *
     * @param request HttpServletRequest
     * @return 서버측 로그 관련 메타 정보
     */
    private Map<String, Object> extractServerMetadata(HttpServletRequest request) {
        Map<String, Object> meta = new LinkedHashMap<>();

        meta.put("timestamp", System.currentTimeMillis());
        meta.put("ip", extractClientIp(request));
        meta.put("userAgent", request.getHeader("User-Agent"));
        meta.put("referer", request.getHeader("Referer"));
        meta.put("lang", request.getHeader("Accept-Language"));

        meta.put("platform", request.getHeader("X-Platform"));
        meta.put("appVersion", request.getHeader("X-App-Version"));
        meta.put("osVersion", request.getHeader("X-OS-Version"));
        meta.put("deviceModel", request.getHeader("X-Device-Model"));
        meta.put("screen", request.getHeader("X-Screen"));

        return meta;
    }

    /**
     * 클라이언트 IP 주소 추출
     *
     * - X-Forwarded-For → RemoteAddr 우선순위로 반환
     *
     * @param request HttpServletRequest
     * @return 최종 IP 주소
     */
    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isEmpty()) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }

    /**
     * UX + 서버 메타데이터를 조합한 구조화 JSON 문자열 생성
     *
     * - 직렬화 실패 시 사용자 정의 예외 발생
     *
     * @param ux UX 로그 데이터
     * @param api 서버측 메타데이터
     * @return 구조화된 JSON 문자열
     */
    private String toStructuredLogJson(Map<String, Object> ux, Map<String, Object> api) {
        try {
            Map<String, Object> combined = new LinkedHashMap<>();
            combined.put("ux", ux);
            combined.put("api", api);
            return objectMapper.writeValueAsString(combined);
        } catch (JsonProcessingException e) {
            throw new LogSerializationException();
        }
    }

}

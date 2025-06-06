package kodanect.domain.logging.controller;

import kodanect.common.response.ApiResponse;
import kodanect.domain.logging.dto.ActionLogPayload;
import kodanect.domain.logging.service.ActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 액션 로그 수집 컨트롤러
 *
 * 클라이언트의 UI 상호작용 로그를 수신하여 비동기 처리 요청
 */
@RestController
@RequiredArgsConstructor
public class ActionLogController {

    private final MessageSourceAccessor messageSource;
    private final ActionLogService actionLogService;

    /**
     * 액션 로그 수집 요청 처리
     *
     * @param payloads 클라이언트가 전송한 액션 로그 리스트
     * @return 표준 API 응답
     */
    @PostMapping("/action-log")
    public ResponseEntity<ApiResponse<Void>> collectLogs(@RequestBody List<ActionLogPayload> payloads) {
        String message = messageSource.getMessage("log.save.success", new Object[]{});
        actionLogService.enqueueLogs(payloads);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message));
    }

}

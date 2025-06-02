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

import javax.servlet.http.HttpServletRequest;

/**
 * 액션 로그 수집 컨트롤러
 *
 * - 클라이언트에서 전달한 UX 로그 정보를 수집하여 비동기로 저장
 */
@RestController
@RequiredArgsConstructor
public class ActionLogController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final ActionLogService actionLogService;

    /**
     * 액션 로그 수집 API
     *
     * @param payload UX 로그 데이터
     * @param request HTTP 요청 정보
     * @return 저장 성공 응답
     */
    @PostMapping("/action-log")
    public ResponseEntity<ApiResponse<Void>> collectLog(@RequestBody ActionLogPayload payload, HttpServletRequest request) {
        String successMessage = messageSourceAccessor.getMessage("log.save.success", new Object[] {});
        actionLogService.saveLog(payload, request);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, successMessage));
    }
}

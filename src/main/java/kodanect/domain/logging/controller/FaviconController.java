package kodanect.domain.logging.controller;

import kodanect.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FaviconController
 *
 * 역할:
 * - 브라우저가 자동 요청하는 "/favicon.ico" 경로를 무시 처리
 * - 불필요한 로그 발생 및 에러 추적 방지를 위한 컨트롤러
 *
 * 설명:
 * - favicon.ico 요청은 HTML head에 명시되지 않으면 브라우저가 기본적으로 백엔드에 요청
 * - 해당 요청을 수신하되, 특별한 처리 없이 204 No Content 응답을 반환함
 * - 모든 응답은 공통 ApiResponse 포맷을 따름
 */
@RestController
public class FaviconController {

    /**
     * /favicon.ico 요청 무시 엔드포인트
     *
     * @return 성공 응답 (204 No Content), 데이터 없음
     */
    @GetMapping("/favicon.ico")
    public ApiResponse<Void> ignoreFavicon() {
        return ApiResponse.success(HttpStatus.NO_CONTENT, "Favicon 요청 무시됨");
    }

}

package kodanect.domain.logging.support;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 로그 메타데이터를 MDC에 주입하는 AOP
 *
 * 컨트롤러 진입 시점에 HTTP 요청 기반의 정보를 MDC에 저장
 * 로그 기록이나 비동기 실행 시 해당 정보가 활용됨
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ActionLogMdcAspect {

    private final HttpServletRequest request;

    /**
     * 컨트롤러 실행 전 HTTP 요청 정보를 MDC에 주입
     * 요청이 끝나면 MDC를 초기화
     *
     * @param joinPoint 실행 대상 메서드
     * @return 실제 메서드 실행 결과
     * @throws Throwable 예외 발생 시 그대로 전달
     */
    @Around("execution(* kodanect.domain.logging.controller..*(..))")
    public Object injectMdcMetadata(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            MDC.put("urlName", request.getRequestURI());
            MDC.put("ipAddr", extractClientIp(request));
            MDC.put("userAgent", request.getHeader("User-Agent"));
            MDC.put("referer", request.getHeader("Referer"));
            MDC.put("lang", request.getHeader("Accept-Language"));
            MDC.put("platform", request.getHeader("X-Platform"));
            MDC.put("appVersion", request.getHeader("X-App-Version"));
            MDC.put("osVersion", request.getHeader("X-OS-Version"));
            MDC.put("deviceModel", request.getHeader("X-Device-Model"));
            MDC.put("screen", request.getHeader("X-Screen"));

            return joinPoint.proceed();
        } finally {
            MDC.clear();
        }
    }

    /**
     * 클라이언트의 IP 주소를 추출
     *
     * 프록시 환경에서는 X-Forwarded-For 헤더의 첫 번째 값을 사용하고,
     * 없을 경우 request.getRemoteAddr() 사용
     *
     * @param request 현재 HTTP 요청
     * @return 클라이언트 IP 주소
     */
    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isEmpty()) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }

}

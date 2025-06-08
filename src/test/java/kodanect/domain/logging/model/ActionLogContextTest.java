package kodanect.domain.logging.model;

import kodanect.domain.logging.dto.ActionLogPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ActionLogContextTest {

    /**
     * 테스트 실행 전 MDC에 메타데이터 주입
     */
    @BeforeEach
    void setUpMdc() {
        MDC.put("urlName", "/test/1");
        MDC.put("ipAddr", "192.168.0.1");
        MDC.put("userAgent", "Chrome");
        MDC.put("referer", "https://test.com");
        MDC.put("lang", "ko");
        MDC.put("platform", "web");
        MDC.put("appVersion", "1.0.0");
        MDC.put("osVersion", "Windows 10");
        MDC.put("deviceModel", "Desktop");
        MDC.put("screen", "1920x1080");
    }

    /**
     * GIVEN: ActionLogPayload가 있고, MDC에 로그 메타데이터가 주입된 상태에서
     * WHEN: from()로 ActionLogContext를 생성하면
     * THEN: 모든 필드가 payload와 MDC에 저장된 값으로 올바르게 세팅되어야 한다
     */
    @Test
    @DisplayName("ActionLogContext 생성 테스트")
    void createContextFromPayloadAndMdc() {
        ActionLogPayload payload = ActionLogPayload.builder()
                .type("read")
                .target("testBtn")
                .build();

        ActionLogContext context = ActionLogContext.from(payload);

        assertThat(context.getType()).isEqualTo("read");
        assertThat(context.getTarget()).isEqualTo("testBtn");
        assertThat(context.getUrlName()).isEqualTo("/test/1");
        assertThat(context.getIpAddr()).isEqualTo("192.168.0.1");
        assertThat(context.getUserAgent()).isEqualTo("Chrome");
        assertThat(context.getReferer()).isEqualTo("https://test.com");
        assertThat(context.getLang()).isEqualTo("ko");
        assertThat(context.getPlatform()).isEqualTo("web");
        assertThat(context.getAppVersion()).isEqualTo("1.0.0");
        assertThat(context.getOsVersion()).isEqualTo("Windows 10");
        assertThat(context.getDeviceModel()).isEqualTo("Desktop");
        assertThat(context.getScreen()).isEqualTo("1920x1080");
        assertThat(context.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

}

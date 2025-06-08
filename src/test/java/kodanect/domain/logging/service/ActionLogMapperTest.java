package kodanect.domain.logging.service;

import kodanect.domain.logging.entity.ActionLog;
import kodanect.domain.logging.model.ActionLogContext;
import kodanect.domain.logging.model.UserActionKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ActionLogMapperTest {

    /**
     * GIVEN: UserActionKey와 ActionLogContext 리스트가 주어지고
     * WHEN: toEntityFromList()를 호출하면
     * THEN: 올바른 ActionLog 엔티티가 생성되어야 한다
     */
    @Test
    @DisplayName("ActionLogContext 리스트 → ActionLog 엔티티 직렬화 테스트")
    void mapActionLogContextToEntitySuccessfully() {
        UserActionKey key = new UserActionKey("192.168.0.1", "R");
        ActionLogContext context = ActionLogContext.builder()
                .type("read")
                .target("testBtn")
                .urlName("/test/1")
                .ipAddr("192.168.0.1")
                .userAgent("Chrome")
                .referer("https://test.com")
                .lang("ko")
                .platform("web")
                .appVersion("1.0.0")
                .osVersion("Windows 10")
                .deviceModel("Desktop")
                .screen("1920x1080")
                .timestamp(LocalDateTime.of(2025, 6, 8, 0, 0))
                .build();

        ActionLog result = ActionLogMapper.toEntityFromList(key, List.of(context));

        assertThat(result.getIpAddr()).isEqualTo("192.168.0.1");
        assertThat(result.getCrudCode()).isEqualTo("R");
        assertThat(result.getUrlName()).isEqualTo("/test/1");
        assertThat(result.getLogText()).contains(
                "read",
                "testBtn",
                "/test/1",
                "192.168.0.1",
                "Chrome",
                "https://test.com",
                "ko",
                "web",
                "1.0.0",
                "Windows 10",
                "Desktop",
                "1920x1080",
                "2025-06-08T00:00"
        );
    }

}

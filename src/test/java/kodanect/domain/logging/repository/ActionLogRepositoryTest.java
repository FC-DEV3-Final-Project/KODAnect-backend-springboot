package kodanect.domain.logging.repository;

import config.TestConfig;
import kodanect.domain.logging.entity.ActionLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class ActionLogRepositoryTest {

    @Autowired
    private ActionLogRepository actionLogRepository;

    /**
     * GIVEN: ActionLog 엔티티를 Builder로 생성하고 저장한 후
     * WHEN: Repository에서 전체 조회하면
     * THEN: 저장한 데이터가 정확히 1건 존재하고, 필드 값들이 일치해야 한다
     */
    @Test
    @DisplayName("ActionLog 저장 후 조회 테스트")
    void saveAndFindActionLog() {
        String logText = """
            {
              "type": "read",
              "target": "testBtn",
              "urlName": "/test/1",
              "ipAddr": "192.168.0.1",
              "userAgent": "Chrome",
              "referer": "https://test.com",
              "lang": "ko",
              "platform": "web",
              "appVersion": "1.0.0",
              "osVersion": "Windows 10",
              "deviceModel": "Desktop",
              "screen": "1920x1080",
              "timestamp": "2025-06-07T00:00:00"
            }
            """;

        ActionLog log = ActionLog.builder()
                .ipAddr("192.168.0.1")
                .crudCode("R")
                .urlName("/test")
                .logText(logText)
                .writeTime(LocalDateTime.now())
                .build();

        actionLogRepository.save(log);
        List<ActionLog> result = actionLogRepository.findAll();

        assertThat(result).hasSize(1);
        ActionLog fetched = result.get(0);

        assertThat(fetched.getIpAddr()).isEqualTo("192.168.0.1");
        assertThat(fetched.getCrudCode()).isEqualTo("R");
        assertThat(fetched.getUrlName()).isEqualTo("/test");
        assertThat(fetched.getLogText()).contains(
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
                "2025-06-07T00:00:00"
        );
        assertThat(fetched.getWriteTime()).isNotNull();
    }

}

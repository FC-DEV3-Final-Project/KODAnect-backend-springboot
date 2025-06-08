package kodanect.domain.logging.service;

import kodanect.domain.logging.model.ActionLogContext;
import kodanect.domain.logging.model.CrudCode;
import kodanect.domain.logging.model.UserActionKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ActionLogBufferTest {

    private ActionLogBuffer buffer;

    /**
     * 테스트 실행 전 ActionLogBuffer 초기화
     * 각 테스트가 독립된 버퍼 상태에서 실행되도록 보장
     */
    @BeforeEach
    void setUp() {
        buffer = new ActionLogBuffer();
    }

    /**
     * GIVEN: 특정 IP와 타입을 가진 로그가 주어지고
     * WHEN: enqueue()로 로그를 추가한 뒤 drainAll()을 호출하면
     * THEN: IP + CRUD 코드 기준으로 로그가 정확히 저장되어 추출되어야 한다
     */
    @Test
    @DisplayName("로그 큐에 IP 및 CRUD 기준으로 저장되는지 테스트")
    void enqueueLogByUserActionKey() {
        ActionLogContext log = buildLog("127.0.0.1", "read");

        buffer.enqueue(log);

        Map<UserActionKey, List<ActionLogContext>> result = buffer.drainAll();
        assertThat(result).hasSize(1);

        UserActionKey expectedKey = new UserActionKey("127.0.0.1", CrudCode.R.toCode());
        assertThat(result).containsKey(expectedKey);
        assertThat(result.get(expectedKey)).containsExactly(log);
    }

    /**
     * GIVEN: 동일한 키로 5개의 로그를 추가한 상태에서
     * WHEN: 임계치를 10으로 설정하면 로그가 추출되지 않고
     *       임계치를 3으로 설정하면 3개가 추출되어야 한다
     * THEN: 조건에 맞는 개수만큼 로그가 추출되어야 한다
     */
    @Test
    @DisplayName("임계치 이상일 때만 로그가 추출되는지 테스트")
    void drainOnlyWhenThresholdMet() {
        for (int i = 0; i < 5; i++) {
            buffer.enqueue(buildLog("192.168.0.1", "read"));
        }

        Map<UserActionKey, List<ActionLogContext>> resultLow = buffer.drainIfThresholdMet(10);
        assertThat(resultLow).isEmpty();

        Map<UserActionKey, List<ActionLogContext>> result = buffer.drainIfThresholdMet(3);
        assertThat(result).hasSize(1);

        UserActionKey key = new UserActionKey("192.168.0.1", CrudCode.R.toCode());
        assertThat(result.get(key)).hasSize(3);
    }

    /**
     * GIVEN: 서로 다른 키로 3개의 로그가 버퍼에 저장된 상태에서
     * WHEN: drainAll()을 호출하면 전체 로그가 추출되고
     * THEN: 이후 drainAll() 재호출 시에는 빈 결과가 반환되어야 한다
     */
    @Test
    @DisplayName("전체 로그가 추출되고 버퍼가 비워지는지 테스트")
    void drainAllShouldEmptyAllQueues() {
        buffer.enqueue(buildLog("10.0.0.1", "create"));
        buffer.enqueue(buildLog("10.0.0.1", "create"));
        buffer.enqueue(buildLog("10.0.0.2", "update"));

        Map<UserActionKey, List<ActionLogContext>> result = buffer.drainAll();
        assertThat(result).hasSize(2);

        Map<UserActionKey, List<ActionLogContext>> afterDrain = buffer.drainAll();
        assertThat(afterDrain).isEmpty();
    }

    /**
     * 테스트용 로그 객체 생성
     *
     * @param ip 특정 IP
     * @param type CRUD 타입 (create, read 등)
     * @return ActionLogContext 인스턴스
     */
    private ActionLogContext buildLog(String ip, String type) {
        return ActionLogContext.builder()
                .ipAddr(ip)
                .type(type)
                .target("testBtn")
                .urlName("/test/1")
                .userAgent("Chrome")
                .referer("https://test.com")
                .lang("ko")
                .platform("web")
                .appVersion("1.0.0")
                .osVersion("Windows 10")
                .deviceModel("Desktop")
                .screen("1920x1080")
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }

}

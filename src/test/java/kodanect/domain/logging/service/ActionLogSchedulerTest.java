package kodanect.domain.logging.service;

import kodanect.domain.logging.entity.ActionLog;
import kodanect.domain.logging.model.ActionLogContext;
import kodanect.domain.logging.model.CrudCode;
import kodanect.domain.logging.model.UserActionKey;
import kodanect.domain.logging.repository.ActionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionLogSchedulerTest {

    private ActionLogBuffer buffer;
    private ActionLogRepository repository;
    private ActionLogScheduler scheduler;
    @Captor
    private ArgumentCaptor<List<ActionLog>> captor;

    /**
     * 테스트 실행 전 ActionLogBuffer 초기화
     * 각 테스트가 독립된 버퍼 상태에서 실행되도록 보장
     */
    @BeforeEach
    void setUp() {
        buffer = mock(ActionLogBuffer.class);
        repository = mock(ActionLogRepository.class);
        scheduler = new ActionLogScheduler(buffer, repository);
    }

    /**
     * GIVEN: READ 로그가 임계치(100)를 초과한 상태에서
     * WHEN: flushReadLogs()를 실행하면
     * THEN: 해당 로그가 저장소에 saveAll()로 저장되어야 한다
     */
    @Test
    @DisplayName("READ 로그 임계치 도달 시 저장 테스트")
    void shouldFlushReadLogsWhenThresholdMet() {
        UserActionKey key = new UserActionKey("127.0.0.1", CrudCode.R.toCode());
        List<ActionLogContext> logs = List.of(buildLog("read"), buildLog("read"));

        when(buffer.drainIfThresholdMet(100)).thenReturn(Map.of(key, logs));

        scheduler.flushReadLogs();

        verify(repository).saveAll(captor.capture());
        List<ActionLog> saved = captor.getValue();
        assertThat(saved).hasSize(1);
    }

    /**
     * GIVEN: C/U/D/X 로그가 각각 임계치(10)를 초과한 상태에서
     * WHEN: flushOtherLogs()를 실행하면
     * THEN: 각 로그가 저장소에 atLeastOnce()로 저장되어야 한다
     */
    @Test
    @DisplayName("기타 로그 임계치 도달 시 저장 테스트")
    void shouldFlushOtherLogsWhenThresholdMet() {
        for (CrudCode code : List.of(CrudCode.C, CrudCode.U, CrudCode.D, CrudCode.X)) {
            UserActionKey key = new UserActionKey("192.168.0.1", code.toCode());
            List<ActionLogContext> logs = List.of(buildLog(code.name()));

            when(buffer.drainIfThresholdMet(10)).thenReturn(Map.of(key, logs));

            scheduler.flushOtherLogs();

            verify(repository, atLeastOnce()).saveAll(any());
        }
    }

    /**
     * GIVEN: 버퍼에 남은 로그가 존재할 때
     * WHEN: flushAllLogsForcefully()를 호출하면
     * THEN: 잔여 로그도 저장소에 저장되어야 한다
     */
    @Test
    @DisplayName("잔여 로그 강제 저장 테스트")
    void shouldFlushAllLogsForcefully() {
        UserActionKey key = new UserActionKey("10.0.0.1", CrudCode.C.toCode());
        List<ActionLogContext> logs = List.of(buildLog("create"));

        when(buffer.drainAll()).thenReturn(Map.of(key, logs));

        scheduler.flushAllLogsForcefully();

        verify(repository).saveAll(any());
    }

    /**
     * 테스트용 로그 객체 생성
     *
     * @param type CRUD 타입 (create, read 등)
     * @return ActionLogContext 인스턴스
     */
    private ActionLogContext buildLog(String type) {
        return ActionLogContext.builder()
                .type(type)
                .target("testBtn")
                .urlName("/test/1")
                .ipAddr("127.0.0.1")
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

package kodanect.domain.logging.service;

import kodanect.domain.logging.entity.ActionLog;
import kodanect.domain.logging.model.ActionLogContext;
import kodanect.domain.logging.model.CrudCode;
import kodanect.domain.logging.model.UserActionKey;
import kodanect.domain.logging.repository.ActionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 액션 로그 저장 스케줄러
 *
 * 사용자 로그가 임계치에 도달하면 DB에 저장
 * 로그 유형별로 서로 다른 임계치와 주기로 처리
 */
@Component
@RequiredArgsConstructor
public class ActionLogScheduler {

    private static final int FIVE_MINUTES_MILLIS = 5 * 60 * 1000;
    private static final int TEN_MINUTES_MILLIS = 10 * 60 * 1000;
    private static final int THIRTY_MINUTES_MILLIS = 30 * 60 * 1000;

    private static final int READ_THRESHOLD = 100;
    private static final int OTHER_THRESHOLD = 10;

    private final ActionLogBuffer actionLogBuffer;
    private final ActionLogRepository actionLogRepository;

    /**
     * READ 로그를 5분 주기로 확인하여 임계치 이상이면 저장
     */
    @Scheduled(fixedDelay = FIVE_MINUTES_MILLIS)
    public void flushReadLogs() {
        flush(CrudCode.R, READ_THRESHOLD);
    }

    /**
     * 기타 로그(C/U/D/X)를 10분 주기로 확인하여 임계치 이상이면 저장
     */
    @Scheduled(fixedDelay = TEN_MINUTES_MILLIS)
    public void flushOtherLogs() {
        for (CrudCode code : List.of(CrudCode.C, CrudCode.U, CrudCode.D, CrudCode.X)) {
            flush(code, OTHER_THRESHOLD);
        }
    }

    /**
     * 지정된 CRUD 코드에 대해 로그를 큐에서 꺼내어 저장
     */
    private void flush(CrudCode crudCode, int threshold) {
        Map<UserActionKey, List<ActionLogContext>> drained = actionLogBuffer.drainIfThresholdMet(threshold);

        List<ActionLog> entities = new ArrayList<>();
        for (Map.Entry<UserActionKey, List<ActionLogContext>> entry : drained.entrySet()) {
            if (crudCode.toCode().equals(entry.getKey().getCrudCode())) {
                ActionLog entity = ActionLogMapper.toEntityFromList(entry.getKey(), entry.getValue());
                entities.add(entity);
            }
        }

        if (!entities.isEmpty()) {
            actionLogRepository.saveAll(entities);
        }
    }

    /**
     * 잔여 로그를 30분 주기로 강제 저장
     *
     * 임계치에 도달하지 않은 사용자 로그를 포함하여 전체 버퍼를 비우고 저장
     */
    @Scheduled(fixedDelay = THIRTY_MINUTES_MILLIS)
    public void flushAllLogsForcefully() {
        Map<UserActionKey, List<ActionLogContext>> allLogs = actionLogBuffer.drainAll();
        List<ActionLog> entities = new ArrayList<>();

        for (Map.Entry<UserActionKey, List<ActionLogContext>> entry : allLogs.entrySet()) {
            ActionLog entity = ActionLogMapper.toEntityFromList(entry.getKey(), entry.getValue());
            entities.add(entity);
        }

        if (!entities.isEmpty()) {
            actionLogRepository.saveAll(entities);
        }
    }

}

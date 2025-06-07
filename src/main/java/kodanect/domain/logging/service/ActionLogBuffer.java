package kodanect.domain.logging.service;

import kodanect.domain.logging.model.ActionLogContext;
import kodanect.domain.logging.model.CrudCode;
import kodanect.domain.logging.model.UserActionKey;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 액션 로그 임시 저장소
 *
 * 사용자 IP와 CRUD 유형 기준으로 로그를 메모리에 분류 저장
 * 일정 개수 이상 쌓이면 로그를 배치로 추출하여 DB에 저장
 */
@Component
public class ActionLogBuffer {

    private final Map<UserActionKey, Queue<ActionLogContext>> buffer = new ConcurrentHashMap<>();

    /**
     * 액션 로그 큐에 추가
     *
     * @param log 로그 컨텍스트
     */
    public void enqueue(ActionLogContext log) {
        String ip = log.getIpAddr();
        String crudCode = CrudCode.fromType(log.getType()).toCode();
        UserActionKey key = new UserActionKey(ip, crudCode);

        buffer.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(log);
    }

    /**
     * 큐별로 임계치 이상 쌓인 로그를 추출하여 반환
     *
     * @param threshold 임계 수량
     * @return 추출된 로그 리스트 (key 기준으로 묶임)
     */
    public Map<UserActionKey, List<ActionLogContext>> drainIfThresholdMet(int threshold) {
        Map<UserActionKey, List<ActionLogContext>> result = new HashMap<>();

        for (Map.Entry<UserActionKey, Queue<ActionLogContext>> entry : buffer.entrySet()) {
            Queue<ActionLogContext> queue = entry.getValue();

            if (queue.size() >= threshold) {
                List<ActionLogContext> drained = new ArrayList<>();

                while (!queue.isEmpty() && drained.size() < threshold) {
                    drained.add(queue.poll());
                }

                result.put(entry.getKey(), drained);
            }
        }

        return result;
    }

    /**
     * 모든 로그 큐를 비워서 추출
     *
     * 임계 수량과 상관없이 전체 사용자 로그를 꺼내어 반환하고,
     * 버퍼에서 해당 로그들을 제거함
     *
     * @return 추출된 로그 리스트 (key 기준으로 묶임)
     */
    public Map<UserActionKey, List<ActionLogContext>> drainAll() {
        Map<UserActionKey, List<ActionLogContext>> result = new HashMap<>();

        for (Map.Entry<UserActionKey, Queue<ActionLogContext>> entry : buffer.entrySet()) {
            Queue<ActionLogContext> queue = entry.getValue();
            List<ActionLogContext> drained = new ArrayList<>();

            while (!queue.isEmpty()) {
                drained.add(queue.poll());
            }

            if (!drained.isEmpty()) {
                result.put(entry.getKey(), drained);
            }
        }
        return result;
    }

}

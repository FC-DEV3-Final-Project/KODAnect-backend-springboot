package kodanect.domain.logging.support;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * 비동기 실행 시 MDC(Log context)를 전파하는 데코레이터
 *
 * 비동기 쓰레드에서 로그 추적 정보를 유지하기 위해 사용
 */
public class MdcTaskDecorator implements TaskDecorator {

    /**
     * 현재 스레드의 MDC 컨텍스트를 캡처해 비동기 작업에 전달
     * 실행 후 MDC를 반드시 초기화
     *
     * @param runnable 비동기 실행 대상
     * @return MDC context가 설정된 Runnable
     */
    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

}

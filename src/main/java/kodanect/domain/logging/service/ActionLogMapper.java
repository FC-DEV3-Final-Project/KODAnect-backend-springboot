package kodanect.domain.logging.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kodanect.domain.logging.entity.ActionLog;
import kodanect.domain.logging.exception.ActionLogConversionException;
import kodanect.domain.logging.model.ActionLogContext;
import kodanect.domain.logging.model.UserActionKey;

import java.util.List;

/**
 * 액션 로그 컨텍스트 → DB 엔티티 변환 유틸리티
 *
 * 로그 데이터를 JSON 문자열로 직렬화하여 DB 저장용 엔티티로 구성
 */
public class ActionLogMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ActionLogMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 액션 로그 리스트를 ActionLog 엔티티로 변환
     *
     * @param key   사용자 IP 및 CRUD 코드 기반 키
     * @param logs  해당 키로 수집된 로그 목록
     * @return DB 저장용 ActionLog 엔티티
     * @throws ActionLogConversionException JSON 직렬화 실패 시 예외 발생
     */
    public static ActionLog toEntityFromList(UserActionKey key, List<ActionLogContext> logs) {
        try {
            String jsonArray = objectMapper.writeValueAsString(logs);
            String representativeUrl = logs.get(0).getUrlName();

            return ActionLog.builder()
                    .urlName(representativeUrl)
                    .ipAddr(key.getIp())
                    .crudCode(key.getCrudCode())
                    .logText(jsonArray)
                    .build();
        } catch (JsonProcessingException e) {
            throw new ActionLogConversionException("Failed to convert log list to JSON.", e);
        }
    }

}

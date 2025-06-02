package kodanect.domain.logging.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 클라이언트에서 전달한 UX 로그 데이터 요청 DTO
 *
 * - 다양한 형태의 UX 로그 데이터를 key-value 형태로 전달받기 위한 구조
 */
@NoArgsConstructor
@Getter
@Setter
public class ActionLogPayload {

    /**
     * 클라이언트가 전달하는 유동적인 UX 로그 데이터
     */
    private Map<String, Object> data;
}

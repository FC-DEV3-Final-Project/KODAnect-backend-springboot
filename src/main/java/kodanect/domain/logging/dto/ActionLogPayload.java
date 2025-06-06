package kodanect.domain.logging.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

/**
 * 클라이언트에서 수집한 액션 로그 요청 데이터
 *
 * 행동 유형과 대상 요소 포함
 */
@Getter
@Builder
public class ActionLogPayload {

    @NotBlank
    private String type;

    @NotBlank
    private String target;

}

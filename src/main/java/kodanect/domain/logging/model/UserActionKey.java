package kodanect.domain.logging.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 액션 로그 그룹핑 키
 *
 * 사용자 IP와 CRUD 코드 조합을 기반으로 로그를 그룹핑
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class UserActionKey {

    private final String ip;
    private final String crudCode;

}

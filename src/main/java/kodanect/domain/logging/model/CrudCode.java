package kodanect.domain.logging.model;

import lombok.Getter;

import java.util.List;

/**
 * 사용자 액션의 CRUD 유형 분류 열거형
 *
 * 프론트에서 전달한 type 값을 기준으로 적절한 코드 매핑
 * - C: create 계열
 * - R: read/view 계열
 * - U: update 계열
 * - D: delete 계열
 * - X: 분류 불가 (기본값)
 */
@Getter
public enum CrudCode {

    C("create"),
    R("read"),
    U("update"),
    D("delete"),
    X();

    private final List<String> types;

    CrudCode(String... types) {
        this.types = List.of(types);
    }

    /**
     * type 문자열을 기반으로 적절한 CRUD 코드 반환
     *
     * 매칭되는 코드가 없으면 X 반환
     */
    public static CrudCode fromType(String type) {
        if (type == null) {
            return X;
        }

        String lowerType = type.toLowerCase();
        for (CrudCode code : values()) {
            if (code.types.contains(lowerType)) {
                return code;
            }
        }
        return X;
    }

    /**
     * Enum 이름(C, R, U, D, X)을 문자열 코드로 반환
     */
    public String toCode() {
        return name();
    }

}

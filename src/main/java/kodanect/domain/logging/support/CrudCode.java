package kodanect.domain.logging.support;

/**
 * CRUD 코드 열거형
 *
 * - HTTP 메서드를 기반으로 CRUD 구분
 * - 지원되지 않는 메서드는 UNKNOWN 처리
 */
public enum CrudCode {
    C, R, U, D, UNKNOWN;

    /**
     * HTTP 메서드를 기반으로 CRUD 코드 반환
     *
     * @param method HTTP 메서드 (예: GET, POST)
     * @return 대응되는 CrudCode (C, R, U, D, UNKNOWN)
     */
    public static CrudCode fromHttpMethod(String method) {
        if (method == null || method.isBlank()) {
            return UNKNOWN;
        }

        return switch (method.toUpperCase()) {
            case "GET" -> R;
            case "POST" -> C;
            case "PUT", "PATCH" -> U;
            case "DELETE" -> D;
            default -> UNKNOWN;
        };
    }
}

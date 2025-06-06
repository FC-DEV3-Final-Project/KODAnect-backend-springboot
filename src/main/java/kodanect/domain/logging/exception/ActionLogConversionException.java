package kodanect.domain.logging.exception;

/**
 * 액션 로그 변환 실패 예외
 *
 * 로그 데이터를 JSON 문자열로 직렬화하는 과정에서 오류 발생 시 사용
 */
public class ActionLogConversionException extends RuntimeException {

    public ActionLogConversionException(String message, Throwable cause) {
      super(message, cause);
    }

    public ActionLogConversionException() {
      super("Failed to convert log data to JSON string.");
    }
}

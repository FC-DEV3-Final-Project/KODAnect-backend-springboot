package kodanect.domain.logging.exception;

/**
 * 로그 JSON 직렬화 실패 예외
 */
public class LogSerializationException extends RuntimeException {

    public LogSerializationException(String message) {
        super(message);
    }

    public LogSerializationException() {
        super("로그 데이터를 JSON 문자열로 변환하는 데 실패했습니다.");
    }

}

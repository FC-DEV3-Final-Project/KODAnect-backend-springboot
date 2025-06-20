package kodanect.domain.remembrance.exception;

import kodanect.common.exception.custom.AbstractCustomException;
import org.springframework.http.HttpStatus;

import static kodanect.common.exception.config.MessageKeys.PAGINATION_INVALID;

/** 페이지 범위가 잘못 됐을 경우 발생하는 예외 */
public class InvalidPaginationException extends AbstractCustomException {

    private final Integer cursor;
    private final int size;
    private final String date;

    public InvalidPaginationException(Integer cursor, int size, String date) {
        super(PAGINATION_INVALID);
        this.cursor = cursor;
        this.size = size;
        this.date = date;
    }

    public InvalidPaginationException(Integer cursor, int size) {
        super(PAGINATION_INVALID);
        this.cursor = cursor;
        this.size = size;
        this.date = null;
    }

    @Override
    public String getMessageKey() {
        return PAGINATION_INVALID;
    }

    @Override
    public Object[] getArguments() {
        return new Object[] {cursor, size, date};
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

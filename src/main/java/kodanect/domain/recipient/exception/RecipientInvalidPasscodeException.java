package kodanect.domain.recipient.exception;

import kodanect.common.exception.custom.AbstractCustomException;
import kodanect.domain.recipient.dto.RecipientRequestDto;
import org.springframework.http.HttpStatus;

import static kodanect.common.exception.config.MessageKeys.RECIPIENT_INVALID_PASSCODE;

public class RecipientInvalidPasscodeException extends AbstractCustomException {

    private final String letterSeq;
    private final RecipientRequestDto requestDto; // 사용자가 입력한 게시글 내용을 담을 필드 추가

    public RecipientInvalidPasscodeException(String letterSeq) {
        super(RECIPIENT_INVALID_PASSCODE);
        this.letterSeq = letterSeq;
        this.requestDto = null;
    }

    // 비밀번호 불일치 메시지와 함께 사용자가 입력한 게시글 내용을 전달하는 생성자
    public RecipientInvalidPasscodeException(String message, RecipientRequestDto requestDto) {
        super(message);         // 전달받은 메시지를 사용 (RECIPIENT_INVALID_PASSCODE 메시지 키와는 별개)
        this.letterSeq = null;
        this.requestDto = requestDto;
    }

    @Override
    public String getMessageKey() {
        return RECIPIENT_INVALID_PASSCODE;
    }

    @Override
    public Object[] getArguments() {
        return new Object[]{letterSeq};
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getMessage() {
        // requestDto가 있으면 해당 메시지를, 없으면 기본 메시지를 반환
        if (requestDto != null) {
            return String.format("비밀번호가 일치하지 않습니다. (게시글 수정 요청 데이터 포함)");
        }
        return String.format("[비밀번호 불일치] letterSeq=%s", letterSeq);
    }

    // 사용자가 입력한 게시글 내용을 반환하는 getter
    public RecipientRequestDto getRequestDto() {
        return requestDto;
    }
}
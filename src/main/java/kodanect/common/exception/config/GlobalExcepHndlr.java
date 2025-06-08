package kodanect.common.exception.config;

import kodanect.common.response.ApiResponse;
import kodanect.domain.donation.exception.*;
import kodanect.domain.remembrance.exception.*;
import kodanect.domain.remembrance.exception.InvalidPaginationRangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * 전역 예외 핸들러
 *
 * 컨트롤러에서 발생하는 404, 500 예외를 ApiResponse 포맷으로 응답
 *
 * 역할
 * - 존재하지 않는 리소스 요청 처리
 * - 알 수 없는 서버 내부 예외 처리
 *
 * 특징
 * - 사용자 정의 예외는 처리하지 않음
 * - 컨트롤러 단에서 발생한 표준 오류 응답 전용
 */
@Slf4j
@RestControllerAdvice
public class GlobalExcepHndlr {

    private final MessageSourceAccessor messageSourceAccessor;

    // 생성자를 통해 MessageSourceAccessor를 주입받습니다.
    public GlobalExcepHndlr(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }
    /**
     * 400 예외 처리
     *
     * 잘못된 입력 발생 시 400 응답 반환
     * */
    @ExceptionHandler({
        InvalidDonateSeqException.class,
        InvalidEmotionTypeException.class,
        InvalidPaginationRangeException.class,
        InvalidReplySeqException.class,
        InvalidSearchDateFormatException.class,
        InvalidSearchDateRangeException.class,
        MissingReplyContentException.class,
        MissingReplyPasswordException.class,
        MissingReplyWriterException.class,
        MissingSearchDateParameterException.class,
        ReplyIdMismatchException.class,
        ReplyPostMismatchException.class,
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest() {
        return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."));
    }

    /**
     * 403 예외 처리
     *
     * 권한 오류 발생 시 403 응답 반환
     * */
    @ExceptionHandler(ReplyPasswordMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."));
    }

    /**
     * 404 예외 처리 (Resource Not Found)
     * - 매핑되지 않은 URI 요청 또는 명시적으로 NOT_FOUND 예외를 던진 경우
     */
    @ExceptionHandler({
        MemorialNotFoundException.class,
        MemorialReplyNotFoundException.class,
        NoHandlerFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFound() {

        String msg = messageSourceAccessor.getMessage("error.notfound", "요청한 자원을 찾을 수 없습니다.");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND, msg));
    }

    /**
     * 409 예외 처리
     *
     * 충돌 발생 시 409 응답 반환
     */
    @ExceptionHandler(ReplyAlreadyDeleteException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict() {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(HttpStatus.CONFLICT, "해당 항목은 이미 삭제되었습니다."));
    }


    /**
     * 500 예외 처리
     *
     * 처리되지 않은 메세지키 미응답시 500 응답 반환
     */
    @ExceptionHandler(NoSuchMessageException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoMessage(NoSuchMessageException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 키 없음"));
    }



    /**
     * 파일 처리 중 발생하는 IO 예외 처리
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<Void>> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다."));
    }



    @ExceptionHandler({
        BadRequestException.class,
        InvalidDeleteOriginalImageException.class,
        InvalidPaginationFormatException.class,
        MissingPaginationParameterException.class,
        NotFoundAreaCode.class,
        NotFoundException.class,
        ValidationFailedException.class,
        PasscodeMismatchException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleDonationClientError(RuntimeException ex) {
        String msg;
        try {
            msg = messageSourceAccessor.getMessage(ex.getMessage());
        }
        catch (Exception e) {
            msg = ex.getMessage(); // 메시지 키가 아니면 그대로
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, msg));
    }

    @ExceptionHandler({
        DonationNotFoundException.class,
        DonationCommentNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(RuntimeException ex) {
        String msg;

        try {
            // 예외 메시지는 메시지 키라고 가정하고 메시지 소스에서 해석
            msg = messageSourceAccessor.getMessage(ex.getMessage());
        } catch (Exception e) {
            // 메시지 키가 없거나 message.properties에 정의 안되어 있을 경우
            msg = ex.getMessage() != null ? ex.getMessage() : "요청하신 리소스를 찾을 수 없습니다.";
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND, msg));
    }

    /**
     * @ModelAttribute 바인딩 실패 예외 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException ex) {
        Optional<String> errorMessageOpt = ex.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst();

        String errorMessage = errorMessageOpt.orElse("잘못된 요청입니다.");
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, errorMessage));
    }



    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Unhandled exception: ", ex);

        String message = messageSourceAccessor.getMessage("error.internal"); //
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }

    /**
     * 500 예외 처리: 나머지 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerError(Exception ex) {
        log.error("Unhandled exception: ", ex);
        String msg = messageSourceAccessor.getMessage("error.internal", "서버 내부 오류가 발생했습니다.");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, msg));
    }

    /**
     * 400 예외 처리: @RequestBody @Valid 검증 실패 시 MethodArgumentNotValidException 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        // 가장 첫 번째 에러 메시지 키를 가져옴
        String defaultMsgKey = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        String resolvedMsg;

        try {
            // 키를 메시지 소스에서 해석
            resolvedMsg = messageSourceAccessor.getMessage(defaultMsgKey);
        }
        catch (Exception e) {
            // 메시지 소스에서 못 찾으면 그냥 키 문자열 그대로 사용
            resolvedMsg = defaultMsgKey;
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, resolvedMsg));
    }
}

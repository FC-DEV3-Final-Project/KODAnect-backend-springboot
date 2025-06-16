package kodanect.common.exception.config;

import io.sentry.Sentry;
import kodanect.common.exception.custom.InvalidFileNameException;
import kodanect.common.response.ApiResponse;
import kodanect.domain.donation.exception.BadRequestException;
import kodanect.domain.donation.exception.DonationNotFoundException;
import kodanect.domain.donation.exception.ValidationFailedException;
import kodanect.domain.donation.exception.*;
import kodanect.domain.remembrance.exception.*;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
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

@RestControllerAdvice
public class GlobalExcepHndlr {

    private static final SecureLogger log = SecureLogger.getLogger(GlobalExcepHndlr.class);
    private final MessageSourceAccessor messageSourceAccessor;

    // 생성자를 통해 MessageSourceAccessor를 주입받습니다.
    public GlobalExcepHndlr(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }


    /**
     * 403 예외 처리
     * <p>
     * 권한 오류 발생 시 403 응답 반환
     */
    @ExceptionHandler(CommentPasswordMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."));
    }

    /**
     * 404 예외 처리 (Resource Not Found)
     * - 매핑되지 않은 URI 요청 또는 명시적으로 NOT_FOUND 예외를 던진 경우
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound() {

        String msg = messageSourceAccessor.getMessage("error.notfound", "요청한 자원을 찾을 수 없습니다.");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND, msg));
    }

    /**
     * 400 예외 처리: @RequestBody @Valid 검증 실패 시 MethodArgumentNotValidException 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String defaultMsgKey = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("default.validation.message");

        String resolvedMsg;
        try {
            resolvedMsg = messageSourceAccessor.getMessage(defaultMsgKey);
        } catch (NoSuchMessageException e) {
            resolvedMsg = defaultMsgKey;
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, resolvedMsg));
    }

    /**
     * 400 예외 처리: @PathVariable, @RequestParam 등에서 @Min, @NotBlank 검증 실패 시 ConstraintViolationException 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ConstraintViolationException ex) {

        String firstMessageKey = ex.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("default.validation.message");

        String resolvedMsg = messageSourceAccessor.getMessage(firstMessageKey, "잘못된 요청입니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, resolvedMsg));
    }


    /**
     * 500 예외 처리
     * <p>
     * 처리되지 않은 메세지키 미응답시 500 응답 반환
     */
    @ExceptionHandler(NoSuchMessageException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoMessage(NoSuchMessageException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 키 없음"));
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
        } catch (Exception e) {
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
        log.info("BindException 발생: {}", errorMessage);
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, errorMessage));
    }


    /**
     * 400 예외 처리: 서비스에서 throw new IllegalArgumentException("...") 한 경우
     * - ex.getMessage() 가 메시지 키라면 메시지 소스로부터 실제 문구를 찾아서 사용
     * - 메시지 키가 아닌 일반 한글 메시지라면 그대로 반환
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArg(IllegalArgumentException ex) {
        String keyOrMsg = ex.getMessage();
        // messageSourceAccessor 에 해당 키가 있는지 먼저 시도
        String msg;
        try {
            msg = messageSourceAccessor.getMessage(keyOrMsg);
        } catch (Exception e) {
            // 키가 없으면 ex.getMessage() 를 그대로 사용
            msg = keyOrMsg;
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, msg));
    }

    /**
     * 파일 처리 중 발생하는 IO 예외 처리
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<Void>> handleIOException(IOException ex) {
        log.error("파일 처리 중 IOException 발생: {}", ex.getMessage(), ex);
        Sentry.captureException(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        Sentry.captureException(ex);
        String message = messageSourceAccessor.getMessage("error.internal"); // ← 이 줄이 핵심
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }


    /**
     * enum 바인딩 실패 처리(valid 오류 터트 리기 위함)
     */

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleEnumConversionError(ConversionFailedException ex) {
        String message = messageSourceAccessor.getMessage("donation.story.areaCode.invalid", null, "잘못된 권역 코드입니다.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, message));
    }


    /**
     * 500 예외 처리: 나머지 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerError(Exception ex) {
        Sentry.captureException(ex);
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        String msg = messageSourceAccessor.getMessage("error.internal", "서버 내부 오류가 발생했습니다.");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, msg));
    }

    /**
     * 잘못된 파일명 예외 처리 (ex: 경로 조작 가능성 등)
     */
    @ExceptionHandler(InvalidFileNameException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidFileName(InvalidFileNameException ex) {
        String msg;
        try {
            msg = messageSourceAccessor.getMessage(ex.getMessageKey(), ex.getArguments(), "허용되지 않는 파일명입니다.");
        } catch (Exception e) {
            msg = ex.getMessage();
        }
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.fail(ex.getStatus(), msg));
    }

    /**
     * 파라미터 타입 불일치 예외 처리 (예: int에 문자열 전달)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = Optional.of(ex.getName()).orElse("알 수 없음");
        String value = Optional.ofNullable(ex.getValue()).map(String::valueOf).orElse("null");
        String expected = Optional.ofNullable(ex.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("Unknown");

        log.warn("잘못된 파라미터 타입 요청: name={}, value={}, requiredType={}", name, value, expected, ex);

        String message = String.format("요청 파라미터 '%s'의 값 '%s'은(는) 타입 '%s'으로 변환할 수 없습니다.",
                name, value, expected);

        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, message));
    }

    /**
     * JSON 파싱 오류, 형식 오류 (예: 잘못된 @RequestBody)
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("요청 본문 파싱 실패", ex);
        Sentry.captureException(ex);
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "요청 본문이 올바르지 않습니다."));
    }

    /**
     * 지원하지 않는 HTTP 메서드 (예: GET만 지원하는데 POST 요청)
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("지원하지 않는 HTTP 메서드", ex);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."));
    }

    /**
     * 필수 요청 파라미터 누락 (@RequestParam)
     */
    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        log.warn("필수 요청 파라미터 '{}' 누락", name);
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다: " + name));
    }

    /**
     * URL 경로 변수 누락 (@PathVariable)
     */
    @ExceptionHandler(org.springframework.web.bind.MissingPathVariableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPathVar(MissingPathVariableException ex) {
        log.warn("URL 경로 변수 '{}' 누락", ex.getVariableName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "URL 경로 변수 '" + ex.getVariableName() + "' 가 누락되었습니다."));
    }

    /**
     * 지원하지 않는 Content-Type 요청
     */
    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        log.warn("지원하지 않는 Content-Type 요청", ex);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.fail(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 Content-Type 입니다."));
    }

    /**
     * 타입 변환 실패 (예: Enum에 없는 값 요청)
     */
    @ExceptionHandler(org.springframework.core.convert.ConversionFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleConversionFailed(ConversionFailedException ex) {
        log.warn("요청 값 변환 실패", ex);
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."));
    }



}

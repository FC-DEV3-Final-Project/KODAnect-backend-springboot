package kodanect.common.exception.config;

import kodanect.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
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

    /**
     * 404 예외 처리
     *
     * 매핑되지 않은 URI 요청에 대해 404 응답 반환
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."));
    }

    /**
     * 500 예외 처리
     *
     * 처리되지 않은 런타임 예외에 대해 500 응답 반환
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerError() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."));
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

    /**
     * @Valided 유효성 검사 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        Optional<String> errorMessageOpt = ex.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst();

        String errorMessage = errorMessageOpt.orElse("유효하지 않은 요청입니다.");
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, errorMessage));
    }

}

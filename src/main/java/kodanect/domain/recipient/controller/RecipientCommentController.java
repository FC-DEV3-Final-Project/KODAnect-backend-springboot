package kodanect.domain.recipient.controller;

import kodanect.common.exception.config.SecureLogger;
import kodanect.common.response.ApiResponse;
import kodanect.domain.recipient.dto.RecipientCommentAuthRequestDto;
import kodanect.common.response.CursorCommentPaginationResponse;
import kodanect.domain.recipient.dto.CommentDeleteRequestDto;
import kodanect.domain.recipient.dto.RecipientCommentRequestDto;
import kodanect.domain.recipient.dto.RecipientCommentResponseDto;
import kodanect.domain.recipient.dto.RecipientCommentUpdateRequestDto;
import kodanect.domain.recipient.exception.RecipientInvalidPasscodeException;
import kodanect.domain.recipient.service.RecipientCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/recipientLetters")
public class RecipientCommentController {

    private static final SecureLogger logger = SecureLogger.getLogger(RecipientCommentController.class);

    private final RecipientCommentService recipientCommentService;

    public RecipientCommentController(RecipientCommentService recipientCommentService) {
        this.recipientCommentService = recipientCommentService;
    }

    /** ## 특정 게시물의 "더보기" 댓글 조회 API (커서 기반 페이징 적용)

     **요청:** `GET /recipientLetters/{letterSeq}/comments`
     **파라미터:** `letterSeq` (Path Variable), `lastCommentId`, `size`
     **응답:** `ApiResponse<CursorReplyPaginationResponse<RecipientCommentResponseDto, Long>>`
     */
    @GetMapping("/{letterSeq}/comments")
    public ResponseEntity<ApiResponse<CursorCommentPaginationResponse<RecipientCommentResponseDto, Integer>>> getPaginatedCommentsForRecipient(
            @PathVariable("letterSeq") Integer letterSeq,
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "3") int size) {  // **댓글 한 번에 가져올 개수 (기본값 3)**
        logger.info("페이징된 댓글 조회 요청: letterSeq={}, lastCommentId={}, size={}", letterSeq, cursor, size);
        CursorCommentPaginationResponse<RecipientCommentResponseDto, Integer> responseData =
                recipientCommentService.selectPaginatedCommentsForRecipient(letterSeq, cursor, size);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "댓글 목록 조회 성공", responseData));
    }

    //  댓글 작성
    @PostMapping("/{letterSeq}/comments")
    public ResponseEntity<ApiResponse<RecipientCommentResponseDto>> writeComment(@PathVariable("letterSeq") Integer letterSeq,
                                                                                 @Valid @RequestBody RecipientCommentRequestDto requestDto) {

        RecipientCommentResponseDto createdComment = recipientCommentService.insertComment(
                letterSeq, // 게시물 번호를 직접 서비스로 전달
                requestDto // DTO 객체 전달
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "댓글이 성공적으로 등록되었습니다.", createdComment));
    }

    // 댓글 인증 API (비밀번호 확인)
    @PostMapping("/{letterSeq}/comments/{commentSeq}/verifyPwd")
    public ResponseEntity<ApiResponse<?>> verifyPwdComment(@PathVariable("letterSeq") Integer letterSeq,
                                                           @PathVariable("commentSeq") Integer commentSeq,
                                                           @Valid @RequestBody RecipientCommentAuthRequestDto authRequestDto) {
        try {
            // 서비스 계층에서 비밀번호 검증 수행. 실패 시 RecipientInvalidPasscodeException(commentSeq) 발생
            recipientCommentService.authenticateComment(commentSeq, authRequestDto.getCommentPasscode());
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "댓글 인증에 성공했습니다.", null));
        }
        catch (RecipientInvalidPasscodeException e) {
            logger.warn("댓글 비밀번호 확인 실패: commentSeq={}, error={}", commentSeq, e.getMessage());
            // 단순 비밀번호 확인 실패이므로, 입력 데이터는 반환하지 않음
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(HttpStatus.UNAUTHORIZED, e.getMessage()));
        }
    }

    // 댓글 수정 API (인증 후 호출)
    @PutMapping("/{letterSeq}/comments/{commentSeq}")
    public ResponseEntity<ApiResponse<?>> updateComment(@PathVariable("letterSeq") Integer letterSeq,
                                                        @PathVariable("commentSeq") Integer commentSeq,
                                                        @Valid @RequestBody RecipientCommentUpdateRequestDto requestDto,
                                                        BindingResult bindingResult // @Valid 유효성 검사 결과
    ) {
        logger.info("댓글 수정 요청 시작: commentSeq={}", commentSeq);

        // @Valid 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            // 유효성 검사 실패 시에도 사용자가 입력한 데이터를 반환
            return ResponseEntity.badRequest().body(ApiResponse.fail(HttpStatus.BAD_REQUEST, errorMessage, requestDto));
        }

        try {
            // 서비스 계층에서 비밀번호 확인 및 댓글 수정 로직 수행
            RecipientCommentResponseDto updatedComment = recipientCommentService.updateComment(commentSeq, requestDto);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "댓글이 성공적으로 수정되었습니다.", null));

        } catch (RecipientInvalidPasscodeException e) {
            // 비밀번호 불일치 예외 처리
            logger.warn("댓글 수정 중 비밀번호 불일치: commentSeq={}, error={}", commentSeq, e.getMessage());
            // RecipientInvalidPasscodeException에 담긴 사용자가 입력한 데이터 (requestDto)를 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (Exception e) {
            // 기타 예외 처리 (예: 댓글을 찾을 수 없는 경우)
            logger.error("댓글 수정 중 오류 발생: commentSeq={}, error={}", commentSeq, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 수정 중 오류가 발생했습니다."));
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{letterSeq}/comments/{commentSeq}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable("letterSeq") Integer letterSeq,
                                                           @PathVariable("commentSeq") Integer commentSeq,
                                                           @Valid @RequestBody CommentDeleteRequestDto requestDto) {
        logger.info("댓글 삭제 요청: letterSeq={}, commentSeq={}", letterSeq, commentSeq);

        // 서비스 계층으로 전달
        recipientCommentService.deleteComment(
                letterSeq,
                commentSeq,
                requestDto.getCommentPasscode()
        );

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "댓글이 성공적으로 삭제되었습니다."));
    }
}
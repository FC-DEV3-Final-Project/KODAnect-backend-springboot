package kodanect.domain.heaven.controller;

import kodanect.common.response.ApiResponse;
import kodanect.common.response.CursorCommentPaginationResponse;
import kodanect.domain.heaven.dto.request.HeavenCommentCreateRequest;
import kodanect.domain.heaven.dto.request.HeavenCommentVerifyRequest;
import kodanect.domain.heaven.dto.response.HeavenCommentResponse;
import kodanect.domain.heaven.service.HeavenCommentService;
import lombok.AllArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/heavenLetters/{letterSeq}/comments")
@AllArgsConstructor
public class HeavenCommentController {

    private final HeavenCommentService heavenCommentService;
    private final MessageSourceAccessor messageSourceAccessor;

    /* 댓글 더보기 */
    @GetMapping
    public ResponseEntity<ApiResponse<CursorCommentPaginationResponse<HeavenCommentResponse, Integer>>> getMoreCommentList(
            @PathVariable Integer letterSeq,
            @RequestParam Integer cursor,
            @RequestParam(defaultValue = "3") int size
    ) {
        CursorCommentPaginationResponse<HeavenCommentResponse, Integer> commentList = heavenCommentService.getMoreCommentList(letterSeq, cursor, size);

        String message = messageSourceAccessor.getMessage("heaven.comment.read.success");

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message, commentList));
    }

    /* 댓글 등록 */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createHeavenComment(
            @PathVariable Integer letterSeq,
            @RequestBody HeavenCommentCreateRequest heavenCommentCreateRequest
    ) {
        heavenCommentService.createHeavenComment(letterSeq, heavenCommentCreateRequest);

        String message = messageSourceAccessor.getMessage("heaven.comment.create.success");

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.CREATED, message));
    }

    /* 댓글 삭제 */
    @DeleteMapping("/{commentSeq}")
    public ResponseEntity<ApiResponse<Void>> deleteHeavenComment(
            @PathVariable Integer letterSeq,
            @PathVariable Integer commentSeq,
            @RequestBody HeavenCommentVerifyRequest heavenCommentVerifyRequest
    ) {
        heavenCommentService.deleteHeavenComment(letterSeq, commentSeq, heavenCommentVerifyRequest);

        String message = messageSourceAccessor.getMessage("heaven.comment.delete.success");

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message));
    }
}

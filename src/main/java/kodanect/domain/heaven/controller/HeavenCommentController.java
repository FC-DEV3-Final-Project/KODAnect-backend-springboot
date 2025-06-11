package kodanect.domain.heaven.controller;

import kodanect.common.response.ApiResponse;
import kodanect.common.response.CursorReplyPaginationResponse;
import kodanect.domain.heaven.dto.HeavenCommentResponse;
import kodanect.domain.heaven.service.HeavenCommentService;
import lombok.AllArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/heavenLetters")
@AllArgsConstructor
public class HeavenCommentController {

    private final HeavenCommentService heavenCommentService;
    private final MessageSourceAccessor messageSourceAccessor;

    /* 댓글 더보기 */
    @GetMapping("/{letterSeq}/comments")
    public ResponseEntity<ApiResponse<CursorReplyPaginationResponse<HeavenCommentResponse, Integer>>> getMoreCommentList(
            @PathVariable Integer letterSeq,
            @RequestParam Integer cursor,
            @RequestParam(defaultValue = "3") int size
    ) {
        CursorReplyPaginationResponse<HeavenCommentResponse, Integer> commentList = heavenCommentService.getMoreCommentList(letterSeq, cursor, size);

        String message = messageSourceAccessor.getMessage("heaven.comment.read.success");

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message, commentList));
    }
}

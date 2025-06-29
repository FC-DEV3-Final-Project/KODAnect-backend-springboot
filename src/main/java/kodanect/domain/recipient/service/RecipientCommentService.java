package kodanect.domain.recipient.service;

import kodanect.common.response.CursorCommentPaginationResponse;
import kodanect.domain.recipient.dto.RecipientCommentRequestDto;
import kodanect.domain.recipient.dto.RecipientCommentResponseDto;
import kodanect.domain.recipient.dto.RecipientCommentUpdateRequestDto;

public interface RecipientCommentService {
    // 댓글 작성
    RecipientCommentResponseDto insertComment(Integer letterSeq, RecipientCommentRequestDto requestDto);

    void authenticateComment(Integer commentSeq, String inputPasscode);

    // 댓글 수정
    RecipientCommentResponseDto updateComment(Integer commentSeq, RecipientCommentUpdateRequestDto requestDto);

    // 댓글 삭제
    void deleteComment(Integer letterSeq, Integer commentSeq, String inputPasscode);

    CursorCommentPaginationResponse<RecipientCommentResponseDto, Integer> selectPaginatedCommentsForRecipient(Integer letterSeq, Integer lastCommentId, Integer size);
}

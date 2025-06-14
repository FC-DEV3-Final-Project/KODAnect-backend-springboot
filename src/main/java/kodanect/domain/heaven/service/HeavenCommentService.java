package kodanect.domain.heaven.service;

import kodanect.common.response.CursorCommentPaginationResponse;
import kodanect.domain.heaven.dto.request.HeavenCommentCreateRequest;
import kodanect.domain.heaven.dto.response.HeavenCommentResponse;

import java.util.List;

public interface HeavenCommentService {

    /* 댓글 전체 조회 (페이징) */
    List<HeavenCommentResponse> getHeavenCommentList(Integer letterSeq, Integer cursor, int size);

    /* 댓글 더보기 (페이징) */
    CursorCommentPaginationResponse<HeavenCommentResponse, Integer> getMoreCommentList(Integer letterSeq, Integer cursor, int size);

    /* 댓글 생성 */
    void createHeavenComment(Integer letterSeq, HeavenCommentCreateRequest heavenCommentCreateRequest);
}

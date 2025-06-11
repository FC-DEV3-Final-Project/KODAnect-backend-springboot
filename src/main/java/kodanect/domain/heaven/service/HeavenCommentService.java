package kodanect.domain.heaven.service;

import kodanect.common.response.CursorReplyPaginationResponse;
import kodanect.domain.heaven.dto.HeavenCommentResponse;

import java.util.List;

public interface HeavenCommentService {

    /* 게시물 전체 조회 (페이징) */
    List<HeavenCommentResponse> getHeavenCommentList(Integer letterSeq, Integer cursor, int size);

    /* 댓글 더보기 (페이징) */
    CursorReplyPaginationResponse<HeavenCommentResponse, Integer> getMoreCommentList(Integer letterSeq, Integer cursor, int size);
}

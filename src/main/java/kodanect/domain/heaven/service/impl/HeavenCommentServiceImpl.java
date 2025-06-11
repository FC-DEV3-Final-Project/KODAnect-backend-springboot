package kodanect.domain.heaven.service.impl;

import kodanect.common.response.CursorReplyPaginationResponse;
import kodanect.common.util.CursorFormatter;
import kodanect.domain.heaven.dto.HeavenCommentResponse;
import kodanect.domain.heaven.repository.HeavenCommentRepository;
import kodanect.domain.heaven.service.HeavenCommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HeavenCommentServiceImpl implements HeavenCommentService {

    private HeavenCommentRepository heavenCommentRepository;

    /* 게시물 전체 조회 (페이징) */
    @Override
    public List<HeavenCommentResponse> getHeavenCommentList(Integer letterSeq, Integer cursor, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        return heavenCommentRepository.findByCursor(letterSeq, cursor, pageable);
    }

    /* 댓글 더보기 (페이징) */
    @Override
    public CursorReplyPaginationResponse<HeavenCommentResponse, Integer> getMoreCommentList(Integer letterSeq, Integer cursor, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        List<HeavenCommentResponse> heavenCommentResponseList = heavenCommentRepository.findByCursor(letterSeq, cursor, pageable);

        return CursorFormatter.cursorReplyFormat(heavenCommentResponseList, size);
    }
}

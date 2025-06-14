package kodanect.domain.heaven.service.impl;

import kodanect.common.response.CursorCommentPaginationResponse;
import kodanect.common.util.CursorFormatter;
import kodanect.domain.heaven.dto.request.HeavenCommentCreateRequest;
import kodanect.domain.heaven.dto.request.HeavenCommentVerifyRequest;
import kodanect.domain.heaven.dto.response.HeavenCommentResponse;
import kodanect.domain.heaven.entity.Heaven;
import kodanect.domain.heaven.entity.HeavenComment;
import kodanect.domain.heaven.exception.HeavenCommentNotFoundException;
import kodanect.domain.heaven.exception.HeavenNotFoundException;
import kodanect.domain.heaven.repository.HeavenCommentRepository;
import kodanect.domain.heaven.repository.HeavenRepository;
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
    private HeavenRepository heavenRepository;

    /* 게시물 전체 조회 (페이징) */
    @Override
    public List<HeavenCommentResponse> getHeavenCommentList(Integer letterSeq, Integer cursor, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        return heavenCommentRepository.findByCursor(letterSeq, cursor, pageable);
    }

    /* 댓글 더보기 (페이징) */
    @Override
    public CursorCommentPaginationResponse<HeavenCommentResponse, Integer> getMoreCommentList(Integer letterSeq, Integer cursor, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        List<HeavenCommentResponse> heavenCommentResponseList = heavenCommentRepository.findByCursor(letterSeq, cursor, pageable);

        return CursorFormatter.cursorCommentFormat(heavenCommentResponseList, size);
    }

    /* 댓글 등록 */
    @Override
    public void createHeavenComment(Integer letterSeq, HeavenCommentCreateRequest heavenCommentCreateRequest) {
        Heaven heaven = heavenRepository.findById(letterSeq)
                .orElseThrow(() -> new HeavenNotFoundException(letterSeq));

        HeavenComment heavenComment = HeavenComment.builder()
                .heaven(heaven)
                .commentWriter(heavenCommentCreateRequest.getCommentWriter())
                .commentPasscode(heavenCommentCreateRequest.getCommentPasscode())
                .contents(heavenCommentCreateRequest.getContents())
                .build();

        heavenCommentRepository.save(heavenComment);
    }

    /* 댓글 삭제 */
    @Override
    public void deleteHeavenComment(Integer letterSeq, Integer commentSeq, HeavenCommentVerifyRequest heavenCommentVerifyRequest) {
        Heaven heaven = heavenRepository.findById(letterSeq)
                .orElseThrow(() -> new HeavenNotFoundException(letterSeq));
        HeavenComment heavenComment = heavenCommentRepository.findById(commentSeq)
                .orElseThrow(() -> new HeavenCommentNotFoundException(commentSeq));

        heavenComment.verifyPasscode(heavenCommentVerifyRequest.getCommentPasscode());

        heavenCommentRepository.delete(heavenComment);
    }
}

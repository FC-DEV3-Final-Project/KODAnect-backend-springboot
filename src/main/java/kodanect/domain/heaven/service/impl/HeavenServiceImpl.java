package kodanect.domain.heaven.service.impl;

import kodanect.common.response.CursorPaginationResponse;
import kodanect.common.response.CursorReplyPaginationResponse;
import kodanect.common.util.CursorFormatter;
import kodanect.domain.heaven.dto.HeavenCommentResponse;
import kodanect.domain.heaven.dto.HeavenDetailResponse;
import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.dto.HeavenVerifyResponse;
import kodanect.domain.heaven.entity.Heaven;
import kodanect.domain.heaven.repository.HeavenCommentRepository;
import kodanect.domain.heaven.repository.HeavenRepository;
import kodanect.domain.heaven.service.HeavenCommentService;
import kodanect.domain.heaven.service.HeavenService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class HeavenServiceImpl implements HeavenService {

    /* 기본값 */
    private static final int COMMENT_SIZE = 3;

    private final HeavenRepository heavenRepository;
    private final HeavenCommentRepository heavenCommentRepository;
    private final HeavenCommentService heavenCommentService;

    /* 게시물 전체 조회 (페이징) */
    @Override
    public CursorPaginationResponse<HeavenResponse, Integer> getHeavenList(Integer cursor, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        List<HeavenResponse> heavenResponseList = heavenRepository.findByCursor(cursor, pageable);

        long count = heavenRepository.count();

        return CursorFormatter.cursorFormat(heavenResponseList, size, count);
    }

    /* 검색을 통한 게시물 전체 조회 (페이징) */
    @Override
    public CursorPaginationResponse<HeavenResponse, Integer> getHeavenListSearchResult(String searchType, String keyword, Integer cursor, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        long count = countBySearchType(searchType, keyword);

        List<HeavenResponse> heavenResponseList = findBySearchType(searchType, keyword, cursor, pageable);

        return CursorFormatter.cursorFormat(heavenResponseList, size, count);
    }

    /* 게시물 상세 조회 */
    @Transactional
    @Override
    public HeavenDetailResponse getHeavenDetail(Integer letterSeq) {
        /* 게시물 상세 조회 */
        Heaven heaven = heavenRepository.findById(letterSeq).orElseThrow();

        /* 조회수 증가 */
        heaven.addReadCount();

        /* 댓글 리스트 조회 */
        List<HeavenCommentResponse> heavenCommentList = heavenCommentService.getHeavenCommentList(letterSeq, null, COMMENT_SIZE);

        /* 댓글 개수 조회 */
        int commentCount = heavenCommentRepository.countByHeaven(heaven);

        CursorReplyPaginationResponse<HeavenCommentResponse, Integer> cursorPaginationResponse = CursorFormatter.cursorReplyFormat(heavenCommentList, COMMENT_SIZE);

        return HeavenDetailResponse.of(heaven, cursorPaginationResponse, commentCount);
    }

    /* 게시물 비밀번호 일치 여부 */
    @Override
    public HeavenVerifyResponse verifyPasscode(Integer letterSeq, String letterPasscode) {
        String findPassCode = heavenRepository.findPassCodeByLetterSeq(letterSeq);

        int result = Objects.equals(findPassCode, letterPasscode) ? 1 : 0;

        return HeavenVerifyResponse.of(result);
    }

    /* 검색 조건에 따른 게시물 개수 조회 */
    private long countBySearchType(String searchType, String keyword) {
        return switch (searchType) {
            case "all"    -> heavenRepository.countByTitleOrContentsContaining(keyword);
            case "title"  -> heavenRepository.countByTitleContaining(keyword);
            case "content"-> heavenRepository.countByContentsContaining(keyword);
            default       -> throw new IllegalArgumentException("Invalid search type: " + searchType);
        };
    }

    /* 검색 조건에 따른 게시물 조회 */
    private List<HeavenResponse> findBySearchType(String searchType, String keyword, Integer cursor, Pageable pageable) {
        return switch (searchType) {
            case "all"    -> heavenRepository.findByTitleOrContentsContaining(keyword, cursor, pageable);
            case "title"  -> heavenRepository.findByTitleContaining(keyword, cursor, pageable);
            case "content"-> heavenRepository.findByContentsContaining(keyword, cursor, pageable);
            default       -> throw new IllegalArgumentException("Invalid search type: " + searchType);
        };
    }
}

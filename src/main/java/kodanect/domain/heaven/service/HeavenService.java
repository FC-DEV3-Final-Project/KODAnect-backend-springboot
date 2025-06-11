package kodanect.domain.heaven.service;

import kodanect.common.response.CursorPaginationResponse;
import kodanect.domain.heaven.dto.HeavenDetailResponse;
import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.dto.HeavenVerifyResponse;

public interface HeavenService {

    /* 게시물 전체 조회 (페이징) */
    CursorPaginationResponse<HeavenResponse, Integer> getHeavenList(Integer cursor, int size);

    /* 검색을 통한 게시물 전체 조회 (페이징) */
    CursorPaginationResponse<HeavenResponse, Integer> getHeavenListSearchResult(String searchType, String keyword, Integer cursor, int size);

    /* 게시물 상세 조회 */
    HeavenDetailResponse getHeavenDetail(Integer letterSeq);

    /* 게시물 비밀번호 일치 여부 */
    HeavenVerifyResponse verifyPasscode(Integer letterSeq, String letterPasscode);
}

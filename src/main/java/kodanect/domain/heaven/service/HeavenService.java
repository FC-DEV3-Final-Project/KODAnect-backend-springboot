package kodanect.domain.heaven.service;

import kodanect.common.response.CursorPaginationResponse;
import kodanect.domain.heaven.dto.HeavenCreateRequest;
import kodanect.domain.heaven.dto.HeavenDetailResponse;
import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.dto.HeavenUpdateRequest;

public interface HeavenService {

    /* 게시물 전체 조회 (페이징) */
    CursorPaginationResponse<HeavenResponse, Integer> getHeavenList(Integer cursor, int size);

    /* 검색을 통한 게시물 전체 조회 (페이징) */
    CursorPaginationResponse<HeavenResponse, Integer> getHeavenListSearchResult(String type, String keyWord, Integer cursor, int size);

    /* 게시물 상세 조회 */
    HeavenDetailResponse getHeavenDetail(Integer letterSeq);

    /* 게시물 비밀번호 일치 여부 */
    void verifyPasscode(Integer letterSeq, String letterPasscode);

    /* 게시물 생성 */
    void createHeaven(HeavenCreateRequest heavenCreateRequest);

    /* 게시물 수정 */
    void updateHeaven(Integer letterSeq, HeavenUpdateRequest heavenUpdateRequest);

    /* 게시물 삭제 */
    void deleteHeaven(Integer letterSeq, String letterPasscode);
}

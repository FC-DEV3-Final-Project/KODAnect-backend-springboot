package kodanect.domain.heaven.controller;

import kodanect.common.response.ApiResponse;
import kodanect.common.response.CursorPaginationResponse;
import kodanect.domain.heaven.dto.HeavenDetailResponse;
import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.service.HeavenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/heavenLetters")
@RequiredArgsConstructor
public class HeavenController {

    private final HeavenService heavenService;
    private final MessageSourceAccessor messageSourceAccessor;

    /* 게시물 전체 조회 (페이징) */
    @GetMapping
    public ResponseEntity<ApiResponse<CursorPaginationResponse<HeavenResponse, Integer>>> getHeavenList(
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        CursorPaginationResponse<HeavenResponse, Integer> heavenList = heavenService.getHeavenList(cursor, size);

        String message = messageSourceAccessor.getMessage("heaven.list.get.success");

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message, heavenList));
    }

    /* 검색을 통한 게시물 전체 조회 (페이징) */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<CursorPaginationResponse<HeavenResponse, Integer>>> searchHeavenList(
            @RequestParam(value = "type") String searchType,
            @RequestParam String keyword,
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        CursorPaginationResponse<HeavenResponse, Integer> heavenList = heavenService.getHeavenListSearchResult(searchType, keyword, cursor, size);

        String message = messageSourceAccessor.getMessage("heaven.list.get.success");

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message, heavenList));
    }

    /* 게시물 상세 조회 */
    @GetMapping("/{letterSeq}")
    public ResponseEntity<ApiResponse<HeavenDetailResponse>> getHeavenDetail(
            @PathVariable Integer letterSeq
    ) {
        HeavenDetailResponse heavenDetailResponse = heavenService.getHeavenDetail(letterSeq);

        String message = messageSourceAccessor.getMessage("heaven.detail.get.success");

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message, heavenDetailResponse));
    }
}

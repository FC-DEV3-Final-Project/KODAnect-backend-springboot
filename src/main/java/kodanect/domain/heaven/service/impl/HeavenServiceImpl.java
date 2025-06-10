package kodanect.domain.heaven.service.impl;

import kodanect.common.response.CursorPaginationResponse;
import kodanect.common.util.CursorFormatter;
import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.repository.HeavenRepository;
import kodanect.domain.heaven.service.HeavenService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeavenServiceImpl implements HeavenService {

    private final HeavenRepository heavenRepository;

    public HeavenServiceImpl(HeavenRepository heavenRepository) {
        this.heavenRepository = heavenRepository;
    }

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

        long count = heavenRepository.count();

        List<HeavenResponse> heavenResponseList = searchByType(searchType, keyword, cursor, pageable);

        return CursorFormatter.cursorFormat(heavenResponseList, size, count);
    }

    private List<HeavenResponse> searchByType(String type, String keyword, Integer cursor, Pageable pageable) {
        return switch (type) {
            case "all"    -> heavenRepository.findByTitleOrContentsContaining(keyword, cursor, pageable);
            case "title"  -> heavenRepository.findByTitleContaining(keyword, cursor, pageable);
            case "content"-> heavenRepository.findByContentsContaining(keyword, cursor, pageable);
            default       -> throw new IllegalArgumentException("Invalid search type: " + type);
        };
    }
}

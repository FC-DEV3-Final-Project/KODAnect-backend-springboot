package kodanect.domain.heaven.service.impl;

import kodanect.common.response.CursorPaginationResponse;
import kodanect.common.response.CursorReplyPaginationResponse;
import kodanect.common.util.CursorFormatter;
import kodanect.common.validation.HeavenCreateRequestValidator;
import kodanect.domain.heaven.dto.*;
import kodanect.domain.heaven.entity.Heaven;
import kodanect.domain.heaven.repository.HeavenCommentRepository;
import kodanect.domain.heaven.repository.HeavenRepository;
import kodanect.domain.heaven.service.HeavenCommentService;
import kodanect.domain.heaven.service.HeavenService;
import kodanect.domain.remembrance.entity.Memorial;
import kodanect.domain.remembrance.repository.MemorialRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@AllArgsConstructor
public class HeavenServiceImpl implements HeavenService {

    /* 기본값 */
    private static final int COMMENT_SIZE = 3;
    private static final String FILE_PATH = "/app/uploads";

    private final HeavenRepository heavenRepository;
    private final HeavenCommentRepository heavenCommentRepository;
    private final MemorialRepository memorialRepository;
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
    public void verifyPasscode(Integer letterSeq, String letterPasscode) {
        Heaven heaven = heavenRepository.findById(letterSeq).orElseThrow(); // 예외 (추후 구현)

        heaven.verifyPasscode(letterPasscode);
    }

    /* 게시물 생성 */
    @Override
    public void createHeaven(HeavenCreateRequest heavenCreateRequest) {

        Memorial memorial = memorialRepository.findById(heavenCreateRequest.getDonateSeq()).orElse(null);
        MultipartFile file = heavenCreateRequest.getFile();
        String fileName = "";
        String orgFileName = "";

        /* memorial과 Request DonorName 유효성 검사 */
        HeavenCreateRequestValidator.validateHeavenCreateRequest(heavenCreateRequest.getDonorName(), memorial);

        /* 파일 생성 */
        if (file != null && !file.isEmpty()) {
            Map<String, String> fileMap = saveFile(file);
            fileName = fileMap.get("fileName");
            orgFileName = fileMap.get("orgFileName");
        }

        Heaven heaven = Heaven.builder()
                .memorial(memorial)
                .letterTitle(heavenCreateRequest.getLetterTitle())
                .donorName(heavenCreateRequest.getDonorName())
                .letterPasscode(heavenCreateRequest.getLetterPasscode())
                .letterWriter(heavenCreateRequest.getLetterWriter())
                .anonymityFlag(heavenCreateRequest.getAnonymityFlag())
                .readCount(0)
                .letterContents(heavenCreateRequest.getLetterContents())
                .fileName(fileName)
                .orgFileName(orgFileName)
                .build();

        heavenRepository.save(heaven);
    }

    /* 게시물 삭제 */
    @Override
    public void deleteHeaven(Integer letterSeq, String letterPasscode) {
        Heaven heaven = heavenRepository.findById(letterSeq).orElseThrow(); // 예외 (추후 구현)

        heaven.verifyPasscode(letterPasscode);

        heavenRepository.deleteById(letterSeq);
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

    /* 파일 저장 및 문자열 처리 */
    private Map<String, String> saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            String orgFileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new RuntimeException("원본 파일명이 없습니다.")); // 임시 처리 추후 구현 예정
            Path path = Paths.get(FILE_PATH, fileName);
            Files.copy(file.getInputStream(), path);

            return Map.of("fileName", fileName, "orgFileName", orgFileName);
        } catch (IOException e) {
            throw new RuntimeException("파일 에러"); // 임시 처리 추후 구현 예정
        }
    }
}

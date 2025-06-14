package kodanect.domain.heaven.service.impl;

import kodanect.common.response.CursorCommentCountPaginationResponse;
import kodanect.common.response.CursorPaginationResponse;
import kodanect.common.util.CursorFormatter;
import kodanect.common.validation.HeavenDonorValidator;
import kodanect.domain.heaven.dto.request.HeavenCreateRequest;
import kodanect.domain.heaven.dto.request.HeavenUpdateRequest;
import kodanect.domain.heaven.dto.response.HeavenCommentResponse;
import kodanect.domain.heaven.dto.response.HeavenDetailResponse;
import kodanect.domain.heaven.dto.response.HeavenResponse;
import kodanect.domain.heaven.entity.Heaven;
import kodanect.domain.heaven.exception.FileStorageException;
import kodanect.domain.heaven.exception.HeavenNotFoundException;
import kodanect.domain.heaven.exception.InvalidTypeException;
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
    private final HeavenCommentService heavenCommentService;
    private final MemorialRepository memorialRepository;

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
    public CursorPaginationResponse<HeavenResponse, Integer> getHeavenListSearchResult(String type, String keyWord, Integer cursor, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        long count = countByType(type, keyWord);

        List<HeavenResponse> heavenResponseList = findByType(type, keyWord, cursor, pageable);

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
        List<HeavenCommentResponse> heavenCommentList = heavenCommentService.getHeavenCommentList(letterSeq, null, COMMENT_SIZE + 1);

        /* 댓글 개수 조회 */
        int commentCount = heavenCommentRepository.countByHeaven(heaven);

        CursorCommentCountPaginationResponse<HeavenCommentResponse, Integer> cursorCommentCountPaginationResponse =
                CursorFormatter.cursorCommentCountFormat(heavenCommentList, COMMENT_SIZE, commentCount);

        return HeavenDetailResponse.of(heaven, cursorCommentCountPaginationResponse);
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

        /* memorial과 Request DonorName 유효성 검사 */
        HeavenDonorValidator.validateDonorNameMatches(heavenCreateRequest.getDonorName(), memorial);

        /* 파일 생성 */
        Map<String, String> fileMap = saveFile(heavenCreateRequest.getFile());
        String fileName = fileMap.get("fileName");
        String orgFileName = fileMap.get("orgFileName");

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

    /* 게시물 수정 */
    @Override
    public void updateHeaven(Integer letterSeq, HeavenUpdateRequest heavenUpdateRequest) {
        Heaven heaven = heavenRepository.findById(letterSeq)
                .orElseThrow(() -> new HeavenNotFoundException(letterSeq));
        Memorial memorial = memorialRepository.findById(heavenUpdateRequest.getDonateSeq()).orElse(null);

        /* 유효성 검사 */
        HeavenDonorValidator.validateDonorNameMatches(heavenUpdateRequest.getDonorName(), memorial);

        Map<String, String> fileMap = updateFile(heavenUpdateRequest.getFile(), heaven);

        heaven.updateHeaven(heavenUpdateRequest, memorial, fileMap);
    }

    /* 게시물 삭제 */
    @Override
    public void deleteHeaven(Integer letterSeq, String letterPasscode) {
        Heaven heaven = heavenRepository.findById(letterSeq)
                .orElseThrow(() -> new HeavenNotFoundException(letterSeq));

        heaven.verifyPasscode(letterPasscode);

        heavenRepository.deleteById(letterSeq);
    }

    /* 검색 조건에 따른 게시물 개수 조회 */
    private long countByType(String type, String keyWord) {
        return switch (type) {
            case "all"    -> heavenRepository.countByTitleOrContentsContaining(keyWord);
            case "title"  -> heavenRepository.countByTitleContaining(keyWord);
            case "content"-> heavenRepository.countByContentsContaining(keyWord);
            default       -> throw new InvalidTypeException(type);
        };
    }

    /* 검색 조건에 따른 게시물 조회 */
    private List<HeavenResponse> findByType(String type, String keyWord, Integer cursor, Pageable pageable) {
        return switch (type) {
            case "all"    -> heavenRepository.findByTitleOrContentsContaining(keyWord, cursor, pageable);
            case "title"  -> heavenRepository.findByTitleContaining(keyWord, cursor, pageable);
            case "content"-> heavenRepository.findByContentsContaining(keyWord, cursor, pageable);
            default       -> throw new InvalidTypeException(type);
        };
    }

    /* 파일 저장 및 문자열 처리 */
    private Map<String, String> saveFile(MultipartFile file) {
        String orgFileName = "";
        String fileName = "";

        if (file != null && !file.isEmpty()) {
            orgFileName = file.getOriginalFilename();
            String extension = orgFileName.substring(orgFileName.lastIndexOf("."));
            fileName = UUID.randomUUID().toString().replace("-", "").toUpperCase() + extension;

            Path path = Paths.get(FILE_PATH);
            Path filePath = path.resolve(fileName);

            try {
                file.transferTo(filePath);
            } catch (IOException e) {
                throw new FileStorageException(filePath, orgFileName);
            }
        }

        return Map.of("fileName", fileName, "orgFileName", orgFileName);
    }

    /* 파일 수정 -> 같은 파일인지 확인 후 같은 거면 그대로 반환 */
    private Map<String, String> updateFile(MultipartFile newFile, Heaven heaven) {
        String newOrgFileName = newFile.getOriginalFilename();
        String currentOrgFileName = heaven.getOrgFileName();
        String currentFileName = heaven.getFileName();

        // 동일 파일인지 비교 (이름만으로 판단하는 경우)
        if (newOrgFileName != null && newOrgFileName.equals(currentOrgFileName)) {
            // 동일한 파일명이면 기존 파일 그대로 사용
            return Map.of("fileName", currentFileName, "orgFileName", currentOrgFileName);
        }

        // 기존 파일 삭제
        Path path = Paths.get(FILE_PATH);
        Path oldFilePath = path.resolve(currentFileName);

        try {
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
            }
        } catch (IOException e) {
            throw new FileStorageException(oldFilePath, "기존 파일 삭제 실패: " + currentFileName);
        }

        // 새 파일 저장
        String extension = newOrgFileName.substring(newOrgFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString().replace("-", "").toUpperCase() + extension;
        Path newFilePath = path.resolve(newFileName);

        try {
            Files.createDirectories(path);
            newFile.transferTo(newFilePath);

            return Map.of("fileName", newFileName, "orgFileName", newOrgFileName);
        } catch (IOException e) {
            throw new FileStorageException(newFilePath, "새 파일 저장 실패: " + newOrgFileName);
        }
    }
}

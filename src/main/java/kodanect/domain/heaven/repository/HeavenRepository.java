package kodanect.domain.heaven.repository;

import kodanect.domain.heaven.dto.HeavenDto;
import kodanect.domain.heaven.dto.response.HeavenResponse;
import kodanect.domain.heaven.dto.response.MemorialHeavenResponse;
import kodanect.domain.heaven.entity.Heaven;
import kodanect.domain.remembrance.entity.Memorial;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HeavenRepository extends JpaRepository<Heaven, Integer> {

    /**
     * 게시물 전체 조회 (페이징)
     *
     * @param cursor
     * @param pageable
     * @return
     */
    @Query(
            value = """
            SELECT new kodanect.domain.heaven.dto.response.HeavenResponse
                    (h.letterSeq, h.letterTitle,
                     CASE
                             WHEN(h.memorial.anonymityFlag = 'Y') THEN
                                     CONCAT(SUBSTRING(h.donorName, 1, 1), REPEAT('*', CHAR_LENGTH(h.donorName) - 1))
                             ELSE h.donorName
                     END AS donorName,
                     h.memorial.anonymityFlag AS memorialAnonymityFlag,
                     CASE
                             WHEN(h.anonymityFlag = 'Y') THEN
                                     CONCAT(SUBSTRING(h.letterWriter, 1, 1), REPEAT('*', CHAR_LENGTH(h.letterWriter) - 1))
                             ELSE h.letterWriter
                     END AS letterWriter,
                     h.anonymityFlag AS heavenAnonymityFlag, h.readCount, h.writeTime)
            FROM Heaven h
            WHERE :cursor IS NULL OR h.letterSeq < :cursor
            ORDER BY h.letterSeq DESC
        """
    )
    List<HeavenResponse> findByCursor(@Param("cursor") Integer cursor, Pageable pageable);

    /**
     * 전체(제목 + 내용)을 통한 게시물 전체 조회 (페이징)
     *
     * @param cursor
     * @param keyWord
     * @param pageable
     * @return
     */
    @Query(
            value = """
            SELECT new kodanect.domain.heaven.dto.response.HeavenResponse
                    (h.letterSeq, h.letterTitle,
                     CASE
                             WHEN(h.memorial.anonymityFlag = 'Y') THEN
                                     CONCAT(SUBSTRING(h.donorName, 1, 1), REPEAT('*', CHAR_LENGTH(h.donorName) - 1))
                             ELSE h.donorName
                     END AS donorName,
                     h.memorial.anonymityFlag AS memorialAnonymityFlag,
                     CASE
                             WHEN(h.anonymityFlag = 'Y') THEN
                                     CONCAT(SUBSTRING(h.letterWriter, 1, 1), REPEAT('*', CHAR_LENGTH(h.letterWriter) - 1))
                             ELSE h.letterWriter
                     END AS letterWriter,
                     h.anonymityFlag AS heavenAnonymityFlag, h.readCount, h.writeTime)
            FROM Heaven h
            WHERE (:cursor IS NULL OR h.letterSeq < :cursor)
            AND (h.letterTitle LIKE %:keyWord% OR h.letterContents LIKE %:keyWord%)
            ORDER BY h.letterSeq DESC
        """
    )
    List<HeavenResponse> findByTitleOrContentsContaining(@Param("keyWord") String keyWord, @Param("cursor") Integer cursor, Pageable pageable);

    /**
     * 제목을 통한 게시물 전체 조회 (페이징)
     *
     * @param keyWord
     * @param cursor
     * @param pageable
     * @return
     */
    @Query(
            value = """
            SELECT new kodanect.domain.heaven.dto.response.HeavenResponse
                    (h.letterSeq, h.letterTitle,
                     CASE
                             WHEN(h.memorial.anonymityFlag = 'Y') THEN
                                     CONCAT(SUBSTRING(h.donorName, 1, 1), REPEAT('*', CHAR_LENGTH(h.donorName) - 1))
                             ELSE h.donorName
                     END AS donorName,
                     h.memorial.anonymityFlag AS memorialAnonymityFlag,
                     CASE
                             WHEN(h.anonymityFlag = 'Y') THEN
                                     CONCAT(SUBSTRING(h.letterWriter, 1, 1), REPEAT('*', CHAR_LENGTH(h.letterWriter) - 1))
                             ELSE h.letterWriter
                     END AS letterWriter,
                     h.anonymityFlag AS heavenAnonymityFlag, h.readCount, h.writeTime)
            FROM Heaven h
            WHERE (:cursor IS NULL OR h.letterSeq < :cursor)
            AND h.letterTitle LIKE %:keyWord%
            ORDER BY h.letterSeq DESC
        """
    )
    List<HeavenResponse> findByTitleContaining(@Param("keyWord") String keyWord, @Param("cursor") Integer cursor, Pageable pageable);

    /**
     * 내용을 통한 게시물 전체 조회 (페이징)
     *
     * @param keyWord
     * @param cursor
     * @param pageable
     * @return
     */
    @Query(
            value = """
            SELECT new kodanect.domain.heaven.dto.response.HeavenResponse
                    (h.letterSeq, h.letterTitle,
                     CASE
                             WHEN(h.memorial.anonymityFlag = 'Y') THEN
                                     CONCAT(SUBSTRING(h.donorName, 1, 1), REPEAT('*', CHAR_LENGTH(h.donorName) - 1))
                             ELSE h.donorName
                     END AS donorName,
                     h.memorial.anonymityFlag AS memorialAnonymityFlag,
                     CASE
                             WHEN(h.anonymityFlag = 'Y') THEN
                                     CONCAT(SUBSTRING(h.letterWriter, 1, 1), REPEAT('*', CHAR_LENGTH(h.letterWriter) - 1))
                             ELSE h.letterWriter
                     END AS letterWriter,
                     h.anonymityFlag AS heavenAnonymityFlag, h.readCount, h.writeTime)
            FROM Heaven h
            WHERE (:cursor IS NULL OR h.letterSeq < :cursor)
            AND h.letterContents LIKE %:keyWord%
            ORDER BY h.letterSeq DESC
        """
    )
    List<HeavenResponse> findByContentsContaining(@Param("keyWord") String keyWord, @Param("cursor") Integer cursor, Pageable pageable);

    /**
     * 기증자 추모관 상세 조회 시 하늘나라 편지 리스트 조회
     *
     * @param memorial
     * @param cursor
     * @param pageable
     * @return
     */
    @Query(
            value = """
            SELECT new kodanect.domain.heaven.dto.response.MemorialHeavenResponse
            (h.letterSeq, h.letterTitle, h.readCount, h.writeTime)
            FROM Heaven h
            WHERE (:cursor IS NULL OR h.letterSeq < :cursor)
            AND h.memorial = :memorial
            ORDER BY h.letterSeq DESC
        """
    )
    List<MemorialHeavenResponse> findMemorialHeavenResponseById(@Param("memorial") Memorial memorial, @Param("cursor") Integer cursor, Pageable pageable);

    /**
     * 상세 조회 시 게시물 조회
     *
     * @param letterSeq
     * @return
     */
    @Query(
            value = """
            SELECT new kodanect.domain.heaven.dto.HeavenDto
                    (h.letterSeq, m.donateSeq, h.letterTitle,
                    CASE
                            WHEN(h.memorial.anonymityFlag = 'Y') THEN
                                    CONCAT(SUBSTRING(h.donorName, 1, 1), REPEAT('*', CHAR_LENGTH(h.donorName) - 1))
                            ELSE h.donorName
                    END AS donorName,
                    m.anonymityFlag AS memorialAnonymityFlag,
                    CASE
                            WHEN(h.anonymityFlag = 'Y') THEN
                                    CONCAT(SUBSTRING(h.letterWriter, 1, 1), REPEAT('*', CHAR_LENGTH(h.letterWriter) - 1))
                            ELSE h.letterWriter
                    END AS letterWriter,
                    h.anonymityFlag AS heavenAnonymityFlag,
                    h.readCount, h.letterContents, h.fileName, h.orgFileName, h.writeTime)
            FROM Heaven h
            LEFT JOIN h.memorial m
            WHERE h.letterSeq = :letterSeq
        """
    )
    HeavenDto findAnonymizedById(@Param("letterSeq") Integer letterSeq);

    /**
     * 전체(제목 + 내용)을 통한 게시물 개수 조회
     *
     * @param keyWord
     * @return
     */
    @Query(
            value = """
            SELECT COUNT(h)
            FROM Heaven h
            WHERE h.letterTitle LIKE %:keyWord% OR h.letterContents LIKE %:keyWord%
        """
    )
    int countByTitleOrContentsContaining(@Param("keyWord") String keyWord);

    /**
     * 제목을 통한 게시물 개수 조회
     *
     * @param keyWord
     * @return
     */
    @Query(
            value = """
            SELECT COUNT(h)
            FROM Heaven h
            WHERE h.letterTitle LIKE %:keyWord%
        """
    )
    int countByTitleContaining(@Param("keyWord") String keyWord);

    /**
     * 내용을 통한 게시물 개수 조회
     *
     * @param keyWord
     * @return
     */
    @Query(
            value = """
            SELECT COUNT(h)
            FROM Heaven h
            WHERE h.letterContents LIKE %:keyWord%
        """
    )
    int countByContentsContaining(@Param("keyWord") String keyWord);

    /**
     * donateSeq를 통한 게시물 개수 조회
     *
     * @param memorial
     * @return
     */
    int countByMemorial(Memorial memorial);
}

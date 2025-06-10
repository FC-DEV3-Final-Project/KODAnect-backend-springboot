package kodanect.domain.heaven.repository;

import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.entity.Heaven;
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
            SELECT new kodanect.domain.heaven.dto.HeavenResponse
                    (h.letterSeq, h.letterTitle, h.donorName, h.letterWriter, h.anonymityFlag, h.readCount, h.writeTime)
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
     * @param keyword
     * @param pageable
     * @return
     */
    @Query(
            value = """
            SELECT new kodanect.domain.heaven.dto.HeavenResponse
                    (h.letterSeq, h.letterTitle, h.donorName, h.letterWriter, h.anonymityFlag, h.readCount, h.writeTime)
            FROM Heaven h
            WHERE (:cursor IS NULL OR h.letterSeq < :cursor)
            AND (h.letterTitle LIKE %:keyword% OR h.letterContents LIKE %:keyword%)
            ORDER BY h.letterSeq DESC
        """
    )
    List<HeavenResponse> findByTitleOrContentsContaining(@Param("keyword") String keyword, @Param("cursor") Integer cursor, Pageable pageable);

    /**
     * 제목을 통한 게시물 전체 조회 (페이징)
     *
     * @param keyword
     * @param cursor
     * @param pageable
     * @return
     */
    @Query(
            value = """
            SELECT new kodanect.domain.heaven.dto.HeavenResponse
                    (h.letterSeq, h.letterTitle, h.donorName, h.letterWriter, h.anonymityFlag, h.readCount, h.writeTime)
            FROM Heaven h
            WHERE (:cursor IS NULL OR h.letterSeq < :cursor)
            AND h.letterTitle LIKE %:keyword%
            ORDER BY h.letterSeq DESC
        """
    )
    List<HeavenResponse> findByTitleContaining(@Param("keyword") String keyword, @Param("cursor") Integer cursor, Pageable pageable);

    /**
     * 내용을 통한 게시물 전체 조회 (페이징)
     *
     * @param keyword
     * @param cursor
     * @param pageable
     * @return
     */
    @Query(
            value = """
            SELECT new kodanect.domain.heaven.dto.HeavenResponse
                    (h.letterSeq, h.letterTitle, h.donorName, h.letterWriter, h.anonymityFlag, h.readCount, h.writeTime)
            FROM Heaven h
            WHERE (:cursor IS NULL OR h.letterSeq < :cursor)
            AND h.letterContents LIKE %:keyword%
            ORDER BY h.letterSeq DESC
        """
    )
    List<HeavenResponse> findByContentsContaining(@Param("keyword") String keyword, @Param("cursor") Integer cursor, Pageable pageable);

    /* 게시물 상세 조회 */
}

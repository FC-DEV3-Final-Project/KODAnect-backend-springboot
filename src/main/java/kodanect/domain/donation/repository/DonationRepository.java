package kodanect.domain.donation.repository;

import kodanect.domain.donation.dto.response.DonationStoryListDto;
import kodanect.domain.donation.entity.DonationStory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<DonationStory, Long> {

    /**
     * 게시글 목록을 Offset 기반으로 슬라이스 조회 (더보기 방식에 사용)
     */

    @Query("""
        SELECT new kodanect.domain.donation.dto.response.DonationStoryListDto(
            d.storySeq, d.storyTitle, d.storyWriter, d.readCount, d.writeTime)
        FROM DonationStory d
        WHERE (:cursor IS NULL OR d.storySeq < :cursor)
        ORDER BY d.storySeq DESC
        """)
        List<DonationStoryListDto> findByCursor(@Param("cursor") Long cursor, Pageable pageable);


    /**
     * 게시글 상세 조회 시 댓글도 함께 가져오기 (댓글 정렬: 오름차순)
     */
    @Query("""
        SELECT s FROM DonationStory s
        LEFT JOIN FETCH s.comments c
        WHERE s.storySeq = :storySeq
        AND c.commentSeq IN (
            SELECT c2.commentSeq FROM DonationStoryComment c2
            WHERE c2.story.storySeq = :storySeq
            ORDER BY c2.commentSeq DESC
        )
        """)
        Optional<DonationStory> findWithTop3CommentsById(@Param("storySeq") Long storySeq);


    @Query("""
        SELECT new kodanect.domain.donation.dto.response.DonationStoryListDto(
            d.storySeq, d.storyTitle, d.storyWriter, d.readCount, d.writeTime)
        FROM DonationStory d
        WHERE (d.storyTitle LIKE %:keyword%) AND (:cursor IS NULL OR d.storySeq < :cursor)
        ORDER BY d.storySeq DESC
        """)
        List<DonationStoryListDto> findByTitleCursor(@Param("keyword") String keyword,
                                                 @Param("cursor") Long cursor,
                                                 Pageable pageable);

    @Query("""
        SELECT new kodanect.domain.donation.dto.response.DonationStoryListDto(
            d.storySeq, d.storyTitle, d.storyWriter, d.readCount, d.writeTime)
        FROM DonationStory d
        WHERE (d.storyContents LIKE %:keyword%) AND (:cursor IS NULL OR d.storySeq < :cursor)
        ORDER BY d.storySeq DESC
        """)
        List<DonationStoryListDto> findByContentsCursor(@Param("keyword") String keyword,
                                                    @Param("cursor") Long cursor,
                                                    Pageable pageable);

    @Query("""
        SELECT new kodanect.domain.donation.dto.response.DonationStoryListDto(
            d.storySeq, d.storyTitle, d.storyWriter, d.readCount, d.writeTime)
        FROM DonationStory d
        WHERE (d.storyTitle LIKE %:keyword% OR d.storyContents LIKE %:keyword%) 
              AND (:cursor IS NULL OR d.storySeq < :cursor)
        ORDER BY d.storySeq DESC
        """)
        List<DonationStoryListDto> findByTitleOrContentsCursor(@Param("keyword") String keyword,
                                                           @Param("cursor") Long cursor,
                                                           Pageable pageable);
    /**
     * 제목 검색 시 일치하는 게시글 개수 반환 (hasNext 판단용)
     */
    @Query("SELECT COUNT(d) FROM DonationStory d WHERE d.storyTitle LIKE %:keyword%")
    long countByTitleContaining(@Param("keyword") String keyword);

    /**
     * 내용 검색 시 일치하는 게시글 개수 반환 (hasNext 판단용)
     */
    @Query("SELECT COUNT(d) FROM DonationStory d WHERE d.storyContents LIKE %:keyword%")
    long countByContentsContaining(@Param("keyword") String keyword);

    /**
     * 제목 또는 내용 검색 시 일치하는 게시글 개수 반환 (hasNext 판단용)
     */
    @Query("SELECT COUNT(d) FROM DonationStory d WHERE d.storyTitle LIKE %:keyword% OR d.storyContents LIKE %:keyword%")
    long countByTitleOrContentsContaining(@Param("keyword") String keyword);


}
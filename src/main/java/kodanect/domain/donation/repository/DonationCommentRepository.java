package kodanect.domain.donation.repository;

import kodanect.domain.donation.dto.response.DonationStoryCommentDto;
import kodanect.domain.donation.entity.DonationStoryComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DonationCommentRepository extends JpaRepository<DonationStoryComment, Long> {

    @Query("""
        SELECT d
        FROM DonationStoryComment d
        WHERE d.story.storySeq = :storySeq
          AND (:cursor IS NULL OR d.commentSeq < :cursor)
        ORDER BY d.commentSeq DESC
        """)
    List<DonationStoryComment> findByCursorEntity(
            @Param("storySeq") Long storySeq,
            @Param("cursor") Long cursor,
            Pageable pageable);

    // ① 최신 댓글 3개를 DTO로 바로 조회
    @Query("""
        SELECT new kodanect.domain.donation.dto.response.DonationStoryCommentDto(
            c.commentSeq,
            c.commentWriter,
            c.comments,
            c.commentWriteTime
        )
        FROM DonationStoryComment c
        WHERE c.story.storySeq = :storySeq
        ORDER BY c.commentSeq DESC
    """)
    List<DonationStoryCommentDto> findLatestComments(
            @Param("storySeq") Long storySeq,
            Pageable pageable
    );

    @Query(value =  "SELECT COUNT(*) FROM tb25_421_donation_story_comment WHERE story_seq = :storySeq ", nativeQuery=true)
    long countAllByStorySeq(Long storySeq);


}
package kodanect.domain.donation.repository;

import kodanect.domain.donation.dto.response.DonationStoryCommentDto;
import kodanect.domain.donation.entity.DonationStoryComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DonationCommentRepository extends JpaRepository<DonationStoryComment, Long> {

    /**
     * 특정 게시글(storySeq)에 작성된 댓글 목록을 조회 (페이지네이션 적용 가능)
     */
    @Query("""
            SELECT new kodanect.domain.donation.dto.response.DonationStoryCommentDto(
                c.commentSeq, c.commentWriter, c.contents, c.writeTime
            )
            FROM DonationStoryComment c
            WHERE c.story.storySeq = :storySeq
            ORDER BY c.commentSeq ASC
            """)
    List<DonationStoryCommentDto> findCommentsByStoryId(@Param("storySeq") Long storySeq, Pageable pageable);


}
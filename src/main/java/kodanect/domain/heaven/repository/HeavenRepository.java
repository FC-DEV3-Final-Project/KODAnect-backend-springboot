package kodanect.domain.heaven.repository;

import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.entity.Heaven;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HeavenRepository extends JpaRepository<Heaven, Integer> {

    /* 게시물 전체 조회 (페이징) */
    @Query(
            value = """
            SELECT new kodanect.domain.heaven.dto.HeavenResponse
                    (h.letterSeq, h.letterTitle, h.donorName, h.letterWriter, h.anonymityFlag, h.readCount, h.writeTime)
            FROM Heaven h
        """
    )
    List<HeavenResponse> findByCursor();

    /* 게시물 상세 조회 */
}

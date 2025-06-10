package kodanect.domain.heaven.repository;

import kodanect.domain.heaven.entity.HeavenComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeavenCommentRepository extends JpaRepository<HeavenComment, Integer> {
}

package kodanect.domain.logging.repository;

import kodanect.domain.logging.entity.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ActionLog 엔티티에 대한 JPA Repository
 *
 * - 기본 CRUD 및 페이징 기능 제공
 */
@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Integer> {
}

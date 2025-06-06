package kodanect.domain.logging.repository;

import kodanect.domain.logging.entity.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 액션 로그 저장용 JPA 리포지토리
 *
 * 기본 CRUD 외 메서드 확장 필요 시 여기에서 정의
 */
@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Integer> {
}

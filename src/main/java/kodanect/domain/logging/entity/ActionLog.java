package kodanect.domain.logging.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 이용 로그 기록을 위한 JPA 엔티티 클래스
 *
 * - DB 테이블: tb25_940_action_log
 * - URL, CRUD 코드, IP, 로그 내용 등의 사용자 액션을 저장
 * - 시스템 로그성 데이터 기록 목적
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb25_940_action_log")
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logSeq;

    @Column(nullable = false, length = 600)
    private String urlName;

    @Column(length = 10)
    private String crudCode;

    @Column(length = 60)
    private String ipAddr;

    @Column(length = 3000)
    private String logText;

    @Column(insertable = false, updatable = false)
    private LocalDateTime writeTime;

}

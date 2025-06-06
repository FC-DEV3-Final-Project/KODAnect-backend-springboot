package kodanect.domain.logging.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 액션 로그 엔티티
 *
 * 사용자 행위 로그를 DB에 저장하기 위한 매핑 클래스
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
    @Column(name = "log_seq")
    private Integer logSeq;

    @Column(name = "url_name", nullable = false, length = 600)
    private String urlName;

    @Column(name = "crud_code", length = 10)
    private String crudCode;

    @Column(name = "ip_addr", length = 60)
    private String ipAddr;

    @Column(name = "log_text", length = 3000)
    private String logText;

    @Column(name = "write_time", nullable = false, insertable = false, updatable = false)
    private LocalDateTime writeTime;

}

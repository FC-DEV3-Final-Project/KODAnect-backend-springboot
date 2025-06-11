package kodanect.domain.heaven.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "HeavenComment")
@Table(name = "tb25_411_heaven_letter_comment")
@NoArgsConstructor
@AllArgsConstructor
@Getter @ToString
@Builder
public class HeavenComment {

    /* 기본값 */
    private static final String DEFAULT_DEL_FLAG = "N";

    /* 댓글 일련번호 */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentSeq;

    /* 편지 일련번호 */
    @ManyToOne
    @JoinColumn(name = "letter_seq")
    private Heaven heaven;

    /* 작성자 이름 */
    @Column(length = 150)
    private String commentWriter;

    /* 댓글 비밀번호 */
    @Column(length = 60)
    private String commentPasscode;

    /* 댓글 내용 */
    @Column(columnDefinition = "TEXT")
    private String contents;

    /* 생성 일시 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime writeTime;

    /* 생성자 아이디 */
    @Column(length = 60)
    private String writerId;

    /* 수정 일시 */
    @Column(insertable = false, updatable = false)
    private LocalDateTime modifyTime;

    /* 수정자 아이디 */
    @Column(length = 60)
    private String modifierId;

    /* 삭제 여부 */
    @Column(nullable = false, length = 1)
    @Builder.Default
    private String delFlag = DEFAULT_DEL_FLAG;

    /* 연관 관계 편의 메서드 */
    public void setHeaven(Heaven heaven) {
        this.heaven = heaven;

        List<HeavenComment> comments = heaven.getComments();
        if (!comments.contains(this)) {
            comments.add(this);
        }
    }

    /* 영속성 컨텍스트에 처음 저장되기 직전에 실행되는 메서드 */
    @PrePersist
    private void prePersist() {
        if (writeTime == null) {
            writeTime = LocalDateTime.now();
        }

        if (modifyTime == null) {
            modifyTime = LocalDateTime.now();
        }
    }
}

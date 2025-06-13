package kodanect.domain.heaven.dto;

import kodanect.common.util.CursorIdentifiable;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @ToString
@Builder
public class HeavenCommentResponse implements CursorIdentifiable<Integer> {

    /* 댓글 일련번호 */
    private int commentSeq;

    /* 작성자 이름 */
    private String commentWriter;

    /* 댓글 내용 */
    private String contents;

    /* 생성 일시 */
    private LocalDateTime writeTime;

    @Override
    public Integer getCursorId() {
        return commentSeq;
    }
}

package kodanect.domain.remembrance.dto;

import kodanect.domain.remembrance.dto.common.BlankGroup;
import kodanect.domain.remembrance.dto.common.PatternGroup;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static kodanect.common.exception.config.MessageKeys.COMMENT_PASSWORD_EMPTY;
import static kodanect.common.exception.config.MessageKeys.COMMENT_PASSWORD_INVALID;

/**
 *
 * 기증자 추모관 댓글 삭제 요청 dto
 *
 * <p>commentPasscode : 댓글 비밀번호</p>
 *
 * */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @ToString
@GroupSequence({MemorialCommentPasswordRequest.class, BlankGroup.class, PatternGroup.class})
public class MemorialCommentPasswordRequest {

    /* 댓글 비밀번호 */
    @NotBlank(message = COMMENT_PASSWORD_EMPTY, groups = BlankGroup.class)
    @Pattern(regexp = "^[a-zA-Z0-9]{8,16}$", message = COMMENT_PASSWORD_INVALID, groups = PatternGroup.class)
    private String commentPasscode;
}

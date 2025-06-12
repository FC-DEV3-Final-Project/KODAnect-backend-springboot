package kodanect.common.response;

import kodanect.common.response.CursorCommentPaginationResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CursorCommentCountPaginationResponse<T, C> extends CursorCommentPaginationResponse<T, C> {

    /** 총 댓글 개수 */
    private Long totalCommentCount;
}
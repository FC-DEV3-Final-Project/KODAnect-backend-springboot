package kodanect.domain.heaven.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kodanect.common.response.CursorCommentCountPaginationResponse;
import kodanect.common.response.CursorCommentPaginationResponse;
import kodanect.domain.heaven.dto.HeavenCommonDto;
import kodanect.domain.heaven.dto.HeavenDto;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @ToString
@Builder
public class HeavenDetailResponse {

    private HeavenCommonDto heavenCommonDto;

    /* 파일 URL */
    private String imageUrl;

    /* 생성 일시 */
    private LocalDateTime writeTime;

    /* 댓글 리스트 */
    private CursorCommentPaginationResponse<HeavenCommentResponse, Integer> cursorCommentPaginationResponse;

    /* 생성 일시 형식화 */
    public String getWriteTime() {
        return writeTime.toLocalDate().toString();
    }

    public static HeavenDetailResponse of(
            HeavenDto heaven,
            CursorCommentCountPaginationResponse<HeavenCommentResponse, Integer> cursorCommentPaginationResponse,
            String imageUrl
    ) {

        return HeavenDetailResponse.builder()
                .heavenCommonDto(heaven.getHeavenCommonDto())
                .imageUrl(imageUrl)
                .writeTime(heaven.getWriteTime())
                .cursorCommentPaginationResponse(cursorCommentPaginationResponse)
                .build();
    }
}

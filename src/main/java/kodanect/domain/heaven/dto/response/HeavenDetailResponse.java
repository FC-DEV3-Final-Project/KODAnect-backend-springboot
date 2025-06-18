package kodanect.domain.heaven.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kodanect.common.response.CursorCommentCountPaginationResponse;
import kodanect.common.response.CursorCommentPaginationResponse;
import kodanect.domain.heaven.dto.HeavenDto;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @ToString
@Builder
public class HeavenDetailResponse {

    /* 편지 일련번호 */
    private int letterSeq;

    /* 기증자 일련번호 */
    private Integer donateSeq;

    /* 편지 제목 */
    private String letterTitle;

    /* 기증자 명 */
    private String donorName;

    /* 기증자 익명 여부 */
    @JsonIgnore
    private String memorialAnonymityFlag;

    /* 편지 작성자 */
    private String letterWriter;

    /* 작성자 익명 여부 */
    @JsonIgnore
    private String heavenAnonymityFlag;

    /* 조회 건수 */
    private Integer readCount;

    /* 편지 내용 */
    private String letterContents;

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
                .letterSeq(heaven.getLetterSeq())
                .donateSeq(heaven.getDonateSeq())
                .letterTitle(heaven.getLetterTitle())
                .donorName(heaven.getDonorName())
                .letterWriter(heaven.getLetterWriter())
                .readCount(heaven.getReadCount())
                .letterContents(heaven.getLetterContents())
                .imageUrl(imageUrl)
                .writeTime(heaven.getWriteTime())
                .cursorCommentPaginationResponse(cursorCommentPaginationResponse)
                .build();
    }
}

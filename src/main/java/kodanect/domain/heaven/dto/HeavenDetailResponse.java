package kodanect.domain.heaven.dto;

import kodanect.common.response.CursorReplyPaginationResponse;
import kodanect.domain.donation.dto.response.AreaCode;
import kodanect.domain.heaven.entity.Heaven;
import kodanect.domain.remembrance.entity.Memorial;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @ToString
@Builder
public class HeavenDetailResponse {

    /* 편지 일련번호 */
    private int letterSeq;

    /* 기증자 일련번호 */
    private Integer donateSeq;

    /* 권역 코드 */
    private AreaCode areaCode;

    /* 편지 제목 */
    private String letterTitle;

    /* 기증자 명 */
    private String donorName;

    /* 비밀번호 */
    private String letterPasscode;

    /* 편지 작성자 */
    private String letterWriter;

    /* 편지 익명여부 */
    private String anonymityFlag;

    /* 조회 건수 */
    private Integer readCount;

    /* 편지 내용 */
    private String letterContents;

    /* 이미지 파일 명 */
    private String fileName;

    /* 이미지 원본 파일 명 */
    private String orgFileName;

    /* 생성 일시 */
    private LocalDateTime writeTime;

    /* 댓글 정보 */
    private List<HeavenCommentResponse> heavenCommentResponseList;

    /* 다음 페이지 번호 */
    private Integer replyNextCursor;

    /* 다음 페이지 존재 여부 */
    private boolean replyHasNext;

    /* 총 댓글 수 */
    private long totalCommentCount;

    public static HeavenDetailResponse of(
            Heaven heaven,
            CursorReplyPaginationResponse<HeavenCommentResponse, Integer> cursorPaginationResponse,
            long totalCommentCount
    ) {
        Memorial memorial = heaven.getMemorial();

        return HeavenDetailResponse.builder()
                .letterSeq(heaven.getLetterSeq())
                .donateSeq((memorial != null) ? memorial.getDonateSeq() : null)
                .areaCode(heaven.getAreaCode())
                .letterTitle(heaven.getLetterTitle())
                .donorName(heaven.getDonorName())
                .letterPasscode(heaven.getLetterPasscode())
                .letterWriter(heaven.getLetterWriter())
                .anonymityFlag(heaven.getAnonymityFlag())
                .readCount(heaven.getReadCount())
                .letterContents(heaven.getLetterContents())
                .fileName(heaven.getFileName())
                .orgFileName(heaven.getOrgFileName())
                .writeTime(heaven.getWriteTime())
                .heavenCommentResponseList(cursorPaginationResponse.getContent())
                .replyNextCursor(cursorPaginationResponse.getReplyNextCursor())
                .replyHasNext(cursorPaginationResponse.isReplyHasNext())
                .totalCommentCount(totalCommentCount)
                .build();
    }
}

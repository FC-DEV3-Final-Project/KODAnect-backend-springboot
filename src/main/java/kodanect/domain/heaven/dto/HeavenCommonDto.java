package kodanect.domain.heaven.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @ToString
@Builder
public class HeavenCommonDto {

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
}

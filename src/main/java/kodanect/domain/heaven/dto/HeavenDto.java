package kodanect.domain.heaven.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @ToString
@Builder
public class HeavenDto {

    private int letterSeq;

    private Integer donateSeq;

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

    private Integer readCount;

    private String letterContents;

    private String fileName;

    private String orgFileName;

    private LocalDateTime writeTime;
}

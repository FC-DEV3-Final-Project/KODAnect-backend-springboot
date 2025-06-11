package kodanect.domain.heaven.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
public class HeavenVerifyResponse {

    /* 성공 여부 */
    private int result;

    public static HeavenVerifyResponse of(int result) {
        return HeavenVerifyResponse.builder()
                .result(result)
                .build();
    }
}

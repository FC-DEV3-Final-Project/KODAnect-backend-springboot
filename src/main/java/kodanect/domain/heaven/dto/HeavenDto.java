package kodanect.domain.heaven.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @ToString
@Builder
public class HeavenDto {

    private HeavenCommonDto heavenCommonDto;

    private String fileName;

    private String orgFileName;

    private LocalDateTime writeTime;
}

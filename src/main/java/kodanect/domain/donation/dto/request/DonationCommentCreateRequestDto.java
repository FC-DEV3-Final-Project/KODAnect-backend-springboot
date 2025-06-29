package kodanect.domain.donation.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationCommentCreateRequestDto {

    @NotBlank(message="{donation.error.required.writer}")
    private String commentWriter;
    @NotBlank(message="{donation.error.required.passcode}")
    private String commentPasscode;
    @NotBlank(message="{donation.comment.contents.blank}")
    private String contents;
}

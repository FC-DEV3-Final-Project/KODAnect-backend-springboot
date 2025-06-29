package kodanect.domain.donation.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCommentPasscodeDto {

    @NotBlank(message="{donation.comment.verify.passcode.blank}")
    private String commentPasscode;
}

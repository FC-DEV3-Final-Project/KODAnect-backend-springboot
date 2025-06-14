package kodanect.common.validation;

import kodanect.domain.heaven.exception.InvalidDonorInformationException;
import kodanect.domain.remembrance.entity.Memorial;

public class HeavenDonorValidator {

    /* 유틸리티 클래스로 생성자 불가 */
    private HeavenDonorValidator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /* HeavenCreateRequest 유효성 검사 */
    public static void validateDonorNameMatches(String donorName, Memorial memorial) {
        boolean hasDonorName = donorName != null && !donorName.isBlank();
        boolean hasMemorial = memorial != null;

        // Case 1: memorial이 없는데 donorName이 있는 경우
        if (!hasMemorial && hasDonorName) {
            throw new InvalidDonorInformationException(donorName, memorial);
        }

        // Case 2: memorial이 있는데 donorName이 없는 경우
        if (hasMemorial && !hasDonorName) {
            throw new InvalidDonorInformationException(donorName, memorial);
        }

        // Case 3: memorial과 donorName이 모두 있을 경우 → 이름 일치 여부 검증
        if (hasMemorial && !donorName.equals(memorial.getDonorName())) {
            throw new InvalidDonorInformationException(donorName, memorial);
        }
    }
}

package kodanect.common.validation;

import kodanect.domain.remembrance.entity.Memorial;

public class HeavenCreateRequestValidator {

    public static void validateHeavenCreateRequest(String donorName, Memorial memorial) {
        boolean hasDonorName = donorName != null && !donorName.isBlank();
        boolean hasMemorial = memorial != null;

        // Case 1: memorial이 없는데 donorName이 있는 경우
        if (!hasMemorial && hasDonorName) {
            throw new RuntimeException("donateSeq 없이 donorName을 제공할 수 없습니다.");
        }

        // Case 2: memorial이 있는데 donorName이 없는 경우
        if (hasMemorial && !hasDonorName) {
            throw new RuntimeException("donorName 없이 donateSeq를 제공할 수 없습니다.");
        }

        // Case 3: memorial과 donorName이 모두 있을 경우 → 이름 일치 여부 검증
        if (hasMemorial && !donorName.equals(memorial.getDonorName())) {
            throw new RuntimeException("요청한 donorName이 DB의 값과 일치하지 않습니다.");
        }
    }
}

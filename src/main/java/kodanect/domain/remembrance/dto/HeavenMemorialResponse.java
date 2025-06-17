package kodanect.domain.remembrance.dto;

public interface HeavenMemorialResponse {

    /* 기증자 일련번호 */
    Integer getDonateSeq();

    /* 기증자 명 */
    String getDonorName();

    /* 기증자 기증 일시 20120101 */
    String getDonateDate();

    /* 기증자 성별 */
    String getGenderFlag();

    /* 기증자 나이 */
    Integer getDonateAge();
}

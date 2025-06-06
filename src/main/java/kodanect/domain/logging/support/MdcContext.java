package kodanect.domain.logging.support;

import org.slf4j.MDC;

import java.util.Optional;

/**
 * MDC에서 로그 메타데이터를 조회하는 유틸리티
 *
 * 로그 저장 시점에 MDC에 주입된 데이터를 꺼낼 때 사용
 * 키 하드코딩을 피하고 일관된 접근 방식 제공
 */
public class MdcContext {

    private MdcContext() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 지정한 키에 해당하는 MDC 값 반환
     * 값이 없으면 빈 문자열 반환
     */
    public static String get(String key) {
        return Optional.ofNullable(MDC.get(key)).orElse("");
    }

    public static String getUrl() {
        return get(MdcKey.URL);
    }

    public static String getIp() {
        return get(MdcKey.IP);
    }

    public static String getUserAgent() {
        return get(MdcKey.USER_AGENT);
    }

    public static String getReferer() {
        return get(MdcKey.REFERER);
    }

    public static String getLang() {
        return get(MdcKey.LANG);
    }

    public static String getPlatform() {
        return get(MdcKey.PLATFORM);
    }

    public static String getAppVersion() {
        return get(MdcKey.APP_VERSION);
    }

    public static String getOsVersion() {
        return get(MdcKey.OS_VERSION);
    }

    public static String getDeviceModel() {
        return get(MdcKey.DEVICE_MODEL);
    }

    public static String getScreen() {
        return get(MdcKey.SCREEN);
    }

}

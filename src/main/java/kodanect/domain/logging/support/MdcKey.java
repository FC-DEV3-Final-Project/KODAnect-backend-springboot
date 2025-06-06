package kodanect.domain.logging.support;

/**
 * MDC에서 사용하는 로그 메타데이터 키 모음
 *
 * AOP에서 주입한 정보를 가져올 때 사용
 * 하드코딩 방지 및 일관된 키 관리 목적
 */
public final class MdcKey {

    public static final String URL = "urlName";
    public static final String IP = "ipAddr";
    public static final String USER_AGENT = "userAgent";
    public static final String REFERER = "referer";
    public static final String LANG = "lang";
    public static final String PLATFORM = "platform";
    public static final String APP_VERSION = "appVersion";
    public static final String OS_VERSION = "osVersion";
    public static final String DEVICE_MODEL = "deviceModel";
    public static final String SCREEN = "screen";

    private MdcKey() {
        throw new UnsupportedOperationException("Utility class");
    }

}

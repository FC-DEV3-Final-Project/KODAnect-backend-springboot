package kodanect.domain.logging.model;

import kodanect.domain.logging.dto.ActionLogPayload;
import kodanect.domain.logging.support.MdcContext;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 액션 로그 저장을 위한 내부 컨텍스트 객체
 *
 * 클라이언트 요청 + MDC 기반 메타데이터를 통합
 */
@Getter
@Builder
public class ActionLogContext {

    private String type;
    private String target;
    private String urlName;
    private String ipAddr;
    private String userAgent;
    private String referer;
    private String lang;
    private String platform;
    private String appVersion;
    private String osVersion;
    private String deviceModel;
    private String screen;
    private LocalDateTime timestamp;

    /**
     * 프론트 요청 DTO 기반 컨텍스트 생성
     * MDC에서 메타데이터를 추출해 함께 빌드
     */
    public static ActionLogContext from(ActionLogPayload payload) {
        return ActionLogContext.builder()
                .type(payload.getType())
                .target(payload.getTarget())
                .urlName(MdcContext.getUrl())
                .ipAddr(MdcContext.getIp())
                .userAgent(MdcContext.getUserAgent())
                .referer(MdcContext.getReferer())
                .lang(MdcContext.getLang())
                .platform(MdcContext.getPlatform())
                .appVersion(MdcContext.getAppVersion())
                .osVersion(MdcContext.getOsVersion())
                .deviceModel(MdcContext.getDeviceModel())
                .screen(MdcContext.getScreen())
                .timestamp(LocalDateTime.now())
                .build();
    }

}

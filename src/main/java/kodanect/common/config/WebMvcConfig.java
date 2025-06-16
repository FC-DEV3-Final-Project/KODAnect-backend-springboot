package kodanect.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 전역 Web MVC 설정 클래스
 *
 * 역할:
 * - CORS(Cross-Origin Resource Sharing) 정책 전역 설정
 * - 외부 정적 리소스 핸들링 경로 설정
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final GlobalsProperties globalsProperties;

    public WebMvcConfig(GlobalsProperties globalsProperties) {
        this.globalsProperties = globalsProperties;
    }

    /**
     * CORS 설정
     * - 특정 도메인에서 오는 요청을 허용
     * - 프론트엔드 로컬 서버에서 API 호출 허용
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://kodanect-frontend.netlify.app",
                        "http://localhost:5173",
                        "http://localhost:3000"
                )
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * 정적 자원 핸들러 설정
     * - 외부 파일 시스템에 저장된 리소스를 특정 URL 경로로 노출
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // null 방지 및 끝에 "/" 붙이기
        String fileStorePath = ensureEndsWithSlash(globalsProperties.getFileStorePath(), "./uploads");
        String fileBaseUrl = ensureEndsWithSlash(globalsProperties.getFileBaseUrl(), "/image/uploads");

        registry.addResourceHandler(fileBaseUrl + "**")
                .addResourceLocations("file:" + fileStorePath);
    }

    private String ensureEndsWithSlash(String path, String defaultValue) {
        if (path == null || path.isBlank()) {
            return defaultValue.endsWith("/") ? defaultValue : defaultValue + "/";
        }
        return path.endsWith("/") ? path : path + "/";
    }
}



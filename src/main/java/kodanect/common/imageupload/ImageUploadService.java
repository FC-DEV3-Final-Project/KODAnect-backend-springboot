package kodanect.common.imageupload;

import kodanect.common.config.GlobalsProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final GlobalsProperties globalsProperties;

    public ImageUploadService(GlobalsProperties globalsProperties) {
        this.globalsProperties = globalsProperties;
    }

    /**
     * CKEditor 이미지 파일을 저장하고 접근 URL을 반환합니다.
     * @param file 업로드된 MultipartFile 객체 (CKEditor에서 'upload'라는 이름으로 전송됨)
     * @return CKEditor가 사용할 이미지 URL 문자열
     * @throws IOException 파일 저장 중 발생할 수 있는 예외
     */
    public String saveImageAndGetUrl(MultipartFile file) throws IOException {
        // 1. 업로드 디렉토리 경로 가져오기 및 생성
        String uploadDirPath = globalsProperties.getFileStorePath();
        Path uploadPath = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath); // 디렉토리가 없으면 생성

        // 2. 파일명 생성 (고유한 이름)
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension; // UUID로 고유한 파일명 생성
        Path filePath = uploadPath.resolve(fileName); // 최종 저장 경로

        // 3. 파일 저장
        file.transferTo(filePath); // 실제 파일 시스템에 저장

        // 4. 저장된 이미지의 웹 접근 URL 생성
        // globalsProperties.getFileBaseUrl() 예: "/uploads"
        // 최종 URL 예: http://localhost:8080/uploads/generated-uuid.jpg
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(globalsProperties.getFileBaseUrl()) // CKEditor에서 접근할 기본 URL (ex: /uploads)
                .path("/")
                .path(fileName) // 생성된 고유 파일명
                .toUriString();
    }
}

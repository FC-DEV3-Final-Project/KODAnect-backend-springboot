package kodanect.domain.donation.controller;

import kodanect.common.config.GlobalsProperties;
import kodanect.domain.donation.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/app/upload") // POST 요청: http://localhost:8080/app/upload/upload_img
@RequiredArgsConstructor
public class ImageUploadController {

    // 도메인 주소 하드코딩 (정적 이미지 접근 URL 앞에 붙는 prefix)
    private static final String DOMAIN_URL = "https://www.koda1458.kr"; // 실제 외부에서 접근하는 웹 도메인

    private final GlobalsProperties globals;
    private final MessageSourceAccessor msg;


    @PostMapping("/upload_img") // CKEditor가 이 경로로 이미지 업로드 요청 보냄
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        // 1. 비어있는 파일 체크
        if (file.isEmpty()) {
            throw new BadRequestException(msg.getMessage("upload.error.empty"));
        }

        // 2. MIME 타입 허용 리스트
        String contentType = file.getContentType();
        List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/gif");
        if (!allowedTypes.contains(contentType)) {
            throw new BadRequestException(msg.getMessage("upload.error.invalidType"));
        }

        // 3. 크기 제한 체크
        if (file.getSize() > globals.getPosblAtchFileSize()) {
            throw new BadRequestException(msg.getMessage("upload.error.sizeExceeded"));
        }

        // 3. 파일명 생성
        String rawFileName = Optional.ofNullable(file.getOriginalFilename()).orElse("unknown.jpg");
        String safeFileName = Paths.get(rawFileName).getFileName().toString(); // 경로 제거
        String ext = safeFileName.contains(".") ? safeFileName.substring(safeFileName.lastIndexOf(".")) : ".jpg";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String storedFileName = timestamp + ext; // 예: 20250617_154530.jpg

        // 4. 저장 경로 설정 (/app/files/upload_img/a1b2c3d4_cat.jpg)
        // 도커에서는 실제 /home/app/files/upload_img/ 에 저장됨
        Path target = Paths.get(globals.getFileStorePath(), "upload_img", storedFileName);
        Files.createDirectories(target.getParent());
        file.transferTo(target.toFile());

        // 5. 클라이언트에게 반환할 URL의 기본 경로 설정
        // application-prod.properties에 정의 안 되어 있으면 기본값 "/upload_img"
        String baseUrl = (globals.getFileBaseUrl() != null) ? globals.getFileBaseUrl() : "/upload_img";

        // 6. 전체 이미지 URL 구성
        // 예: "https://www.koda1458.kr/upload_img/a1b2c3d4_cat.jpg"
        String fileUrl = DOMAIN_URL + baseUrl + "/" + storedFileName;

        // 7. CKEditor5가 필요로 하는 형식으로 JSON 응답
        Map<String, String> response = Map.of("url", fileUrl);
        return ResponseEntity.ok(response);
    }
}
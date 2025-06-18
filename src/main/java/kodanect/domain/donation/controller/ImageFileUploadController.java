package kodanect.domain.donation.controller;

import kodanect.domain.donation.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/app/upload")
@RequiredArgsConstructor
public class ImageFileUploadController {

    private static final String PROD_DOMAIN = "https://koda1.elementsoft.biz";
    private static final String DEV_DOMAIN  = "http://localhost:8080";
    private static final Integer EXT_LENGTH = 10;

    private final MessageSourceAccessor msg;
    private final Environment env;

    @PostMapping("/upload_img/{category}")
    public ResponseEntity<Map<String,String>> uploadImage(
            @PathVariable("category") String category,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        // 1) 빈 파일 체크
        if (file.isEmpty()) {
            throw new BadRequestException(msg.getMessage("upload.error.empty"));
        }

        // 2) MIME 타입 체크
        String contentType = file.getContentType();
        if (!List.of("image/jpeg", "image/png", "image/gif").contains(contentType)) {
            throw new BadRequestException(msg.getMessage("upload.error.invalidType"));
        }

        // 3) 크기 제한 체크
        long maxSize = Long.parseLong(
                Optional.ofNullable(env.getProperty("globals.posbl-atch-file-size"))   // dev 프로퍼티
                        .orElse(env.getProperty("Globals.posblAtchFileSize", "5242880")) // prod 프로퍼티
        );
        if (file.getSize() > maxSize) {
            throw new BadRequestException(msg.getMessage("upload.error.sizeExceeded"));
        }
        // 카테고리 경로 검증
        if (category.contains("..") || category.contains("/") || category.contains("\\")) {
            throw new BadRequestException(msg.getMessage("error.wrong.path"));
        }

        // 4) 파일명 생성 (timestamp + 랜덤숫자 +  확장자)
        String rawName   = Optional.ofNullable(file.getOriginalFilename()).orElse("unknown.jpg");
        String safeName  = Paths.get(rawName).getFileName().toString();
        String ext       = safeName.contains(".")
                ? safeName.substring(safeName.lastIndexOf("."))
                : ".jpg";
        String ts        = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomNum = new Random().nextInt(900) + 100; //100~999사이의 숫자

        if (ext.length() > EXT_LENGTH || ext.contains("/") || ext.contains("\\")) {
            throw new BadRequestException("error.wrong.ext");
        }
        String storedFileName = ts + "_" + randomNum + ext;

        // 5) 실제 저장 경로 결정
        String storePath = Optional.ofNullable(env.getProperty("Globals.fileStorePath"))   // prod
                .orElse(env.getProperty("globals.file-store-path", "./uploads"));  // dev
        String absStore  = Paths.get(storePath).toAbsolutePath().normalize().toString();
        Path target      = Paths.get(absStore, "upload_img", category, storedFileName);
        Files.createDirectories(target.getParent());
        file.transferTo(target.toFile());

        // 6) 도메인 선택 (prod vs dev)
        boolean isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");
        String domain  = isProd ? PROD_DOMAIN : DEV_DOMAIN;

        // 7) 반환 URL 조합 (★여기서 upload_img 폴더를 꼭 포함해야 함★)
        //    WebMvcConfig: "/image/uploads/**" → "file:/app/files/"
        String fileUrl = domain
                + "/image/uploads"
                + "/upload_img"
                + "/" + category
                + "/" + storedFileName;

        // 8) 응답
        return ResponseEntity.ok(Map.of("url", fileUrl));
    }
}
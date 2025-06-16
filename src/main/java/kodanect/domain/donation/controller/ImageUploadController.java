package kodanect.domain.donation.controller;

import kodanect.common.config.GlobalsProperties;
import kodanect.domain.donation.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/app/upload")
@RequiredArgsConstructor
public class ImageUploadController {
    // application.properties 에서 바인딩
    private final GlobalsProperties globals;
    private final MessageSourceAccessor msg;

    @PostMapping("/upload_img/donation")
    public ResponseEntity<Map<String,String>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        // 1) 검증: 비어있는지, 확장자/크기 등
        if (file.isEmpty()) {
            throw new BadRequestException(msg.getMessage("upload.error.empty"));
        }
        String contentType = file.getContentType();
        if (!List.of("image/jpeg","image/png","image/gif")
                .contains(contentType)) {
            throw new BadRequestException(msg.getMessage("upload.error.invalidType"));
        }
        if (file.getSize() > globals.getPosblAtchFileSize()) {
            throw new BadRequestException(msg.getMessage("upload.error.sizeExceeded"));
        }

        // 2) 저장: globals.fileStorePath + "/upload_img/"
        String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path target = Paths.get(globals.getFileStorePath(), "upload_img", storedName);
        Files.createDirectories(target.getParent());
        file.transferTo(target);

        // 3) 클라이언트에 반환할 URL 조합
        // fileBaseUrl = "/upload_img" (application.properties)
        String fileUrl = globals.getFileBaseUrl() + "/upload_img/" + storedName;
        return ResponseEntity.ok(Map.of("url", fileUrl));
    }
}
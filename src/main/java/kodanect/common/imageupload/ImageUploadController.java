package kodanect.common.imageupload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ImageUploadController {

    private static final String ERROR_KEY = "error";
    private static final String URL_KEY = "url";

    private final ImageUploadService imageUploadService;

    public ImageUploadController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    /**
     * CKEditor 이미지 업로드 API 엔드포인트
     * CKEditor는 'upload'라는 이름으로 MultipartFile을 전송합니다.
     * @param file CKEditor에서 업로드된 이미지 파일
     * @return CKEditor가 요구하는 JSON 응답 (성공 시 {"url": "이미지 URL"}, 실패 시 {"error": "에러 메시지"})
     */
    @PostMapping("/api/ckeditor/image-upload") // CKEditor의 uploadUrl과 일치시켜야 합니다.
    public ResponseEntity<Map<String, String>> uploadCkeditorImage(
            @RequestParam("upload") MultipartFile file) { // CKEditor 필드명은 'upload'

        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put(ERROR_KEY, "파일이 존재하지 않습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            String imageUrl = imageUploadService.saveImageAndGetUrl(file);
            response.put(URL_KEY, imageUrl); // CKEditor가 기대하는 JSON 형식
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException ex) {
            response.put(ERROR_KEY, "파일 업로드 중 오류가 발생했습니다: " + ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            response.put(ERROR_KEY, "알 수 없는 오류가 발생했습니다: " + ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

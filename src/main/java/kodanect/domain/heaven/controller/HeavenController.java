package kodanect.domain.heaven.controller;

import kodanect.common.response.ApiResponse;
import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.service.HeavenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/heavenLetters")
@RequiredArgsConstructor
public class HeavenController {

    private final HeavenService heavenService;
    private final MessageSourceAccessor messageSourceAccessor;

    /* 게시물 전체 조회 (페이징) */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HeavenResponse>>> getHeavenList() {
        List<HeavenResponse> heavenList = heavenService.getHeavenList();

        String message = messageSourceAccessor.getMessage("heaven.list.get.success");

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message, heavenList));
    }
}

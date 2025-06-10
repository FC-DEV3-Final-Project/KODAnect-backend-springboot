package kodanect.domain.heaven.service;

import kodanect.domain.heaven.dto.HeavenResponse;

import java.util.List;

public interface HeavenService {

    /* 게시물 전체 조회 (페이징) */
    List<HeavenResponse> getHeavenList();
}

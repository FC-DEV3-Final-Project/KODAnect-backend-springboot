package kodanect.domain.heaven.service.impl;

import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.repository.HeavenRepository;
import kodanect.domain.heaven.service.HeavenService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeavenServiceImpl implements HeavenService {

    private final HeavenRepository heavenRepository;

    public HeavenServiceImpl(HeavenRepository heavenRepository) {
        this.heavenRepository = heavenRepository;
    }

    /* 게시물 전체 조회 (페이징) */
    @Override
    public List<HeavenResponse> getHeavenList() {
        return heavenRepository.findByCursor();
    }
}

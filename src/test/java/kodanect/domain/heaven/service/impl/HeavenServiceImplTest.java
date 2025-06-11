package kodanect.domain.heaven.service.impl;

import kodanect.common.response.CursorPaginationResponse;
import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.repository.HeavenCommentRepository;
import kodanect.domain.heaven.repository.HeavenRepository;
import kodanect.domain.heaven.service.HeavenCommentService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HeavenServiceImplTest {

    @InjectMocks
    private HeavenServiceImpl heavenServiceImpl;
    @Mock
    private HeavenRepository heavenRepository;
    @Mock
    private HeavenCommentRepository heavenCommentRepository;
    @Mock
    private HeavenCommentService heavenCommentService;

    @Test
    @DisplayName("게시물 전체 조회 테스트")
    public void getHeavenListTest() throws Exception {
        /* given */
        Integer cursor = 1000;
        int size = 20;

        String anonymityFlag = "N";
        int readCount = 5;
        LocalDateTime now = LocalDateTime.now();

        List<HeavenResponse> heavenResponseList = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            heavenResponseList.add(new HeavenResponse(i, "제목"+i, "기증자"+i, "작성자"+i, anonymityFlag, readCount, now));
        }

        when(heavenRepository.findByCursor(eq(cursor), any(Pageable.class))).thenReturn(heavenResponseList);
        when(heavenRepository.count()).thenReturn(50L);

        /* when */
        CursorPaginationResponse<HeavenResponse, Integer> cursorPaginationResponse = heavenServiceImpl.getHeavenList(cursor, size);
        HeavenResponse firstHeavenResponse = cursorPaginationResponse.getContent().get(0);

        /* then */
        assertNotNull(cursorPaginationResponse);
        assertEquals(size, cursorPaginationResponse.getContent().size());
        assertTrue(cursorPaginationResponse.isHasNext());
        assertEquals(50, cursorPaginationResponse.getTotalCount());

        assertEquals(1, firstHeavenResponse.getLetterSeq());
        assertEquals("제목1", firstHeavenResponse.getLetterTitle());
        assertEquals("기증자1", firstHeavenResponse.getDonorName());
        assertEquals("작성자1", firstHeavenResponse.getLetterWriter());
        assertEquals(anonymityFlag, firstHeavenResponse.getAnonymityFlag());
        assertEquals(Integer.valueOf(readCount), firstHeavenResponse.getReadCount());
        assertEquals(now, firstHeavenResponse.getWriteTime());

    }
}
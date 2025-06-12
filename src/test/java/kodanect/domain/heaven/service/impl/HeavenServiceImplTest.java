package kodanect.domain.heaven.service.impl;

import kodanect.common.response.CursorPaginationResponse;
import kodanect.common.response.CursorReplyPaginationResponse;
import kodanect.common.util.CursorFormatter;
import kodanect.domain.heaven.dto.HeavenCommentResponse;
import kodanect.domain.heaven.dto.HeavenDetailResponse;
import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.entity.Heaven;
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
import java.util.Optional;

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
        Long heavenCount = 50L;

        String anonymityFlag = "N";
        int readCount = 5;
        LocalDateTime now = LocalDateTime.now();

        List<HeavenResponse> heavenResponseList = new ArrayList<>();

        for (int i = 1; i <= heavenCount; i++) {
            heavenResponseList.add(new HeavenResponse(i, "제목"+i, "기증자"+i, "작성자"+i, anonymityFlag, readCount, now));
        }

        when(heavenRepository.findByCursor(eq(cursor), any(Pageable.class))).thenReturn(heavenResponseList);
        when(heavenRepository.count()).thenReturn(heavenCount);

        /* when */
        CursorPaginationResponse<HeavenResponse, Integer> cursorPaginationResponse = heavenServiceImpl.getHeavenList(cursor, size);
        HeavenResponse firstHeavenResponse = cursorPaginationResponse.getContent().get(0);

        /* then */
        assertNotNull(cursorPaginationResponse);
        assertEquals(size, cursorPaginationResponse.getContent().size());
        assertTrue(cursorPaginationResponse.isHasNext());
        assertEquals(heavenCount, cursorPaginationResponse.getTotalCount());

        assertEquals(1, firstHeavenResponse.getLetterSeq());
        assertEquals("제목1", firstHeavenResponse.getLetterTitle());
        assertEquals("기증자1", firstHeavenResponse.getDonorName());
        assertEquals("작성자1", firstHeavenResponse.getLetterWriter());
        assertEquals(anonymityFlag, firstHeavenResponse.getAnonymityFlag());
        assertEquals(Integer.valueOf(readCount), firstHeavenResponse.getReadCount());
        assertEquals(now, firstHeavenResponse.getWriteTime());
    }

    @Test
    @DisplayName("검색을 통한 게시물 전체 조회 테스트")
    public void getHeavenListSearchResultTest() {
        /* given */
        String type = "all";
        String keyWord = "제목";
        Integer cursor = 1000;
        int size = 20;
        Long heavenCount = 30L;

        String anonymityFlag = "N";
        int readCount = 13;
        LocalDateTime now = LocalDateTime.now();

        List<HeavenResponse> heavenResponseList = new ArrayList<>();

        for (int i = 1; i <= heavenCount; i++) {
            heavenResponseList.add(new HeavenResponse(i, "제목"+i, "기증자"+i, "작성자"+i, anonymityFlag, readCount, now));
        }

        when(heavenRepository.findByTitleOrContentsContaining(eq(keyWord), eq(cursor), any(Pageable.class))).thenReturn(heavenResponseList);
        when(heavenRepository.countByTitleOrContentsContaining(eq(keyWord))).thenReturn(30);

        /* when */
        CursorPaginationResponse<HeavenResponse, Integer> cursorPaginationResponse = heavenServiceImpl.getHeavenListSearchResult(type, keyWord, cursor, size);
        HeavenResponse firstHeavenResponse = cursorPaginationResponse.getContent().get(0);

        /* then */
        assertNotNull(cursorPaginationResponse);
        assertEquals(size, cursorPaginationResponse.getContent().size());
        assertTrue(cursorPaginationResponse.isHasNext());
        assertEquals(heavenCount, cursorPaginationResponse.getTotalCount());

        assertEquals(1, firstHeavenResponse.getLetterSeq());
        assertEquals("제목1", firstHeavenResponse.getLetterTitle());
        assertEquals("기증자1", firstHeavenResponse.getDonorName());
        assertEquals("작성자1", firstHeavenResponse.getLetterWriter());
        assertEquals(anonymityFlag, firstHeavenResponse.getAnonymityFlag());
        assertEquals(Integer.valueOf(readCount), firstHeavenResponse.getReadCount());
        assertEquals(now, firstHeavenResponse.getWriteTime());
    }

    @Test
    @DisplayName("게시물 상세 조회 테스트")
    public void getHeavenDetailTest() {
        // 조회 수 증가 테스트

        /* given */
        int letterSeq = 1;
        LocalDateTime now = LocalDateTime.now();
        int commentSize = 3;
        int commentCount = 10;

        Heaven heaven = Heaven.builder()
                .letterSeq(letterSeq)
                .letterTitle("사랑하는 가족에게")
                .letterPasscode("asdf1234")
                .letterWriter("작성자")
                .anonymityFlag("Y")
                .readCount(0)
                .letterContents("이 편지는 하늘로 보냅니다.")
                .writeTime(now)
                .build();

        List<HeavenCommentResponse> heavenCommentResponseList = new ArrayList<>();
        for (int i = 1; i <= commentCount; i++) {
            heavenCommentResponseList.add(new HeavenCommentResponse(i, "댓글 작성자"+i, "댓글 내용"+i, now));
        }

        when(heavenRepository.findById(eq(letterSeq))).thenReturn(Optional.of(heaven));
        when(heavenCommentService.getHeavenCommentList(eq(letterSeq), eq(null), eq(commentSize))).thenReturn(heavenCommentResponseList);
        when(heavenCommentRepository.countByHeaven(heaven)).thenReturn(commentCount);

        CursorReplyPaginationResponse<HeavenCommentResponse, Integer> cursorReplyPaginationResponse = CursorFormatter.cursorReplyFormat(heavenCommentResponseList, commentSize);

        /* when */
        HeavenDetailResponse heavenDetailResponse = heavenServiceImpl.getHeavenDetail(letterSeq);
        HeavenCommentResponse firstHeavenCommentResponse = heavenDetailResponse.getHeavenCommentResponseList().get(0);

        /* then */
        assertNotNull(heavenDetailResponse);
        assertEquals(commentSize, heavenDetailResponse.getHeavenCommentResponseList().size());
        assertEquals(Integer.valueOf(3), heavenDetailResponse.getReplyNextCursor());
        assertTrue(heavenDetailResponse.isReplyHasNext());
        assertEquals(commentCount, heavenDetailResponse.getTotalCommentCount());

        assertEquals(letterSeq, heavenDetailResponse.getLetterSeq());
        assertEquals("사랑하는 가족에게", heavenDetailResponse.getLetterTitle());
        assertEquals("asdf1234", heavenDetailResponse.getLetterPasscode());
        assertEquals("작성자", heavenDetailResponse.getLetterWriter());
        assertEquals("Y", heavenDetailResponse.getAnonymityFlag());
        assertEquals(Integer.valueOf(1), heavenDetailResponse.getReadCount()); // 조회수 1 증가
        assertEquals("이 편지는 하늘로 보냅니다.", heavenDetailResponse.getLetterContents());
        assertEquals(now, heavenDetailResponse.getWriteTime());

        assertEquals(1, firstHeavenCommentResponse.getCommentSeq());
        assertEquals("댓글 작성자1", firstHeavenCommentResponse.getCommentWriter());
        assertEquals("댓글 내용1", firstHeavenCommentResponse.getContents());
        assertEquals(now, firstHeavenCommentResponse.getWriteTime());
    }
}
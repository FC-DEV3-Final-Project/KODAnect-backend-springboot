package kodanect.domain.heaven.controller;

import kodanect.common.response.CursorPaginationResponse;
import kodanect.domain.heaven.dto.HeavenResponse;
import kodanect.domain.heaven.service.HeavenService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(HeavenController.class)
public class HeavenControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HeavenService heavenService;
    @MockBean
    private MessageSourceAccessor messageSourceAccessor;

    @Before
    public void beforeEach() {
        when(messageSourceAccessor.getMessage("heaven.list.get.success")).thenReturn("게시물 전체 조회 성공");
    }

    @Test
    @DisplayName("게시물 전체 조회 테스트")
    public void getHeavenListTest() throws Exception {
        /* given */
        String anonymityFlag = "N";
        int readCount = 5;
        LocalDateTime now = LocalDateTime.now();
        Integer nextCursor = 10;
        boolean hasNext = true;
        long totalCount = 30;


        List<HeavenResponse> heavenResponseList = new ArrayList<>();

        for (int i = 1; i <= totalCount; i++) {
            heavenResponseList.add(new HeavenResponse(i, "제목"+i, "기증자"+i, "작성자"+i, anonymityFlag, readCount, now));
        }

        CursorPaginationResponse<HeavenResponse, Integer> cursorPaginationResponse = CursorPaginationResponse.<HeavenResponse, Integer>builder()
                .content(heavenResponseList)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .totalCount(totalCount)
                .build();
        System.out.println("cursorPaginationResponse.getContent() = " + cursorPaginationResponse.getContent());

        when(heavenService.getHeavenList(eq(30), eq(20))).thenReturn(cursorPaginationResponse);

        /* when & then */
        mockMvc.perform(get("/heavenLetters")
                    .param("cursor", "30")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("게시물 전체 조회 성공"))
                .andExpect(jsonPath("$.data.content[29].letterSeq").value(30))
                .andExpect(jsonPath("$.data.nextCursor").value(10))
                .andExpect(jsonPath("$.data.hasNext").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(30));
    }
}
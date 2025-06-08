package kodanect.domain.logging.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kodanect.domain.logging.dto.ActionLogPayload;
import kodanect.domain.logging.entity.ActionLog;
import kodanect.domain.logging.repository.ActionLogRepository;
import kodanect.domain.logging.service.ActionLogScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ActionLogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActionLogRepository actionLogRepository;

    @Autowired
    private ActionLogScheduler actionLogScheduler;

    /**
     * 각 테스트 전 DB 초기화
     */
    @BeforeEach
    void setUp() {
        actionLogRepository.deleteAll();
    }

    /**
     * GIVEN: 클라이언트가 두 개의 유효한 액션 로그를 전송하고
     * WHEN: 컨트롤러가 해당 로그를 수신하여 메모리 버퍼에 적재한 후
     *       스케줄러가 강제로 flushAllLogsForcefully()를 호출해 로그를 DB에 저장하면
     * THEN: CRUD 유형별로 2개의 로그 엔티티가 DB에 저장되어야 하며,
     *       각 엔티티의 logText에는 상세 로그 정보가 포함되어야 한다
     */
    @Test
    @DisplayName("액션 로그 수집 통합 테스트")
    void shouldSaveActionLogsThroughApi() throws Exception {
        List<ActionLogPayload> payloads = List.of(
                ActionLogPayload.builder().type("read").target("btn1").build(),
                ActionLogPayload.builder().type("click").target("btn2").build()
        );

        mockMvc.perform(post("/action-log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloads)))
                .andExpect(status().isOk());

        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            actionLogScheduler.flushAllLogsForcefully();
            List<ActionLog> saved = actionLogRepository.findAll();
            assertThat(saved).isNotEmpty();
        });

        List<ActionLog> savedLogs = actionLogRepository.findAll();
        assertThat(savedLogs).hasSize(2);
    }

}

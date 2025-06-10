package kodanect.domain.donation.service;

import kodanect.common.util.MessageResolver;
import kodanect.domain.donation.dto.OffsetBasedPageRequest;
import kodanect.domain.donation.dto.request.DonationStoryCreateRequestDto;
import kodanect.domain.donation.dto.request.DonationStoryModifyRequestDto;
import kodanect.domain.donation.dto.request.VerifyStoryPasscodeDto;
import kodanect.domain.donation.dto.response.AreaCode;
import kodanect.domain.donation.dto.response.DonationStoryDetailDto;
import kodanect.domain.donation.dto.response.DonationStoryListDto;
import kodanect.domain.donation.dto.response.DonationStoryWriteFormDto;
import kodanect.domain.donation.entity.DonationStory;
import kodanect.domain.donation.exception.BadRequestException;
import kodanect.domain.donation.exception.DonationNotFoundException;
import kodanect.domain.donation.exception.NotFoundException;
import kodanect.domain.donation.repository.DonationRepository;
import kodanect.domain.donation.service.impl.DonationServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DonationServiceTest {

    @Mock private DonationRepository donationRepository;
    @Mock private MessageResolver messageResolver;
    @InjectMocks private DonationServiceImpl donationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findStoriesWithCursor_정상_조회() {
        Long cursor = null;
        int size = 2;
        Pageable pageable = PageRequest.of(0, size + 1); // 커서 기반은 size+1로 조회

        List<DonationStoryListDto> mockList = List.of(
                new DonationStoryListDto(1L, "제목1", "작성자1", 10, null),
                new DonationStoryListDto(2L, "제목2", "작성자2", 20, null),
                new DonationStoryListDto(3L, "제목3", "작성자3", 30, null) // size + 1개로 더보기 확인
        );

        when(donationRepository.findByCursor(cursor, pageable)).thenReturn(mockList);

        var result = donationService.findStoriesWithCursor(cursor, size);

        assertThat(result.getContent()).hasSize(2); // 실제 응답은 요청 size만큼만 제공
        assertThat(result.isHasNext()).isTrue();    // 다음 페이지 존재 여부 확인
        assertThat(result.getNextCursor()).isEqualTo(2L); // 마지막으로 반환된 storySeq
    }

    @Test
    public void loadDonationStoryFormData_정상() {
        DonationStoryWriteFormDto dto = donationService.loadDonationStoryFormData();
        assertThat(dto.getAreaOptions()).containsExactly(AreaCode.AREA100, AreaCode.AREA200, AreaCode.AREA300);
    }

    @Test
    public void createDonationStory_정상등록_파일없음() {
        DonationStoryCreateRequestDto dto = new DonationStoryCreateRequestDto(
                AreaCode.AREA100, "제목", "abcd1234", "작성자", "내용", null
        );
        donationService.createDonationStory(dto);
        verify(donationRepository).save(any(DonationStory.class));
    }

    @Test(expected = BadRequestException.class)
    public void createDonationStory_비밀번호형식오류_예외() {
        DonationStoryCreateRequestDto dto = new DonationStoryCreateRequestDto(
                AreaCode.AREA100, "제목", "1234", "작성자", "내용", null
        );
        when(messageResolver.get(any())).thenReturn("비밀번호 형식 오류");
        donationService.createDonationStory(dto); // 예외 발생
    }

    @Test
    public void findDonationStory_정상조회_조회수증가() {
        // given
        DonationStory story = DonationStory.builder()
                .storySeq(1L)
                .storyTitle("제목")
                .storyPasscode("abcd1234")
                .storyContents("내용")
                .readCount(0)
                .areaCode(AreaCode.AREA100)
                .writeTime(LocalDateTime.now())
                .delFlag("N")
                .build();

        when(donationRepository.findWithCommentsById(1L)).thenReturn(Optional.of(story));

        // when
        DonationStoryDetailDto dto = donationService.findDonationStoryWithTopComments(1L);

        // then
        assertThat(dto.getTitle()).isEqualTo("제목");
        assertThat(story.getReadCount()).isEqualTo(1); // 조회수 증가 확인
    }

    @Test
    public void findDonationStory_존재하지않음_예외() {
        // given
        when(donationRepository.findWithCommentsById(99L)).thenReturn(Optional.empty());
        when(messageResolver.get(any())).thenReturn("찾을 수 없음");

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(DonationNotFoundException.class, () ->
                donationService.findDonationStoryWithTopComments(99L));
    }

    @Test
    public void verifyPasswordWithPassword_불일치_예외() {
        // given
        DonationStory story = DonationStory.builder().storySeq(1L).storyPasscode("abcd1234").build();
        when(donationRepository.findById(1L)).thenReturn(Optional.of(story));
        when(messageResolver.get(any())).thenReturn("비밀번호 불일치");

        VerifyStoryPasscodeDto passcode = new VerifyStoryPasscodeDto("wrongpass");

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () ->
                donationService.verifyPasswordWithPassword(1L, passcode));
    }

    @Test
    public void deleteDonationStory_성공() {
        // given
        DonationStory story = DonationStory.builder().storySeq(1L).storyPasscode("abcd1234").build();
        when(donationRepository.findWithCommentsById(1L)).thenReturn(Optional.of(story));

        // when
        donationService.deleteDonationStory(1L, new VerifyStoryPasscodeDto("abcd1234"));

        // then
        verify(donationRepository).delete(story);
    }

    @Test
    public void modifyDonationStory_성공() {
        // given
        DonationStory story = DonationStory.builder()
                .storySeq(1L)
                .storyTitle("기존제목")
                .areaCode(AreaCode.AREA100)
                .build();

        when(donationRepository.findWithCommentsById(1L)).thenReturn(Optional.of(story));

        DonationStoryModifyRequestDto dto = DonationStoryModifyRequestDto.builder()
                .storyTitle("수정된제목")
                .areaCode(AreaCode.AREA200)
                .build();

        // when
        donationService.updateDonationStory(1L, dto);

        // then
        assertThat(story.getStoryTitle()).isEqualTo("수정된제목");
        assertThat(story.getAreaCode()).isEqualTo(AreaCode.AREA200);
    }
}
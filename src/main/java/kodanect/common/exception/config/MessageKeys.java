package kodanect.common.exception.config;

/**
 * 국제화 메시지 키 상수 정의
 *
 * 역할:
 * - 메시지 키 하드코딩 방지
 * - 메시지 키 일괄 관리
 */
public final class MessageKeys {
    // 게시글 관련
    public static final String ARTICLE_NOT_FOUND = "article.notFound";
    public static final String INVALID_BOARD_CODE = "board.invalidCode";
    public static final String FILE_NOT_FOUND = "file.notFound";
    public static final String FILE_ACCESS_VIOLATION = "file.accessViolation";
    public static final String ARTICLE_DETAIL_SUCCESS = "article.detailSuccess";
    public static final String ARTICLE_LIST_SUCCESS = "article.listSuccess";

    private MessageKeys() {}

}

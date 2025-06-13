package kodanect.common.util;

import kodanect.common.response.CursorPaginationResponse;

import java.util.List;

public class CursorPaginationUtils {

    private CursorPaginationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T,C> CursorPaginationResponse<T, C> paginationWithCursor(List<T> list, T cursor, int size) {

        int start = cursor == null ? 0 : list.indexOf(cursor) + 1;

        int end = Math.min(start + size+1, list.size());

        List<T> slice = list.subList(start, end);

        boolean hasNext = slice.size() > size;

        List<T> content = slice.stream().limit(size).toList();

        return CursorPaginationResponse.<T, C>builder()
                .hasNext(hasNext)
                .content(content)
                .build();
    }
}

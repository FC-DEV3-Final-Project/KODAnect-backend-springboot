package config;

import kodanect.common.exception.config.SecureLogger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SecureLoggerTest {

    private Logger mockLogger;
    private SecureLogger secureLogger;

    @Before
    public void setUp() {
        mockLogger = mock(Logger.class);
        secureLogger = SecureLogger.forTest(mockLogger);
    }

    @Test
    public void testInfo_sanitizesMessage() {
        when(mockLogger.isInfoEnabled()).thenReturn(true);

        secureLogger.info("로그\n인젝션\r테스트");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).info(captor.capture());

        assertEquals("로그인젝션테스트", captor.getValue());
    }

    @Test
    public void testWarn_sanitizesArgs() {
        when(mockLogger.isWarnEnabled()).thenReturn(true);

        secureLogger.warn("정상 메시지", "파라\n\r미터");

        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).warn(eq("정상 메시지"), argCaptor.capture());

        assertEquals("파라미터", argCaptor.getValue());
    }

    @Test
    public void testError_withThrowable_messageSanitized() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        Throwable ex = new RuntimeException("예외");

        secureLogger.error("에러\n메시지", ex);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).error(captor.capture(), eq(ex));

        assertEquals("에러메시지", captor.getValue());
    }

    @Test
    public void testDebug_nullHandledGracefully() {
        when(mockLogger.isDebugEnabled()).thenReturn(true);

        secureLogger.debug(null, (Object[]) null);

        verify(mockLogger).debug(null, (Object[]) null);
    }
}

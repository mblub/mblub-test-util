package com.mblub.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

public class Log4j2TestingUtil {
  private static final String MOCK_APPENDER_NAME = "mockAppender";
  private static List<String> actualLogMessages;

  public static void beforeEach() {
    actualLogMessages = new ArrayList<>();
    LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
    Configuration logConfig = logContext.getConfiguration();
    Appender appender = logConfig.getAppender(MOCK_APPENDER_NAME);
    if (appender == null) {
      Appender mockLogAppender = mock(Appender.class);
      when(mockLogAppender.getName()).thenReturn(MOCK_APPENDER_NAME);
      when(mockLogAppender.isStarted()).thenReturn(true);

      logConfig.addAppender(mockLogAppender);
      logContext.getRootLogger().addAppender(logConfig.getAppender(MOCK_APPENDER_NAME));
      logContext.updateLoggers();

      doAnswer(inv -> actualLogMessages.add(((LogEvent) inv.getArgument(0)).getMessage().getFormattedMessage()))
              .when(mockLogAppender).append(any());
    }
  }

  public static void assertLogMessages(String... expectLogMessages) {
    assertThat("log4j2 messages", actualLogMessages, contains(expectLogMessages));
  }

  public static void assertLogMessages(List<String> expectLogMessages) {
    assertEquals(expectLogMessages, actualLogMessages, "log4j2 messages");
  }

  public static void assertLogMessageCount(int expectCount) {
    assertEquals(expectCount, actualLogMessages.size(), "log message count");
  }
}

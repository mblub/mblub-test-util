package com.mblub.unit;

import static com.mblub.unit.Assertions.assertStreamContents;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

public class Log4j2TestingUtil {
  private static final String MOCK_APPENDER_NAME = "mockAppender";
  private static List<String> actualLogMessages;
  private static List<LogEventExtract> actualLogEventExtracts;

  public static class LogEventExtract {
    private Map<String, String> context;
    private String message;

    public LogEventExtract(LogEvent event) {
      context = event.getContextData().toMap();
      message = event.getMessage().getFormattedMessage();
    }

    public Map<String, String> getContext() {
      return context;
    }

    public String getMessage() {
      return message;
    }
  }

  public static void beforeEach() {
    actualLogMessages = new ArrayList<>();
    actualLogEventExtracts = new ArrayList<>();
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

      doAnswer(inv -> {
        LogEvent event = inv.getArgument(0);
        LogEventExtract extract = new LogEventExtract(event);
        actualLogEventExtracts.add(extract);
        actualLogMessages.add(extract.getMessage());
        return null;
      }).when(mockLogAppender).append(any());
    }
  }

  public static List<String> getActualLogMessages() {
    return actualLogMessages;
  }

  public static List<LogEventExtract> getActualLogEventExtracts() {
    return actualLogEventExtracts;
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

  public static void assertLogMessages(Function<LogEventExtract, String> messageFormatter,
          String... expectLogMessages) {
    assertStreamContents("formatted log4j2 events", Stream.of(expectLogMessages),
            actualLogEventExtracts.stream().map(messageFormatter));
  }

  public static void assertLogMessages(Function<LogEventExtract, String> messageFormatter,
          List<String> expectLogMessages) {
    assertStreamContents("formatted log4j2 events", expectLogMessages.stream(),
            actualLogEventExtracts.stream().map(messageFormatter));
  }
}

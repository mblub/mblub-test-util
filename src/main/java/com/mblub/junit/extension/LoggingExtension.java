package com.mblub.junit.extension;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.descriptor.ClassExtensionContext;

public class LoggingExtension implements BeforeEachCallback, AfterEachCallback {
  private static final Logger LOG = LogManager.getLogger(LoggingExtension.class);
  private long startTime;

  private transient StringBuilder displayNameBuilder;
  private String displayName;

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    startTime = System.currentTimeMillis();
    displayNameBuilder = new StringBuilder();
    appendDisplayNames(context);
    displayName = displayNameBuilder.toString();
    LOG.info(displayName + ": start");
  }

  private void appendDisplayNames(ExtensionContext context) {
    if (!(context instanceof ClassExtensionContext)) {
      context.getParent().ifPresent(this::appendDisplayNames);
      displayNameBuilder.append(':');
    }
    displayNameBuilder.append(context.getDisplayName());
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    long durationMillis = System.currentTimeMillis() - startTime;
    Duration duration = Duration.ofMillis(durationMillis);
    LOG.info(displayName + ": end; duration: " + duration.toString().substring(2).toLowerCase());
  }
}

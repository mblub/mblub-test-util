package com.mblub.junit.runner;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.mockito.junit.MockitoJUnitRunner;

// TODO: convert to the equivalent Junit5 functionality
public class DescriptiveMockitoRunner extends MockitoJUnitRunner {
  private static final Logger LOG = LogManager.getLogger(DescriptiveMockitoRunner.class);

  public DescriptiveMockitoRunner(Class<?> runnerClass) throws InvocationTargetException {
    super(runnerClass);
  }

  @Override
  public void run(RunNotifier notifier) {
    notifier.addListener(new RunListener() {

      @Override
      public void testStarted(Description description) throws Exception {
        super.testStarted(description);
        if (Boolean.parseBoolean(System.getProperty("descriptiveMockitoRunner.active", "true"))) {
          LOG.info(description.getMethodName());
        }
      }
    });

    super.run(notifier);
  }
}

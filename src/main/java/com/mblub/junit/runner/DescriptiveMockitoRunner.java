package com.mblub.junit.runner;

import java.lang.reflect.InvocationTargetException;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.mockito.junit.MockitoJUnitRunner;

public class DescriptiveMockitoRunner extends MockitoJUnitRunner {

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
          System.out.println(description.getMethodName());
        }
      }
    });

    super.run(notifier);
  }
}

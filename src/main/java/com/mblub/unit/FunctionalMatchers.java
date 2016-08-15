package com.mblub.unit;

import java.util.function.Predicate;

import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class FunctionalMatchers {

  public static <T> T argThatMatches(Predicate<T> predicate) {
    LambdaMatcher<T> matcher = new LambdaMatcher<>(predicate);
    return Matchers.argThat(matcher);
  }

  public static class LambdaMatcher<T> implements ArgumentMatcher<T> {
    private final Predicate<T> predicate;

    public LambdaMatcher(Predicate<T> predicate) {
      this.predicate = predicate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object argument) {
      return predicate.test((T) argument);
    }
  }
}

package com.mblub.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;

public class Assertions {
  public static <T> void assertStreamContents(Stream<T> expectStream, Stream<T> actualStream) {
    assertStreamContents(null, expectStream, actualStream);
  }

  public static <T> void assertStreamContents(String context, Stream<T> expectStream, Stream<T> actualStream) {
    assertIteratorContents(context, expectStream.iterator(), actualStream.iterator());
  }

  public static <T> void assertIteratorContents(Iterator<T> expectIter, Iterator<T> actualIter) {
    assertIteratorContents(null, expectIter, actualIter);
  }

  private static String getContextPrefix(String context) {
    return StringUtils.isEmpty(context) ? "" : context + ": ";
  }

  public static <T> void assertIteratorContents(String context, Iterator<T> expectIter, Iterator<T> actualIter) {
    String contextPrefix = getContextPrefix(context);
    int itemIdx = 0;
    while (expectIter.hasNext() && actualIter.hasNext()) {
      assertEquals(contextPrefix + "item " + itemIdx + " differs", expectIter.next(), actualIter.next());
      itemIdx += 1;
    }
    assertFalse(contextPrefix + "actual has " + itemIdx + " entries, but expected more", expectIter.hasNext());
    assertFalse(contextPrefix + "expected " + itemIdx + " entries, but actual had more", actualIter.hasNext());
  }

  public static <T> void assertOptionalPresent(String context, Optional<T> candidate) {
    assertTrue(getContextPrefix(context) + "not present", candidate.isPresent());
  }

  public static <T> void assertOptionalSame(String context, T expectValue, Optional<T> actualOptional) {
    assertOptionalPresent(context, actualOptional);
    assertSame(context, expectValue, actualOptional.get());
  }

  public static <T> void assertOptionalSame(String context, Optional<T> expectOptional, Optional<T> actualOptional) {
    assertEquals(getContextPrefix(context) + "presence of optional", expectOptional.isPresent(),
            actualOptional.isPresent());
    if (expectOptional.isPresent()) {
      assertSame(context, expectOptional.get(), actualOptional.get());
    }
  }

  public static <T> void assertOptionalEquals(String context, T expectValue, Optional<T> actualOptional) {
    assertOptionalPresent(context, actualOptional);
    assertEquals(context, expectValue, actualOptional.get());
  }

  public static <T> void assertOptionalEquals(String context, Optional<T> expectOptional, Optional<T> actualOptional) {
    assertEquals(getContextPrefix(context) + "presence of optional", expectOptional.isPresent(),
            actualOptional.isPresent());
    if (expectOptional.isPresent()) {
      assertEquals(context, expectOptional.get(), actualOptional.get());
    }
  }

  public static <T> void assertOptionalThat(String context, Optional<T> candidate, Matcher<? super T> matcher) {
    assertOptionalPresent(context, candidate);
    assertThat(context, candidate.get(), matcher);
  }
}

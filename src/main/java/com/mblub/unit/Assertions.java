package com.mblub.unit;

import java.util.Iterator;
import java.util.stream.Stream;

import org.junit.Assert;

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

  public static <T> void assertIteratorContents(String context, Iterator<T> expectIter, Iterator<T> actualIter) {
    int itemIdx = 0;
    while (expectIter.hasNext() && actualIter.hasNext()) {
      Assert.assertEquals((context == null ? "" : context + ": ") + "item " + itemIdx + " differs", expectIter.next(),
              actualIter.next());
      itemIdx += 1;
    }
    Assert.assertFalse("actual has " + itemIdx + " entries, but expected more", expectIter.hasNext());
    Assert.assertFalse("expected " + itemIdx + " entries, but actual had more", actualIter.hasNext());
  }
}

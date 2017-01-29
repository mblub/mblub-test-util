package com.mblub.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.stream.Stream;

public class StreamTestingUtil {

    public static <T> void assertStreamsEqual(String messageHeader, Stream<T> expectStream, Stream<T> actualStream) {
        Iterator<T> expectIter = expectStream.iterator();
        Iterator<T> actualIter = actualStream.iterator();
        int idx = 0;
        while (expectIter.hasNext()) {
            assertTrue(messageHeader + ": actual stream has " + (idx + 1) + " elements, but expected stream has more",
                actualIter.hasNext());
            assertEquals(messageHeader + ": element idx " + idx, expectIter.next(), actualIter.next());
            idx += 1;
        }
        assertFalse(messageHeader + ": expected stream has " + (idx + 1) + " elements, but actual stream has more",
            actualIter.hasNext());
    }
}

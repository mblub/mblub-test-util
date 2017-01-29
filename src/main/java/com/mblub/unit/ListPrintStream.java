package com.mblub.unit;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListPrintStream extends PrintStream {
  private int flushCount;

  public ListPrintStream() {
    super(new ByteArrayOutputStream());
    flushCount = 0;
  }

  public List<String> getResultLines() {
    return getResultLineStream().collect(Collectors.toList());
  }

  public Stream<String> getResultLineStream() {
    ByteArrayOutputStream baos = (ByteArrayOutputStream) out;
    byte[] resultBytes = baos.toByteArray();
    BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(resultBytes)));
    return reader.lines();
  }
  
  public int getFlushCount() {
    return flushCount;
  }

  @Override
  public void flush() {
    flushCount += 1;
    super.flush();
  }
}
package com.mblub.unit;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListPrintWriter extends PrintWriter {

  public ListPrintWriter() {
    super(new StringWriter());
  }

  public List<String> getResultLines() {
    return getResultLineStream().collect(Collectors.toList());
  }

  public Stream<String> getResultLineStream() {
    flush();
    StringWriter sw = (StringWriter) out;
    String resultString = sw.toString();
    BufferedReader reader = new BufferedReader(new StringReader(resultString));
    return reader.lines();
  }
}
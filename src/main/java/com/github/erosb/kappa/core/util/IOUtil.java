package com.github.erosb.kappa.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UncheckedIOException;

/**
 * Utility class related to IO.
 */
public final class IOUtil {
  private static final int EOF = -1;
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

  private IOUtil() {
  }

  public static String toString(final InputStream input, final String charset)
    throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int length;

    while ((length = input.read(buffer)) != EOF) {
      result.write(buffer, 0, length);
    }

    return result.toString(charset);
  }

  public static String toString(Reader reader) {
    BufferedReader buff = new BufferedReader(reader);
    String line;
    StringBuilder sb = new StringBuilder();
    try {
      while ((line = buff.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return sb.toString();
  }
}

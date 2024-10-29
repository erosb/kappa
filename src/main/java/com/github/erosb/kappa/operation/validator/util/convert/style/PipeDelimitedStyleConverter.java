package com.github.erosb.kappa.operation.validator.util.convert.style;

public class PipeDelimitedStyleConverter extends DelimitedStyleConverter {
  private static final PipeDelimitedStyleConverter INSTANCE = new PipeDelimitedStyleConverter();

  private PipeDelimitedStyleConverter() {
    super("|");
  }

  public static PipeDelimitedStyleConverter instance() {
    return INSTANCE;
  }
}

package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.github.erosb.jsonsKema.SourceLocation;
import com.github.erosb.jsonsKema.UnknownSource;

public class PipeDelimitedStyleConverter extends DelimitedStyleConverter {
  private static final PipeDelimitedStyleConverter INSTANCE = new PipeDelimitedStyleConverter(UnknownSource.INSTANCE);

  private PipeDelimitedStyleConverter(SourceLocation location) {
    super(location, "|");
  }

  public static PipeDelimitedStyleConverter instance() {
    return INSTANCE;
  }
}

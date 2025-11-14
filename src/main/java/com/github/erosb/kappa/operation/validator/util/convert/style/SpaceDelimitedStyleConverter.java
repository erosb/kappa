package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.github.erosb.jsonsKema.SourceLocation;
import com.github.erosb.jsonsKema.UnknownSource;

public class SpaceDelimitedStyleConverter extends DelimitedStyleConverter {
  private static final SpaceDelimitedStyleConverter INSTANCE = new SpaceDelimitedStyleConverter(UnknownSource.INSTANCE);

  private SpaceDelimitedStyleConverter(SourceLocation sourceLocation) {
    super(sourceLocation, " ");
  }

  public static SpaceDelimitedStyleConverter instance() {
    return INSTANCE;
  }
}

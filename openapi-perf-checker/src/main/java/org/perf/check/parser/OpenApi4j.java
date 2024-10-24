package org.perf.check.parser;

import com.github.erosb.kappa.core.exception.ResolutionException;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.parser.OpenApi3Parser;

class OpenApi4j implements PerfParser {
  @Override
  public String load(String schemaFile) {
    // Check parsing with validation
    try {
      new OpenApi3Parser().parse(getClass().getClassLoader().getResource(schemaFile), false);
    } catch (ValidationException | ResolutionException e) {
      return e.toString();
    }

    return null;
  }

  @Override
  public String getVersion() {
    return OpenApi3Parser.class.getPackage().getImplementationVersion();
  }
}

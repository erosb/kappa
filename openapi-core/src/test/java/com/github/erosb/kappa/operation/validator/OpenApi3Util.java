package com.github.erosb.kappa.operation.validator;

import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import com.github.erosb.kappa.parser.OpenApi3Parser;

import java.net.URL;

public class OpenApi3Util {
  public static OpenApi3 loadApi(String path) throws Exception {
    URL specPath = OpenApi3Util.class.getResource(path);

    return new OpenApi3Parser().parse(specPath, false);
  }
}

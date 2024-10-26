package com.github.erosb.kappa.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.erosb.kappa.parser.model.AbsRefOpenApiSchema;
import com.github.erosb.kappa.parser.model.OpenApiSchema;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsExtendedRefOpenApiSchema<M extends OpenApiSchema<M>> extends AbsRefOpenApiSchema<M> {
  @JsonIgnore
  private Map<String, Object> extensions;

  // Extensions
  @JsonAnyGetter
  public Map<String, Object> getExtensions() {
    return extensions;
  }

  public void setExtensions(Map<String, Object> extensions) {
    this.extensions = extensions;
  }

  @JsonAnySetter
  public void setExtension(String name, Object value) {
    if (extensions == null) {
      extensions = new HashMap<>();
    }
    extensions.put(name, value);
  }
}

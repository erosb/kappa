package com.github.erosb.kappa.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.parser.model.AbsRefOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedReturnValue")
public class Callback extends AbsRefOpenApiSchema<Callback> {
  @JsonIgnore
  private Map<String, Path> callbackPaths;
  @JsonIgnore
  private Map<String, Object> extensions;

  // CallbackPath
  public Map<String, Path> getCallbackPaths() {
    return callbackPaths;
  }

  public Callback setCallbackPaths(Map<String, Path> callbackPaths) {
    this.callbackPaths = callbackPaths;
    return this;
  }

  public boolean hasCallbackPath(String expression) {
    return mapHas(callbackPaths, expression);
  }

  public Path getCallbackPath(String expression) {
    return mapGet(callbackPaths, expression);
  }

  public Callback setCallbackPath(String expression, Path callbackPath) {
    if (callbackPaths == null) {
      callbackPaths = new HashMap<>();
    }
    callbackPaths.put(expression, callbackPath);
    return this;
  }

  public Callback removeCallbackPath(String expression) {
    mapRemove(callbackPaths, expression);
    return this;
  }

  // Extensions
  public Map<String, Object> getExtensions() {
    return extensions;
  }

  public void setExtensions(Map<String, Object> extensions) {
    this.extensions = extensions;
  }

  public void setExtension(String name, Object value) {
    if (extensions == null) {
      extensions = new HashMap<>();
    }
    extensions.put(name, value);
  }

  /**
   * Don't use this!  Only for internal serialization usage !
   *
   * @return paths and/or extensions
   */
  @JsonAnyGetter
  @SuppressWarnings("unused")
  private Map<String, Object> any() {
    if (callbackPaths != null && extensions != null) {
      extensions.putAll(callbackPaths);
      return extensions;
    }

    if (callbackPaths != null) {
      return new HashMap<>(callbackPaths);
    }

    return extensions;
  }

  /**
   * Don't use this!  Only for internal deserialization usage !
   *
   * @param name  the key
   * @param value the value : path or extension
   */
  @JsonAnySetter
  @SuppressWarnings("unused")
  private void add(String name, Object value) {
    if (value == null) return;

    try {
      Path path = TreeUtil.json.convertValue(value, Path.class);
      setCallbackPath(name, path);
    } catch (IllegalArgumentException ex) {
      setExtension(name, value);
    }
  }

  @Override
  public Callback copy() {
    Callback copy = new Callback();

    if (isRef()) {
      copy.setRef(getRef());
      copy.setCanonicalRef(getCanonicalRef());
    } else {
      copy.setCallbackPaths(copyMap(getCallbackPaths()));
      copy.setExtensions(copySimpleMap(getExtensions()));
    }

    return copy;
  }
}

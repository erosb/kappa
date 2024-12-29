package com.github.erosb.kappa.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.reference.Reference;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;

import java.net.URL;

/**
 * Base class for Open API schema which can be represented as reference.
 */
public abstract class AbsRefOpenApiSchema<M extends OpenApiSchema<M>> extends AbsOpenApiSchema<M> {
  @JsonProperty(OAI3SchemaKeywords.$REF)
  private String ref;
  @JsonProperty(value = Reference.ABS_REF_FIELD)
  @JsonView(Views.Internal.class)
  private String canonicalRef;

  // $ref
  public String getRef() {
    return ref;
  }

  public boolean isRef() {
    return ref != null;
  }

  protected void setRef(String ref) {
    this.ref = ref;
  }

  protected void setCanonicalRef(String canonicalRef) {
    this.canonicalRef = canonicalRef;
  }

  public String getCanonicalRef() {
    return canonicalRef;
  }

  public Reference getReference(OAIContext context) {
    System.out.println("getRef: " + (canonicalRef != null ? canonicalRef : ref));
    return context.getReferenceRegistry().getRef(canonicalRef != null ? canonicalRef : ref);
  }

  public Reference setReference(OAIContext context, URL url, String ref) {
    Reference reference = context.getReferenceRegistry().addRef(url, ref);
    setRef(reference.getRef());
    setCanonicalRef(reference.getCanonicalRef());

    return reference;
  }
}

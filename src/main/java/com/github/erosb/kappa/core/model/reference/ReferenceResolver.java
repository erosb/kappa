package com.github.erosb.kappa.core.model.reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.jsonsKema.JsonObject;
import com.github.erosb.kappa.core.model.AuthOption;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The default JSON reference resolver.
 */
public class ReferenceResolver extends AbstractReferenceResolver {
  public ReferenceResolver(URL baseUrl, List<AuthOption> authOptions, JsonNode apiNode, String refKeyword, ReferenceRegistry referenceRegistry) {
    super(baseUrl, authOptions, apiNode, refKeyword, referenceRegistry);
  }

  private Collection<JsonNode> getReferencePaths(JsonNode document, Set<JsonNode> foundRefs) {
    if (document.isObject()) {
      JsonNode ref = document.get("$ref");
      if (ref != null) {
        foundRefs.add(ref);
      }
      document.properties().forEach(entry -> {
        if (entry.getKey().equals("schema")) {
          return;
        }
        getReferencePaths(entry.getValue(), foundRefs);
      });

    } else if (document.isArray()) {
      document.forEach(child -> getReferencePaths(child, foundRefs));
    }
    return foundRefs;
  }

  @Override
  protected Collection<JsonNode> getReferencePaths(JsonNode document) {
    return getReferencePaths(document, new HashSet<>());
  }
}

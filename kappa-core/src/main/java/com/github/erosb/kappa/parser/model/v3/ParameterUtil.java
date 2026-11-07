package com.github.erosb.kappa.parser.model.v3;

import com.github.erosb.kappa.core.exception.DecodeException;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.reference.Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class ParameterUtil {
  private ParameterUtil() {
  }

  static List<Parameter> getIn(OAIContext context, Collection<Parameter> parameters, String in) {
    List<Parameter> inParameters = new ArrayList<>();

    if (parameters != null) {
      for (Parameter param : parameters) {
        if (param.isRef()) {
          Reference ref = context.getReferenceRegistry().getRef(param.getCanonicalRef());
          try {
            param = ref.getMappedContent(Parameter.class);
          } catch (DecodeException e) {
            // Will never happen
          }
        }

        if (param != null && in.equalsIgnoreCase(param.getIn())) {
          inParameters.add(param);
        }
      }
    }

    return inParameters;
  }
}

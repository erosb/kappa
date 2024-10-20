package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.OpenApiValidationFailure;
import org.openapi4j.parser.model.OpenApiSchema;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.Schema;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.SkemaBackedJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

class ParameterValidator<M extends OpenApiSchema<M>> {

  private final ValidationContext<OAI3> context;
  private final Map<String, JsonValidator> specValidators;
  private final Map<String, AbsParameter<M>> specParameters;

  ParameterValidator(ValidationContext<OAI3> context, Map<String, AbsParameter<M>> specParameters) {
    this.context = context;
    this.specParameters = specParameters;
    specValidators = initValidators(specParameters);
  }

  Map<String, AbsParameter<M>> getParameters() {
    return specParameters;
  }

  void validate(final Map<String, JsonNode> values,
                final ValidationData<?> validation) {

    if (specValidators == null) {
      return;
    }

    for (Map.Entry<String, JsonValidator> entry : specValidators.entrySet()) {
      String paramName = entry.getKey();

      if (checkRequired(paramName, specParameters.get(paramName), values, validation)) {
        JsonNode paramValue = values.get(paramName);
        entry.getValue().validate(paramValue, validation);
      }
    }
  }

  private Map<String, JsonValidator> initValidators(Map<String, AbsParameter<M>> specParameters) {
    if (specParameters == null || specParameters.isEmpty()) {
      return null;
    }

    Map<String, JsonValidator> validators = new HashMap<>();

    for (Map.Entry<String, AbsParameter<M>> paramEntry : specParameters.entrySet()) {
      String paramName = paramEntry.getKey();
      AbsParameter<M> parameter = paramEntry.getValue();
      Schema paramSchema = null;

      if (parameter.getContentMediaTypes() != null) {
        for (Map.Entry<String, MediaType> entry : parameter.getContentMediaTypes().entrySet()) {
          MediaType mediaType = entry.getValue();
          paramSchema = mediaType.getSchema();
          break;
        }
      } else {
        paramSchema = parameter.getSchema();
      }

      if (paramSchema != null) {
        try {
          JsonValidator v = new SkemaBackedJsonValidator(
            TreeUtil.json.convertValue(paramSchema.copy(), JsonNode.class), context.getContext().getBaseUrl().toURI());
          validators.put(paramName, v);
        } catch (URISyntaxException e) {
          throw new RuntimeException(e);
        }
      }
    }

    return validators.isEmpty() ? null :validators;
  }

  private boolean checkRequired(final String paramName,
                                final AbsParameter<?> parameter,
                                final Map<String, JsonNode> paramValues,
                                final ValidationData<?> validation) {

    if (!paramValues.containsKey(paramName)) {
      if (parameter.isRequired()) {
        validation.add(OpenApiValidationFailure.missingRequiredParameter(paramName));
      }
      return false;
    }

    return true;
  }
}

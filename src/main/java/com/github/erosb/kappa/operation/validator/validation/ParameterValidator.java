package com.github.erosb.kappa.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.PrimitiveValidationStrategy;
import com.github.erosb.jsonsKema.ValidatorConfig;
import com.github.erosb.kappa.core.model.v3.OAI3;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.OperationContextUriFactory;
import com.github.erosb.kappa.core.validation.URIFactory;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;
import com.github.erosb.kappa.parser.model.v3.MediaType;
import com.github.erosb.kappa.parser.model.v3.Schema;
import com.github.erosb.kappa.schema.validator.JsonValidator;
import com.github.erosb.kappa.schema.validator.SKemaBackedJsonValidator;
import com.github.erosb.kappa.schema.validator.ValidationContext;
import com.github.erosb.kappa.schema.validator.ValidationData;
import com.github.erosb.kappa.parser.model.OpenApiSchema;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class ParameterValidator<M extends OpenApiSchema<M>> {

  private final ValidationContext<OAI3> context;
  private final Map<String, SKemaBackedJsonValidator> specValidators;
  private final Map<String, AbsParameter<M>> specParameters;
  private final OperationContextUriFactory uriFactory;

  ParameterValidator(ValidationContext<OAI3> context, Map<String, AbsParameter<M>> specParameters) {
    this.context = context;
    this.specParameters = specParameters;
    this.uriFactory = context.requestScopedUriFactory();
    specValidators = initValidators(specParameters);
  }

  Map<String, AbsParameter<M>> getParameters() {
    return specParameters;
  }

  void validate(final Map<String, IJsonValue> values,
                final ValidationData<?> validation) {

    if (specValidators == null) {
      return;
    }

    for (Map.Entry<String, SKemaBackedJsonValidator> entry : specValidators.entrySet()) {
      String paramName = entry.getKey();

      if (checkRequired(paramName, specParameters.get(paramName), values, validation)) {
        IJsonValue paramValue = values.get(paramName);
        SKemaBackedJsonValidator validator = entry.getValue();
        validator.validate(paramValue,
          // uriFactory.pathParam(paramName),
          validation,
          ValidatorConfig.builder().primitiveValidationStrategy(PrimitiveValidationStrategy.LENIENT).build());
      }
    }
  }

  private Map<String, SKemaBackedJsonValidator> initValidators(Map<String, AbsParameter<M>> specParameters) {
    if (specParameters == null || specParameters.isEmpty()) {
      return null;
    }

    Map<String, SKemaBackedJsonValidator> validators = new HashMap<>();

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
        URI pathParamDefinitionURI = uriFactory.pathParamDefinition(paramName);
        SKemaBackedJsonValidator v = new SKemaBackedJsonValidator(paramSchema.copy(), context, pathParamDefinitionURI);
        validators.put(paramName, v);
      }
    }

    return validators.isEmpty() ? null : validators;
  }

  private boolean checkRequired(final String paramName,
                                final AbsParameter<?> parameter,
                                final Map<String, IJsonValue> paramValues,
                                final ValidationData<?> validation) {

    if (!paramValues.containsKey(paramName)) {
      if (parameter.isRequired()) {
        validation.add(
          OpenApiValidationFailure.missingRequiredParameter(paramName, context.requestScopedUriFactory().definitionPath()));
      }
      return false;
    }

    return true;
  }
}

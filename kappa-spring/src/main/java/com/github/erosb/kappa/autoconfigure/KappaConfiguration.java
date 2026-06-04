package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.jsonsKema.ReadWriteContext;
import com.github.erosb.jsonsKema.ValidatorConfig;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiBasedRequestValidationFilter;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.ValidationFailureSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class KappaConfiguration {

  @Autowired(required = false)
  KappaSpringConfiguration configuration;

  private ValidatorConfig.Companion.ValidatorConfigBuilder requestBodyConfigBuilder() {
    return ValidatorConfig.builder().readWriteContext(ReadWriteContext.WRITE);
  }

  @Bean
  public FilterRegistrationBean<OpenApiBasedRequestValidationFilter> openApiBasedRequestValidationFilter() {
    if (configuration == null) {
      configuration = new KappaSpringConfiguration();
    }
    ValidatorConfig.Companion.ValidatorConfigBuilder reqBodyValidatorConfigBuilder = requestBodyConfigBuilder();
    configuration.getRequestBodyValidatorConfigCustomizer().accept(reqBodyValidatorConfigBuilder);
    OpenApiBasedRequestValidationFilter filter = OpenApiBasedRequestValidationFilter.forApiLookup(
      new PathPatternMatchingOpenApiLookup(configuration),
      configuration.getValidationFailureSender(),
      reqBodyValidatorConfigBuilder.build()
    );
    FilterRegistrationBean<OpenApiBasedRequestValidationFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.setOrder(2);
    registration.addUrlPatterns("/*");
    return registration;
  }

}

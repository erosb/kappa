package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiBasedRequestValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class KappaConfiguration {

  @Autowired(required = false)
  KappaSpringConfiguration configuration;

  @Bean
  public FilterRegistrationBean<OpenApiBasedRequestValidationFilter> openApiBasedRequestValidationFilter() {
    if (configuration == null) {
      configuration = new KappaSpringConfiguration();
    }
    OpenApiBasedRequestValidationFilter filter = OpenApiBasedRequestValidationFilter.forApiLookup(
      new PathPatternMatchingOpenApiLookup(configuration));
    FilterRegistrationBean<OpenApiBasedRequestValidationFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.setOrder(2);
    registration.addUrlPatterns("/*");
    return registration;
  }
}

package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiBasedRequestValidationFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class KappaConfiguration {
  @Bean
  public FilterRegistrationBean<OpenApiBasedRequestValidationFilter> openApiBasedRequestValidationFilter(
    KappaSpringConfiguration kappaConfig
  ) {
    OpenApiBasedRequestValidationFilter filter = OpenApiBasedRequestValidationFilter.forApiLookup(
      new SpringOpenApiLookup(kappaConfig));
    FilterRegistrationBean<OpenApiBasedRequestValidationFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.setOrder(2);
    registration.addUrlPatterns("/*");
    return registration;
  }
}

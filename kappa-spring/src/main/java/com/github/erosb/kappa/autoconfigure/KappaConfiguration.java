package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiBasedRequestValidationFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KappaConfiguration {

  @Bean
  @ConfigurationProperties(prefix = "kappa")
  public KappaSpringConfiguration configuration() {
    return new KappaSpringConfiguration();
  }

  @Bean
  public FilterRegistrationBean<OpenApiBasedRequestValidationFilter> openApiBasedRequestValidationFilter() {
    OpenApiBasedRequestValidationFilter filter = OpenApiBasedRequestValidationFilter.forApiLookup(
      new SpringOpenApiLookup(configuration())
    );
    FilterRegistrationBean<OpenApiBasedRequestValidationFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.setOrder(2);
    registration.addUrlPatterns("/users");
    return registration;
  }
}

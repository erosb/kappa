package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiBasedRequestValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@AutoConfiguration()
@Configuration(proxyBeanMethods = false)
//@Import(DefaultKappaSpringConfiguration.class)
public class KappaConfiguration {

  @Autowired(required = false)
  KappaSpringConfiguration configuration;
  //
  //  @Bean
  //  @ConditionalOnMissingBean
  //  public KappaSpringConfiguration configuration() {
  //    return new KappaSpringConfiguration();
  //  }

  @Bean
  public FilterRegistrationBean<OpenApiBasedRequestValidationFilter> openApiBasedRequestValidationFilter() {
    if (configuration == null) {
      configuration = new KappaSpringConfiguration();
    }
    OpenApiBasedRequestValidationFilter filter = OpenApiBasedRequestValidationFilter.forApiLookup(
      new SpringOpenApiLookup(configuration));
    FilterRegistrationBean<OpenApiBasedRequestValidationFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.setOrder(2);
    registration.addUrlPatterns("/*");
    return registration;
  }
}

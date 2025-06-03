package com.github.erosb.kappa.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class KappaContractTestingConfiguration {

  @Autowired(required = false)
  KappaSpringConfiguration configuration;

  @Bean
  public FilterRegistrationBean<KappaContractTestingFilter> kappaContractTestingFilter() {
    if (configuration == null) {
      configuration = new KappaSpringConfiguration();
    }
    KappaContractTestingFilter filter = KappaContractTestingFilter.forApiLookup(
      new PathPatternMatchingOpenApiLookup(configuration));
    FilterRegistrationBean<KappaContractTestingFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.setOrder(2);
    registration.addUrlPatterns("/*");
    return registration;
  }
}

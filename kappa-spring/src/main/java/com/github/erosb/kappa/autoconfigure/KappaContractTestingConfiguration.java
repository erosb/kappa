package com.github.erosb.kappa.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

@AutoConfiguration
public class KappaContractTestingConfiguration implements ImportBeanDefinitionRegistrar {

  @Autowired(required = false)
  KappaSpringConfiguration configuration;

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(
      importingClassMetadata.getAnnotationAttributes(
        EnableKappaContractTesting.class.getName(), false
      )
    );

    registry.registerBeanDefinition("", kappaContractTestingFilter());
    System.out.println(attributes);
  }

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

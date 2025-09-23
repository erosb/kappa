package com.github.erosb.kappa.autoconfigure;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

@AutoConfiguration()
public class KappaContractTestingConfiguration
  implements ImportBeanDefinitionRegistrar {

  @Autowired(required = false)
  KappaSpringConfiguration configuration;

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(
      importingClassMetadata.getAnnotationAttributes(
        EnableKappaContractTesting.class.getName(), false
      )
    );
    for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
      BeanDefinition def = registry.getBeanDefinition(beanDefinitionName);
      //      def.getBeanClassName()
    }
    ConstructorArgumentValues cav = new ConstructorArgumentValues();
    MutablePropertyValues pvs = new MutablePropertyValues();
//    pvs.
    new RootBeanDefinition(FilterRegistrationBean.class, cav, pvs);

    registry.registerBeanDefinition("kappaContractTestingFilter",
      new RootBeanDefinition(FilterRegistrationBean.class, this::kappaContractTestingFilter));
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

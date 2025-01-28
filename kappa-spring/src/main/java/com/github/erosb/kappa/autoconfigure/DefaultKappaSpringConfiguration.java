package com.github.erosb.kappa.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
//@Configuration(proxyBeanMethods = true)
@ConditionalOnMissingBean(KappaSpringConfiguration.class)
public class DefaultKappaSpringConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public KappaSpringConfiguration configuration() {
    return new KappaSpringConfiguration();
  }

}

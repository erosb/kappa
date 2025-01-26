package com.github.erosb.kappa.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class DefaultKappaSpringConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public KappaSpringConfiguration configuration() {
    return new KappaSpringConfiguration();
  }

}

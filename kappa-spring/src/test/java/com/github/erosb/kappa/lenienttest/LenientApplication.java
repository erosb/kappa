package com.github.erosb.kappa.lenienttest;

import com.github.erosb.jsonsKema.PrimitiveValidationStrategy;
import com.github.erosb.jsonsKema.ValidatorConfig;
import com.github.erosb.kappa.autoconfigure.EnableKappaRequestValidation;
import com.github.erosb.kappa.autoconfigure.KappaSpringConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;

@SpringBootApplication
@EnableKappaRequestValidation
public class LenientApplication {


  public static void main(String[] args) {
    SpringApplication.run(LenientApplication.class);
  }

  @Bean
  public KappaSpringConfiguration kappaSpringConfiguration() {
    KappaSpringConfiguration kappaConfig = new KappaSpringConfiguration();
    var pathPatternToOpenapiDescription = new LinkedHashMap<String, String>();
    pathPatternToOpenapiDescription.put("/**", "/openapi/users-api.yaml");
    kappaConfig.setOpenapiDescriptions(pathPatternToOpenapiDescription);
    kappaConfig.setRequestBodyValidatorConfig(ValidatorConfig.builder()
      .primitiveValidationStrategy(PrimitiveValidationStrategy.LENIENT)
      .build()
    );
    return kappaConfig;
  }
}

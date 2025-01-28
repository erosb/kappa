package com.github.erosb.kappa.samplecontroller;

import com.github.erosb.kappa.autoconfigure.EnableKappaRequestValidation;
import com.github.erosb.kappa.autoconfigure.KappaSpringConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;

@SpringBootApplication
@EnableKappaRequestValidation
public class UsersApplication {

  public static void main(String[] args) {
    SpringApplication.run(UsersApplication.class);
  }

  //@Bean
  public KappaSpringConfiguration kappaSpringConfiguration() {
    KappaSpringConfiguration kappaConfig = new KappaSpringConfiguration();
    var pathPatternToOpenapiDescription = new LinkedHashMap<String, String>();
    pathPatternToOpenapiDescription.put("/users", "/openapi/users-api.yaml");
    pathPatternToOpenapiDescription.put("/customers/*/address", "/openapi/customers-api.yaml");
    kappaConfig.setOpenapiDescriptions(pathPatternToOpenapiDescription);
    return kappaConfig;
  }

}

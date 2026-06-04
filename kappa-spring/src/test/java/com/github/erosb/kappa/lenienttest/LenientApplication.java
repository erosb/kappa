package com.github.erosb.kappa.lenienttest;

import com.github.erosb.jsonsKema.PrimitiveValidationStrategy;
import com.github.erosb.jsonsKema.ValidatorConfig;
import com.github.erosb.kappa.autoconfigure.EnableKappaRequestValidation;
import com.github.erosb.kappa.autoconfigure.KappaSpringConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

class CreateUserRequest {
  private String name;
  private String email;
  private Integer age;
}

@RestController
@RequestMapping("/users")
class UsersController {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  void createUser(@RequestBody CreateUserRequest request) {
    System.out.println("received request: " + request);
  }
}

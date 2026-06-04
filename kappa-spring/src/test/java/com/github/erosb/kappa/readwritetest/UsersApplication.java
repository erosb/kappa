package com.github.erosb.kappa.readwritetest;

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
public class UsersApplication {

  public static void main(String[] args) {
    SpringApplication.run(UsersApplication.class, args);
  }


  @Bean
  public KappaSpringConfiguration kappaSpringConfiguration() {
    KappaSpringConfiguration kappaConfig = new KappaSpringConfiguration();
    var pathPatternToOpenapiDescription = new LinkedHashMap<String, String>();
    pathPatternToOpenapiDescription.put("/users/**", "/openapi/readwrite-users-api.yaml");
    kappaConfig.setOpenapiDescriptions(pathPatternToOpenapiDescription);
    kappaConfig.setIgnoredPathPatterns("/health", "/swagger-ui**", "/upload");
    return kappaConfig;
  }
}

class CreateUserRequest {
  String name;
  String password;
}


@RestController
@RequestMapping("/users")
class UserController {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  void createUser(@RequestBody CreateUserRequest request) {

  }
}

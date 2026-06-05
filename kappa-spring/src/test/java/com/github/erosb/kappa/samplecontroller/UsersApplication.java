package com.github.erosb.kappa.samplecontroller;

import com.github.erosb.kappa.autoconfigure.EnableKappaRequestValidation;
import com.github.erosb.kappa.autoconfigure.KappaSpringConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@EnableKappaRequestValidation
public class UsersApplication {

  public static void main(String[] args) {
    SpringApplication.run(UsersApplication.class);
  }

  @Bean
  public KappaSpringConfiguration kappaSpringConfiguration() {
    KappaSpringConfiguration kappaConfig = new KappaSpringConfiguration();
    var pathPatternToOpenapiDescription = new LinkedHashMap<String, String>();
    pathPatternToOpenapiDescription.put("/users/**", "/openapi/users-api.yaml");
    pathPatternToOpenapiDescription.put("/customers/**", "/openapi/customers-api.yaml");
    kappaConfig.setOpenapiDescriptions(pathPatternToOpenapiDescription);
    kappaConfig.setIgnoredPathPatterns("/health", "/swagger-ui**", "/upload");
    return kappaConfig;
  }

}


@RestController
class CustomersController {

  @PutMapping("/customers/{id}/address")
  void put(@PathVariable("id") int id) {

  }
}


@RestController
@RequestMapping("/upload")
class UploadController {

  @PostMapping()
  public ResponseEntity<String> uploadFile(@RequestParam("myfile") MultipartFile file) throws IOException {
    System.out.println("uploadFile: " + new String(file.getInputStream().readAllBytes()));
    return new ResponseEntity<>(HttpStatus.OK);
  }

}

@RestController
@RequestMapping("/users")
class UsersController {

  public UsersController() {
    System.out.println("create controller");
  }

  @PostMapping
  public ResponseEntity<String> createUser(@RequestBody Map<String, String> body) {
    System.out.println("received POST " + body);
    return ResponseEntity.status(HttpStatus.OK)
      .contentType(MediaType.APPLICATION_JSON)
      .body("true")
      ;
  }

  @GetMapping("/me")
  public String getMe() {
    return "Me here!";
  }

  @GetMapping(value = "/feed", produces = "application/xml")
  public String getFeed() {
    return "<feed></feed>";
  }

  @GetMapping(value = "/feed-wildcard", produces = "application/json")
  public String getFeedCorrect() {
    return "[]";
  }

  @GetMapping("/error")
  public String getUsers() {
    throw new IllegalStateException("TODO");
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handle(IllegalStateException e) {

  }
}

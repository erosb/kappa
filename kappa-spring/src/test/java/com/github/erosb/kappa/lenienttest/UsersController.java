package com.github.erosb.kappa.lenienttest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

class CreateUserRequest {
  private String name;
  private String email;
  private Integer age;
}

@RestController
@RequestMapping("/users")
public class UsersController {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  void createUser(@RequestBody CreateUserRequest request) {

  }
}

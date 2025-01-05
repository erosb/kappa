package com.github.erosb.kappa.samplecontroller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

  @PostMapping("/users")
  public void createUser(@RequestBody Object body) {

  }
}

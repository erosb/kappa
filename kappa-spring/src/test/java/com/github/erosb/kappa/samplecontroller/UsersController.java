package com.github.erosb.kappa.samplecontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {

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

  @GetMapping("/error")
  public String getUsers() {
    throw new IllegalStateException("TODO");
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handle(IllegalStateException e) {

  }
}

package com.github.erosb.kappa.samplecontroller;

import com.github.erosb.kappa.autoconfigure.EnableKappaRequestValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKappaRequestValidation
public class UsersApplication {

  public static void main(String[] args) {
    SpringApplication.run(UsersApplication.class);
  }
}

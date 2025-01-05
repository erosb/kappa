package com.github.erosb.kappa.samplecontroller;

import com.github.erosb.kappa.autoconfigure.EnableKappa;
import com.github.erosb.kappa.autoconfigure.KappaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableKappa
@Import(KappaConfiguration.class)
public class UsersApplication {

  public static void main(String[] args) {
    SpringApplication.run(UsersApplication.class);
  }
}

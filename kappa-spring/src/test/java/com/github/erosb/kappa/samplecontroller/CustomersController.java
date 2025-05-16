package com.github.erosb.kappa.samplecontroller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomersController {

  @PutMapping("/customers/{id}/address")
  void put(@PathVariable("id") int id) {

  }
}

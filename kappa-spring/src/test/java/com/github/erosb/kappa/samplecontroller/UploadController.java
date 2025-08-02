package com.github.erosb.kappa.samplecontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

@RestController
@RequestMapping("/upload")
public class UploadController {

  @PostMapping()
  public ResponseEntity<String> uploadFile(@RequestParam("myfile") MultipartFile file) {
    System.out.println("uploadFile: " + new String(file.getInputStream().readAllBytes()));
    return new ResponseEntity<>(HttpStatus.OK);
  }

}

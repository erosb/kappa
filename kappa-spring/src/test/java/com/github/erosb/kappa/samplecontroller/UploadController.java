package com.github.erosb.kappa.samplecontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {


  @PostMapping()
  public ResponseEntity<String> uploadFile(@RequestParam("myfile") MultipartFile file) throws Exception {
    System.out.println("uploadFile: " + new String(file.getInputStream().readAllBytes()));
    return file.isEmpty() ?
      new ResponseEntity<String>(HttpStatus.NOT_FOUND) : new ResponseEntity<String>(HttpStatus.OK);
  }

}

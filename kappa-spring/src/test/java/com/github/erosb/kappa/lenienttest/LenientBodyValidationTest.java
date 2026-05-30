package com.github.erosb.kappa.lenienttest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LenientBodyValidationTest {

  @Autowired
  MockMvc mvc;

  @Test
  public void lenientModeWorks() throws Exception {
    mvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
      // age should not be null as per the schema, but in lenient mode it is accepted
        // since it is an optional property
        .content("""
          {
            "name": "John Doe",
            "email": "johndoe@example.org",
            "age": null
          }
    """))
      .andExpect(status().isCreated());
  }
}

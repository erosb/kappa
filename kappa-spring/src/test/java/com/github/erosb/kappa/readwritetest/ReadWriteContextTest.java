package com.github.erosb.kappa.readwritetest;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReadWriteContextTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void readWriteContextWorks() throws Exception {
    mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
      .content("""
      {
        "id": 1,
        "name": "John Doe",
        "password": "verySuperS3cr3t"
      }
      """))
      .andExpect(status().isBadRequest())
      .andExpect(content().json("""
        {
          "errors" : [ {
            "dataLocation" : "$request.body#/id (line 2, position 9)",
            "schemaLocation" : "openapi/readwrite-users-api.yaml#/components/schemas/User/properties/id/readOnly",
            "dynamicPath" : "#/$ref/properties/id/readOnly",
            "message" : "read-only property \\"id\\" should not be present in write context"
          } ]
        }
"""));
  }
}

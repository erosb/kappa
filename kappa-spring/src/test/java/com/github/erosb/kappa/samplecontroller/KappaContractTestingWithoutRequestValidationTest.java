package com.github.erosb.kappa.samplecontroller;

import com.github.erosb.kappa.autoconfigure.EnableKappaContractTesting;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableKappaContractTesting(validateRequests = false)
public class KappaContractTestingWithoutRequestValidationTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  public void contractFailureInRequest() throws Exception {
      mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("""
        {
          "id": 1,
          "name": null,
          "Email": "johndoe@example.org"
        }
        """))
        .andDo(print());

//    System.out.println(exc);
//    SoftAssertions.assertSoftly(a -> {
//      a.assertThat(exc.getMessage()).contains("instance location: $request.body#/id (line 2");
//      a.assertThat(exc.getMessage()).contains("schemas.json#/$defs/CreateUser/additionalProperties");
//      a.assertThat(exc.getMessage()).containsPattern(
//        "evaluated on dynamic path: .*/users-api.yaml#/\\$ref/\\$ref/additionalProperties/false");
//    });
  }

}

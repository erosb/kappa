package com.github.erosb.kappa.samplecontroller;

import com.github.erosb.kappa.autoconfigure.EnableKappaContractTesting;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableKappaContractTesting
public class KappaContractTestingTest {

  @Autowired
  UsersController usersController;

  @Autowired
  MockMvc mockMvc;

  @Test
  public void contractFailureInRequest()
    throws Exception {
    Throwable exc = assertThrows(AssertionError.class, () ->
      mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("""
          {
            "id": 1,
            "name": null,
            "Email": "johndoe@example.org"
          }
          """))
        .andDo(print())
    );

    System.out.println(exc);
    SoftAssertions.assertSoftly(a -> {
      a.assertThat(exc.getMessage()).contains("instance location: $request.body#/id (line 2");
      a.assertThat(exc.getMessage()).contains("schemas.json#/$defs/CreateUser/additionalProperties");
      a.assertThat(exc.getMessage()).containsPattern(
        "evaluated on dynamic path: .*/users-api.yaml#/\\$ref/\\$ref/additionalProperties/false");
    });//
  }

  @Test
  public void wrongMethodFailureInRequest()
    throws Exception {
    Throwable exc = assertThrows(AssertionError.class, () ->
      mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON).content("""
          {
            "id": 1,
            "name": "John Doe",
            "Email": "johndoe@example.org"
          }
          """))
        .andDo(print())
    );
    assertThat(exc.getMessage().trim()).isEqualTo("Operation not found from URL 'http://localhost/users' with method 'GET'.");
  }

  @Test
  public void wrongPathFailureInRequest()
    throws Exception {
    Throwable exc = assertThrows(AssertionError.class, () ->
      mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).content("""
          {
            "id": 1,
            "name": "John Doe",
            "Email": "johndoe@example.org"
          }
          """))
        .andDo(print())
    );

    System.out.println(exc);
  }

  @Test
  public void wrongContentTypeFailureInRequest()
    throws Exception {
    Throwable exc = assertThrows(AssertionError.class, () ->
      mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_ATOM_XML).content("""
          {
            "id": 1,
            "name": "John Doe",
            "Email": "johndoe@example.org"
          }
          """))
        .andDo(print())
    );

    System.out.println(exc);
  }

}

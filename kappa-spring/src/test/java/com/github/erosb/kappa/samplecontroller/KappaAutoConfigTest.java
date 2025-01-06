package com.github.erosb.kappa.samplecontroller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class KappaAutoConfigTest {

  @Autowired UsersController usersController;

  @Autowired
  MockMvc mockMvc;

  @Test
  public void automaticRequestValidationHappens() throws Exception {
    assertNotNull(usersController);
    mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {}
          """)
      )
      .andDo(print())
    .andExpect(status().isBadRequest());
  }
}

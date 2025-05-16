package com.github.erosb.kappa.samplecontroller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class KappaAutoConfigTest {

  @Autowired
  UsersController usersController;

  @Autowired
  MockMvc mockMvc;

  @Test
  public void automaticRequestValidationHappens()
    throws Exception {
    assertNotNull(usersController);
    String actualResponse = mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {}
          """)
      )
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andReturn().getResponse().getContentAsString();
    JSONAssert.assertEquals("""
      {
        "errors" : [ {
          "dataLocation" : "$request.body (line 1, position 1)",
          "schemaLocation" : "openapi/users/schemas.json#/$defs/CreateUser/required",
          "dynamicPath" : "#/$ref/$ref/required",
          "message" : "required properties are missing: name, email"
        } ]
      }
      """, actualResponse, true);
  }

  @Test
  public void putCustomerAddressFailure()
    throws Exception {
    String actualResponse = mockMvc.perform(put("/customers/22/address")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"country": "EN-GB"}
          """)
      )
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andReturn().getResponse().getContentAsString();
    JSONAssert.assertEquals("""
      {
        "errors" : [ {
          "dataLocation" : "$request.body (line 1, position 1)",
          "schemaLocation" : "openapi/customers-api.yaml#/required",
          "dynamicPath" : "#/required",
          "message" : "required properties are missing: city, zipCode, defaultShippingAddress"
        }, {
          "dataLocation" : "$request.body#/country (line 1, position 13)",
          "schemaLocation" : "openapi/customers-api.yaml#/components/schemas/CountryCode/maxLength",
          "dynamicPath" : "#/properties/country/$ref/maxLength",
          "message" : "actual string length 5 exceeds maxLength 2"
        } ]
      }
      """, actualResponse, true);
  }
}

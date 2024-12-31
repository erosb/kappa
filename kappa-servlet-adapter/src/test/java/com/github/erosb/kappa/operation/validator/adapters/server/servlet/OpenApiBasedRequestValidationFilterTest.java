package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import com.github.erosb.kappa.core.exception.ResolutionException;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.parser.OpenApi3Parser;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiBasedRequestValidationFilter.forApiDescription;
import static com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiBasedRequestValidationFilter.forApiLookup;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenApiBasedRequestValidationFilterTest {

  OpenApi3 usersApi;

  {
    try {
      usersApi = new OpenApi3Parser().parse(getClass().getResource("/users-api.yaml"), false);
    } catch (ResolutionException | ValidationException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void bodySchemaFailure()
    throws IOException, JSONException {
    OpenApiBasedRequestValidationFilter filter = forApiDescription(usersApi);
    HttpServletResponse resp = mock(HttpServletResponse.class);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(out);
    when(resp.getWriter()).thenReturn(pw);
    filter.doFilter(MockHttpServletRequestBuilder.post().build(), resp, null);

    pw.flush();
    out.flush();

    String responseBody = new String(out.toByteArray());
    JSONAssert.assertEquals("{\n"
      + "  \"errors\" : [ {\n"
      + "    \"dataLocation\" : \"$request.body\",\n"
      + "    \"dynamicPath\" : \"#/$ref/required\",\n"
      + "    \"message\" : \"required properties are missing: name, email\"\n"
      + "  } ]\n"
      + "}", responseBody, false);
  }

  @Test
  public void multipleApiYamlLookup() throws Exception {
    OpenApiBasedRequestValidationFilter filter = forApiLookup(path -> usersApi);
    HttpServletResponse resp = mock(HttpServletResponse.class);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(out);
    when(resp.getWriter()).thenReturn(pw);
    filter.doFilter(MockHttpServletRequestBuilder.post().build(), resp, null);

    pw.flush();
    out.flush();

    String responseBody = new String(out.toByteArray());
    JSONAssert.assertEquals("{\n"
      + "  \"errors\" : [ {\n"
      + "    \"dataLocation\" : \"$request.body\",\n"
      + "    \"dynamicPath\" : \"#/$ref/required\",\n"
      + "    \"message\" : \"required properties are missing: name, email\"\n"
      + "  } ]\n"
      + "}", responseBody, false);
  }
}

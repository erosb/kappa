# Kappa - OpenAPI validator for Java and JVM projects

This project is a successor (permanent fork) of the [archived openapi4j](https://github.com/openapi4j/openapi4j) project.


Kappa can be used to validate HTTP requests and responses against OpenAPI 3.1 definitions.

Under the hood ist uses the [erosb/json-sKema](https://github.com/erosb/json-sKema) library for JSON Schema validation.

<!-- TOC -->
* [Kappa - OpenAPI validator for Java and JVM projects](#kappa---openapi-validator-for-java-and-jvm-projects)
  * [Validating incoming HTTP requests](#validating-incoming-http-requests)
    * [Installation - Maven](#installation---maven)
    * [Add a filter to validate the request](#add-a-filter-to-validate-the-request)
    * [Register your bean in Spring Context](#register-your-bean-in-spring-context)
  * [Supported versions](#supported-versions)
  * [Contributing](#contributing)
  * [License](#license)
  * [Contributor notes](#contributor-notes)
<!-- TOC -->

## Validating incoming HTTP requests

If you want to validate the HTTP requests received by a Spring Boot service, you can do it by implementing
a simple `Filter` and intercepting incoming requests against your OpenAPI descriptions.

### Installation - Maven

```xml
<dependency>
  <groupId>com.github.erosb</groupId>
  <artifactId>kappa-servlet-adapter</artifactId>
  <version>2.0.0-RC8</version>
</dependency>

```

### Add a filter to validate the request

The best way to implement OpenAPI-based input validation is doing it in a servlet filter.

```java

public class OpenApiBackedRequestValidationFilter implements Filter {
  // reading the OpenAPI description of our API
  private final OpenApi3 api = new OpenApi3Parser().parse(getClass().getResource("/openapi/pets-api.yaml"), false);

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException {
    HttpServletRequest httpReq = (HttpServletRequest) req;
    HttpServletResponse httpResp = (HttpServletResponse) resp;

    try {

      // we need to wrap the original request instance into a MemoizingServletRequest,
      // since we will need to parse the request body twice: once for the OpenAPI-validation
      // and once for the jackson parsing.
      // basic HttpServletRequests cannot be read twice, hence we use the
      // MemoizingServletRequest shipped with Kappa
      // more here: https://www.baeldung.com/spring-reading-httpservletrequest-multiple-times
      MemoizingServletRequest memoizedReq = new MemoizingServletRequest(httpReq);

      // Kappa can understand different representations of HTTP requests and responses
      // here we use the Servlet API specific adapter of Kappa, to get a Kappa Request instance
      // which wraps a HttpServletRequest
      JakartaServletRequest jakartaRequest = JakartaServletRequest.of(memoizedReq);

      // we do the validation
      new RequestValidator(api).validate(jakartaRequest);

      // if no request validation error was found, we proceed with the request execution
      chain.doFilter(memoizedReq, httpResp);

    } catch (ValidationException ex) {
      // if the request validation failed, we represents the validation failures in a simple
      // json response and send it back to the client
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode respObj = objectMapper.createObjectNode();
      ArrayNode itemsJson = objectMapper.createArrayNode();
      ex.results().forEach(item -> {
        ObjectNode itemJson = objectMapper.createObjectNode();
        itemJson.put("dataLocation", item.describeInstanceLocation());
        itemJson.put("schemaLocation", item.describeSchemaLocation());
        itemJson.put("message", item.message);
        itemsJson.add(itemJson);
      });
      respObj.put("errors", itemsJson);
      httpResp.setStatus(400);
      httpResp.getWriter().print(objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(respObj)
      );
    }
  }
}
```

### Register your bean in Spring Context

```java
@Bean
public FilterRegistrationBean<OpenApiBackedRequestValidationFilter> filterRegistration() {
  FilterRegistrationBean<OpenApiBackedRequestValidationFilter> registration = new FilterRegistrationBean<>();
  registration.setFilter(new OpenApiBackedRequestValidationFilter());
  registration.setOrder(2);
  registration.addUrlPatterns("/api/*");
  return registration;
}
```

## Supported versions

Kappa targets supporting OpenAPI 3.1. Currently it uses a draft2020-12 compliant validator for JSON Schema.
## Contributing

Reporting issues, making comments, ... Any help is welcome !

We accept Pull Requests via GitHub. There are some guidelines which will make applying PRs easier for us :

* Respect the code style and indentation. .editorconfig file is provided to not be worried about this.
* Create minimal diffs - disable on save actions like reformat source code or organize imports. If you feel the source code should be reformatted create a separate PR for this change.
* Provide JUnit tests for your changes and make sure your changes don't break anything by running `gradlew clean check`.
* Provide a self explanatory but brief commit message with issue reference if any, as it will be reported directly for release changelog.

## License

Kappa is released under the Apache 2.0 license. See [LICENSE](https://github.com/openapi4j/openapi4j/blob/master/LICENSE.md) for details.


## Contributor notes

Release to local maven repo: `./gradlew build publishToMavenLocal`

Release to maven central: `./gradlew build publishToSonatype closeAndReleaseSonatypeStagingRepository`

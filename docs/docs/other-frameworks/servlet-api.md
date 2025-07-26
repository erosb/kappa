# Using Kappa with the Servlet API

## Installation


=== "Maven"

    ```xml
    <dependency>
      <groupId>com.github.erosb</groupId>
      <artifactId>kappa-servlet-adapter</artifactId>
      <version>2.0.0-RC16</version>
    </dependency>
    ```

=== "Gradle"

    ```kotlin
      testImplementation("com.github.erosb:kappa-servlet-adapter:2.0.0-RC16")
    ```

## Usage

Kappa's Servlet adapter can be used to programmatically validate HTTP requests and responses
in any environment which uses the `jakarta.servlet` API.

Examples:
```java
// openAPI & operation objects are from openapi4j parser
RequestValidator val = new RequestValidator(openAPI);

Request request = JakartaServletRequest.of(HttpServletRequest servletRequest);

// Default usage
val.validate(Request request);
// other usages
val.validate(Request request, Path path, Operation peration);
val.validate(Request request, ValidationData<?> validation); // If you need to get back info/warn content

// With response
val.validate(Response response, Path path, Operation operation);
// ...

```


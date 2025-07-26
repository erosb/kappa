# Using Kappa with Undertow

## Installation

=== "Maven"

    ```xml
    <dependency>
      <groupId>com.github.erosb</groupId>
      <artifactId>kappa-undertow-adapter</artifactId>
      <version>{{ kappa_version }}</version>
    </dependency>
    ```

=== "Gradle"

    ```kotlin
      testImplementation("com.github.erosb:kappa-undertow-adapter:{{ kappa_version }}")
    ```

## Usage

Kappa's Servlet adapter can be used to programmatically validate HTTP requests and responses
in any environment which uses the `jakarta.servlet` API.

Examples:
```java
// openAPI & operation objects are from openapi4j parser
RequestValidator val = new RequestValidator(openAPI);

// maps Undertow's HttpServerExchange object to a Kappa Request
// understood by the RequestValidator
Request request = UndertowRequest.of(HttpServerExchange hse);

// Default usage
val.validate(Request request);
// other usages
val.validate(Request request, Path path, Operation peration);
// If you need to get back info/warn content
val.validate(Request request, ValidationData<?> validation);

// With response
val.validate(Response response, Path path, Operation operation);
// ...

```


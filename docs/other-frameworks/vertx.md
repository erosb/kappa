# Using Kappa with Vert.x

## Installation

=== "Maven"

    ```xml
    <dependency>
      <groupId>com.github.erosb</groupId>
      <artifactId>kappa-vertx-adapter</artifactId>
      <version>{{ kappa_version }}</version>
    </dependency>
    ```

=== "Gradle"

    ```kotlin
      testImplementation("com.github.erosb:kappa-vertx-adapter:{{ kappa_version }}")
    ```



## Usage

Requests and responses are wrapped with the specific adapter.
```java
// openAPI & operation objects are from openapi4j parser
RequestValidator val = new RequestValidator(openAPI);
// Vert.x
Request request = VertxRequest.of(RoutingContext rc);
// validate...

// Default usage
val.validate(Request request);
// other usages
val.validate(Request request, Path path, Operation peration);
// If you need to get back info/warn content
val.validate(Request request, ValidationData<?> validation);

// With response
val.validate(Response response, Path path, Operation operation);
```


## Router factory

Like Vert.x vertx-web-api-contract router factory, this adapter comes with its equivalent adapted to this toolset.

Load the specification :
```java
OpenApi3RouterFactory.create(vertx, "src/main/resources/api.yaml", ar -> {
  if (ar.succeeded()) {
    OpenApi3RouterFactory routerFactory = ar.result();
  } else {
    // Something went wrong...
  }
});
```

Mounting the handlers :
```java
BodyHandler commonBodyHandler = BodyHandler.create();
// This body handler won't be used as this operation uses GET HTTP method.
routerFactory.addOperationHandler("operationIdGet", commonBodyHandler, routingContext -> {
  // Do something with the validated request
});
...
// The body handler can be dedicated
BodyHandler restrictedBodyHandler = BodyHandler.create().setBodyLimit(150);
routerFactory.addOperationHandler(
  "operationIdPost", restrictedBodyHandler, foo::processPost
);
```

Getting the request parameters :
```java
routerFactory.addOperationHandler("my_op", commonBodyHandler, routingContext -> {
  // Access the mapped parameters.
  RequestParameters rqParameters = rc.get("rqParameters");
  // Mapped as JsonNode to get direct structured value primitive, array or object.
  JsonNode ... = rqParameters.getPathParameter("your_spec_param");
  JsonNode ... = rqParameters.getQueryParameter("your_spec_param");
  JsonNode ... = rqParameters.getHeaderParameter("your_spec_param");
  JsonNode ... = rqParameters.getCookieParameter("your_spec_param");
});
```

Mounting the security handlers :

You must conform to your specification when an operation declares security requirements.
Otherwise, the router generation (#.getRouter()) will fail by throwing a ResolutionException.
```java
// security handlers can be custom or official handlers from the Vert.x stack
routerFactory.addSecurityHandler("security_scheme_name", securityHandler);
// OAuth2 scope
routerFactory.addSecurityScopedHandler(
  "security_scheme_name", "read:document" securityHandler
);
```

Generate the router :
```java
Router router = routerFactory.getRouter();
HttpServer server = vertx.createHttpServer(new HttpServerOptions()
  .setPort(8080)
  .setHost("localhost")
);
server.requestHandler(router).listen();
```

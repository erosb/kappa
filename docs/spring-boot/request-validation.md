# Request validation

Kappa can validate your incoming HTTP requests against your OpenAPI descriptions. Malformed requests won't
reach the spring controllers, hence the bad request will fail early. Still the HTTP client will receive a programmer-readable
error description about what went wrong.

## Installation

=== "Maven"

    ```xml
    <dependency>
      <groupId>com.github.erosb</groupId>
      <artifactId>kappa-spring</artifactId>
      <version>{{ kappa_version }}</version>
    </dependency>
    ```

=== "Gradle"

    ```kotlin
      testImplementation("com.github.erosb:kappa-spring:{{ kappa_version }}")
    ```


## Enable OpenAPI-based HTTP request validation

=== "Add an API description"

    [`openapi/pets-api.yaml`](https://github.com/erosb/kappa-examples/blob/master/kappa-spring-boot-examples/src/main/resources/openapi/pets-api.yaml) :

    ```yaml
    openapi: "3.1.0"
    info:
      title: "Pets API"
      version: 0.0.1
    paths:
      /api/pets:
        post:
          requestBody:
            content:
              application/json:
                schema:
                  $ref: "#/components/schemas/CreatePetRequest"
    components:
      schemas:
        CreatePetRequest:
          type: object
          additionalProperties: false
          required:
            - name
            - owner
          properties:
            name:
              $ref: "#/components/schemas/Name"
            owner:
              $ref: "./common-types.yaml#/UserIdentifier"
            birthDate:
              type: string
              format: date
        Name:
          type: string
          minLength: 1
    ```

=== "Configure Kappa"

    [`KappaSpringBootExampleApplication.java`](https://github.com/erosb/kappa-examples/blob/master/kappa-spring-boot-examples/src/main/java/com/github/erosb/kappa/examples/KappaSpringBootExampleApplication.java):

    ```java
    @SpringBootApplication
    @EnableKappaRequestValidation // (1)
    public class KappaSpringBootExampleApplication {

    	public static void main(String[] args) {
    		SpringApplication.run(KappaSpringBootExampleApplication.class, args);
    	}

    	@Bean
    	public KappaSpringConfiguration kappaConfig() {
    		var kappaConfig = new KappaSpringConfiguration();
    		var mapping = new LinkedHashMap<String, String>();
    		mapping.put("/**", "/openapi/pets-api.yaml");
    		kappaConfig.setOpenapiDescriptions(mapping);
    		return kappaConfig;
    	}
    }
    ```

    1.    Sets up a servlet filter for validating incoming HTTP requests
=== "Add the REST Controller"

    ```java
    record UserIdentifier(String id) {
    }

    record CreatePetRequest(String name, UserIdentifier owner, LocalDate birthDate) {
    }

    @RestController
    @RequestMapping("/api/pets")
    public class PetController {

        @PostMapping
        void createPet(@RequestBody CreatePetRequest requestBody) {
            System.out.println("requestBody = " + requestBody);
        }
    }

    ```

## Try it out!

If you start `KappaSpringBootExampleApplication` and send a request with `curl` (or your preferred HTTP client), the request will
also be validated:

```bash
curl -XPOST http://localhost:8080/api/pets \
  -H 'content-type: application/json' \
  --data '{"name": null,"type":"cat","owner":{"id": -5},"birthDate":"20230708"}'
```

the above command will print the following output:

```json
{
  "errors" : [ {
    "dataLocation" : "$request.body#/type (line 1, position 22)",
    "schemaLocation" : "openapi/pets-api.yaml#/components/schemas/CreatePetRequest/additionalProperties",
    "dynamicPath" : "#/$ref/additionalProperties/false",
    "message" : "false schema always fails"
  }, {
    "dataLocation" : "$request.body#/name (line 1, position 10)",
    "schemaLocation" : "openapi/pets-api.yaml#/components/schemas/Name/type",
    "dynamicPath" : "#/$ref/properties/name/$ref/type",
    "message" : "expected type: string, actual: null"
  }, {
    "dataLocation" : "$request.body#/owner/id (line 1, position 43)",
    "schemaLocation" : "openapi/common-types.yaml#/Id",
    "dynamicPath" : "#/$ref/properties/owner/$ref/properties/id/$ref/minimum",
    "message" : "-5 is lower than minimum 0"
  }, {
    "dataLocation" : "$request.body#/birthDate (line 1, position 59)",
    "schemaLocation" : "openapi/pets-api.yaml#/components/schemas/CreatePetRequest/properties/birthDate/format",
    "dynamicPath" : "#/$ref/properties/birthDate/format",
    "message" : "instance does not match format 'date'"
  } ]
}
```

These json schema validation errors tell us the following problems with the json payload:

 * the `"type"` field sent in the request is not recognized by the service
 * the `"name"` should be a string, never null, like in our request
 * the `"owner.id"` property should be non-negative, so `-5` is invalid
 * the `"birthDate"` also does not match the expected date format


## Managing undocumented endpoints

It is often useful to exclude some requests from the OpenAPI-based request validation, either because they are not REST endpoints,
or we don't have documentation for them. This is doable with the `KappaSpringConfiguration#setignoredPathPatterns(String... pathPatterns)` configuration.

This `ignoredPathPatterns` property works as follows:

 * if an incoming HTTP request doesn't match any of the path patterns (map keys)
in the `kappaSpringConfig.openapiDescriptions` map,
 * but the request matches a pattern in `ignoredPathPatterns`,
 * then no error will be reported to
the client, and the request will successfully complete.


### Example - configuring Swagger UI

Given you already have OpenAPI descriptions for all your endpoints, it is easy to make those available via
swagger UI. We just have to make sure that the swagger UI related HTTP requests aren't rejected by Kappa due to OpenAPI the validation.

Let's go through it step by step:


=== "Add Springdoc UI dependency"

    Maven:
    ```
      <dependency>
          <groupId>org.springdoc</groupId>
          <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
          <version>2.8.9</version>
      </dependency>
    ```

    Gradle:
    ```
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    ```

=== "Configure Swagger"

    `application.yaml`:

    ```
    springdoc:
      swagger-ui:
        urls:
          - name: Pets API
            url: /openapi/pets-api.yaml
    ```

=== "Configure Kappa"

    ```java
      @Bean
    	public KappaSpringConfiguration kappaConfig() {
    		var kappaConfig = new KappaSpringConfiguration();
    		var mapping = new LinkedHashMap<String, String>();
    		mapping.put("/api/**", "/static/openapi/pets-api.yaml"); // (1)
    		kappaConfig.setOpenapiDescriptions(mapping);
    		kappaConfig.setIgnoredPathPatterns(
    		  "/swagger-ui/**", // (2)
    		  "/v3/api-docs/**", // (3)
    		  "/openapi/**" // (4)
    		);
    		return kappaConfig;
    	}
    ```

    1. Make sure the openapi descriptions are under `src/main/resources/static` (this is required for `springdoc` to find the files)
    2. Enable requests for loading Swagger UI client
    3. Enable loading swagger UI configuration
    4. Enable loading our OpenAPI descriptions


### Try it out!

The Swagger UI setup is already included in the [kappa-examples](https://github.com/erosb/kappa-examples) repo. Let's just clone and try it locally:

 * `git clone https://github.com/erosb/kappa-examples`
 * `cd kappa-examples/kappa-spring-boot-examples`
 * `./gradlew bootRun`
 * open your browser at `http://localhost:8080/swagger-ui/index.html`

# Contract testing


Kappa has first-class support for testing if your API under testing conforms to its defined OpenAPI description. Seamlessly integrates with MockMvc-based SpringBootTests.

## Installation

### Maven

```xml
<dependency>
  <groupId>com.github.erosb</groupId>
  <artifactId>kappa-spring</artifactId>
  <version>2.0.0-RC15</version>
</dependency>
```

### Gradle

```kotlin
	testImplementation("com.github.erosb:kappa-spring:2.0.0-RC15")
```



## Add a contract-driven test

### Add an OpenAPI definition to your classpath

=== "Add an API definition"

    `src/main/resources/api/openapi.yaml` :

    ```yaml
    openapi: 3.1.0
    info:
        version: 0.0.1
        title: employee API
    paths:
    /employees:
        get:
        description: get employee list
        responses:
            '200':
            content:
                application/json:
                schema:
                    type: array
                    items:
                    $ref: "#/components/schemas/Employee"
        post:
        requestBody:
            required: true
            content:
            application/json:
                schema:
                $ref: "#/components/schemas/Employee"
        responses:
            '201':
            description: 'successfully created'
            content:
                application/json:
                schema:
                    $ref: "#/components/schemas/Employee"
    /employees/{id}:
        get:
        description: get employee list
        responses:
            '200':
            content:
                application/json:
                schema:
                    $ref: "#/components/schemas/Employee"
            '404':
            content:
                application/json:
                schema:
                    $ref: "#/components/schemas/NotFoundResponseBody"
    components:
    schemas:
        Employee:
        type: object
        additionalProperties: false
        properties:
            id:
            type: integer
            name:
            type: string
            role:
            type: string
        NotFoundResponseBody:
        type: object
        required:
            - id
            - message
        properties:
            id:
            type: integer
            message:
            type: string
    ```
=== "Tell Kappa about your API definition"

    `src/main/java/EmployeeApplication.java`:

    ```java
    @SpringBootApplication
    public class EmployeeApplication {

        public static void main(String[] args) {
            SpringApplication.run(DemoApplication.class, args);
        }

        @Bean
        public KappaSpringConfiguration kappaSpringConfiguration() {
            KappaSpringConfiguration kappaConfig = new KappaSpringConfiguration();
            var pathPatternToOpenapiDescription = new LinkedHashMap<String, String>();
            pathPatternToOpenapiDescription.put("/**", "/api/openapi.yaml");
            kappaConfig.setOpenapiDescriptions(pathPatternToOpenapiDescription);
            return kappaConfig;
        }

    }
    ```
    
=== "Add an API test"

    `src/test/java/EmployeeApiTest.java`:
  
    ```java
    @SpringBootTest
    @AutoConfigureMockMvc
    @EnableKappaContractTesting // (1)
    public class EmployeeApiTest {

        @Autowired
        MockMvc mvc;

        @Test
        void notFoundResponseBodyMismatch() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/employees/22")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                    // actually, this is the json that will be returned by the endpoint, but it doesn't match the openapi description
                    // due to the missing "id" property, so the test fails
                .andExpect(content().json("""
                        {
                            "message": "Could not find employee 22"
                        }
                        """));
        }

    }
    ```

    1.  This annotation enables contract verification on every request and response.
        If either the request or response doesn't match, the API, the test fails

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

=== "src/main/resources/api/openapi.yaml"

    ```yaml
    openapi: 3.1.0
    info:
        version: 0.0.1
        title: employee API
    paths:
    /employees: # (1)
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

    1.  :man_raising_hand: I'm an annotation! I can contain `code`, __formatted
        text__, images, ... basically anything that can be expressed in Markdown.
=== "src/test/java/EmployeeApiTest.java"
  
    ```java
    @SpringBootTest()
    @AutoConfigureMockMvc
    @EnableKappaContractTesting // enable contract verification
    public class EmployeeApiTest {

        @Autowired
        MockMvc mvc;

        @Test
        void notFoundResponseBodyMismatch() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/employees/22").accept(MediaType.APPLICATION_JSON))
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

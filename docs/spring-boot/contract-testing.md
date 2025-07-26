# Contract testing


Kappa has first-class support for testing if your API under testing conforms to its defined OpenAPI description. Seamlessly integrates with MockMvc-based SpringBootTests.

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



## Add a contract-driven test

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
        get:
          responses:
            '200':
              content:
                application/json:
                  schema:
                    type: array
                    items:
                      $ref: "#/components/schemas/Pet"
    components:
      schemas:
        Name:
          type: string
          minLength: 1
        Pet:
          type: object
          additionalProperties: false
          required:
            - id
            - name
          properties:
            id:
              type: integer
            name:
              $ref: "#/components/schemas/Name"
            owner:
              type: object
              additionalProperties: false
              required:
                - id
                - name
              properties:
                id:
                  type: integer
                name:
                  $ref: "#/components/schemas/Name"
            birthDate:
              type: string
              format: date

    ```
=== "Configure Kappa"

    [`KappaSpringBootExampleApplication.java`](https://github.com/erosb/kappa-examples/blob/master/kappa-spring-boot-examples/src/main/java/com/github/erosb/kappa/examples/KappaSpringBootExampleApplication.java):

    ```java
    @SpringBootApplication
    public class KappaSpringBootExampleApplication {

    	public static void main(String[] args) {
    		SpringApplication.run(KappaSpringBootExampleApplication.class, args);
    	}

    	@Bean
    	public KappaSpringConfiguration kappaConfig() {
    		var kappaConfig = new KappaSpringConfiguration();
    		var mapping = new LinkedHashMap<String, String>();
    		mapping.put("/**", "/openapi/pets-api.yaml"); // (1)
    		kappaConfig.setOpenapiDescriptions(mapping);
    		return kappaConfig;
    	}
    }

    ```

    1. If your OpenAPI descriptions are split into multiple files, you can map multiple request paths to yaml files describing them

=== "Implement the API"

    [`PetController.java`](https://github.com/erosb/kappa-examples/blob/master/kappa-spring-boot-examples/src/main/java/com/github/erosb/kappa/examples/PetController.java):

    ```java
    record User(int id, String firstName, String lastName) {
    }

    record Pet(int id, String name, User owner, long birthDate) {
    }

    @RestController
    @RequestMapping("/api/pets")
    public class PetController {

        @GetMapping
        List<Pet> getPets() {
            return List.of(
                new Pet(
                    1,
                    "",
                    new User(2, "John", "Doe"),
                    LocalDate.parse("2017-08-08").toEpochDay()
                )
            );
        }
    }
    ```

=== "Add an API test"

    [`ContractDrivenApiTest.java`](https://github.com/erosb/kappa-examples/blob/master/kappa-spring-boot-examples/src/test/java/com/github/erosb/kappa/examples/ContractDrivenApiTest.java):

    ```java
    @SpringBootTest
    @AutoConfigureMockMvc
    @EnableKappaContractTesting //(1)
    public class ContractDrivenApiTest {


        @Autowired
        MockMvc mvc;

        @Test
        void testListPets() throws Exception {
            mvc.perform(get("/api/pets")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1)); // (2)
        }
    }

    ```

    1.  This annotation enables contract verification on every request and response.
        If either the request or response doesn't match, the API, the test fails
    2.  The assertions of the test pass, but Kappa will catch the structural mismatches of the response



If you run the above `ContractDrivenApiTest`, it will fail and report the following errors with the response structure:

 * an empty pet name is returned, while it is described as a `minLength: 1` string
 * two undefined properties of the owner are returned: `firstName` and `lastName`
 * on the other hand, the mandatory `name` field of the owner is missing


The complete example is available in the [Kappa Examples](https://github.com/erosb/kappa-examples/tree/master/kappa-spring-boot-examples) repo.

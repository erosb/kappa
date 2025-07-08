# Kappa - OpenAPI validator for Java and JVM projects

This project is a successor (permanent fork) of the [archived openapi4j](https://github.com/openapi4j/openapi4j) project.


Kappa can be used to validate HTTP requests and responses against OpenAPI 3.1 definitions.

Under the hood ist uses the [erosb/json-sKema](https://github.com/erosb/json-sKema) library for JSON Schema validation.

<!-- TOC start (generated with https://github.com/derlin/bitdowntoc) -->

- [Kappa - OpenAPI validator for Java and JVM projects](#kappa-openapi-validator-for-java-and-jvm-projects)
   * [Installation - Maven](#installation-maven)
   * [Contract Testing - use Kappa to ensure all your requests & responses conform to your OpenAPI descriptions](#contract-testing-use-kappa-to-ensure-all-your-requests-responses-conform-to-your-openapi-descriptions)
      + [Programmatically configure Kappa](#programmatically-configure-kappa)
      + [Verify that everything going through MockMvc conforms to your OpenAPI description](#verify-that-everything-going-through-mockmvc-conforms-to-your-openapi-description)
   * [Supported versions](#supported-versions)
   * [Contributing](#contributing)
   * [License](#license)
   * [Contributor notes](#contributor-notes)

<!-- TOC end -->

## Installation - Maven

```xml
<dependency>
  <groupId>com.github.erosb</groupId>
  <artifactId>kappa-spring</artifactId>
  <version>2.0.0-RC15</version>
</dependency>

```


## Contract Testing - use Kappa to ensure all your requests & responses conform to your OpenAPI descriptions

### Programmatically configure Kappa

Tell where are your openapi descriptions on the classpath:

```java
public class UsersApplication {

  public static void main(String[] args) {
    SpringApplication.run(UsersApplication.class);
  }

  @Bean
  public KappaSpringConfiguration kappaSpringConfiguration() {
    KappaSpringConfiguration kappaConfig = new KappaSpringConfiguration();
    var pathPatternToOpenapiDescription = new LinkedHashMap<String, String>();
    // customize the mapping (path pattern -> api desctiption) if your description is split into multiple openapi files
    pathPatternToOpenapiDescription.put("/**", "/api/openapi.yaml");
    kappaConfig.setOpenapiDescriptions(pathPatternToOpenapiDescription);
    return kappaConfig;
  }

}
```

### Verify that everything going through MockMvc conforms to your OpenAPI description


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



## Supported versions

Kappa targets supporting OpenAPI 3.1. Currently it uses a draft2020-12 compliant validator for JSON Schema.

## Contributing

Reporting issues, making comments, ... Any help is welcome !

We accept Pull Requests via GitHub. There are some guidelines which will make applying PRs easier for us :

* Provide JUnit tests for your changes and make sure your changes don't break anything by running `gradlew clean check`.
* Provide a self explanatory but brief commit message with issue reference if any, as it will be reported directly for release changelog.

## License

Kappa is released under the Apache 2.0 license. See [LICENSE](https://github.com/openapi4j/openapi4j/blob/master/LICENSE.md) for details.

## Contributor notes

Build: `./gradlew clean build publish`

Release to maven central: `./gradlew jreleaserFullRelease`

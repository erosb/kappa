# Kappa - OpenAPI validator for Java and JVM projects

This project is a successor of the [archived openapi4j](https://github.com/openapi4j/openapi4j) project.


Kappa can be used to validate HTTP requests and responses against OpenAPI 3.1 definitions.

Under the hood ist uses the [erosb/json-sKema](https://github.com/erosb/json-sKema) library for JSON Schema validation.

## Modules

* [Parser](openapi-parser) allows the (de-)serialization and manipulation of the schema and its validation.
* [Schema validator](openapi-schema-validator) allows the validation of data against a given schema.
* [Request validator](openapi-operation-validator) is high level module to manage validation for requests and/or responses against operations. More details in the related project.
* [Request adapters](openapi-operation-adapters) is the repository of specific adapters to wrap requests and responses.

## Versioning and compatibility

All modules follow the [Semantic Versioning 2.0.0](https://semver.org) and are aligned on each release even there's no changes.

```xml
<dependency>
    <groupId>com.github.erosb.kappa</groupId>
    <artifactId>openapi-[module]</artifactId>
</dependency>
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

Kappa and all the modules are released under the Apache 2.0 license. See [LICENSE](https://github.com/openapi4j/openapi4j/blob/master/LICENSE.md) for details.


## Contributor notes

Release to local maven repo: `./gradlew build publishToMavenLocal`
Release to maven central: `./gradlew build publishToSonatype closeAndReleaseSonatypeStagingRepository`

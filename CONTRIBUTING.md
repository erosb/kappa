## Contributing

Reporting issues, making comments, ... Any help is welcome !

We accept Pull Requests via GitHub. There are some guidelines which will make applying PRs easier for us :

* Provide JUnit tests for your changes and make sure your changes don't break anything by running `mvn clean verify`.
* Provide a self explanatory but brief commit message with issue reference if any, as it will be reported directly for release changelog.

## License

Kappa is released under the Apache 2.0 license. See [LICENSE](https://github.com/erosb/kappa/blob/master/LICENSE.md) for details.

## Release process

 - `git checkout -b release/v<version-number>`
 - Bump version number in the root `pom.xml` and `mkdocs.yml`.
 - `git commit -am 'bump version number'`
 - Build: `mvn clean verify`
 - Release to Maven Central: `mvn -P release -pl :kappa-parent,:kappa,:kappa-restassured-adapter,:kappa-servlet-adapter,:kappa-undertow-adapter,:kappa-vertx-adapter,:kappa-spring -am deploy`
 - Wait until the release is available in Maven Central (usually a few minutes)
 - `git tag v<version-number>`
 - `git checkout master && git merge release/v<version-number>`
 - `git push origin v<version-number>`
 - `mkdocs gh-deploy`
 - Bump version number in `kappa-examples`, commit & push

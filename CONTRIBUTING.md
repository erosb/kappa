## Contributing

Reporting issues, making comments, ... Any help is welcome !

We accept Pull Requests via GitHub. There are some guidelines which will make applying PRs easier for us :

* Provide JUnit tests for your changes and make sure your changes don't break anything by running `gradlew clean check`.
* Provide a self explanatory but brief commit message with issue reference if any, as it will be reported directly for release changelog.

## License

Kappa is released under the Apache 2.0 license. See [LICENSE](https://github.com/erosb/kappa/blob/master/LICENSE.md) for details.

## Contributor notes

Build: `./gradlew clean build publish`

Release to maven central: `./gradlew jreleaserFullRelease`

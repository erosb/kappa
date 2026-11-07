## Contributing

Reporting issues, making comments, ... Any help is welcome !

We accept Pull Requests via GitHub. There are some guidelines which will make applying PRs easier for us :

* Provide JUnit tests for your changes and make sure your changes don't break anything by running `mvn clean verify`.
* Provide a self explanatory but brief commit message with issue reference if any, as it will be reported directly for release changelog.

## License

Kappa is released under the Apache 2.0 license. See [LICENSE](https://github.com/erosb/kappa/blob/master/LICENSE.md) for details.

## Release process

1. Create a release branch, update the version in the root `pom.xml` and `mkdocs.yml`, then run `mvn clean verify`.
2. Commit and merge the release changes to the default branch.
3. Create and push an annotated tag whose version matches the root POM, for example:

   ```sh
   git tag -a v2.1.0 -m "Release v2.1.0"
   git push origin v2.1.0
   ```

4. In GitHub, open **Actions → Publish to Maven Central → Run workflow**, enter the pushed tag (for example `v2.1.0`), and start the workflow. It checks out that tag and fails if its version does not match the root POM.
5. Monitor the workflow and the deployment in the Maven Central Publisher Portal. The workflow auto-publishes the signed release; verify the artifacts are available before announcing the release.
6. Publish the documentation with `mkdocs gh-deploy` and update `kappa-examples` as needed.

### One-time publishing setup

Maven Central requires published artifacts to include valid PGP signatures, so configure a dedicated signing key and Central Portal credentials before running the workflow.

1. Generate a signing key locally with `gpg --full-generate-key` (choose an RSA signing key of at least 3072 bits, set a strong passphrase, and retain the passphrase securely). Export the ASCII-armored private key:

   ```sh
   gpg --armor --export-secret-keys KEY_ID
   ```

   Add the complete output as the GitHub Actions secret `MAVEN_GPG_PRIVATE_KEY`, and add its passphrase as `MAVEN_GPG_PASSPHRASE`. Never commit or share the private key.
2. Publish the corresponding **public** key to a public OpenPGP keyserver so Maven Central can verify signatures, for example:

   ```sh
   gpg --keyserver hkps://keys.openpgp.org --send-keys KEY_ID
   ```

   Complete any keyserver email verification it requests.
3. Create a user token in the Sonatype Central Portal. Store its username and password as the GitHub Actions secrets `CENTRAL_USERNAME` and `CENTRAL_PASSWORD` (these are Portal token values, not the former OSSRH credentials).

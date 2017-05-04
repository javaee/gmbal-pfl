# Java EE Primitive Function Library

This is the [gmbal-pfl project](https://javaee.github.io/gmbal-pfl/).
 
## Releasing

* Make sure `gpg-agent` is running.
* Execute `mvn -B release:prepare release:perform`

For publishing the site do the following:

```
cd target/checkout
mvn -Psite verify site site:stage scm-publish:publish-scm
```

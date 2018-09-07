#### :warning:This project is now part of the EE4J initiative. This repository has been archived as all activities are now happening in the [corresponding Eclipse repository](https://github.com/eclipse-ee4j/orb-gmbal-pfl). See [here](https://www.eclipse.org/ee4j/status.php) for the overall EE4J transition status.
 
---
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

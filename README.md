Bindings for <a href="https://github.com/reactive-streams/reactive-streams-jvm" target="_blank">Reactive Streams</a> and the <a href="https://github.com/sphereio/sphere-jvm-sdk">commercetools JVM SDK</a>

![SPHERE.IO icon](https://admin.sphere.io/assets/images/sphere_logo_rgb_long.png)

[![][travis img]][travis]
[![][maven img]][maven]
[![][license img]][license]

[Javadoc](http://commercetools.github.io/commercetools-jvm-sdk-reactive-streams-add-ons/)



Maven:

```xml
<dependency>
    <groupId>com.commercetools.sdk.jvm.reactive-streams</groupId>
    <artifactId>commercetools-reactive-streams</artifactId>
    <version>replace with version above (maven central)</version>
</dependency>
```

Getting a publisher:

```java
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.queries.ProductQuery;
import io.sphere.sdk.reactivestreams.SphereReactiveStreams;
import org.reactivestreams.Publisher;

public class QueryAllIdentifiableDemo {
    public static void demo(final SphereClient client) {
        final Publisher<Product> productPublisher =
            SphereReactiveStreams.publisherOf(ProductQuery.of(), client);
    }
}
```

[travis]:https://travis-ci.org/sphereio/commercetools-jvm-sdk-reactive-streams-add-ons
[travis img]:https://travis-ci.org/sphereio/commercetools-jvm-sdk-reactive-streams-add-ons.svg?branch=master

[maven]:http://search.maven.org/#search|gav|1|g:"com.commercetools.sdk.jvm.reactive-streams"%20AND%20a:"commercetools-reactive-streams"
[maven img]:https://maven-badges.herokuapp.com/maven-central/com.commercetools.sdk.jvm.reactive-streams/commercetools-reactive-streams/badge.svg

[license]:LICENSE.md
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg

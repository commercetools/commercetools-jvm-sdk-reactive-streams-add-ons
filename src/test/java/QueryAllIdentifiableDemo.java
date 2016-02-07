import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.queries.ProductQuery;
import io.sphere.sdk.reactivestreams.SphereReactiveStreams;
import org.reactivestreams.Publisher;

public class QueryAllIdentifiableDemo {
    public static void demo(final SphereClient client) {
        final Publisher<Product> productPublisher = SphereReactiveStreams.publisherOf(ProductQuery.of(), client);
    }
}

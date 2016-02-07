import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.reactivestreams.SphereReactiveStreams;
import org.reactivestreams.Publisher;

public class QueryAllIIdExtractorDemo {
    public static void demoProductProjection(final SphereClient client) {
        final ProductProjectionQuery seedQuery = ProductProjectionQuery.ofCurrent()
                .withPredicates(m -> m.categories().id().is("category-id"));
        final Publisher<ProductProjection> productPublisher =
                SphereReactiveStreams.publisherOf(seedQuery, ProductProjection::getId, client);
    }
}

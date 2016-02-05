import io.sphere.sdk.client.*;
import io.sphere.sdk.http.HttpClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductDraftBuilder;
import io.sphere.sdk.products.ProductVariantDraftBuilder;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.commands.ProductDeleteCommand;
import io.sphere.sdk.products.queries.ProductQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.queries.ProductTypeByKeyGet;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.reactivestreams.SphereReactiveStreams;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class QueryPublisherVerificationTest extends PublisherVerification<Product> {
    public static final int PRODUCT_COUNT_FOR_TEST = 500;//important that it is <= 500 for deletion convenience
    public static final java.util.function.Consumer<CompletionStage<Product>> BLOCK_FOR_STAGE_CONSUMER = s -> SphereClientUtils.blockingWait(s, 30, TimeUnit.SECONDS);
    private static BlockingSphereClient client;

    public QueryPublisherVerificationTest() {
        super(new TestEnvironment(500));
    }

    @BeforeClass
    public static void setupClient() {
        if (client == null) {
            final SphereClientConfig config = getSphereClientConfig();
            final HttpClient httpClient = SphereClientFactory.of().createHttpClient();
            final SphereAccessTokenSupplier tokenSupplier = SphereAccessTokenSupplier.ofAutoRefresh(config, httpClient, false);
            final SphereClient underlying = SphereClient.of(config, httpClient, tokenSupplier);
            client = BlockingSphereClient.of(underlying, 20, TimeUnit.SECONDS);
        }
        final PagedQueryResult<Product> existingProducts = client.executeBlocking(
                ProductQuery.of().withLimit(PRODUCT_COUNT_FOR_TEST));
        final Long existingProductsCount = existingProducts.getTotal();
        if (existingProductsCount != PRODUCT_COUNT_FOR_TEST) {
            delete(existingProducts.getResults());
            createProducts();
        }
    }

    private static void createProducts() {
        final ProductType productType = getOrCreateProductType();
        final List<CompletionStage<Product>> stages = new ArrayList<>(PRODUCT_COUNT_FOR_TEST);
        for(int i = 0; i < PRODUCT_COUNT_FOR_TEST; i++) {
            final LocalizedString slug = LocalizedString.ofEnglish(slugFor(i));
            final ProductDraft productDraft = ProductDraftBuilder.of(productType, slug, slug, ProductVariantDraftBuilder.of().build()).build();
            stages.add(client.execute(ProductCreateCommand.of(productDraft)));
        }
        stages.forEach(BLOCK_FOR_STAGE_CONSUMER);
    }

    private static String slugFor(final long i) {
        return String.format("product-%04d", i);
    }

    private static ProductType getOrCreateProductType() {
        final String key = "reactive-streams";
        final ProductTypeDraft productTypeDraft = ProductTypeDraft.of(key, key, key, Collections.emptyList());
        return Optional.ofNullable(client.executeBlocking(ProductTypeByKeyGet.of(key)))
                .orElseGet(() -> client.executeBlocking(ProductTypeCreateCommand.of(productTypeDraft)));
    }

    private static void delete(final List<Product> products) {
        final List<CompletionStage<Product>> stages = new LinkedList<>();
        products.forEach(p -> {
            final CompletionStage<Product> completionStage = client.execute(ProductDeleteCommand.of(p));
            stages.add(completionStage);
        });
        stages.forEach(BLOCK_FOR_STAGE_CONSUMER);
    }

    public static SphereClientConfig getSphereClientConfig() {
        final File file = new File("integrationtest.properties");
        return file.exists() ? loadViaProperties(file) : loadViaEnvironmentArgs();
    }

    private static SphereClientConfig loadViaEnvironmentArgs() {
        return SphereClientConfig.ofEnvironmentVariables("JVM_SDK_REACTIVE_STREAMS_IT");
    }

    private static SphereClientConfig loadViaProperties(final File file) {
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            final Properties properties = new Properties();
            properties.load(fileInputStream);
            return SphereClientConfig.ofProperties(properties, "");
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @AfterClass
    public static void cleanupClient() {
        client.close();
        client = null;
    }



    @Override
    public Publisher<Product> createPublisher(final long elements) {
        final ProductQuery query = ProductQuery.of()
                .withPredicates(m -> m.masterData().staged().slug().locale(Locale.ENGLISH).isLessThan(slugFor(elements)));
        return SphereReactiveStreams.publisherOf(query, client);
    }


    @Override
    public Publisher<Product> createFailedPublisher() {
        return null;
    }

    @Override
    public long maxElementsFromPublisher() {
        return PRODUCT_COUNT_FOR_TEST;
    }
}
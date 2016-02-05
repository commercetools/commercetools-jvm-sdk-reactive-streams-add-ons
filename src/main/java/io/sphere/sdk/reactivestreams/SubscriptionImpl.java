package io.sphere.sdk.reactivestreams;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.queries.QueryDsl;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

final class SubscriptionImpl<T, C extends QueryDsl<T, C>> extends Base implements Subscription {
    SubscriptionsState state;
    SubscriptionImpl(final QueryDsl<T, C> seedQuery, final Function<T, String> idExtractor, final SphereClient sphereClient, final Subscriber<? super T> subscriber) {
        state = new RunningSubscription<T, C>(seedQuery, idExtractor, sphereClient, subscriber, this);
    }

    @Override
    public void request(final long n) {
        state.request(n);
    }

    @Override
    public void cancel() {
        state.cancel();
    }
}

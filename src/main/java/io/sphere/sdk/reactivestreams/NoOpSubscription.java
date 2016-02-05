package io.sphere.sdk.reactivestreams;

import io.sphere.sdk.models.Base;

class NoOpSubscription extends Base implements SubscriptionsState {
    @Override
    public void cancel() {

    }

    @Override
    public void request(final long n) {

    }
}
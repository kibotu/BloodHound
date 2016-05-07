package net.kibotu.android.easygoogleanalyticstracking;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.Arrays;

public final class EventTracker {

    private final Tracker tracker;
    @NonNull
    private final HitBuilders.EventBuilder builder;

    private String screenName;
    private Dimension[] dimensions;

    private EventTracker(final Tracker tracker) {
        this.tracker = tracker;
        this.builder = new HitBuilders.EventBuilder();
    }

    @NonNull
    public static EventTracker from(final Tracker tracker) {
        return new EventTracker(tracker);
    }

    @NonNull
    public EventTracker screenName(final String screenName) {
        this.screenName = screenName;
        return this;
    }

    @NonNull
    public EventTracker category(final String category) {
        builder.setCategory(category);
        return this;
    }

    @NonNull
    public EventTracker action(final String action) {
        builder.setAction(action);
        return this;
    }

    @NonNull
    public EventTracker label(@Nullable final String label) {
        if (label != null) {
            builder.setLabel(label);
        }
        return this;
    }

    @NonNull
    public EventTracker value(final long value) {
        builder.setValue(value);
        return this;
    }

    @NonNull
    public EventTracker addProduct(final Product product) {
        builder.addProduct(product);
        return this;
    }

    @NonNull
    public EventTracker productAction(final ProductAction productAction) {
        builder.setProductAction(productAction);
        return this;
    }

    @NonNull
    public EventTracker customDimension(final Dimension dimension) {
        if (dimensions == null) {
            dimensions = new Dimension[1];
            dimensions[0] = dimension;
        } else {
            dimensions = Arrays.copyOf(dimensions, dimensions.length + 1);
            dimensions[dimensions.length - 1] = dimension;
        }
        return this;
    }

    public void track() {
        if (dimensions != null && dimensions.length > 0) {
            for (final Dimension dimension : dimensions) {
                builder.setCustomDimension(dimension.index, dimension.value);
            }
        }
        tracker.setScreenName(screenName);
        tracker.send(builder.build());
        tracker.setScreenName(null);
    }

    public void setNewSession() {
        builder.setNewSession();
    }
}
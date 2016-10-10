package net.kibotu.android.bloodhound.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Arrays;

public final class TimingTracker {
    private final Tracker tracker;
    @NonNull
    private final HitBuilders.TimingBuilder builder;

    private String screenName;
    private Dimension[] dimensions;

    public TimingTracker(final Tracker tracker) {
        this.tracker = tracker;
        this.builder = new HitBuilders.TimingBuilder();
    }

    @NonNull
    public static TimingTracker from(final Tracker tracker) {
        return new TimingTracker(tracker);
    }

    @NonNull
    public TimingTracker screenName(final String screenName) {
        this.screenName = screenName;
        return this;
    }

    @NonNull
    public TimingTracker category(final String category) {
        builder.setCategory(category);
        return this;
    }

    @NonNull
    public TimingTracker variable(final String variable) {
        builder.setVariable(variable);
        return this;
    }

    @NonNull
    public TimingTracker label(@Nullable final String label) {
        if (label != null) {
            builder.setLabel(label);
        }
        return this;
    }

    @NonNull
    public TimingTracker value(final long timeInMillis) {
        builder.setValue(timeInMillis);
        return this;
    }

    @NonNull
    public TimingTracker customDimension(final Dimension dimension) {
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
}
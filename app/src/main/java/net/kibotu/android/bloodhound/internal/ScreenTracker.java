package net.kibotu.android.bloodhound.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public final class ScreenTracker {

    private final String screenName;
    private final Tracker tracker;
    @NonNull
    private final HitBuilders.ScreenViewBuilder builder;

    private ScreenTracker(final Tracker tracker, final String screenName) {
        this.tracker = tracker;
        this.screenName = screenName;
        this.builder = new HitBuilders.ScreenViewBuilder();
    }

    @NonNull
    public static ScreenTracker from(final Tracker tracker, final String screenName) {
        return new ScreenTracker(tracker, screenName);
    }

    public void track() {
        tracker.setScreenName(screenName);
        tracker.send(builder.build());
        tracker.setScreenName(null);
    }

    public void setNewSession() {
        builder.setNewSession();
    }
}
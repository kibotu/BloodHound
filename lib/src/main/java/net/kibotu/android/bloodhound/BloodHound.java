package net.kibotu.android.bloodhound;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import net.kibotu.android.bloodhound.internal.EventTracker;
import net.kibotu.android.bloodhound.internal.ScreenTracker;
import net.kibotu.android.bloodhound.internal.TimingTracker;
import net.kibotu.android.bloodhound.internal.Version;

import java.util.Locale;

import static com.google.android.gms.analytics.Logger.LogLevel.ERROR;
import static com.google.android.gms.analytics.Logger.LogLevel.VERBOSE;
import static net.kibotu.android.bloodhound.ContextExtensions.getApplicationName;
import static net.kibotu.android.bloodhound.ContextExtensions.getPackageName;

/**
 * Created by Nyaruhodo on 07.05.2016.
 */
public class BloodHound {

    // region vars

    private static final String TAG = BloodHound.class.getSimpleName();

    @Nullable
    private static BloodHound instance;

    @NonNull
    private final Application context;

    private Tracker tracker;
    private GoogleAnalytics analytics;

    private boolean enableDebugging;

    private int sessionLimit;

    private int sessionCounter;

    @NonNull
    private String currentScreen;

    private boolean enableAutoActivityTracking;

    // endregion

    // region init

    private BloodHound(@NonNull final Application context, @NonNull final String trackingId) {
        BloodHound.instance = this;
        this.context = context;
        analytics = GoogleAnalytics.getInstance(context);
        tracker = analytics.newTracker(trackingId);
        currentScreen = "";
        init();
        log("[init] with " + trackingId);
    }

    public static BloodHound with(@NonNull final Application context, @NonNull final String trackingId) {
        return instance != null
                ? instance
                : new BloodHound(context, trackingId);
    }

    public static BloodHound with(@NonNull final Application context, @StringRes final int trackingId) {
        return with(context, getString(trackingId));
    }

    private void init() {
        context.registerActivityLifecycleCallbacks(createActivityLifeCycleCallbacks());

        enableLogging(enableDebugging);

        enableExceptionReporting(false);
        enableAdvertisingIdCollection(true);
        enableAutoActivityTracking(false);
        setSessionTimeout(300);
        setSampleRate(100);
        setSessionLimit(500);

        enableDryRun(false);

        analytics.setLocalDispatchPeriod(enableDebugging
                ? 15
                : 300);

        tracker.setAppVersion(new Version(context).toString());
        tracker.setAppName(getPackageName(context));
        tracker.setLanguage(Locale.getDefault().getDisplayLanguage());
    }

    private static BloodHound getInstance() {
        if (instance == null)
            throw new IllegalStateException("Please initialize: BloodHound.with() first. (:");
        return instance;
    }

    // endregion

    // region helper

    private static void log(@NonNull final String message) {
        if (instance.enableDebugging)
            Log.v(TAG, message);
    }

    private static String getString(@StringRes final int resourceId) {
        return instance.context.getString(resourceId);
    }

    // endregion

    // region settings

    public BloodHound enableDryRun(final boolean isEnabled) {
        analytics.setDryRun(isEnabled);
        return instance;
    }

    public BloodHound enableExceptionReporting(final boolean isEnabled) {
        tracker.enableExceptionReporting(isEnabled);
        return instance;
    }

    public BloodHound enableAdvertisingIdCollection(final boolean isEnabled) {
        tracker.enableAdvertisingIdCollection(isEnabled);
        return instance;
    }

    public BloodHound enableAutoActivityTracking(final boolean isEnabled) {
        tracker.enableAutoActivityTracking(isEnabled);
        enableAutoActivityTracking = isEnabled;
        return instance;
    }

    public BloodHound setSessionTimeout(final long sessionTimeout) {
        tracker.setSessionTimeout(sessionTimeout);
        return instance;
    }

    public BloodHound setSampleRate(final double sampleRate) {
        tracker.setSampleRate(sampleRate);
        return instance;
    }

    public BloodHound setLocalDispatchPeriod(final int localDispatchPeriod) {
        analytics.setLocalDispatchPeriod(localDispatchPeriod);
        return instance;
    }

    public BloodHound setSessionLimit(final int sessionLimit) {
        this.sessionLimit = sessionLimit;
        return instance;
    }

    public BloodHound enableLogging(final boolean loggingEnabled) {
        this.enableDebugging = loggingEnabled;

        analytics.getLogger().setLogLevel(loggingEnabled
                ? VERBOSE
                : ERROR);

        return instance;
    }

    // endregion

    // region tracking activity start and stop

    private void reportActivityStart(@NonNull final Activity activity) {
        if (!getInstance().enableAutoActivityTracking)
            track(getApplicationName(activity));
    }

    private void reportActivityStop(@NonNull final Activity activity) {
        if (!getInstance().enableAutoActivityTracking)
            GoogleAnalytics.getInstance(activity).reportActivityStop(activity);
    }

    private static Application.ActivityLifecycleCallbacks createActivityLifeCycleCallbacks() {
        return new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (!getInstance().enableAutoActivityTracking)
                    getInstance().reportActivityStart(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (!getInstance().enableAutoActivityTracking)
                    getInstance().reportActivityStop(activity);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
    }

    // endregion

    // region tracking events

    public static BloodHound track(@NonNull final ScreenNameProvider screenNameProvider, @StringRes final int categoryId, @StringRes final int actionId, @StringRes final int labelId) {
        return track(screenNameProvider.getScreenName(), categoryId, actionId, getString(labelId));
    }

    private static BloodHound track(String screenName, int categoryId, int actionId, String labelId) {
        return track(screenName, getString(categoryId), getString(actionId), labelId);
    }

    public static BloodHound track(@StringRes final int screenNameId, @StringRes final int categoryId, @StringRes final int actionId, @StringRes final int labelId) {
        return track(screenNameId, categoryId, actionId, getString(labelId));
    }

    public static BloodHound track(@NonNull final String screenName, @StringRes final int categoryId, @StringRes final int actionId, @StringRes final int labelId) {
        return track(screenName, getString(categoryId), getString(actionId), getString(labelId));
    }

    public static BloodHound track(@StringRes final int screenNameId, @StringRes final int categoryId, @StringRes final int actionId, @StringRes final String label) {
        return track(getString(screenNameId), getString(categoryId), getString(actionId), label);
    }

    /**
     * https://developers.google.com/analytics/devguides/collection/android/v4/
     *
     * @param screenName
     * @param category
     * @param action
     * @param label
     */
    public static BloodHound track(@NonNull final String screenName, @NonNull final String category, @NonNull final String action, @StringRes final String label) {
        final BloodHound instance = getInstance();
        ++instance.sessionCounter;
        final EventTracker tracker = event()
                .screenName(screenName)
                .category(category)
                .action(action)
                .label(label);
        if (instance.sessionCounter >= instance.sessionLimit) {
            tracker.setNewSession();
            instance.sessionCounter = 0;
        }
        tracker.track();
        log("[ " + screenName + " | " + category + " | " + action + " | " + label + " ]");
        return instance;
    }

    // endregion

    // region tracking screen

    public static BloodHound track(@NonNull final ScreenNameProvider screenNameProvider) {
        return track(screenNameProvider.getScreenName());
    }

    public static BloodHound track(@StringRes final int screenNameId) {
        return track(getString(screenNameId));
    }

    public static BloodHound track(@NonNull final String screenName) {
        final BloodHound instance = getInstance();
        if (screenName.equals(instance.currentScreen))
            return instance;
        instance.currentScreen = screenName;
        ++instance.sessionCounter;
        final ScreenTracker tracker = screen(instance.currentScreen);
        if (instance.sessionCounter >= instance.sessionLimit) {
            tracker.setNewSession();
            instance.sessionCounter = 0;
        }
        tracker.track();
        log(instance.currentScreen);
        return instance;
    }

    // endregion

    // region tracker

    @NonNull
    private static ScreenTracker screen(final String screenName) {
        return ScreenTracker.from(getInstance().tracker, screenName);
    }

    @NonNull
    private static EventTracker event() {
        return EventTracker.from(getInstance().tracker);
    }

    @NonNull
    private static TimingTracker timing() {
        return TimingTracker.from(getInstance().tracker);
    }

    // endregion
}

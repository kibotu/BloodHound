/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

package net.kibotu.bloodhound

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.annotation.Size
import androidx.appcompat.app.AppCompatActivity
import net.kibotu.bloodhound.internal.firebase.FirebaseAnalyticsTracker
import net.kibotu.bloodhound.internal.firebase.FirebaseOptions
import net.kibotu.bloodhound.internal.gms.GoogleAnalyitcsOptions
import net.kibotu.bloodhound.internal.gms.GoogleAnalyticsTracker
import java.lang.ref.WeakReference

object BloodHound {

    private val TAG = BloodHound::class.java.simpleName

    private var _application: WeakReference<Application>? = null

    internal var context: Application?
        get() = _application?.get()
        set(value) {
            _application = WeakReference(value!!)
        }

    private var _activity: WeakReference<Activity>? = null

    internal var activity: Activity?
        get() = _activity?.get()
        set(value) {
            _activity = WeakReference(value!!)
        }

    private var enableDebugging: Boolean = false

    private var googleAnalyticsTracker: GoogleAnalyticsTracker? = null

    private var firebaseAnalyticsTracker: FirebaseAnalyticsTracker? = null

    // endregion

    @RequiresPermission(allOf = ["android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"])
    fun withGoogleAnalytics(context: Application, trackingId: String, options: GoogleAnalyitcsOptions? = null) {
        this.context = context
        context.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        googleAnalyticsTracker = GoogleAnalyticsTracker.create(context, trackingId, options)
        enableDebugging = options?.enableDebugging == true
        log("[withGoogleAnalytics] with $trackingId")
    }

    @RequiresPermission(allOf = ["android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WAKE_LOCK"])
    fun withFirebase(context: Application, options: FirebaseOptions? = null) {
        this.context = context
        context.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        firebaseAnalyticsTracker = FirebaseAnalyticsTracker.create(context, options)
        enableDebugging = options?.enableDebugging == true
        log("[withFirebase]")
    }

    // region activity tracker

    private val activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks by lazy {
        object : Application.ActivityLifecycleCallbacks {

            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
                if (activity !is AppCompatActivity)
                    return

                BloodHound.activity = activity
                googleAnalyticsTracker?.reportActivityStart(activity)
                firebaseAnalyticsTracker?.reportActivityStart(activity)
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
                if (activity !is AppCompatActivity)
                    return

                BloodHound.activity = activity
                googleAnalyticsTracker?.reportActivityStop(activity)
                firebaseAnalyticsTracker?.reportActivityStop(activity)
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                if (activity !is AppCompatActivity)
                    return

                BloodHound.activity = activity
            }
        }
    }

    // endregion

    /**
     * https://firebase.google.com/docs/analytics/android/events
     * @param screenName Screen Name
     * @parame event see [FirebaseAnalytics.Event][com.google.firebase.analytics.FirebaseAnalytics.Event]
     * @parame arguments see [FirebaseAnalytics.Event][com.google.firebase.analytics.FirebaseAnalytics.Param]
     */
    fun track(screenName: String, @Size(min = 1L, max = 40L) event: String, arguments: Map<String, String>? = null) {

        if (firebaseAnalyticsTracker != null)
            firebaseAnalyticsTracker?.track(screenName, event, arguments)
        else
            Log.w(TAG, "[track] not tracking: initialize firebase tracker BloodHound#withFirebase")
    }

    /**
     * https://developers.google.com/analyticsTracker/devguides/collection/android/v4/
     *
     * @param screenName
     * @param category
     * @param action
     * @param label
     * @param params
     */
    fun track(screenName: String, category: String, action: String, label: String, params: Map<String, String>? = null) {
        if (googleAnalyticsTracker != null)
            googleAnalyticsTracker?.track(screenName, category, action, label, params)
        else
            Log.w(TAG, "[track] not tracking: initialize google analytics tracker BloodHound#withGoogleAnalytics")
    }

    /**
     * https://developers.google.com/analyticsTracker/devguides/collection/android/v4/
     *
     * @param screenName
     */
    fun track(screenName: String) {
        firebaseAnalyticsTracker?.track(screenName)
        googleAnalyticsTracker?.track(screenName)

        if (firebaseAnalyticsTracker == null && googleAnalyticsTracker == null)
            Log.w(TAG, "[track] not tracking: initialize tracker either BloodHound#withGoogleAnalytics and/or BloodHound#withFirebase ")
    }

    fun reset() {
        googleAnalyticsTracker?.reset()
        firebaseAnalyticsTracker?.reset()
    }

    // endregion

    fun log(message: String?) {
        if (enableDebugging)
            Log.v(TAG, message)
    }

    fun onTerminate() = context?.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
}
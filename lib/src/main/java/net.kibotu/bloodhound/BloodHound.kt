/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

package net.kibotu.bloodhound

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Logger.LogLevel.ERROR
import com.google.android.gms.analytics.Logger.LogLevel.VERBOSE
import com.google.android.gms.analytics.Tracker
import net.kibotu.bloodhound.internal.EventTracker
import net.kibotu.bloodhound.internal.ScreenTracker
import net.kibotu.bloodhound.internal.TimingTracker
import java.lang.ref.WeakReference
import java.util.*

object BloodHound {

    private val TAG = BloodHound::class.java.simpleName

    private var _application: WeakReference<Application>? = null

    private var context: Application?
        get() = _application?.get()
        set(value) {
            _application = WeakReference(value!!)
        }

    private lateinit var tracker: Tracker

    private lateinit var analytics: GoogleAnalytics

    private var sessionCounter: Int = 0

    private var currentScreen: String = ""

    // region settings

    private var dryRun: Boolean = false
        set(value) = analytics.setDryRun(value)

    private var exceptionReporting: Boolean = false
        set(value) = tracker.enableExceptionReporting(value)

    private var advertisingIdCollection: Boolean = true
        set(value) = tracker.enableAdvertisingIdCollection(value)

    private var enableAutoActivityTracking: Boolean = false

    private var autoActivityTracking: Boolean = true
        set(value) {
            tracker.enableAutoActivityTracking(value)
            enableAutoActivityTracking = value
        }

    private var sessionTimeout: Long = 300
        set(value) = tracker.setSessionTimeout(value)

    private var sampleRate: Double = 100.0
        set(value) = tracker.setSampleRate(value)

    private var localDispatchPeriod: Int = 500
        set(value) = analytics.setLocalDispatchPeriod(value)

    private var sessionLimit: Int = 0

    private var anonymizeIp: Boolean = true
        set(value) = tracker.setAnonymizeIp(value)

    private var enableDebugging: Boolean = false
        set(value) {
            field = value
            analytics.logger.logLevel = if (value)
                VERBOSE
            else
                ERROR
        }

    // endregion

    // region vars

    @RequiresPermission(allOf = ["android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"])
    fun with(context: Application, trackingId: String, options: TrackingOptions? = null) {
        this.context = context
        analytics = GoogleAnalytics.getInstance(context)
        tracker = analytics.newTracker(trackingId)
        currentScreen = ""
        configure(options)
        log("[init] with $trackingId")
    }

    // endregion

    private fun configure(options: TrackingOptions?) {
        context!!.registerActivityLifecycleCallbacks(createActivityLifeCycleCallbacks())

        options?.let {
            enableDebugging = it.enableDebugging
            exceptionReporting = it.exceptionReporting
            advertisingIdCollection = it.advertisingIdCollection
            autoActivityTracking = it.autoActivityTracking
            sessionTimeout = it.sessionTimeout
            sampleRate = it.sampleRate
            sessionLimit = it.sessionLimit
            dryRun = it.dryRun
            anonymizeIp = it.anonymizeIp
        }

        localDispatchPeriod = if (enableDebugging)
            15
        else
            300

        with(tracker) {
            setAppVersion("${context!!.versionName} (${context!!.versionCode})")
            setAppName(context!!.packageName)
            setLanguage(Locale.getDefault().displayLanguage)
        }
    }

    // endregion

    // region tracking activity start and stop

    private fun reportActivityStart(activity: Activity) {
        if (!enableAutoActivityTracking)
            track(activity.ApplicationName)
    }

    private fun reportActivityStop(activity: Activity) {
        if (!enableAutoActivityTracking)
            GoogleAnalytics.getInstance(activity).reportActivityStop(activity)
    }

    // region helper

    private fun log(message: String?) {
        if (enableDebugging)
            Log.v(TAG, message)
    }

    private fun createActivityLifeCycleCallbacks(): Application.ActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {

        override fun onActivityPaused(activity: Activity?) {
        }

        override fun onActivityResumed(activity: Activity?) {
        }

        override fun onActivityStarted(activity: Activity?) {
            if (!enableAutoActivityTracking)
                reportActivityStart(activity ?: return)
        }

        override fun onActivityDestroyed(activity: Activity?) {
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
            if (!enableAutoActivityTracking)
                reportActivityStop(activity ?: return)
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        }
    }

    // endregion

    /**
     * https://developers.google.com/analytics/devguides/collection/android/v4/advanced#end-user-deletion
     */
    fun deleteGoogleAnalyticsClientSideData() = context!!.deleteFile("gaClientId")

    // region tracking events

    /**
     * https://developers.google.com/analytics/devguides/collection/android/v4/
     *
     * @param screenName
     * @param category
     * @param action
     * @param label
     * @param params
     */
    fun track(screenName: String, category: String, action: String, label: String, params: Map<String, String>? = null) {
        ++sessionCounter
        val tracker = event()
                .screenName(screenName)
                .category(category)
                .action(action)
                .label(label)

        params?.forEach { (key, value) -> tracker.value(key, value) }

        if (sessionCounter >= sessionLimit) {
            tracker.setNewSession()
            sessionCounter = 0
        }
        tracker.track()
        log("[ $screenName | $category | $action | $label | $params ]")
    }

    // endregion

    // region tracking screen

    fun track(screenName: String) {
        if (screenName == currentScreen)
            return
        currentScreen = screenName
        ++sessionCounter
        val tracker = screen(currentScreen)
        if (sessionCounter >= sessionLimit) {
            tracker.setNewSession()
            sessionCounter = 0
        }
        tracker.track()
        log(currentScreen)
    }

    // endregion

    // region tracker

    private fun screen(screenName: String): ScreenTracker = ScreenTracker(tracker, screenName)

    private fun event(): EventTracker = EventTracker(tracker)

    private fun timing(): TimingTracker = TimingTracker(tracker)

    // endregion
}

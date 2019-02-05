/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

package net.kibotu.bloodhound.internal.gms

import android.app.Activity
import android.app.Application
import androidx.annotation.RequiresPermission
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Logger
import com.google.android.gms.analytics.Tracker
import net.kibotu.bloodhound.BloodHound
import net.kibotu.bloodhound.internal.AnalyticsTracker
import net.kibotu.bloodhound.versionCode
import net.kibotu.bloodhound.versionName
import java.util.*


internal class GoogleAnalyticsTracker private constructor() : AnalyticsTracker {

    companion object {
        @RequiresPermission(allOf = ["android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"])
        fun create(context: Application, trackingId: String, options: GoogleAnalyitcsOptions? = null): GoogleAnalyticsTracker = GoogleAnalyticsTracker().apply {
            analytics = GoogleAnalytics.getInstance(context)
            currentScreen = ""
            tracker = analytics.newTracker(trackingId)
            configure(options)
        }
    }

    private lateinit var tracker: Tracker

    private lateinit var analytics: GoogleAnalytics

    private var sessionCounter: Int = 0

    // region settings

    private var currentScreen: String = ""

    private var dryRun: Boolean = false
        set(value) = analytics.setDryRun(value)

    private var exceptionReporting: Boolean = false
        set(value) = tracker.enableExceptionReporting(value)

    private var advertisingIdCollection: Boolean = true
        set(value) = tracker.enableAdvertisingIdCollection(value)

    private var autoActivityTracking: Boolean = true

    private var sessionTimeout: Long = 300
        set(value) = tracker.setSessionTimeout(value)

    private var sampleRate: Double = 100.0
        set(value) = tracker.setSampleRate(value)

    private var localDispatchPeriod: Int = 500
        set(value) = analytics.setLocalDispatchPeriod(value)

    private var sessionLimit: Int = 0

    private var anonymizeIp: Boolean = true
        set(value) = tracker.setAnonymizeIp(value)

    private var enableLogging: Boolean = false
        set(value) {
            field = value
            analytics.logger.logLevel = if (value)
                Logger.LogLevel.VERBOSE
            else
                Logger.LogLevel.ERROR
        }

    // endregion

    private fun configure(options: GoogleAnalyitcsOptions?) {

        options?.let {
            enableLogging = it.enableLogging
            exceptionReporting = it.exceptionReporting
            advertisingIdCollection = it.advertisingIdCollection
            autoActivityTracking = it.autoActivityTracking
            sessionTimeout = it.sessionTimeout
            sampleRate = it.sampleRate
            sessionLimit = it.sessionLimit
            dryRun = it.dryRun
            anonymizeIp = it.anonymizeIp
        }

        localDispatchPeriod = if (options?.enableDebugging == true)
            15
        else
            300

        with(tracker) {
            setAppVersion("${BloodHound.context!!.versionName} (${BloodHound.context!!.versionCode})")
            setAppName(BloodHound.context!!.packageName)
            setLanguage(Locale.getDefault().displayLanguage)
        }
    }

    override fun reportActivityStart(activity: Activity?) {
        if (!autoActivityTracking)
            analytics.reportActivityStart(activity ?: return)
    }

    override fun reportActivityStop(activity: Activity?) {
        if (autoActivityTracking)
            analytics.reportActivityStop(activity ?: return)
    }

    /**
     * https://developers.google.com/analyticsTracker/devguides/collection/android/v4/advanced#end-user-deletion
     */
    override fun reset() {
        BloodHound.context!!.deleteFile("gaClientId")
    }

    // region tracking events

    /**
     * https://developers.google.com/analyticsTracker/devguides/collection/android/v4/
     *
     * @param screenName
     * @param category
     * @param action
     * @param label
     * @param params
     */
    fun track(category: String, action: String, label: String, params: Map<String, String>? = null) {
        ++sessionCounter
        val tracker = event()
                .screenName(currentScreen)
                .category(category)
                .action(action)
                .label(label)

        params?.forEach { (key, value) -> tracker.value(key, value) }

        if (sessionCounter >= sessionLimit) {
            tracker.setNewSession()
            sessionCounter = 0
        }
        tracker.track()
        BloodHound.log("[ $currentScreen | $category | $action | $label | $params ]")
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
        BloodHound.log(currentScreen)
    }

    // endregion

    // region tracker

    private fun screen(screenName: String): ScreenTracker = ScreenTracker(tracker, screenName)

    private fun event(): EventTracker = EventTracker(tracker)

    private fun timing(): TimingTracker = TimingTracker(tracker)

    // endregion
}
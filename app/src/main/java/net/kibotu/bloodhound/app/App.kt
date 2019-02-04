/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

package net.kibotu.bloodhound.app

import android.app.Application
import net.kibotu.bloodhound.BloodHound
import net.kibotu.bloodhound.TrackingOptions
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        BloodHound.with(this, getString(R.string.google_analytics_tracking_id), TrackingOptions(
                enableDebugging = true,
                exceptionReporting = false,
                advertisingIdCollection = true,
                autoActivityTracking = false,
                sessionTimeout = 300,
                sampleRate = 100.0,
                sessionLimit = 500,
                dryRun = false,
                anonymizeIp = true
        ))

        // tracking screen
        BloodHound.track("main_screen")

        // tracking events
        BloodHound.track("main_screen", "category", "action", "app_start",
                mapOf("user" to UUID.randomUUID().toString())) // additional params

        // reset client, e.g. logout
        BloodHound.deleteGoogleAnalyticsClientSideData()
    }
}
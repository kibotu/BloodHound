/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

package net.kibotu.bloodhound.app

import android.app.Application
import net.kibotu.bloodhound.BloodHound
import net.kibotu.bloodhound.internal.firebase.FirebaseOptions
import net.kibotu.bloodhound.internal.gms.GoogleAnalyitcsOptions

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        trackWithFirebase()
        trackWithGoogleAnalytics()
    }

    private fun trackWithFirebase() {
        BloodHound.withFirebase(this, FirebaseOptions(
                enableDebugging = true,
                enableLogging = true
        ))
    }

    private fun trackWithGoogleAnalytics() {
        BloodHound.withGoogleAnalytics(this, getString(R.string.google_analytics_tracking_id), GoogleAnalyitcsOptions(
                enableDebugging = true,
                enableLogging = true,
                exceptionReporting = false,
                advertisingIdCollection = true,
                autoActivityTracking = false,
                sessionTimeout = 300,
                sampleRate = 100.0,
                sessionLimit = 500,
                dryRun = false,
                anonymizeIp = true
        ))
    }

    override fun onTerminate() {
        super.onTerminate()
        BloodHound.onTerminate()
    }
}
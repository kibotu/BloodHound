/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

package net.kibotu.bloodhound

data class TrackingOptions(
        var enableDebugging: Boolean = true,
        var exceptionReporting: Boolean = false,
        var advertisingIdCollection: Boolean = true,
        var autoActivityTracking: Boolean = false,
        var sessionTimeout: Long = 300,
        var sampleRate: Double = 100.0,
        var sessionLimit: Int = 500,
        var dryRun: Boolean = false,
        var anonymizeIp: Boolean = true
)
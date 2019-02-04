/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

package net.kibotu.bloodhound.internal

import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker

internal class ScreenTracker(private val tracker: Tracker, private val screenName: String) {

    private val builder: HitBuilders.ScreenViewBuilder = HitBuilders.ScreenViewBuilder()

    fun track() {
        tracker.setScreenName(screenName)
        tracker.send(builder.build())
        tracker.setScreenName(null)
    }

    fun setNewSession() {
        builder.setNewSession()
    }
}
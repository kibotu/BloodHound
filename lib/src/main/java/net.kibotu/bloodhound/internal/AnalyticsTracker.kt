/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

package net.kibotu.bloodhound.internal

import android.app.Activity

internal interface AnalyticsTracker {

    fun reportActivityStart(activity: Activity?)

    fun reportActivityStop(activity: Activity?)

    fun reset()
}
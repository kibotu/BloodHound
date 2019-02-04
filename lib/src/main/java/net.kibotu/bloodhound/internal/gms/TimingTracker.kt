/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

package net.kibotu.bloodhound.internal.gms

import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker

internal class TimingTracker(private val tracker: Tracker) {

    private val builder: HitBuilders.TimingBuilder = HitBuilders.TimingBuilder()

    private var screenName: String? = null

    private var dimensions = mutableListOf<Dimension>()

    fun screenName(screenName: String): TimingTracker {
        this.screenName = screenName
        return this
    }

    fun category(category: String): TimingTracker {
        builder.setCategory(category)
        return this
    }

    fun variable(variable: String): TimingTracker {
        builder.setVariable(variable)
        return this
    }

    fun label(label: String?): TimingTracker {
        if (label != null) {
            builder.setLabel(label)
        }
        return this
    }

    fun value(timeInMillis: Long): TimingTracker {
        builder.setValue(timeInMillis)
        return this
    }

    fun value(key: String, value: String): TimingTracker {
        builder.set(key, value)
        return this
    }

    fun customDimension(dimension: Dimension): TimingTracker {
        dimensions.add(dimension)
        return this
    }

    fun track() {
        for ((index, value) in dimensions) {
            builder.setCustomDimension(index, value)
        }
        tracker.setScreenName(screenName)
        tracker.send(builder.build())
        tracker.setScreenName(null)
    }
}
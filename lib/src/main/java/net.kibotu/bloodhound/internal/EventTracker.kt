/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

package net.kibotu.bloodhound.internal

import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.analytics.ecommerce.Product
import com.google.android.gms.analytics.ecommerce.ProductAction

internal class EventTracker(private val tracker: Tracker) {

    private val builder: HitBuilders.EventBuilder = HitBuilders.EventBuilder()

    private var screenName: String? = null

    private var dimensions = mutableListOf<Dimension>()

    fun screenName(screenName: String): EventTracker {
        this.screenName = screenName
        return this
    }

    fun category(category: String): EventTracker {
        builder.setCategory(category)
        return this
    }

    fun action(action: String): EventTracker {
        builder.setAction(action)
        return this
    }

    fun label(label: String?): EventTracker {
        if (label != null) {
            builder.setLabel(label)
        }
        return this
    }

    fun value(value: Long): EventTracker {
        builder.setValue(value)
        return this
    }

    fun value(key: String, value: String): EventTracker {
        builder.set(key, value)
        return this
    }

    fun addProduct(product: Product): EventTracker {
        builder.addProduct(product)
        return this
    }

    fun productAction(productAction: ProductAction): EventTracker {
        builder.setProductAction(productAction)
        return this
    }

    fun customDimension(dimension: Dimension): EventTracker {
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

    fun setNewSession() {
        builder.setNewSession()
    }
}
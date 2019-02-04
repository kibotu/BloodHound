/*
 * Created by [Jan Rabe](https://about.me/janrabe)
 */

@file:JvmName("Extensions")

package net.kibotu.bloodhound

import android.annotation.TargetApi
import android.content.Context
import android.os.Build

internal val Context.ApplicationName: String
    get() = applicationInfo.loadLabel(packageManager).toString()

internal val Context.PackageName: String
    get() = applicationInfo.packageName

internal val Context.versionName: String
    get() = packageManager.getPackageInfo(packageName, 0).packageName

internal val Context.versionCode: Int
    get() = packageManager.getPackageInfo(packageName, 0).versionCode

internal val Context.longVersionCode: Long
    @TargetApi(Build.VERSION_CODES.P)
    get() = packageManager.getPackageInfo(packageName, 0).longVersionCode
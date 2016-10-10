package net.kibotu.android.bloodhound.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

public class Version {

    private String versionName;
    private int versionCode;

    public Version(@NonNull final Context context) {
        getVersionInfo(context);
    }

    private void getVersionInfo(@NonNull final Context context) {
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (@NonNull final PackageManager.NameNotFoundException e) {
            Log.wtf("PackageManager", e);
        }
    }

    private String getVersionName() {
        return versionName;
    }

    private int getVersionCode() {
        return versionCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        if (versionCode != version.versionCode) return false;
        return versionName != null ? versionName.equals(version.versionName) : version.versionName == null;

    }

    @Override
    public int hashCode() {
        int result = versionName != null ? versionName.hashCode() : 0;
        result = 31 * result + versionCode;
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return getVersionName() + " (" + getVersionCode() + ")";
    }
}
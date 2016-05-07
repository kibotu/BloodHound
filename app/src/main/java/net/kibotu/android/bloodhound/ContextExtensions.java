package net.kibotu.android.bloodhound;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Nyaruhodo on 07.05.2016.
 */
public class ContextExtensions {

    public static String getApplicationName(@NonNull final Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public static String getPackageName(@NonNull final Context context) {
        return context.getApplicationInfo().packageName;
    }
}

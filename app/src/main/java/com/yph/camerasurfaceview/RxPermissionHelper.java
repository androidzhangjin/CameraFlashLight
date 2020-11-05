package com.yph.camerasurfaceview;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;


public class RxPermissionHelper {

    public interface PermissionGetListener {
        void onSuccess();

        void onFailed();
    }

    public static void requesPermission(FragmentActivity activity, PermissionGetListener listener, String... perms) {
        new RxPermissions(activity)
                .request(perms)
                .subscribe(granted -> {
                    if (granted) {
                        listener.onSuccess();
                    } else {
                        listener.onFailed();
                    }
                }, Throwable::printStackTrace);
    }
}

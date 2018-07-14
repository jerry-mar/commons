package com.jerry_mar.commons;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil implements Runnable {
    private static Toast toast;
    private static Handler handler;
    private final static long duration;
    private final static ToastUtil task;
    private final static int resid;
    private final static int messageID;

    static {
        handler = new Handler(Looper.getMainLooper());
        task = new ToastUtil();
        duration = 2000;
        Resources resources = Resources.getSystem();
        resid = resources.getIdentifier("transient_notification", "layout", "android");
        messageID = resources.getIdentifier("message", "id", "android");
    }

    public static void show(Context context, String message) {
        show(context, message, duration);
    }

    public static void show(Context context, String message, long duration) {
        if (toast != null) {
            handler.removeCallbacks(task);
            toast.cancel();
        }
        LayoutInflater inflate = LayoutInflater.from(context);
        View view = inflate.inflate(resid, null);
        toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);

        TextView tv = (TextView) view.findViewById(messageID);
        tv.setText(message);
        toast.show();
        handler.postDelayed(task, duration);
    }

    public static void hide() {
        if(toast != null) {
            toast.cancel();
            toast = null;
        }
    }

    @Override
    public void run() {
        hide();
    }
}

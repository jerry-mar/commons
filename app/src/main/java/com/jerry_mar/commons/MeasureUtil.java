package com.jerry_mar.commons;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class MeasureUtil {
    private static int STATUS_HEIGHT;
    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;

    /**
     * @since 1.0
     * @param context 上下文
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        if (STATUS_HEIGHT == 0) {
            try {
                int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    STATUS_HEIGHT = context.getResources().getDimensionPixelSize(resourceId);
                }
            } catch (Resources.NotFoundException e) {
                STATUS_HEIGHT = 0;
            }
        }
        return STATUS_HEIGHT;
    }

    /**
     * @since 1.0
     * @param context 上下文
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        if (SCREEN_WIDTH == 0) {
            initScreenSize(context);
        }
        return SCREEN_WIDTH;
    }

    /**
     * @since 1.0
     * @param context 上下文
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        if (SCREEN_HEIGHT == 0) {
            initScreenSize(context);
        }
        return SCREEN_HEIGHT;
    }

    private static void initScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        try {
            Point point = new Point();
            Display.class.getMethod("getRealSize", Point.class)
                    .invoke(display, point);
            SCREEN_WIDTH = point.x;
            SCREEN_HEIGHT = point.y;
        } catch (Exception ignored) {
            SCREEN_WIDTH = metrics.widthPixels;
            SCREEN_HEIGHT = metrics.heightPixels;
        }
    }

    /**
     * @since 1.0
     * @param context 上下文
     * @param type 屏幕方向
     * @return 内容区域宽度
     */
    public static int getContentWidth(Context context, int type) {
        int result;
        if(Configuration.ORIENTATION_PORTRAIT == type) {
            result = getScreenWidth(context);
        } else {
            result = getScreenHeight(context);
        }
        return result;
    }

    /**
     * @since 1.0
     * @param context 上下文
     * @param type 屏幕方向
     * @return 内容区域高度
     */
    public static int getContentHeight(Context context, int type) {
        int result;
        if(Configuration.ORIENTATION_PORTRAIT == type) {
            result = getScreenHeight(context) - getStatusBarHeight(context);
        } else {
            result = getScreenWidth(context) - getStatusBarHeight(context);
        }
        return result;
    }

    /**
     * @since 1.0
     * @param context 上下文
     * @return 书评内容区域宽度
     */
    public static int getContentWidth(Context context) {
        return getContentWidth(context, Configuration.ORIENTATION_PORTRAIT);
    }

    /**
     * @since 1.0
     * @param context 上下文
     * @return 书评内容区域高度
     */
    public static int getContentHeight(Context context) {
        return getContentHeight(context, Configuration.ORIENTATION_PORTRAIT);
    }

    /**
     * @since 1.0
     * @param context 上下文
     * @param dp 尺寸
     * @return 实际px尺寸
     */
    protected int dp2px(Context context, int dp){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f * (dp >= 0 ? 1 : -1));
    }

    /**
     * @since 1.0
     * @param context 上下文
     * @param px 尺寸
     * @return 屏幕dp尺寸
     */
    protected int px2dp(Context context, int px){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(px / scale + 0.5f * (px >= 0 ? 1 : -1));
    }
}

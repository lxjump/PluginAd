package com.mess.ad.Utils;

import android.content.Context;


public class IdentifierGetter {

    private final static String style = "style";
    private final static String color = "color";
    private final static String drawable = "drawable";
    private final static String layout = "layout";
    private final static String id = "id";
    private final static String string = "string";
    private final static String anim = "anim";
    public static int getIndentifier(Context context, String str, String str2) {
        return context.getResources().getIdentifier(str, str2, context.getPackageName());
    }

    public static int getLayoutIdentifier(Context context, String str) {
        return getIndentifier(context, str, layout);
    }

    public static int getIDIdentifier(Context context, String str) {
        return getIndentifier(context, str, id);
    }

    public static int getStyleIdentifier(Context context, String str) {
        return getIndentifier(context, str, style);
    }

    public static int getDrawableIdentifier(Context context, String str) {
        return getIndentifier(context, str, drawable);
    }

    public static int getStringIdentifier(Context context, String str) {
        return getIndentifier(context, str, string);
    }

    public static int getColorIdentifier(Context context, String str) {
        return getIndentifier(context, str, color);
    }

}

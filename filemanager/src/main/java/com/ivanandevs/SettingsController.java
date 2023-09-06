package com.ivanandevs;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsController {
    private Context context;
    private SharedPreferences settingsPreferences;

    public static SettingsController getInstance(Context context) {
        return new SettingsController(context);
    }

    private SettingsController(Context context) {
        settingsPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
    }

    public void set(String key, int value) {
        setString(key, value + "");
    }

    public void set(String key, boolean value) {
        setString(key, value + "");
    }

    public void set(String key, float value) {
        setString(key, value + "");
    }

    public void set(String key, double value) {
        setString(key, value + "");
    }

    public void set(String key, long value) {
        setString(key, value + "");
    }

    public void set(String key, String value) {
        setString(key, value);
    }

    public int get(String key, int defaultValue) {
        return Integer.parseInt(get(key, defaultValue + ""));
    }

    public boolean get(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, defaultValue + ""));
    }

    public float get(String key, float defaultValue) {
        return Float.parseFloat(get(key, defaultValue + ""));
    }

    public double get(String key, double defaultValue) {
        return Double.parseDouble(get(key, defaultValue + ""));
    }

    public long get(String key, long defaultValue) {
        return Long.parseLong(get(key, defaultValue + ""));
    }

    public String get(String key, String defaultValue) {
        return getString(key, defaultValue);
    }

    public void setString(String key, String value) {
        if (key.equals("ProfileJWT") || key.equals("Language") || key.equals("DarkTheme")) {
            settingsPreferences.edit().putString(key, value).commit();
        } else {
            settingsPreferences.edit().putString(key, encrypt(value + "")).commit();
        }
    }

    public String getString(String key, String defaultValue) {
        if (key.equals("ProfileJWT") || key.equals("Language") || key.equals("DarkTheme")) {
            return settingsPreferences.getString(key, defaultValue);
        }
        return decrypt(settingsPreferences.getString(key, encrypt(defaultValue + "") + ""));
    }

    public String encrypt(String string) {
        return string;
    }

    public String decrypt(String string) {
        return string;
    }

}

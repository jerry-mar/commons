package com.jerry_mar.commons;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StorageUtil {
    private static Context applicationContext;
    private static Map<String, SharedPreferences> preferences;
    private static Map<String, SharedPreferences.Editor> editors;

    private SharedPreferences reader;
    private SharedPreferences.Editor writer;

    public static StorageUtil getStorage(String name) {
        return getStorage(AppUtil.getApplicationContext(), name);
    }

    public static StorageUtil getStorage(Context context, String name) {
        if (applicationContext == null) {
            synchronized (StorageUtil.class) {
                if (applicationContext == null) {
                    applicationContext = context.getApplicationContext();
                    StorageUtil.preferences = new HashMap<>();
                    StorageUtil.editors = new HashMap<>();
                }
            }
        }
        return new StorageUtil(name);
    }

    private StorageUtil(String name) {
        reader = preferences.get(name);
        writer = editors.get(name);
        if (reader == null || writer == null) {
            synchronized (StorageUtil.class) {
                reader = preferences.get(name);
                writer = editors.get(name);
                if (reader == null || writer == null) {
                    reader = applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE);
                    writer = reader.edit();
                    preferences.put(name, reader);
                    editors.put(name, writer);
                }
            }
        }
    }

    public <T> T getBean(String name, Class<T> cls) {
        String value = getString(name, null);
        T obj = null;
        if (value != null) {
            obj = JSON.parseObject(value, cls);
        }
        return obj;
    }

    public String getString(String name) {
        return getString(name, null);
    }

    public String getString(String name, String defValue) {
        return reader.getString(name, defValue);
    }

    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public boolean getBoolean(String name, boolean defValue) {
        return reader.getBoolean(name, defValue);
    }

    public int getInt(String name) {
        return getInt(name, 0);
    }

    public int getInt(String name, int defValue) {
        return reader.getInt(name, defValue);
    }

    public float getFloat(String name) {
        return getFloat(name, 0);
    }

    public float getFloat(String name, float defValue) {
        return reader.getFloat(name, defValue);
    }

    public long getLong(String name) {
        return getLong(name, 0);
    }

    public long getLong(String name, long defValue) {
        return reader.getLong(name, defValue);
    }

    public Set<String> getStringSet(String name) {
        return getStringSet(name, null);
    }

    public Set<String> getStringSet(String name, Set<String> defValue) {
        return reader.getStringSet(name, defValue);
    }

    public Map<String, ?> getAll() {
        return reader.getAll();
    }

    public StorageUtil remove(String name) {
        writer.remove(name);
        return this;
    }

    public StorageUtil clear() {
        writer.clear();
        return this;
    }

    public StorageUtil putBean(String name, Object obj) {
        String value = JSON.toJSONString(obj, StringUtil.features);
        writer.putString(name, value);
        return this;
    }

    public StorageUtil putString(String name, String value) {
        writer.putString(name, value);
        return this;
    }

    public StorageUtil putBoolean(String name, boolean value) {
        writer.putBoolean(name, value);
        return this;
    }

    public StorageUtil putInt(String name, int value) {
        writer.putInt(name, value);
        return this;
    }

    public StorageUtil putFloat(String name, float value) {
        writer.putFloat(name, value);
        return this;
    }

    public StorageUtil putLong(String name, long value) {
        writer.putLong(name, value);
        return this;
    }

    public StorageUtil putStringSet(String name, Set<String> value) {
        writer.putStringSet(name, value);
        return this;
    }

    public StorageUtil apply() {
        writer.apply();
        return this;
    }

    public StorageUtil commit() {
        writer.commit();
        return this;
    }
}

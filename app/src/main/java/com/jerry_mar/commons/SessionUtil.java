package com.jerry_mar.commons;

import com.jerry_mar.commons.subscriber.Observable;
import com.jerry_mar.commons.subscriber.Observer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SessionUtil implements Observable, Observer {
    private static final String CACHE_DATA_MONITORED = "cache_data_monitored";
    private static final String CACHE_DATA_RUNTIME = "cache_data_runtime";
    private volatile static SessionUtil session;
    private Map<String, Object> monitoreData;
    private Map<String, Object> runtimeData;
    private List<Observer> observers;
    private boolean atomic;

    private SessionUtil() {
        monitoreData = new HashMap<>();
        runtimeData = new HashMap<>();
        observers = new LinkedList<>();
    }

    public synchronized static SessionUtil get() {
        if (session == null) {
            session = new SessionUtil();
            restore();
        }
        return session;
    }

    public static  <T> T get(String name) {
        SessionUtil session = get();
        return (T) session.monitoreData.get(name);
    }

    public static <T> T getData(String name) {
        SessionUtil session = get();
        return (T) session.runtimeData.get(name);
    }

    public static void put(String name, Object target) {
        SessionUtil session = get();
        Object obj = session.monitoreData.get(name);
        if (obj == null || !obj.equals(target)) {
            session.monitoreData.put(name, target);
            if (target instanceof Observable) {
                ((Observable) target).bind(session);
                ((Observable) target).update(session, name, target);
            } else {
                session.update(name, target);
            }
        }
    }

    public static void putData(String name, Object target) {
        SessionUtil session = get();
        session.runtimeData.put(name, target);
    }

    public static <T> T remove(String name) {
        SessionUtil session = get();
        T result = (T) session.monitoreData.remove(name);
        if (result instanceof Observable) {
            ((Observable) result).unbind(session);
        }
        return result;
    }

    public static <T> T removeData(String name) {
        SessionUtil session = get();
        return (T) session.runtimeData.remove(name);
    }

    public static void register(Observer o) {
        SessionUtil session = get();
        session.observers.add(o);
    }

    public static void unregister(Observer o) {
        SessionUtil session = get();
        session.observers.add(o);
    }

    @Override
    public synchronized void bind(Observer o) {
        observers.add(o);
    }

    @Override
    public synchronized void unbind(Observer o) {
        observers.remove(o);
    }

    @Override
    public void update(String name, Object target) {
        Observer[] temp = null;
        synchronized (this) {
            if (!atomic) {
                atomic = true;
                temp = observers.toArray(new Observer[observers.size()]);
                atomic = false;
            }
        }

        if (temp != null) {
            for (int i = temp.length - 1; i >= 0; i--)
                temp[i].urgentNotify(name, target);
        }
    }

    @Override
    public void update(Observer o, String name, Object arg) {
        o.urgentNotify(name, arg);
    }

    @Override
    public synchronized void clear() {
        observers.clear();
    }

    @Override
    public void urgentNotify(String name, Object obj) {
        update(name, obj);
    }

    public static void save() {
        save(session.monitoreData, CACHE_DATA_MONITORED);
        save(session.runtimeData, CACHE_DATA_RUNTIME);
    }

    private static void save(Map<String, Object> data, String name) {
        StorageUtil storage = StorageUtil.getStorage(name);
        storage.clear();
        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value instanceof String) {
                storage.putString(key, value.toString());
            } else {
                storage.putBean(key + "@" + value.getClass(), value);
            }
        }
        storage.apply();
    }

    public static void restore() {
        restore(session.monitoreData, CACHE_DATA_MONITORED);
        restore(session.runtimeData, CACHE_DATA_RUNTIME);
    }

    private static void restore(Map<String, Object> data, String name) {
        StorageUtil storage = StorageUtil.getStorage(name);
        Map<String, String> cache = (Map<String, String>) storage.getAll();
        for (String key : cache.keySet()) {
            int index = key.indexOf('@');
            if (index == -1) {
                data.put(key, storage.getString(key));
            } else {
                String keyName = key.substring(0, index);
                String clsName = key.substring(index + 1);
                Object obj;
                try {
                    obj = storage.getBean(keyName, Class.forName(clsName));
                } catch (ClassNotFoundException e) {
                    continue;
                }
                data.put(key, obj);
            }
        }
        storage.clear().apply();
    }
}

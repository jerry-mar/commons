package com.jerry_mar.commons.subscriber;

public interface Observable {
    void bind(Observer o);
    void unbind(Observer o);
    void update(String name, Object arg);
    void update(Observer o, String name, Object arg);
    void clear();
}

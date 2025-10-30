package io.tmgg.web.persistence;

public interface BaseDto<T> {
    public void parse(T t);
}

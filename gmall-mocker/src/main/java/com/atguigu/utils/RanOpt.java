package com.atguigu.utils;

/**
 * @author Chijago
 * @create 2020-02-18 20:25
 */
public class RanOpt<T> {

    private T value;
    private int weight;

    public RanOpt(T value, int weight) {
        this.value = value;
        this.weight = weight;
    }

    public T getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }
}

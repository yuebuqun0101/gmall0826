package com.atguigu.utils;

import java.util.Random;

/**
 * @author Chijago
 * @create 2020-02-18 20:39
 */
public class RandomNum {
    public static int getRandInt(int fromNum, int toNum) {
        return fromNum + new Random().nextInt(toNum - fromNum + 1);
    }
}

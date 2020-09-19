package com.pfoss.countdownlivewallpaper.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class RandomBeautifulColor {

    private Stack<Integer> recycle, colors;

    public RandomBeautifulColor() {
        colors = new Stack<>();
        recycle = new Stack<>();
        recycle.addAll(Arrays.asList(
                0xfff44336, 0xffe91e63, 0xff9c27b0, 0xff673ab7,
                0xff3f51b5, 0xff2196f3, 0xff03a9f4, 0xff00bcd4,
                0xff009688, 0xff4caf50, 0xff8bc34a, 0xffcddc39,
                0xffffeb3b, 0xffffc107, 0xffff9800, 0xffff5722,
                0xff795548, 0xff9e9e9e, 0xff607d8b, 0xff333333
                )
        );
    }

    public int getColor() {
        if (colors.size() == 0) {
            while (!recycle.isEmpty())
                colors.push(recycle.pop());
            Collections.shuffle(colors);
        }
        Integer c = colors.pop();
        recycle.push(c);
        return c;
    }
}


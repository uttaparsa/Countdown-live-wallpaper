package com.pfoss.countdownlivewallpaper.data;

public enum BackgroundTheme{
    GRADIENT ("GR",0),
    PICTURE ("PIC",1),
    SOLID ("S",2);

    private final String name;
    private final int value;

    BackgroundTheme(String s , int Value) {
        name = s;
        value = Value;
    }

    public static BackgroundTheme getValueOf(String s){
        for (BackgroundTheme bt: BackgroundTheme.values()
             ) {
            if(s.equals(bt.toString())) return bt;
        }
        return null;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public final int getValue() {
        return value;
    }

    public String toString() {
        return this.name;
    }
}

package com.pfoss.countdownlivewallpaper.utils;


import android.graphics.Color;

public enum UnitType  {
   YEAR ("year"),
   MONTH ("month"),
   DAY ("day"),
   HOUR ("hour"),
   MINUTE ("minute"),
   SECOND ("second");
   private final String name;

   private UnitType(String s) {
      name = s;
   }
   public String toString() {
      return this.name;
   }

}

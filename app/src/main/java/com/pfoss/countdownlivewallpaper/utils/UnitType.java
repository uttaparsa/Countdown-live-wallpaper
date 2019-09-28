package com.pfoss.countdownlivewallpaper.utils;


import android.graphics.Color;

import com.pfoss.countdownlivewallpaper.R;

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
      return  this.name;
   }
   public int getStringResource(){
      switch (this.name){
         case "year":
            return R.string.year;
         case "month":
            return R.string.month;
         case "day":
            return R.string.day;
         case "hour":
            return R.string.hour;
         case "minute":
            return R.string.minute;
         case "second":
            return R.string.second;
         default:return -1;
      }
   }

    public int getUnitInSecond() {
       switch (this.name){
          case "year":
             return 31556952;
          case "month":
             return 2629746;
          case "day":
             return 86400;
          case "hour":
             return 3600;
          case "minute":
             return 60;
          case "second":
             return 1;
          default:return -1;
       }
    }

}

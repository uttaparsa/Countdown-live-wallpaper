package com.pfoss.countdownlivewallpaper;

import android.media.Image;

import java.io.Serializable;
import java.util.Date;

public class TimerRecord  implements Serializable {
    private Image backgroundPicture;
    private String label;
    private boolean priorToShow;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setBackgroundPicture(Image backgroundPicture) {
        this.backgroundPicture = backgroundPicture;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isPriorToShow() {
        return priorToShow;
    }

    public void setPriorToShow(boolean priorToShow) {
        this.priorToShow = priorToShow;
    }
}

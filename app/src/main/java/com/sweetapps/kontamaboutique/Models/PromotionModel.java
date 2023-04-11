package com.sweetapps.kontamaboutique.Models;

import com.google.firebase.firestore.Exclude;

public class PromotionModel {

    public final int TYPE_LISTED = 1;

    String promoId;
    String promoCode;
    String promoImg;
    long timeStamp;
    long promoStartDate;
    long promoEndDate;
    int promoType;
    boolean isRunning;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Exclude
    public String getPromoId() {
        return promoId;
    }

    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getPromoImg() {
        return promoImg;
    }

    public void setPromoImg(String promoImg) {
        this.promoImg = promoImg;
    }

    public long getPromoStartDate() {
        return promoStartDate;
    }

    public void setPromoStartDate(long promoStartDate) {
        this.promoStartDate = promoStartDate;
    }

    public long getPromoEndDate() {
        return promoEndDate;
    }

    public void setPromoEndDate(long promoEndDate) {
        this.promoEndDate = promoEndDate;
    }

    public int getPromoType() {
        return promoType;
    }

    public void setPromoType(int promoType) {
        this.promoType = promoType;
    }
}

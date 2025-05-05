package com.mess.ad.sdk.bean;

import java.util.List;

public class AdInfo {

    List<String> adIds;
    int adIndex;
    AdType adType;
    String appId;
    String appKey;
    int bannerGravity;
    List<String> blackCity;
    int iconAdBottomMargin;
    int iconAdLeftMargin;
    boolean isTimingPower;
    long showInterval;
    long showStart;
    long showTimingInterval;
    long showTimingStart;

    /* loaded from: classes3.dex */
    public enum AdType {
        BANNER,
        INTER,
        FULL,
        VIDEO,
        SPLASH,
        ICON
    }

    public AdType getAdType() {
        return this.adType;
    }

    public void setAdType(AdType adType) {
        this.adType = adType;
    }

    public String getAppId() {
        return this.appId;
    }

    public List<String> getBlackCity() {
        return this.blackCity;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setAppKey(String str) {
        this.appKey = str;
    }

    public void setBlackCity(List<String> list) {
        this.blackCity = list;
    }

    public void setAppId(String str) {
        this.appId = str;
    }

    public int getAdIndex() {
        return this.adIndex;
    }

    public void setAdIndex(int i) {
        this.adIndex = i;
    }

    public List<String> getAdIds() {
        return this.adIds;
    }

    public void setAdIds(List<String> list) {
        this.adIds = list;
    }

    public int getBannerGravity() {
        return this.bannerGravity;
    }

    public void setBannerGravity(int i) {
        this.bannerGravity = i;
    }

    public long getShowInterval() {
        return this.showInterval;
    }

    public void setShowInterval(long j) {
        this.showInterval = j;
    }

    public long getShowStart() {
        return this.showStart;
    }

    public void setShowStart(long j) {
        this.showStart = j;
    }

    public int getIconAdLeftMargin() {
        return this.iconAdLeftMargin;
    }

    public void setIconAdLeftMargin(int i) {
        this.iconAdLeftMargin = i;
    }

    public int getIconAdBottomMargin() {
        return this.iconAdBottomMargin;
    }

    public void setIconAdBottomMargin(int i) {
        this.iconAdBottomMargin = i;
    }

    public boolean isTimingPower() {
        return this.isTimingPower;
    }

    public void setTimingPower(boolean z) {
        this.isTimingPower = z;
    }

    public long getShowTimingStart() {
        return this.showTimingStart;
    }

    public void setShowTimingStart(long j) {
        this.showTimingStart = j;
    }

    public long getShowTimingInterval() {
        return this.showTimingInterval;
    }

    public void setShowTimingInterval(long j) {
        this.showTimingInterval = j;
    }

    public String toString() {
        return "AdInfo{adType=" + this.adType + ", adIndex=" + this.adIndex + ", appId='" + this.appId + "', appKey='" + this.appKey + "', adIds=" + this.adIds + ", blackCity=" + this.blackCity + ", bannerGravity=" + this.bannerGravity + ", showInterval=" + this.showInterval + ", showStart=" + this.showStart + ", iconAdLeftMargin=" + this.iconAdLeftMargin + ", iconAdBottomMargin=" + this.iconAdBottomMargin + ", isTimingPower=" + this.isTimingPower + ", showTimingStart=" + this.showTimingStart + ", showTimingInterval=" + this.showTimingInterval + '}';
    }


}

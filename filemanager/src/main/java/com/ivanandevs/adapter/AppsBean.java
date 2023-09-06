package com.ivanandevs.adapter;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppsBean {
    private Drawable icon;
    private ApplicationInfo info;
    private long installTime;
    private boolean isSystem;
    private String name;
    private String packageName;
    private boolean select = false;

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIcon(Drawable drawable) {
        this.icon = drawable;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public ApplicationInfo getInfo() {
        return this.info;
    }

    public void setInfo(ApplicationInfo applicationInfo) {
        this.info = applicationInfo;
    }

    public AppsBean() {
    }

    public AppsBean(String str, boolean z, long j, ApplicationInfo applicationInfo) {
        this.packageName = str;
        this.isSystem = z;
        this.installTime = j;
        this.info = applicationInfo;
    }

    public boolean isSelect() {
        return this.select;
    }

    public void setSelect(boolean z) {
        this.select = z;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public boolean isSystem() {
        return this.isSystem;
    }

    public void setSystem(boolean z) {
        this.isSystem = z;
    }

    public long getInstallTime() {
        return this.installTime;
    }

    public void setInstallTime(long j) {
        this.installTime = j;
    }
}

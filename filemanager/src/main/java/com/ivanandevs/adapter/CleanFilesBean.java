package com.ivanandevs.adapter;

public class CleanFilesBean {
    private int type;
    private String uri;

    public CleanFilesBean(String str, int i) {
        this.uri = str;
        this.type = i;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String str) {
        this.uri = str;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }
}

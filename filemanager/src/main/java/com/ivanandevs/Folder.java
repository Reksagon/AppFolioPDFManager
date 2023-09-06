package com.ivanandevs;


import java.io.File;

public class Folder {

    public enum Type {
        File,
        Directory,
        Back,
        Loading
    }

    public File file;
    public Type type;

    public static Folder getInstance() {
        return new Folder();
    }

    private Folder() {
        this.file = null;
        this.type = Type.Loading;
    }

    public static Folder getInstance(File file, Type type) {
        return new Folder(file, type);
    }

    private Folder(File file, Type type) {
        this.file = file;
        this.type = type;
    }


}

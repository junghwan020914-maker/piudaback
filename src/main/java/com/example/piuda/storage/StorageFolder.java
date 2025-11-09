package com.example.piuda.storage;

public enum StorageFolder {
    NOTIFY("notify"),
    REPORT("report"),
    ORG_PROFILE("org_profile"),
    PRIVATE_PROFILE("private_profile");

    private final String folderName;
    StorageFolder(String folderName) { this.folderName = folderName; }
    public String getFolderName() { return folderName; }
}

package com.storedobject.core;

public interface FileFolderGenerator {
    ObjectIterator<FileFolder> listFolders();
    ObjectIterator<FileData> listFiles();
}

package com.storedobject.ui.util;

import com.storedobject.core.AttachmentDefinition;
import com.storedobject.core.FileData;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectLink;

import java.util.stream.Stream;

public class StreamAttachmentData implements StoredObjectLink<FileData> {

    public StreamAttachmentData() {
    }

    public StreamAttachment addAttachment(AttachmentDefinition definition) {
        return null;
    }

    public StreamAttachment getAttachment(String name) {
        return null;
    }

    public void setMaster(StoredObject master) {
    }

    @Override
    public StoredObject getMaster() {
        return null;
    }

    @Override
    public int getType() {
        return 9;
    }

    @Override
    public String getName() {
        return "$a";
    }

    @Override
    public boolean contains(FileData file) {
        return false;
    }

    @Override
    public boolean isAdded(FileData file) {
        return false;
    }

    @Override
    public boolean isDeleted(FileData file) {
        return false;
    }

    @Override
    public boolean isEdited(FileData file) {
        return false;
    }

    @Override
    public Stream<FileData> streamAll() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean append(FileData file) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(FileData file) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(FileData file) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean undelete(FileData file) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean update(FileData file) {
        throw new UnsupportedOperationException();
    }

    private StreamAttachment get(FileData file) {
        return null;
    }

    private StreamAttachment getOld(FileData file) {
        return null;
    }
}
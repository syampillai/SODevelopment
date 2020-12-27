package com.storedobject.ui.util;

import com.storedobject.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StreamAttachmentData implements StoredObjectLink<FileData> {

    private final List<StreamAttachment> attachments = new ArrayList<>();
    private StoredObject master;

    public StreamAttachmentData() {
    }

    public StreamAttachment addAttachment(AttachmentDefinition definition) {
        StreamAttachment a = new StreamAttachment(this, definition);
        attachments.add(a);
        return a;
    }

    public StreamAttachment getAttachment(String name) {
        return attachments.stream().filter(a -> a.getName().endsWith(name)).findAny().orElse(null);
    }

    public void setMaster(StoredObject master) {
        if(master == this.master) {
            return;
        }
        this.master = master;
        attachments.forEach(StreamAttachment::load);
    }

    @Override
    public boolean isDetail(FileData object) {
        return true;
    }

    @Override
    public StoredObject getMaster() {
        return master;
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
        return streamAll().anyMatch(f -> f == file);
    }

    @Override
    public boolean isAdded(FileData file) {
        StreamAttachment a = get(file);
        return a != null && a.isAdded();
    }

    @Override
    public boolean isDeleted(FileData file) {
        StreamAttachment a = get(file);
        if(a == null) {
            a = getOld(file);
        }
        return a != null && a.isDeleted();
    }

    @Override
    public boolean isEdited(FileData file) {
        StreamAttachment a = get(file);
        return a != null && a.isEdited();
    }

    @Override
    public Stream<FileData> streamAll() {
        Stream<FileData> all, old;
        all = attachments.stream().filter(a -> !a.isNull()).map(StreamAttachment::getValue);
        old = attachments.stream().filter(a -> !a.isNull() && a.classChanged()).map(StreamAttachment::getOldValue);
        return Stream.concat(all, old);
    }

    @Override
    public int size() {
        return (int)streamAll().count();
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
        return attachments.stream().filter(a -> a.getValue() == file).findAny().orElse(null);
    }

    private StreamAttachment getOld(FileData file) {
        return attachments.stream().filter(a -> a.getOldValue() == file).findAny().orElse(null);
    }
}
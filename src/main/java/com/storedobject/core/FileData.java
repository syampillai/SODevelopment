package com.storedobject.core;

import com.storedobject.common.DateUtility;
import com.storedobject.common.HTTP;
import com.storedobject.core.annotation.Column;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

public class FileData extends StoredObject implements Detail, HasParents, ContentType, HasStreamData {

    private final static ComputedDate roDate = new RODate();
    private final static ComputedMinute roMinute = new ROMinute();
    private Id fileId = Id.ZERO;
    StreamData file;
    private String name;
    private Id oldFiledId = null;
    FileFolder parent;

    public FileData() {
    }

    public FileData(String name) {
        this.name = name;
    }

    public FileData(FileData link) {
        this(link.name, link);
    }

    public FileData(String name, FileData link) {
        this.name = name;
        this.fileId = link.fileId;
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("File", "id");
    }

    public static void indices(Indices indices) {
        indices.add("File", false);
        indices.add("lower(Name)", false);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(caption = "Name/Reference", order = 1)
    public String getName() {
        return name;
    }

    public final void setFile(Id fileId) {
        this.fileId = fileId;
    }

    public void setFile(BigDecimal idValue) {
        setFile(new Id(idValue));
    }

    public void setFile(StreamData file) {
        this.file = file;
        setFile(file == null ? null : file.getId());
    }

    @Column(order = 2)
    public Id getFileId() {
        return fileId;
    }

    public StreamData getFile() {
        if(file == null) {
            file = get(StreamData.class, fileId);
        }
        return file;
    }

    @Override
    public StreamData getStreamData() {
        return getFile();
    }

    public boolean isEmpty() {
        return getFile() == null || Id.isNull(file.getId());
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        ComputedDate d1 = getReadBefore();
        ComputedDate d2 = getExpiryDate();
        if(inserted()) {
            Date today = DateUtility.today();
            if(d1.consider()) {
                if(d1.before(today)) {
                    throw new Invalid_Value("Read Before Date");
                }
                ComputedMinute t = getReadBeforeTime();
                if(t.consider()) {
                    long now = DateUtility.now().getTime();
                    long time = DateUtility.startTime(d1).getTime() + (t.getValue() * 60000L);
                    now /= 60000L;
                    time /= 60000L;
                    if(time < now) {
                        throw new Invalid_Value("Read Before Time");
                    }
                }
            }
            if(d2.consider() && d2.before(today)) {
                throw new Invalid_Value("Expiry Date");
            }
        }
        if(d1.consider() && d2.consider() && d1.after(d2)) {
            throw new Invalid_Value("Read Before Date / Expiry Date");
        }
        if(validName(name)) {
            fileId = tm.checkType(this, fileId, StreamData.class,false);
        } else {
            throw new Invalid_Value("Name");
        }
        super.validateData(tm);
    }

    /**
     * Check the given name is a valida file name or not.
     *
     * @param name Name to check.
     * @return True/false.
     */
    public static boolean validName(String name) {
        return !(StringUtility.isWhite(name) || name.contains("/") || name.contains(";") || name.contains("|")
                || name.contains("\""));
    }

    /**
     * Restore the previous version of this.
     *
     * @param transaction Transaction.
     * @return Previous version of this file.
     * @throws Exception Raises if transaction fails or if no previous version exists..
     */
    public FileData restore(Transaction transaction) throws Exception {
        FileData previous = getPreviousVersion();
        if(previous == null) {
            throw new SOException("No previous version found!");
        }
        unlink(transaction, previous.getId(), false);
        replaceInt(transaction, previous);
        delete(transaction);
        return previous;
    }

    public FileData getPreviousVersion() {
        return listLinks(getClass(), null, "Id").limit(1).findFirst();
    }

    /**
     * Replace this version with another version.
     *
     * @param transaction Transaction.
     * @param file New version of the document.
     * @throws Exception Raises if transaction fails.
     */
    public void replaceWith(Transaction transaction, FileData file) throws Exception {
        if(file.getClass() != getClass()) {
            throw new Invalid_State("Type mismatch: '" + name + "/" + file.name + "'");
        }
        if(file.existsLinks(transaction, StoredObject.class, true) ||
                file.countMasters(transaction, StoredObject.class, true) > 0) {
            throw new Invalid_State("File '" + file.name + "' is not fresh!");
        }
        replaceInt(transaction, file);
        file.addLink(transaction, this);
        ObjectIterator<FileCirculation> fcs = file.listLinks(transaction, FileCirculation.class,
                "Status>0 AND Status<3");
        for(FileCirculation fc: fcs) {
            try {
                fc.setStatus(3);
                fc.save(transaction);
            } catch(Exception e) {
                fcs.close();
                throw e;
            }
        }
    }

    private void replaceInt(Transaction transaction, FileData file) throws Exception {
        ObjectIterator<StoredObject> objects = listLinks(StoredObject.class, true);
        ArrayList<StoredObject> list = new ArrayList<>();
        objects.collectAll(list);
        unlink(transaction, getId(), true);
        for(StoredObject so: list) {
            if(so.getId().equals(file.getId())) {
                continue;
            }
            file.addLink(transaction, so);
        }
        objects = listMasters(StoredObject.class, true);
        list.clear();
        objects.collectAll(list);
        for(StoredObject so: list) {
            so.addLink(transaction, file);
        }
        unlink(transaction, getId(), false);
    }

    private static void unlink(Transaction transaction, Id id, boolean master) {
        try {
            ((DBTransaction)transaction).updateSQL("DELETE FROM core.Link WHERE " + (master ? "From" : "To") + "Id=" + id);
        } catch(Exception e) {
            ((AbstractTransaction)transaction).setError(e);
            throw e;
        }
    }

    public void unlinkFrom(Transaction transaction, FileFolder folder) throws Exception {
        setTransaction(transaction);
        try {
            ((DBTransaction)transaction).updateSQL("DELETE FROM core.Link WHERE ToId=" + getId() + " AND FromId=" + folder.getId());
        } catch(Exception e) {
            ((AbstractTransaction)transaction).setError(e);
            throw e;
        }
        if(countMasters(transaction, FileFolder.class) == 0) {
            delete(transaction);
        }
    }

    @Override
    public void validateParentDetach(StoredObject parent, int linkType) throws Exception {
        if(!deleted() && parent instanceof FileFolder) {
            throw new Invalid_State("Can not detach '" + name + "' from folder '" + parent + "'");
        }
        super.validateParentDetach(parent, linkType);
    }

    @Override
    public void validate() throws Exception {
        FileFolder ff = getFolder();
        LoginMessage alert = null;
        ObjectIterator<SystemUser> sus = null;
        ObjectIterator<FileCirculation> fcs = listLinks(FileCirculation.class);
        try {
            for(FileCirculation fc: fcs) {
                sus = list(SystemUser.class, "Person=" + fc.getPersonId());
                for(SystemUser su: sus) {
                    if(alert == null) {
                        alert = new LoginMessage();
                        alert.setMessage("Please note that details and/or content of the file/document '" + this +
                                "'\nthat was forwarded to you earlier for reading has been updated!");
                        alert.save(getTransaction());
                        alert.setGeneratedBy(getTransaction(), ff);
                    }
                    alert.addLink(getTransaction(), su);
                }
            }
        } finally {
            fcs.close();
            if(sus != null) {
                sus.close();
            }
        }
        super.validate();
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    void loadedCore() {
        super.loadedCore();
        oldFiledId = fileId;
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        file = null;
        if(Id.isNull(oldFiledId) || fileId.equals(oldFiledId)) {
            return;
        }
        ObjectIterator<FileData> list = null;
        RawSQL sql = ((AbstractTransaction)getTransaction()).getSQL();
        try {
            sql.execute("INSERT INTO core.H_StreamContent(Id,Block,TranId,Content) SELECT Id,Block," + getTransactionId() +
                    ",Content FROM core.StreamContent WHERE Id=" + oldFiledId);
            sql.execute("DELETE FROM core.StreamContent WHERE Id=" + oldFiledId);
            list = list(FileData.class, "File=" + oldFiledId + " AND Id<>" + getId(), null, true);
            ClassAttribute<?> ca;
            for(FileData fd: list) {
                sql.execute(fd.historyString());
                ca = StoredObjectUtility.classAttribute(fd);
                sql.execute("UPDATE " + ca.moduleName + "." + ca.tableName + " SET File=" + fileId +
                        ",TranId=" + getTransactionId() + " WHERE Id=" + fd.getId());
            }
        } finally {
            if(list != null) {
                list.close();
            }
            sql.close();
        }
    }

    @Override
    public boolean isLink() {
        StreamData sd = getFile();
        return sd != null && sd.isLink();
    }

    @Override
    public String getContentType() {
        StreamData sd = getFile();
        return sd == null ? null : sd.getMimeType();
    }

    public String getFileExtension() {
        StreamData sd = getFile();
        return sd == null ? null : sd.getFileExtension();
    }

    @Override
    public boolean isImage() {
        StreamData sd = getFile();
        return sd != null && sd.isImage();
    }

    @Override
    public boolean isVideo() {
        StreamData sd = getFile();
        return sd != null && sd.isVideo();
    }

    @Override
    public boolean isAudio() {
        StreamData sd = getFile();
        return sd != null && sd.isAudio();
    }

    @Override
    public boolean isPDF() {
        StreamData sd = getFile();
        return sd != null && sd.isPDF();
    }

    public void saveInFolder(Transaction transaction, String folderPath) throws Exception {
        FileFolder ff = FileFolder.createFolders(transaction, folderPath);
        if(ff == null) {
            throw new SOException("Unable to create folder - " + folderPath);
        }
        save(transaction);
        ff.addLink(transaction, this);
    }

    public static FileData get(String path) {
        if(!path.contains("/") || path.endsWith("/")) {
            return list(FileData.class, "lower(Name)='" + path.toLowerCase() + "'", true)
                    .single(true);
        }
        int p = path.lastIndexOf('/');
        String file = path.substring(p + 1);
        FileFolder ff = FileFolder.get(path.substring(0, p));
        if(ff == null) {
            return null;
        }
        return ff.listLinks(FileData.class, "Name='" + file + "'", true).single(false);
    }

    public static FileData create(String path, StreamData streamData, Transaction transaction) throws Exception {
        if(!path.contains("/") || path.endsWith("/")) {
            throw new Invalid_State("Invalid folder path name");
        }
        int p = path.lastIndexOf('/');
        String file = path.substring(p + 1);
        String folderPath = path.substring(0, p);
        FileFolder ff = FileFolder.createFolders(transaction, folderPath);
        if(ff == null) {
            throw new Invalid_State("Unable for create folder '" + folderPath + "'");
        }
        FileData fd = ff.listLinks(FileData.class, "Name='" + file + "'", true).single(false);
        if(fd == null) {
            fd = new FileData(file);
        }
        streamData.save(transaction);
        fd.setFile(streamData);
        fd.save(transaction);
        ff.addLink(transaction, fd);
        return fd;
    }

    public void circulate(Transaction transaction) throws Exception {
        LoginMessage alert = null;
        for(SystemUserGroup group: listLinks(SystemUserGroup.class)) {
            alert = circulate(transaction, group, alert);
        }
        for(FileFolder folder: listMasters(FileFolder.class)) {
            alert = circulate(folder, transaction, alert);
        }
    }

    private LoginMessage circulate(FileFolder folder, Transaction transaction, LoginMessage alert) throws Exception {
        for(SystemUserGroup group: folder.listLinks(SystemUserGroup.class)) {
            alert = circulate(transaction, group, alert);
        }
        for(FileFolder parentFolder: folder.listMasters(FileFolder.class)) {
            alert = circulate(parentFolder, transaction, alert);
        }
        return alert;
    }

    private LoginMessage circulate(Transaction transaction, SystemUserGroup group, LoginMessage alert) throws Exception {
        if(group.equals(SystemUserGroup.getDefault())) {
            for(SystemUser user: list(SystemUser.class)) {
                alert = circulate(transaction, user.getPersonId(), alert);
            }
            return alert;
        }
        for(SystemUser user: group.listMasters(SystemUser.class)) {
            alert = circulate(transaction, user.getPersonId(), alert);
        }
        return alert;
    }

    private LoginMessage circulate(Transaction transaction, Id personId, LoginMessage alert) throws Exception {
        FileCirculation fc = listLinks(transaction, FileCirculation.class, "Person=" + personId).
                single(true);
        if(fc != null && fc.getStatus() != 3) {
            return alert;
        }
        boolean newVersion = fc == null;
        if(newVersion) {
            fc = new FileCirculation();
            fc.setPerson(personId);
        } else {
            fc.setStatus(0);
        }
        fc.save(transaction);
        if(newVersion) {
            addLink(transaction, fc);
        }
        FileFolder ff = getFolder();
        try (ObjectIterator<SystemUser> sus = list(SystemUser.class, "Person=" + personId)) {
            for (SystemUser su : sus) {
                if (alert == null) {
                    alert = new LoginMessage();
                    StringBuilder s = new StringBuilder("File/Document '");
                    s.append(this).append("'");
                    if(!newVersion) {
                        s.append(" (Updated Version)");
                    }
                    s.append("\nhas been forwarded to you for reading.");
                    ComputedDate rbd = getReadBefore();
                    if (rbd.consider()) {
                        s.append(" Please read before ").append(rbd);
                        ComputedMinute rbt = getReadBeforeTime();
                        if (rbt.consider()) {
                            s.append(' ').append(rbt).append(" UTC");
                        }
                    }
                    alert.setMessage(s.toString());
                    Timestamp now = rbd.consider() ? DateUtility.createTimestamp(rbd) : DateUtility.now();
                    now = DateUtility.addDay(now, rbd.consider() ? 1 : 30);
                    alert.setValidTo(now);
                    alert.save(transaction);
                    alert.setGeneratedBy(transaction, ff);
                }
                alert.addLink(transaction, su);
            }
        }
        return alert;
    }

    public FileFolder getFolder() {
        return parent == null ? getMaster(FileFolder.class) : parent;
    }

    public FileFolder getRootFolder() {
        FileFolder ff = getFolder();
        return ff == null ? null : ff.getRootFolder();
    }

    public FileCirculation getCirculation(Person person) {
        return getCirculation(person.getId());
    }

    public FileCirculation getCirculation(Id personId) {
        return isVirtual() ? null : listLinks(FileCirculation.class, "Person=" + personId)
                .single(false);
    }

    public String getReadStamp() {
        String s = null;
        ComputedDate d = getReadBefore();
        if(d.consider()) {
            s = DateUtility.formatShortDate(d);
            ComputedMinute m = getReadBeforeTime();
            if(m.consider()) {
                s += " " + m;
            }
        }
        return s;
    }

    public ComputedDate getExpiryDate() {
        return roDate;
    }

    public ComputedDate getReadBefore() {
        return roDate;
    }

    public ComputedMinute getReadBeforeTime() {
        return roMinute;
    }

    public String getDetails() {
        return null;
    }

    public void view(Device device) {
        device.view(this);
    }

    private static class RODate extends ComputedDate {

        private RODate() {
            super(0, true);
        }

        @Override
        public void setComputed(boolean computed) {
        }

        @Override
        public void consider(boolean consider) {
        }

        @Override
        public void ignore(boolean ignore) {
        }
    }

    private static class ROMinute extends ComputedMinute {

        private ROMinute() {
            super(0, true);
        }

        @Override
        public void set(AbstractComputedInteger value) {
        }

        @Override
        public void setValue(int value) {
        }

        @Override
        public void setComputed(boolean computed) {
        }

        @Override
        public void consider(boolean consider) {
        }

        @Override
        public void ignore(boolean ignore) {
        }
    }

    public void linkTo(Transaction transaction, StoredObject object, String attribute) throws Exception {
        String link = "db:" + object.getClass().getName() + "/" + attribute + "/" + object.getId();
        StreamData content = StreamData.getViaLink(link);
        if(content == null) {
            throw new Invalid_State("No content from link " + link);
        }
        linkSave(transaction, link, content.getContentType());
    }

    public void linkTo(Transaction transaction, String link) throws Exception {
        HTTP http = new HTTP(link);
        InputStream in = http.getInputStream();
        String contentType = http.getConnection().getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        in.close();
        linkSave(transaction, link, contentType);
    }

    private void linkSave(Transaction transaction, String link, String contentType) throws  Exception {
        StreamData sd = getFile();
        if(sd == null) {
            sd = new StreamData();
        }
        sd.setContentType("l:" + contentType);
        sd.setStreamDataProvider(streamData -> new ByteArrayInputStream(link.getBytes(StandardCharsets.UTF_8)));
        sd.save(transaction);
        setFile(sd);
        save(transaction);
    }
}

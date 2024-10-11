package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.job.MessageGroup;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class Site extends StoredObject {

    private String name, imageName;
    private String project;
    private String timeZone = "GMT";
    private ZoneId zoneId;
    private int code;
    private boolean active;
    private Id messageGroupId;

    public Site() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("Project", "text");
        columns.add("TimeZone","text");
        columns.add("Code", "int");
        columns.add("ImageName", "text");
        columns.add("Active", "boolean");
        columns.add("MessageGroup", "id");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
        indices.add("Code,T_Family", true);
    }

    public String getUniqueCondition() {
        return "lower(Name)='" + getName().trim().toLowerCase().replace("'", "''") + "'";
    }

    public static Site get(String name) {
        return StoredObjectUtility.get(Site.class, "Name", name, false);
    }

    public static ObjectIterator<Site> list(String name) {
        return StoredObjectUtility.list(Site.class, "Name", name, false);
    }

    @Override
    public String toString() {
        return getName();
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100)
    public String getName() {
        return name;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Column(style = "(large)", order = 200)
    public String getProject() {
        return project;
    }


    @Column(style = "(timezone)", order = 300)
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Column(order = 400, caption = "Unique Site Code")
    public int getCode() {
        return code;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Column(order = 500, required = false)
    public String getImageName() {
        return imageName;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(order = 100000)
    public boolean getActive() {
        return active;
    }

    public void setMessageGroup(Id messageGroupId) {
        this.messageGroupId = messageGroupId;
    }

    public void setMessageGroup(BigDecimal idValue) {
        setMessageGroup(new Id(idValue));
    }

    public void setMessageGroup(MessageGroup messageGroup) {
        setMessageGroup(messageGroup == null ? null : messageGroup.getId());
    }

    @Column(order = 1300)
    public Id getMessageGroupId() {
        return messageGroupId;
    }

    /**
     * Message group used to send alert messages when data values deviate from their normal values.
     * <p>Note: This can be overridden at the {@link Block} level.</p>
     *
     * @return Message group configured.
     */
    public MessageGroup getMessageGroup() {
        return getRelated(MessageGroup.class, messageGroupId);
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        DataSet.scheduleRefresh();
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(!deleted()) {
            timeZone = checkTimeZone(timeZone);
            if(StringUtility.isWhite(name)) {
                throw new Invalid_Value("Name");
            }
            if(StringUtility.isWhite(project)) {
                throw new Invalid_Value("Project");
            }
        }
        messageGroupId = tm.checkType(this, messageGroupId, MessageGroup.class, false);
        super.validateData(tm);
    }

    @Override
    public String toDisplay() {
        String p = project;
        if (p.length() > 30) {
            p = p.substring(0, 30) + "...";
        }
        return name + " (" + p + ")";
    }

    private ZoneId zId() {
        if(zoneId == null) {
            zoneId = ZoneId.of(timeZone);
        }
        return zoneId;
    }

    public <D extends java.util.Date> D dateGMT(D date) {
        ZoneOffset zo = zId().getRules().getOffset(DateUtility.localTime(date));
        D d = DateUtility.clone(date);
        d.setTime(d.getTime() - (zo.getTotalSeconds() * 1000L));
        return d;
    }

    public <D extends java.util.Date> D date(D dateGMT) {
        ZoneOffset zo = zId().getRules().getOffset(DateUtility.localTime(dateGMT));
        D d = DateUtility.clone(dateGMT);
        d.setTime(d.getTime() + (zo.getTotalSeconds() * 1000L));
        return d;
    }

    public int getTimeDifference() {
        Date now = DateUtility.today();
        return (int) (date(now).getTime() - now.getTime());
    }

    public ObjectIterator<Block> listBlocks() {
        return list(Block.class, "Site=" + getId());
    }

    public ObjectIterator<Block> listActiveBlocks() {
        return list(Block.class, "Site=" + getId() + " AND Active");
    }
}

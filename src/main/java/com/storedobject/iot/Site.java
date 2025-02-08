package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.job.MessageGroup;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Represents a Site entity that stores detailed information such as name, project affiliation,
 * time zone, unique code, and its associated attributes.
 * The Site entity provides methods for handling specific operations related to site management,
 * timezone handling, and data validation.
 *
 * @author Syam
 */
public final class Site extends StoredObject {

    private String name, imageName;
    private String project;
    private String timeZone = "GMT";
    private ZoneId zoneId;
    private int code;
    private boolean active;
    private Id messageGroupId;

    /**
     * Constructs a new instance of the Site class.
     * <p></p>
     * This constructor initializes a Site object with its default values. The Site class
     * represents an entity with attributes such as name, project, timeZone, zoneId, code,
     * and other related properties. An instance of the Site class can also interact with
     * methods for managing its fields, utilizing unique identifiers, handling message groups,
     * and defining specific behaviors upon actions such as saving or data validation.
     * <p></p>
     * The Site class extends the StoredObject class, which provides a base for its persistence
     * and associated operations.
     */
    public Site() {
    }

    /**
     * Defines the columns for the Site entity by adding them to the specified `Columns` object.
     *
     * @param columns The Columns object to which the column definitions are added.
     */
    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("Project", "text");
        columns.add("TimeZone","text");
        columns.add("Code", "int");
        columns.add("ImageName", "text");
        columns.add("Active", "boolean");
        columns.add("MessageGroup", "id");
    }

    /**
     * Defines indices for the Site entity. The indices are added with specified column names and
     * configurations.
     *
     * @param indices An object representing the collection of indices to be defined. The method
     *                adds two indices: one on the column "lower(Name)" and another composite index
     *                on the columns "Code" and "T_Family", both as unique.
     */
    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
        indices.add("Code,T_Family", true);
    }

    /**
     * Generates a unique condition string based on the value of the Name property
     * of the object. The condition is case-insensitive and trims unnecessary
     * spaces, also escaping single quotes to ensure safe usage in database queries.
     *
     * @return A unique condition string of the format "lower(Name)='value'", where
     *         'value' is the sanitized and lower-cased representation of the Name property.
     */
    public String getUniqueCondition() {
        return "lower(Name)='" + getName().trim().toLowerCase().replace("'", "''") + "'";
    }

    /**
     * Retrieves a {@code Site} object based on the provided name.
     *
     * @param name the name of the {@code Site} to retrieve
     * @return the {@code Site} object matching the given name, or {@code null} if not found
     */
    public static Site get(String name) {
        return StoredObjectUtility.get(Site.class, "Name", name, false);
    }

    /**
     * Lists all Site objects that match the specified name.
     *
     * @param name the name of the Site to match
     * @return an iterator over Site objects matching the specified name
     */
    public static ObjectIterator<Site> list(String name) {
        return StoredObjectUtility.list(Site.class, "Name", name, false);
    }

    /**
     * Returns the string representation of the Site object, which is the value of its name field.
     *
     * @return the name of the site as a string
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Provides hint value for the type of object, typically used to optimize processing
     * or rendering by indicating the expected size or type of data represented.
     *
     * @return An integer representing the hint, where the value corresponds to a predefined
     * hint constant, such as {@code ObjectHint.SMALL_LIST}.
     */
    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    /**
     * Sets the name for this instance.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the name of the site.
     *
     * @return the name of the site
     */
    @Column(order = 100)
    public String getName() {
        return name;
    }

    /**
     * Sets the project associated with the Site instance.
     *
     * @param project The name or identifier of the project to associate with the site.
     */
    public void setProject(String project) {
        this.project = project;
    }

    /**
     * Retrieves the project associated with the site.
     *
     * @return The name of the project as a String.
     */
    @Column(style = "(large)", order = 200)
    public String getProject() {
        return project;
    }


    /**
     * Retrieves the time zone associated with the site.
     *
     * @return The time zone as a string.
     */
    @Column(style = "(timezone)", order = 300)
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the time zone for the site.
     *
     * @param timeZone the time zone to be set, represented as a string
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Sets the unique code for the site.
     *
     * @param code The unique code to be assigned to the site.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Retrieves the unique site code of the Site.
     *
     * @return the integer value representing the unique site code
     */
    @Column(order = 400, caption = "Unique Site Code")
    public int getCode() {
        return code;
    }

    /**
     * Sets the image name for the site.
     *
     * @param imageName The name of the image to set.
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    /**
     * Retrieves the name of the image associated with the site.
     *
     * @return the image name as a String, or null if no image name is set
     */
    @Column(order = 500, required = false)
    public String getImageName() {
        return imageName;
    }

    /**
     * Sets the active status for the site.
     *
     * @param active A boolean value indicating whether the site is active (true) or inactive (false).
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Retrieves the active status of the site.
     *
     * @return true if the site is active, false otherwise.
     */
    @Column(order = 100000)
    public boolean getActive() {
        return active;
    }

    /**
     * Sets the message group identifier for this site.
     *
     * @param messageGroupId The identifier of the message group to be associated with the site.
     */
    public void setMessageGroup(Id messageGroupId) {
        this.messageGroupId = messageGroupId;
    }

    /**
     * Sets the message group for the site using the provided BigDecimal identifier value.
     *
     * @param idValue The BigDecimal value representing the identifier of the message group to be set.
     */
    public void setMessageGroup(BigDecimal idValue) {
        setMessageGroup(new Id(idValue));
    }

    /**
     * Sets the message group associated with this site.
     *
     * @param messageGroup The message group to associate with the site. If null,
     *                     the message group association will be removed.
     */
    public void setMessageGroup(MessageGroup messageGroup) {
        setMessageGroup(messageGroup == null ? null : messageGroup.getId());
    }

    /**
     * Retrieves the unique identifier for the message group associated with this site.
     *
     * @return The unique identifier of the message group as an {@code Id} object.
     */
    @Column(order = 1300)
    public Id getMessageGroupId() {
        return messageGroupId;
    }

    /**
     * Retrieves the related MessageGroup instance for the current Site.
     *
     * @return the associated MessageGroup object.
     */
    public MessageGroup getMessageGroup() {
        return getRelated(MessageGroup.class, messageGroupId);
    }

    /**
     * This method is triggered after the object is saved to the storage.
     * It ensures proper functionality by invoking the parent class's
     * `saved()` method for maintaining standard behaviors and then
     * schedules a refresh of associated datasets.
     *
     * @throws Exception if any error occurs during the execution of the parent class's `saved()` method or while scheduling a dataset refresh.
     */
    @Override
    public void saved() throws Exception {
        super.saved();
        DataSet.scheduleRefresh();
    }

    /**
     * Validates the data fields of the Site object before saving or processing.
     *
     * @param tm The TransactionManager instance used for performing validation and other related operations.
     * @throws Exception if any validation fails, such as an invalid time zone, missing required fields,
     *                   or an invalid associated message group ID.
     */
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

    /**
     * Generates a display-friendly string representation of the object,
     * combining the name and a truncated version of the project field, if necessary.
     * The project field is truncated to a maximum of 30 characters, appending
     * "..." if truncation occurs.
     *
     * @return A string in the format "name (project)", where "project" may be truncated.
     */
    @Override
    public String toDisplay() {
        String p = project;
        if (p.length() > 30) {
            p = p.substring(0, 30) + "...";
        }
        return name + " (" + p + ")";
    }

    /**
     * Retrieves the ZoneId instance associated with the time zone of this Site.
     * If the ZoneId has not been initialized, it initializes it using the specified timeZone field.
     *
     * @return The ZoneId instance for the specified time zone of this Site.
     */
    private ZoneId zId() {
        if(zoneId == null) {
            zoneId = ZoneId.of(timeZone);
        }
        return zoneId;
    }

    /**
     * Adjusts the given date to Greenwich Mean Time (GMT) by applying the time zone offset
     * of the site.
     *
     * @param <D>   The type of the Date object passed to the method.
     * @param date  The date object that needs to be adjusted to GMT.
     * @return The date object adjusted to GMT.
     */
    public <D extends java.util.Date> D dateGMT(D date) {
        ZoneOffset zo = zId().getRules().getOffset(DateUtility.localTime(date));
        D d = DateUtility.clone(date);
        d.setTime(d.getTime() - (zo.getTotalSeconds() * 1000L));
        return d;
    }

    /**
     * Adjusts the provided date to the local time based on the ZoneId associated with this Site.
     * The adjustment is made by adding the offset in milliseconds to the input date.
     *
     * @param <D>      the specific subclass of java.util.Date
     * @param dateGMT  the date in GMT to be adjusted to the local time
     * @return the adjusted date object of the same type as the input
     */
    public <D extends java.util.Date> D date(D dateGMT) {
        ZoneOffset zo = zId().getRules().getOffset(DateUtility.localTime(dateGMT));
        D d = DateUtility.clone(dateGMT);
        d.setTime(d.getTime() + (zo.getTotalSeconds() * 1000L));
        return d;
    }

    /**
     * Calculates the time difference between the current date and time in the local time zone
     * and the corresponding date and time in GMT.
     *
     * @return the time difference, in milliseconds, between the local time and GMT.
     */
    public int getTimeDifference() {
        Date now = DateUtility.today();
        return (int) (date(now).getTime() - now.getTime());
    }

    /**
     * Retrieves an iterator over all blocks associated with this site.
     *
     * @return An ObjectIterator of Block representing all blocks associated with the site.
     */
    public ObjectIterator<Block> listBlocks() {
        return list(Block.class, "Site=" + getId());
    }

    /**
     * Retrieves an iterator over all active blocks associated with this site.
     *
     * @return An ObjectIterator containing all active Block objects linked to this site.
     */
    public ObjectIterator<Block> listActiveBlocks() {
        return list(Block.class, "Site=" + getId() + " AND Active");
    }
}

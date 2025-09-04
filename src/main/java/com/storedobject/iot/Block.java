package com.storedobject.iot;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.job.MessageGroup;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a Block (or a Building or something similar), which is a specific unit of functionality or
 * data in the application framework,
 * extending the capabilities of {@link AbstractUnit}.
 * This class provides various functionalities to manage blocks, such as storing and retrieving attributes
 * like site information, layout styles, image names, and more.
 * <br/>
 * It also supports statistical computations, consumption computations, and validation of the block's data.
 * The class is immutable (marked as final) and aims to encapsulate block-related business logic.
 * <br/>
 * Key features:
 * <pre>
 * - Handles block configuration including associated site, image name, captions, and layout style.
 * - Provides mechanisms to list or retrieve blocks based on search criteria or links.
 * - Offers utility methods for recomputing statistics and consumption of resources associated with the block.
 * - Ensures uniqueness of key attributes, such as block codes, through validation.
 * - Encapsulates connectivity to related entities like {@link Site} and {@link MessageGroup}.
 * </pre>
 *
 * @author Syam
 */
public final class Block extends AbstractUnit {

    private static final String DEFAULT_STYLE = "font-weight:bold\nfont-size:xx-large";
    private static final String[] layoutStyleValues = Unit.getOrdinalityValues();
    private int code, layoutStyle = 0;
    private String imageName;
    private int captionX, captionY;
    private String captionStyle;
    private Id siteId, messageGroupId = Id.ZERO;
    private Site site;

    /**
     * Constructs a new instance of the Block class.
     * <br/>
     * The constructor initializes a Block object with default values suitable for
     * representing a Block entity. It sets up the initial state required for further
     * operations on the Block.
     */
    public Block() {
    }

    /**
     * Configures and adds specific columns to the given Columns object.
     *
     * @param columns The Columns object to which various columns are added,
     *                specifying attributes such as name and type.
     */
    public static void columns(Columns columns) {
        columns.add("Site", "id");
        columns.add("Code", "int");
        columns.add("ImageName", "text");
        columns.add("CaptionX", "int");
        columns.add("CaptionY", "int");
        columns.add("CaptionStyle", "text");
        columns.add("MessageGroup", "id");
        columns.add("LayoutStyle", "int");
    }

    /**
     * Configures specific indices for the block entity.
     * Adds certain predefined fields like "Code" and "Site" with corresponding configurations to the given indices.
     *
     * @param indices The indices object where specific index configurations are added.
     */
    public static void indices(Indices indices) {
        indices.add("Code", true);
        indices.add("Site");
    }

    /**
     * Retrieves a list of column names relevant to the search functionality.
     *
     * @return An array of strings containing the names of the search columns:
     * "Name", "Active", "Code", and "LayoutStyle".
     */
    public static String[] searchColumns() {
        return new String[] { "Name", "Active", "Code", "LayoutStyle" };
    }

    /**
     * Retrieves a Block object by searching for it using the specified name.
     *
     * @param name The name of the Block to retrieve.
     * @return The Block object that matches the specified name or null if no match is found.
     */
    public static Block get(String name) {
        return StoredObjectUtility.get(Block.class, "Name", name, false);
    }

    /**
     * Retrieves an iterator over Block objects filtered by the specified name.
     *
     * @param name The name of the Block objects to be listed.
     * @return An ObjectIterator containing Block objects with the specified name.
     */
    public static ObjectIterator<Block> list(String name) {
        return StoredObjectUtility.list(Block.class, "Name", name, false);
    }

    /**
     * Provides an array of link definitions related to the Block entity.
     *
     * @return An array of strings where each entry represents a link definition
     *         in the format "Label|TargetClass".
     */
    public static String[] links() {
        return new String[] {
                "Skip Control Schedules|com.storedobject.iot.ControlSchedule",
        };
    }

    /**
     * This method is invoked when the current instance is successfully saved.
     * It performs the following actions:
     * <br/>
     * <pre>
     * 1. Calls the superclass implementation of the `saved` method.
     * 2. Resets the `site` field to `null`.
     * 3. Registers a commit listener for the current transaction, which ensures
     *    that the `Controller.restart()` method is called once the transaction is committed.
     *    </pre>
     *
     * @throws Exception if an error occurs during the execution of the method.
     */
    @Override
    public void saved() throws Exception {
        super.saved();
        site = null;
        getTransaction().addCommitListener(t -> Controller.restart());
    }

    /**
     * Provides a hint value used for specifying a small list layout style within the system.
     * The returned value corresponds to a predefined constant that designates a layout suggestion.
     *
     * @return the hint constant representing a small list, specifically {@code ObjectHint.SMALL_LIST}.
     */
    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    /**
     * Sets the site for this block.
     *
     * @param siteId The identifier of the site to be associated with the block.
     */
    public void setSite(Id siteId) {
        this.siteId = siteId;
    }

    /**
     * Sets the site for this block using its unique identifier represented as a {@code BigDecimal}.
     *
     * @param idValue The unique identifier of the site as a {@code BigDecimal}.
     */
    public void setSite(BigDecimal idValue) {
        setSite(new Id(idValue));
    }

    /**
     * Sets the site associated with this block.
     *
     * @param site The site to associate. If null, the site ID will be set to null.
     */
    public void setSite(Site site) {
        setSite(site == null ? null : site.getId());
    }

    /**
     * Retrieves the identifier of the associated site or project.
     *
     * @return The identifier of the site or project as an {@code Id} object.
     */
    @Column(caption = "Site/Project", order = 200)
    public Id getSiteId() {
        return siteId;
    }

    /**
     * Retrieves the associated Site object for this Block instance.
     * If the site has not been initialized, it fetches the related Site using the siteId.
     *
     * @return The associated Site object. If the site is not already loaded, it is fetched and initialized.
     */
    @Override
    public Site getSite() {
        if(site == null) {
            site = getRelated(Site.class, siteId);
        }
        return site;
    }

    /**
     * Sets the name of the image after trimming any leading or trailing whitespace.
     *
     * @param imageName The name of the image to set.
     */
    public void setImageName(String imageName) {
        this.imageName = imageName.trim();
    }

    /**
     * Retrieves the image name for the block. If the image name is null or blank,
     * it fetches the default image name by calling the {@code defaultImageName} method.
     *
     * @return the image name if set; otherwise, the default image name.
     */
    @Column(order = 400, required = false)
    public String getImageName() {
        return imageName == null || imageName.isBlank() ? defaultImageName() : imageName;
    }

    /**
     * Sets the X-coordinate for the caption.
     *
     * @param captionX the X-coordinate value to set for the caption
     */
    public void setCaptionX(int captionX) {
        this.captionX = captionX;
    }

    /**
     * Retrieves the X-coordinate for the caption.
     *
     * @return the X-coordinate value of the caption as an integer.
     */
    @Column(order = 500, required = false, caption = "X-Coordinate for Caption")
    public int getCaptionX() {
        return captionX;
    }

    /**
     * Sets the Y-coordinate for the caption of the block.
     *
     * @param captionY the Y-coordinate value to be set for the caption
     */
    public void setCaptionY(int captionY) {
        this.captionY = captionY;
    }

    /**
     * Retrieves the Y-coordinate value for the caption.
     *
     * @return the Y-coordinate for the caption as an integer
     */
    @Column(order = 600, required = false, caption = "Y-Coordinate for Caption")
    public int getCaptionY() {
        return captionY;
    }

    /**
     * Sets the caption style for the block.
     *
     * @param captionStyle the style to be applied to the caption, typically represented as a string.
     */
    public void setCaptionStyle(String captionStyle) {
        this.captionStyle = captionStyle;
    }

    /**
     * Retrieves the style applied to the caption. If the caption style is not defined or blank,
     * a default style value is returned.
     *
     * @return The caption style string if defined; otherwise, returns the default style string.
     */
    @Column(order = 700, required = false, style = "(large)")
    public String getCaptionStyle() {
        return captionStyle == null || captionStyle.isBlank() ? DEFAULT_STYLE : captionStyle;
    }

    /**
     * Sets the unique code for the block.
     *
     * @param code The unique integer value representing the block's code.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Retrieves the unique block code for the block.
     *
     * @return the unique code representing the block.
     */
    @Column(order = 1200, caption = "Unique Block Code")
    public int getCode() {
        return code;
    }

    /**
     * Sets the message group associated with this block.
     *
     * @param messageGroupId The identifier of the message group to be set.
     */
    public void setMessageGroup(Id messageGroupId) {
        this.messageGroupId = messageGroupId;
    }

    /**
     * Sets the message group for the block using a {@link BigDecimal} value.
     *
     * @param idValue The {@link BigDecimal} value to be used to create an {@link Id}
     *                for the message group.
     */
    public void setMessageGroup(BigDecimal idValue) {
        setMessageGroup(new Id(idValue));
    }

    /**
     * Sets the message group associated with this block by converting the provided {@link MessageGroup}
     * object to its corresponding identifier. If the provided message group is null, it sets the
     * message group identifier to null.
     *
     * @param messageGroup The {@link MessageGroup} object representing the message group to associate
     *                     with this block, or null to clear the association.
     */
    public void setMessageGroup(MessageGroup messageGroup) {
        setMessageGroup(messageGroup == null ? null : messageGroup.getId());
    }

    /**
     * Retrieves the identifier of the message group associated with the block.
     *
     * @return the ID of the message group, or null if no message group is associated.
     */
    @Column(order = 1300, required = false)
    public Id getMessageGroupId() {
        return messageGroupId;
    }

    /**
     * Retrieves the associated {@link MessageGroup} for this Block. If no specific
     * MessageGroup is linked to this Block, the default MessageGroup of the current
     * Site is returned.
     *
     * @return The {@code MessageGroup} associated with this Block, or the default
     *         {@code MessageGroup} of the Site if none is specifically associated.
     */
    public MessageGroup getMessageGroup() {
        MessageGroup mg = getRelated(MessageGroup.class, messageGroupId);
        return mg == null ? getSite().getMessageGroup() : mg;
    }
    
    /**
     * Sets the layout style for the block.
     *
     * @param layoutStyle the layout style to be set, represented as an integer.
     *                    This value determines the visual or structural arrangement of the block.
     */
    public void setLayoutStyle(int layoutStyle) {
        this.layoutStyle = layoutStyle;
    }

    /**
     * Retrieves the layout style identifier for the block.
     *
     * @return An integer representing the layout style of the block.
     */
    @Column(order = 1400)
    public int getLayoutStyle() {
        return layoutStyle;
    }

    /**
     * Retrieves the layout style value for the current layoutStyle of the block.
     *
     * @return The layout style value corresponding to the current layoutStyle.
     */
    public String getLayoutStyleValue() {
        return getLayoutStyleValue(layoutStyle);
    }

    /**
     * Returns the layout style value corresponding to the given layout style index.
     * The method retrieves the value from the `layoutStyleValues` array using a modulus operation
     * to ensure the index remains within bounds of the array.
     *
     * @param layoutStyle an integer representing the index of the layout style
     * @return a String representing the layout style value
     */
    public static String getLayoutStyleValue(int layoutStyle) {
        return layoutStyleValues[layoutStyle % layoutStyleValues.length];
    }

    /**
     * Retrieves all layout style values available in the system.
     *
     * @return An array of strings representing the layout style values.
     */
    public static String[] getLayoutStyleValues() {
        return layoutStyleValues;
    }

    /**
     * Validates the data of the Block object by performing various checks such as ensuring certain fields are populated, validating relationships,
     * and defaulting missing attributes where necessary.
     *
     * @param tm The TransactionManager instance that provides utilities for type-checking and validation of related entities.
     * @throws Exception If any validation rule fails during the process.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(!deleted()) {
            siteId = tm.checkType(this, siteId, Site.class, false);
            checkForDuplicate("Code");
            if (imageName == null || imageName.isBlank()) {
                imageName = defaultImageName();
            }
            messageGroupId = tm.checkType(this, messageGroupId, MessageGroup.class, true);
        }
        super.validateData(tm);
    }

    private String defaultImageName() {
        Site site = getSite();
        if(site == null) {
            return "";
        }
        Block block = list(Block.class, "Site=" + site.getId() + " AND Active")
                .filter(b -> !b.imageName.isBlank()).findFirst();
        if(block == null) {
            block = list(Block.class, "Site=" + site.getId())
                    .filter(b -> !b.imageName.isBlank()).findFirst();
        }
        return block == null ? site.getImageName() : block.imageName;
    }

    /**
     * Recomputes the statistical data for all units associated with this block.
     *
     * @param tm The transaction manager to be used for accessing and updating the data.
     * @throws Exception If an error occurs during the statistics computation process.
     */
    public void recomputeStatistics(TransactionManager tm) throws Exception {
        List<Unit> units = listUnits().toList();
        for(Unit unit : units) {
            unit.recomputeStatistics(tm);
        }
    }

    /**
     * Deletes the consumption data for the given resource.
     *
     * @param tm The transaction manager to handle database transactions for the operation.
     * @param resource The resource for which the consumption needs to be deleted.
     * @throws Exception If an error occurs during the transaction.
     */
    public void deleteConsumption(TransactionManager tm, Resource resource) throws Exception {
        String uids;
        List<AbstractUnit> units = listAllUnits().toList();
        if(units.isEmpty()) {
            uids = "" + getId();
        } else {
            uids = units.stream().map(u -> u.getId().toString()).collect(Collectors.joining(","));
            uids += "," + getId();
        }
        String finalUids = uids;
        tm.transact(t -> {
            for(Consumption<?> c: list(Consumption.class,
                    "Resource=" + resource.getId() + " AND Item IN (" + finalUids + ")", true)) {
                c.delete(t);
            }
        });
    }

    /**
     * Recomputes the consumption statistics for all resources associated with the object.
     *
     * @param tm The transaction manager used to handle transactional operations during the computation.
     * @throws Exception If an error occurs during the computation process.
     */
    public void recomputeConsumption(TransactionManager tm) throws Exception {
        List<Resource> resources = list(Resource.class).toList();
        for(Resource resource: resources) {
            recomputeConsumption(tm, resource);
        }
    }

    /**
     * Recomputes the consumption data for the given resource by first removing existing
     * consumption entries associated with the resource and then recalculating the
     * consumption values using the current data and requirements.
     *
     * @param tm The transaction manager to handle database transactions for the operation.
     * @param resource The resource for which the consumption needs to be recalculated.
     * @throws Exception If an error occurs during the transaction or computation process.
     */
    public void recomputeConsumption(TransactionManager tm, Resource resource) throws Exception {
        deleteConsumption(tm, resource);
        computeConsumption(tm, resource);
    }

    /**
     * Computes the consumption for all resources retrieved as a list of {@code Resource} objects
     * and processes them using the specified {@code TransactionManager}.
     *
     * @param tm The transaction manager used to oversee the consumption computation process.
     * @throws Exception If an error occurs during the computation process.
     */
    public void computeConsumption(TransactionManager tm) throws Exception {
        computeConsumption(tm, list(Resource.class).toList());
    }

    /**
     * Calculates the consumption of resources using the provided transaction manager.
     *
     * @param tm the transaction manager used for processing resource consumption
     * @param resources an iterable collection of resources for which consumption is to be computed
     * @throws Exception if an error occurs during the computation process
     */
    public void computeConsumption(TransactionManager tm, Iterable<Resource> resources) throws Exception {
        for(Resource resource: resources) {
            computeConsumption(tm, resource);
        }
    }

    /**
     * Computes the consumption of a given resource by repeatedly invoking the consumption process
     * until the operation no longer returns 1, which indicates further processing is required.
     *
     * @param tm The TransactionManager instance used to manage transactions during computation.
     * @param resource The Resource object for which consumption is to be computed.
     * @throws Exception If an error occurs during the consumption computation process.
     */
    public void computeConsumption(TransactionManager tm, Resource resource) throws Exception {
        if(!consumes(resource.getCode())) {
            return;
        }
        int result = 1;
        while (result == 1) {
            result = consumption(tm, resource);
        }
    }

    /**
     * Computes the consumption for a specific resource based on transaction and historical data.
     *
     * @param tm The transaction manager to be used for database operations.
     * @param resource The resource for which consumption is being computed.
     * @return The computed consumption value for the specified resource.
     * @throws Exception If an error occurs during computation or database access.
     */
    private int consumption(TransactionManager tm, Resource resource) throws Exception {
        ResultSet rs;
        int y, h;
        Date dateGMT;
        try (Query q = query(HourlyConsumption.class, "/Max(Year)", "Resource=" + resource.getId()
                + " AND Item=" + getId())) {
            rs = q.getResultSet();
            y = rs.getInt(1);
            if(rs.wasNull()) {
                y = -1;
            }
        }
        if(y == -1) { // Never computed
            List<Unit> units = list(Unit.class, "Active AND Block=" + getId(), true).toList();
            if(units.isEmpty()) {
                return 0;
            }
            String uids = units.stream().map(u -> u.getId().toString()).collect(Collectors.joining(","));
            long first;
            try (Query q = query(Data.class, "/Min(CollectedAt)", "Unit IN (" + uids + ")", true)) {
                rs = q.getResultSet();
                first = rs.getLong(1);
                if(rs.wasNull()) {
                    return 0;
                }
                dateGMT = new Date(first);
            }
        } else {
            try (Query q = query(HourlyConsumption.class, "/Max(Hour)", "Resource=" + resource.getId()
                    + " AND Item=" + getId() + " AND Year=" + y)) {
                rs = q.getResultSet();
                h = rs.getInt(1);
                dateGMT = DateUtility.create(y, 1, 1); // Site date
                dateGMT = new Date(dateGMT.getTime() + (h * 3600000L)); // Hour offset + 1 hour
                dateGMT = getSite().dateGMT(dateGMT); // To GMT
            }
        }
        int result;
        while ((result = consumption(tm, resource, dateGMT)) == -2) { // Data gap?
            dateGMT = new Date(dateGMT.getTime() + 3600000L); // Look in the later hour
        }
        return result;
    }

    /**
     * Computes and processes the consumption of a given resource over a specific time period.
     * The method calculates consumption data at different levels (hourly, daily, weekly, etc.)
     * and updates or saves them into the provided transaction.
     *
     * @param tm the transaction manager to manage database transactions
     * @param resource the resource for which consumption is to be computed
     * @param dateGMT the date (in GMT) for which the computation is to be performed
     * @return an integer representing the status of the operation:
     * <pre>
     *         - 1 if the operation was successful,
     *         - -1 if the data period is undefined,
     *         - -2 if no consumption data was processed,
     *         - 0 if there are no active units associated with the block
     *         </pre>
     *
     * @throws Exception if there are any issues during computation or database interaction
     */
    private int consumption(TransactionManager tm, Resource resource, Date dateGMT) throws Exception {
        DataPeriod dataPeriod = getDataPeriod(dateGMT);
        if(dataPeriod == null) {
            return -1;
        }
        Date siteDate = dataPeriod.siteDate();
        List<Consumption<?>> consumptionList = new ArrayList<>();
        List<AbstractUnit> units = list(Unit.class, "Block=" + getId() + " AND Active", true)
                .filter(u -> !(u instanceof SuperUnit)) // Super-units are skipped first
                .toList(u -> u);
        if(units.isEmpty()) {
            return 0;
        }
        int count = units.size();
        for(int i = 0; i < count; i++) {
            list(UnitItem.class, "Unit=" + units.get(i).getId() + " AND Active", true)
                    .forEach(units::add);
        }
        list(SuperUnit.class, "Block=" + getId() + " AND Active", true)
                .forEach(units::add); // Super-units are added at the bottom of the list
        Id id = getId();
        HourlyConsumption hc, hcB = resource.createHourlyConsumption(id, siteDate);
        DailyConsumption dc, dcB = resource.createDailyConsumption(id, siteDate);
        WeeklyConsumption wc, wcB = resource.createWeeklyConsumption(id, siteDate);
        MonthlyConsumption mc, mcB = resource.createMonthlyConsumption(id, siteDate);
        YearlyConsumption yc, ycB = resource.createYearlyConsumption(id, siteDate);
        Double consumption;
        double addC;
        for(AbstractUnit unit: units) {
            if(unit instanceof SuperUnit su) {
                List<Unit> children = su.childrenAll();
                if (children.isEmpty()) {
                    continue;
                }
                consumption = null;
                for (Unit child: children) {
                    for (Consumption<?> c: consumptionList) {
                        if(c.getItemId().equals(child.getId())) {
                            if(consumption == null) {
                                consumption = c.getConsumption();
                            } else {
                                consumption += c.getConsumption();
                            }
                        }
                    }
                }
            } else {
                consumption = unit.consumption(resource.getCode(), dataPeriod.from(), dataPeriod.to());
            }
            if(consumption == null) {
                continue;
            }
            id = unit.getId();
            hc = resource.createHourlyConsumption(id, siteDate);
            addC = consumption - hc.getConsumption();
            hc.setConsumption(consumption);
            consumptionList.add(hc);
            dc = resource.createDailyConsumption(id, siteDate);
            dc.addConsumption(addC);
            consumptionList.add(dc);
            wc = resource.createWeeklyConsumption(id, siteDate);
            wc.addConsumption(addC);
            consumptionList.add(wc);
            mc = resource.createMonthlyConsumption(id, siteDate);
            mc.addConsumption(addC);
            consumptionList.add(mc);
            yc = resource.createYearlyConsumption(id, siteDate);
            yc.addConsumption(addC);
            consumptionList.add(yc);
            if(unit instanceof UnitItem ui && !ui.getIndependent()) { // Dependent unit-type skipped for the block
                continue;
            }
            if(unit instanceof SuperUnit) { // Super-unit consumption should not be added to the block consumption
                continue;
            }
            hcB.addConsumption(addC);
            dcB.addConsumption(addC);
            wcB.addConsumption(addC);
            mcB.addConsumption(addC);
            ycB.addConsumption(addC);
        }
        if(consumptionList.isEmpty()) {
            return -2; // Data gap?
        }
        consumptionList.add(hcB);
        consumptionList.add(dcB);
        consumptionList.add(wcB);
        consumptionList.add(mcB);
        consumptionList.add(ycB);
        tm.transact(t -> {
            for(Consumption<?> c: consumptionList) {
                if(c.isVirtual()) {
                    c.makeNew();
                }
                c.save(t);
            }
        });
        return 1;
    }

    /**
     * Computes the consumption of a resource over a specified time interval.
     *
     * @param resource The identifier of the resource for which the consumption is to be calculated.
     * @param from The start time of the interval, represented as a timestamp in milliseconds.
     * @param to The end time of the interval, represented as a timestamp in milliseconds.
     * @return The consumption value as a Double, representing the amount of resource consumed during the specified interval.
     * @throws SORuntimeException if the computation cannot be performed.
     */
    @Override
    protected Double computeConsumption(int resource, long from, long to) {
        throw new SORuntimeException();
    }

    /**
     * Retrieves the unit identifier associated with this object or context.
     *
     * @return the unique identifier of the unit as an instance of {@code Id}.
     * @throws SORuntimeException if the operation fails or is not supported.
     */
    @Override
    Id unitId() {
        throw new SORuntimeException();
    }

    /**
     * Retrieves the unique identifier for the block.
     *
     * @return the unique identifier of the block as an {@code Id} object
     */
    @Override
    public Id getBlockId() {
        return getId();
    }

    /**
     * Retrieves an iterator of units associated with the block.
     *
     * @return An ObjectIterator of Unit entities that are linked to this block.
     */
    public ObjectIterator<Unit> listUnits() {
        return list(Unit.class, "Block=" + getId(), true);
    }

    /**
     * Lists all units, including the main units and associated independent units,
     * while ensuring that the active status of related units is synchronized.
     *
     * @return An {@code ObjectIterator} containing all the units, including the main units
     *         and additional independent units after processing their active states.
     */
    public ObjectIterator<AbstractUnit> listAllUnits() {
        List<AbstractUnit> units = listUnits().map(u -> (AbstractUnit)u).toList();
        ObjectIterator<AbstractUnit> all = ObjectIterator.create(units);
        for(AbstractUnit unit: units) {
            all = all.add(list(UnitItem.class, "Unit=" + unit.getId() + " AND Independent", true)
                    .map(u -> {
                        if(u.getActive() && !unit.getActive()) {
                            u.setActive(false);
                        }
                        return u;
                    }));
        }
        return all;
    }

    @Override
    public boolean consumes(int resource) {
        return listAllUnits().filter(u -> !(u instanceof Block)).anyMatch(u -> u.consumes(resource));
    }
}

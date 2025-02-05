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

public final class Block extends AbstractUnit {

    private static final String DEFAULT_STYLE = "font-weight:bold\nfont-size:xx-large";
    private static final String[] layoutStyleValues = Unit.getOrdinalityValues();
    private int code, layoutStyle = 0;
    private String imageName;
    private int captionX, captionY;
    private String captionStyle;
    private Id siteId, messageGroupId = Id.ZERO;
    private Site site;

    public Block() {
    }

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

    public static void indices(Indices indices) {
        indices.add("Code", true);
        indices.add("Site");
    }

    public static String[] searchColumns() {
        return new String[] { "Name", "Active", "Code", "LayoutStyle" };
    }

    public static Block get(String name) {
        return StoredObjectUtility.get(Block.class, "Name", name, false);
    }

    public static ObjectIterator<Block> list(String name) {
        return StoredObjectUtility.list(Block.class, "Name", name, false);
    }

    public static String[] links() {
        return new String[] {
                "Skip Control Schedules|com.storedobject.iot.ControlSchedule",
        };
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        site = null;
        getTransaction().addCommitListener(t -> Controller.restart());
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public void setSite(Id siteId) {
        this.siteId = siteId;
    }

    public void setSite(BigDecimal idValue) {
        setSite(new Id(idValue));
    }

    public void setSite(Site site) {
        setSite(site == null ? null : site.getId());
    }

    @Column(caption = "Site/Project", order = 200)
    public Id getSiteId() {
        return siteId;
    }

    @Override
    public Site getSite() {
        if(site == null) {
            site = getRelated(Site.class, siteId);
        }
        return site;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName.trim();
    }

    @Column(order = 400, required = false)
    public String getImageName() {
        return imageName == null || imageName.isBlank() ? defaultImageName() : imageName;
    }

    public void setCaptionX(int captionX) {
        this.captionX = captionX;
    }

    @Column(order = 500, required = false, caption = "X-Coordinate for Caption")
    public int getCaptionX() {
        return captionX;
    }

    public void setCaptionY(int captionY) {
        this.captionY = captionY;
    }

    @Column(order = 600, required = false, caption = "Y-Coordinate for Caption")
    public int getCaptionY() {
        return captionY;
    }

    public void setCaptionStyle(String captionStyle) {
        this.captionStyle = captionStyle;
    }

    @Column(order = 700, required = false, style = "(large)")
    public String getCaptionStyle() {
        return captionStyle == null || captionStyle.isBlank() ? DEFAULT_STYLE : captionStyle;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Column(order = 1200, caption = "Unique Block Code")
    public int getCode() {
        return code;
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

    @Column(order = 1300, required = false)
    public Id getMessageGroupId() {
        return messageGroupId;
    }

    /**
     * Message group used to send alert messages when data values deviate from their normal values.
     *
     * @return Message group configured. If no message group is configured, {@link Site#getMessageGroup()} is returned.
     */
    public MessageGroup getMessageGroup() {
        MessageGroup mg = getRelated(MessageGroup.class, messageGroupId);
        return mg == null ? getSite().getMessageGroup() : mg;
    }
    
    public void setLayoutStyle(int layoutStyle) {
        this.layoutStyle = layoutStyle;
    }

    @Column(order = 1400)
    public int getLayoutStyle() {
        return layoutStyle;
    }

    public String getLayoutStyleValue() {
        return getLayoutStyleValue(layoutStyle);
    }

    public static String getLayoutStyleValue(int layoutStyle) {
        return layoutStyleValues[layoutStyle % layoutStyleValues.length];
    }

    public static String[] getLayoutStyleValues() {
        return layoutStyleValues;
    }

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
        String name = "";
        try (ObjectIterator<Block> blocks = list(Block.class, "Site=" + site.getId())) {
            for(Block block: blocks) {
                if (block.imageName != null && !block.imageName.isBlank()) {
                    return block.imageName;
                }
                if (!name.isEmpty()) {
                    continue;
                }
                Unit unit = list(Unit.class, "Block=" + getId()).findFirst();
                if (unit != null) {
                    name = unit.getClass().getName();
                    name = name.substring(name.lastIndexOf('.') + 1);
                }
            }
        }
        return name;
    }

    public void recomputeStatistics(TransactionManager tm) throws Exception {
        List<Unit> units = listUnits().toList();
        for(Unit unit : units) {
            unit.recomputeStatistics(tm);
        }
    }

    public void recomputeConsumption(TransactionManager tm) throws Exception {
        List<Resource> resources = list(Resource.class).toList();
        for(Resource resource: resources) {
            recomputeConsumption(tm, resource);
        }
    }

    public void recomputeConsumption(TransactionManager tm, Resource resource) throws Exception {
        tm.transact(t -> {
            for(Consumption c: list(Consumption.class, "Resource=" + resource.getId(), true)) {
                c.delete(t);
            }
        });
        computeConsumption(tm, resource);
    }

    public void computeConsumption(TransactionManager tm) throws Exception {
        List<Resource> resources = list(Resource.class).toList();
        for(Resource resource: resources) {
            computeConsumption(tm, resource);
        }
    }

    public void computeConsumption(TransactionManager tm, Resource resource) throws Exception {
        int result = 1;
        while (result == 1) {
            result = consumption(tm, resource);
        }
    }

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
            List<Unit> units = list(Unit.class, "Active", true).toList();
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
            dateGMT = new Date(dateGMT.getTime() + 3600000L); // Look in the subsequent hour
        }
        return result;
    }

    private int consumption(TransactionManager tm, Resource resource, Date dateGMT) throws Exception {
        DataPeriod dataPeriod = getDataPeriod(dateGMT);
        if(dataPeriod == null) {
            return -1;
        }
        Date siteDate = dataPeriod.siteDate();
        List<Consumption> consumptionList = new ArrayList<>();
        List<AbstractUnit> units = list(Unit.class, "Block=" + getId() + " AND Active", true)
                .toList(u -> u);
        if(units.isEmpty()) {
            return 0;
        }
        int count = units.size();
        for(int i = 0; i < count; i++) {
            list(UnitItem.class, "Unit=" + units.get(i).getId() + " AND Active", true)
                    .forEach(units::add);
        }
        Id id = getId();
        HourlyConsumption hc, hcB = resource.createHourlyConsumption(id, siteDate);
        DailyConsumption dc, dcB = resource.createDailyConsumption(id, siteDate);
        WeeklyConsumption wc, wcB = resource.createWeeklyConsumption(id, siteDate);
        MonthlyConsumption mc, mcB = resource.createMonthlyConsumption(id, siteDate);
        YearlyConsumption yc, ycB = resource.createYearlyConsumption(id, siteDate);
        Double consumption;
        double addC;
        for(AbstractUnit unit: units) {
            consumption = unit.consumption(resource.getCode(), dataPeriod.from(), dataPeriod.to());
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
            if(unit instanceof UnitItem ui && !ui.getIndependent()) {
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
            for(Consumption c: consumptionList) {
                if(c.isVirtual()) {
                    c.makeNew();
                }
                c.save(t);
            }
        });
        return 1;
    }

    @Override
    protected Double computeConsumption(int resource, long from, long to) {
        throw new SORuntimeException();
    }

    @Override
    Id unitId() {
        throw new SORuntimeException();
    }

    @Override
    public Id getBlockId() {
        return getId();
    }

    public ObjectIterator<Unit> listUnits() {
        return list(Unit.class, "Block=" + getId(), true);
    }

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
}

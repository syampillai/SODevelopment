package com.storedobject.ui.iot;

import com.storedobject.common.StringFiller;
import com.storedobject.core.*;
import com.storedobject.helper.ID;
import com.storedobject.iot.*;
import com.storedobject.ui.*;
import com.storedobject.ui.util.HtmlTemplate;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.shared.Registration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SiteView extends ImageViewer implements Transactional, CloseableView {

    private final GUI gui;
    private final ELabel lastUpdate = new ELabel();
    private Consumer<Id> refresher;
    private Block block;
    private final ObjectComboField<Site> sitesField = new ObjectComboField<>(Site.class, "Active", true);
    private final ButtonLayout buttons;
    private record Dim(int w, int h) {}
    private final Map<Id, Dim> dims = new HashMap<>();
    private final Registration size;
    private double scale;

    public SiteView(GUI gui, boolean devMode) {
        super("Site View");
        this.gui = gui;
        gui.application.closeMenu();
        if(devMode) {
            HtmlTemplate.clearCache();
            allowMovement();
        }
        sitesField.setItemLabelGenerator(Site::getName);
        sitesField.addValueChangeListener(e -> {
            Site site = sitesField.getObject();
            if(site != null) {
                GUI.setSite(site);
            }
            imageNames.clear();
            components.clear();
            loadSite();
        });
        buttons = new ButtonLayout(
                devMode ? new ELabel("[Development Mode] ", "font-weight:bold") : null,
                new ELabel("Site:"),
                sitesField,
                new Button("Switch Block", VaadinIcon.CHART_3D, e -> loadBlocks()),
                lastUpdate,
                new Button("Dashboard", VaadinIcon.DASHBOARD, e -> gui.dashboard()),
                new Button("Value Charts", "chart", e -> gui.chart()),
                new Button("Status", VaadinIcon.GRID, e -> gui.statusGrid()),
                new Button("Send Control Command", VaadinIcon.PAPERPLANE_O, e -> gui.sendCommand()),
                gui.consumptionButton(),
                gui.dataButton(),
                new Button("Exit", e -> close())
        );
        sitesField.setValue(GUI.site() == null ? StoredObject.get(Site.class, "Active") : GUI.site());
        if(GUI.isFixedSite()) {
            sitesField.setReadOnly(true);
        }
        Application a = Application.get();
        a.getContentWidth();
        size = a.addContentResizedListener((w, h) -> reload());
    }

    private void refreshStatus(Id blockId) {
        gui.application.access(() -> {
            if(block != null && block.getId().equals(blockId)) {
                loadBlockData();
            }
            gui.lastUpdate(lastUpdate);
        });
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(block == null) {
            block = StoredObject.get(Block.class, "Active");
            if(block == null) {
                warning("No active block found");
                return;
            }
        }
        super.execute(parent, doNotLock);
        if(refresher == null) {
            loadBlockData();
            gui.lastUpdate(lastUpdate);
            gui.application.setPollInterval(this, 30000);
            refresher = this::refreshStatus;
            DataSet.register(refresher);
        }
    }

    @Override
    public void clean() {
        size.remove();
        if(refresher != null) {
            DataSet.unregister(refresher);
            refresher = null;
        }
        gui.application.stopPolling(this);
        super.clean();
    }

    private void reload() {
        if(block != null) {
            loadBlock(block);
            return;
        }
        loadSite();
    }

    private void loadSite() {
        block = null;
        gui.units.clear();
        removeAll();
        if(GUI.site() == null) {
            warning("Select a site");
            return;
        }
        MediaFile mf = null;
        String in = GUI.site().getImageName();
        if(!in.isBlank()) {
            mf = getMedia(in);
        }
        setBackgroundSource(mf);
        loadBlocks();
    }

    private void loadBlocks() {
        if(!executing()) {
            return;
        }
        gui.units.clear();
        block = null;
        gui.selectBlock(this::loadBlock);
    }

    private void loadBlock(Block block) {
        this.block = block;
        if(block != null) {
            loadBlock();
        }
    }

    private void loadBlock() {
        gui.units.clear();
        StoredObject.list(Unit.class, "Block=" + block.getId() + " AND Active", true).collectAll(gui.units);
        if(gui.units.isEmpty()) {
            warning("No units defined/active for - " + block.getName());
            return;
        }
        MediaFile mf = getMedia(block.getImageName());
        if(mf == null) {
            return;
        }
        removeAll();
        setBackgroundSource((String) null);
        setBlockImage(mf);
        buildTree(true);
        if(!(DataSet.getTime() > 0 || isMovementAllowed())) {
            Span span = new Span("UNKNOWN STATE - UNABLE TO GET DATA");
            String transform = "rotate(-45deg)";
            if(scale > 0) {
               transform += " scale(" + scale + ")";
            }
            span.getStyle().set("font-weight", "bold").set("transform", transform)
                    .set("transform-origin", "50% 50%").set("background", "red").set("color", "white");
            add(span, 0, 200);
        }
    }

    private Dim dim(MediaFile mf) {
        Dim dim = dims.get(mf.getId());
        if (dim == null) {
            try {
                BufferedImage bi = ImageIO.read(mf.getFile().getContent());
                dim = new Dim(bi.getWidth(), bi.getHeight());
            } catch (IOException e) {
                return null;
            }
            dims.put(mf.getId(), dim);
        }
        return dim;
    }

    private void setBlockImage(MediaFile mf) {
        scale = 0;
        int offsetX = 0, offsetY = 0;
        int iw = -1, ih = -1;
        try {
            Dim dim = dim(mf);
            if(dim == null) {
                return;
            }
            Application a = Application.get();
            int dw = a.getContentWidth(), dh = a.getContentHeight();
            if(!isMovementAllowed()) {
                scale = Math.min(dw * 0.8 / dim.w, dh * 0.8 / dim.h);
            }
            if(Math.abs(scale - 1) < 0.1) { // Scaling is not worth here
                scale = 0;
            }
            iw = dim.w;
            ih = dim.h;
            if(scale > 0) {
                iw = (int)((iw * scale) + 0.5);
                ih = (int)((ih * scale) + 0.5);
            }
            offsetX = Math.max(0, (dw >> 1) - (iw >> 1));
            offsetY = Math.max(0, (dh >> 1) - (ih >> 1));
        } finally {
            Image bi = new Image(mf);
            if(scale > 0) {
                bi.setWidth(iw + "px");
                bi.setHeight(ih + "px");
            } else {
                bi.setWidth(null);
                bi.setHeight(null);
            }
            setParentImage(bi, offsetX, offsetY);
            Div div = div(block.getName(), block.getCaptionStyle());
            if(scale > 0) {
                div.getStyle().set("transform", "scale(" + scale + ")");
            }
            add(div, block.getCaptionX(), block.getCaptionY(), true);
        }
    }

    private Div div(String text, String styleText) {
        Div div = new Div();
        div.setText(text);
        Style style = div.getStyle();
        styleText.lines().forEach(s -> {
            int p = s.indexOf(':');
            if(p > 0) {
                style.set(s.substring(0, p), s.substring(p + 1));
            }
        });
        return div;
    }

    private void loadBlockData() {
        if(GUI.site() != null && block != null) {
            buildTree(false);
        }
    }

    private void buildTree(boolean init) {
        if(init) {
            blockContents.clear();
            Block b = gui.block();
            if(b != null) {
                BlockDisplayContent.list(b).forEach(BlockContent::new);
            }
        }
        DataSet.getSites().stream().filter(s -> s.getSite().getId().equals(GUI.site().getId()))
                .forEach(s -> buildBranches(init, s));
    }

    private void buildBranches(boolean init, DataSet.AbstractData parent) {
        parent.children().forEach(row -> {
            if(row instanceof DataSet.UnitData ud) {
                if(gui.units.contains(ud.getUnit())) {
                    ud.getDataStatus().forEach(ds -> add(ds, init));
                }
            }
            buildBranches(init, row);
        });
    }

    private void add(DataSet.DataStatus<?> ds, boolean init) {
        updateBlockContents(ds);
        ValueDefinition<?> vd = ds.getValueDefinition();
        if(!vd.getShowImage()) {
            return;
        }
        Component component = getComponent(ds);
        if(init) {
            Unit unit = ds.getUnit();
            if(scale > 0) {
                add(component, (int)((vd.getImageX(unit) * scale) + 0.5),
                        (int)((vd.getImageY(unit) * scale) + 0.5));
            } else {
                add(component, vd.getImageX(unit), vd.getImageY(unit), ds);
            }
        }
        if(component instanceof ValueImage image) {
            image.setSource(getMedia(false, imageName(vd).name(ds)));
        } else if(component instanceof ValueDiv valueDiv){
            valueDiv.set(ds);
        }
    }

    @Override
    public void removeAll() {
        super.removeAll();
        add(buttons, 0, 0, true);
    }

    private MediaFile getMedia(String... imageNames) {
        return getMedia(true, imageNames);
    }

    private MediaFile getMedia(boolean warn, String... imageNames) {
        MediaFile mf = SOServlet.getImage(imageNames);
        if(warn && mf == null) {
            StringBuilder s = new StringBuilder();
            for(String in: imageNames) {
                if(!s.isEmpty()) {
                    s.append(", ");
                }
                s.append(in);
            }
            warning("No image found: " + s);
        }
        return mf;
    }

    private final Map<Long, Component> components = new HashMap<>();

    private Component getComponent(DataSet.DataStatus<?> ds) {
        Component component = components.get(ds.getId());
        if(component == null) {
            ValueDefinition<?> vd = ds.getValueDefinition();
            if(new ImageName(vd).isEmpty()) {
                component = new ValueDiv(vd.getLabel(ds.getUnit()) + ' ');
            } else {
                component = new ValueImage();
            }
            String tooltip = vd.getTooltip(ds.getUnit());
            if(tooltip.length() > 1) {
                component.getElement().setAttribute("title", tooltip);
            }
            components.put(ds.getId(), component);
        }
        return component;
    }

    private final Map<String, ImageName> imageNames = new HashMap<>();

    ImageName imageName(ValueDefinition<?> vd) {
        ImageName in = imageNames.get(vd.getId() + "/" + block.getId());
        if(in == null) {
            in = new ImageName(vd);
        }
        return in;
    }

    private class ImageName extends ArrayList<String> {

        private ImageName(ValueDefinition<?> vd) {
            try {
                String p = vd.getImagePrefix() + "-";
                String u = block.getImageName() + "-";
                String po;
                if (vd instanceof AlarmSwitch) {
                    po = p + "Off";
                    find(u + po, po);
                    po = p + "On";
                    find(u + po, po);
                    if (stream().allMatch(Objects::isNull)) {
                        clear();
                        return;
                    }
                    if (stream().anyMatch(Objects::isNull)) {
                        clear();
                        po = p + "Off";
                        find(true, u + po, po);
                        po = p + "On";
                        find(true, u + po, po);
                    }
                    return;
                }
                po = p + "Lowest";
                find(u + po, po);
                po = p + "Lower";
                find(u + po, po);
                po = p + "Low";
                find(u + po, po);
                po = p + "Normal";
                find(u + po, po);
                po = p + "High";
                find(u + po, po);
                po = p + "Higher";
                find(u + po, po);
                po = p + "Highest";
                find(u + po, po);
                if (stream().allMatch(Objects::isNull)) {
                    clear();
                    return;
                }
                if (get(3) == null) {
                    warning("No alert image found for [" + vd.getCaption() + "]");
                }
                String s = getAny(0, 1, 2, 6, 5, 4);
                if (s == null) {
                    warning("No alert image found for " + "[" + vd.getCaption() + "]");
                    return;
                }
                set(s, 0, 1, 2);
                s = getAny(6, 5, 4);
                if (s == null) {
                    s = get(0);
                }
                set(s, 6, 5, 4);
            } finally {
                if(!isEmpty()) {
                    imageNames.put(vd.getId() + "/" + block.getId(), this);
                }
            }
        }

        private void set(String s, int... is) {
            for(int i: is) {
                if(get(i) == null) {
                    set(i, s);
                }
            }
        }

        private String getAny(int... is) {
            String s;
            for(int i: is) {
                if((s = get(i)) != null) {
                    return s;
                }
            }
            return null;
        }

        private void find(String... names) {
            find(false, names);
        }

        private void find(boolean warn, String... names) {
            MediaFile mf = getMedia(warn, names);
            add(mf == null ? null : mf.getName());
        }

        String name(DataSet.DataStatus<?> ds) {
            if(ds instanceof DataSet.AlarmStatus as) {
                return get(as.getValue() ? 1 : 0);
            }
            return get(ds.alarm() + 3);
        }
    }

    private class ValueImage extends Image {

        String current;

        @Override
        public void setSource(MediaFile mediaFile) {
            String name = mediaFile == null ? null : mediaFile.getName();
            if(!Objects.equals(current, name)) {
                current = name;
                super.setSource(mediaFile);
            }
            Dim dim = mediaFile != null && scale > 0 ? dim(mediaFile) : null;
            if (dim == null) {
                setWidth(null);
                setHeight(null);
            } else {
                setWidth((int)((dim.w * scale) + 0.5) + "px");
                setHeight((int)((dim.h * scale) + 0.5) + "px");
            }
        }
    }

    private class ValueDiv extends Div {

        private final Span value;

        private ValueDiv(String label) {
            var style = getStyle();
            for(com.storedobject.common.StyledBuilder.Style s: ValueStyle.textStyle()) {
                if(s != null) {
                    style.set(s.name(), s.value());
                }
            }
            if(label != null && !label.isBlank()) {
                Span span = new Span(label);
                style = span.getStyle();
                for(com.storedobject.common.StyledBuilder.Style s: ValueStyle.labelStyle()) {
                    if(s != null) {
                        style.set(s.name(), s.value());
                    }
                }
                add(span);
            }
            value = new Span("");
            add(value);
        }

        private void set(DataSet.DataStatus<?> ds) {
            var style = value.getStyle();
            for(com.storedobject.common.StyledBuilder.Style s: ValueStyle.styles(ds, false, "")) {
                if(s != null) {
                    style.set(s.name(), s.value());
                }
            }
            value.setText(ds.value());
            if(scale > 0) {
                getStyle().set("transform", "scale(" + scale + ")");
            } else {
                getStyle().remove("transform");
            }
        }
    }

    @Override
    protected boolean savePosition(Object item, int x, int y) {
        if(item instanceof DataSet.DataStatus<?> ds) {
            ValueDefinition<?> vd = ds.getValueDefinition();
            vd.setImageX(x);
            vd.setImageY(y);
            try {
                getTransactionManager().transact(t -> vd.savePosition(t, x, y, ds.getUnit()));
                return true;
            } catch (Exception e) {
                vd.reload();
            }
        }
        return false;
    }

    private final List<BlockContent> blockContents = new ArrayList<>();

    private void updateBlockContents(DataSet.DataStatus<?> ds) {
        blockContents.forEach(bc -> bc.values.stream().filter(v -> v.ds == ds).forEach(BlockContent.Value::set));
    }

    private class BlockContent extends TemplateLayout implements StringFiller {

        private static final String[] LABEL_STYLES = { "label-normal", "label-low", "label-lower", "label-lowest",
                "label-high", "label-higher", "label-highest" };
        private static final String[] VALUE_STYLES = { "value-normal", "value-low", "value-lower", "value-lowest",
                "value-high", "value-higher", "value-highest" };
        private static final String[] ALARM_STYLES = { "alarm-on", "alarm-off" };
        private final List<Value> values;

        BlockContent(BlockDisplayContent bc) {
            this(new BlockContentFiller(bc), bc);
        }

        BlockContent(BlockContentFiller filler, BlockDisplayContent bc) {
            super(content(bc));
            values = new ArrayList<>(filler.values);
            if(scale > 0) {
                add(this, (int)(bc.getX() * scale + 0.5), (int)(bc.getY() * scale + 0.5));
            }
            add(this, bc.getX(), bc.getY());
            blockContents.add(this);
        }

        private static Supplier<String> content(BlockDisplayContent bc) {
            String c = bc.getContent();
            return () -> c;
        }

        @Override
        protected Component createComponentForId(String cid) {
            String id = cid;
            if(id.startsWith("so-sv-")) {
                int p = id.lastIndexOf('_');
                if (p > 0) {
                    id = id.substring(p + 1);
                    if(StringUtility.isDigit(id)) {
                        long vid = Long.parseLong(id);
                        Value value = values.stream().filter(v -> v.id == vid).findAny().orElse(null);
                        if(value != null) {
                            return value;
                        }
                    }
                }
            }
            return super.createComponentForId(cid);
        }

        private static class Value extends Span {

            final long id = ID.newID();
            final DataSet.DataStatus<?> ds;
            final Function<DataSet.DataStatus<?>, String> value;
            private final int type; // 0: Label/caption, 1: Value, 2: Others

            private Value(DataSet.DataStatus<?> ds, Function<DataSet.DataStatus<?>, String> value, int type) {
                this.ds = ds;
                this.value = value;
                this.type = type;
                if(type == 2) {
                    setClassName("value-normal");
                }
            }

            void set() {
                setText(value.apply(ds));
                switch (type) {
                    case 0 -> {
                        removeClassNames(BlockContent.LABEL_STYLES);
                        addClassName("label-" + ds.statusLabel());
                    }
                    case 1 -> {
                        if(ds instanceof DataSet.AlarmStatus) {
                            removeClassNames(BlockContent.ALARM_STYLES);
                            addClassName("alarm-" + ds.statusLabel());
                        } else {
                            removeClassNames(BlockContent.VALUE_STYLES);
                            addClassName("value-" + ds.statusLabel());
                        }
                    }
                }
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (obj == null || getClass() != obj.getClass()) {
                    return false;
                }
                return id == ((Value)obj).id;
            }

            @Override
            public int hashCode() {
                return Objects.hash(id);
            }
        }
    }

    private class BlockContentFiller implements StringFiller {

        private final List<BlockContent.Value> values = new ArrayList<>();

        BlockContentFiller(BlockDisplayContent bc) {
            bc.setContent(StringUtility.fill(bc.getContent(), this));
        }

        @Override
        public String fill(String name) {
            if(name.startsWith("media:")) {
                return "${" + name + "}";
            }
            String unitName = null;
            int ordinality = 0;
            int p1 = name.indexOf(':');
            if(p1 > 0) {
                String ord = name.substring(p1 + 1);
                int p2 = ord.indexOf(':');
                if(p2 >= 0) {
                    unitName = ord.substring(p2 + 1);
                    ord = ord.substring(0, p2);
                }
                if(!ord.isEmpty()) {
                    if (!StringUtility.isDigit(ord)) {
                        return "??";
                    }
                    ordinality = Integer.parseInt(ord) - 1;
                }
                name = name.substring(0, p1);
            }
            boolean label = false;
            if(name.startsWith("Label-") && name.length() > 6) {
                label = true;
                name = name.substring(6);
            }
            boolean caption = false;
            if(!label && name.startsWith("Caption-") && name.length() > 8) {
                caption = true;
                name = name.substring(8);
            }
            boolean other = false;
            Function<DataSet.DataStatus<?>, String> value = null;
            if(!label && !caption) {
                p1 = name.indexOf('-');
                if(p1 > 0) {
                    String prefix = name.substring(0, p1);
                    switch (prefix) {
                        case "Min", "MinH" -> value = d -> d.value(d.hourlyStatistics().getMin(), true);
                        case "Max", "MaxH" -> value = d -> d.value(d.hourlyStatistics().getMax(), true);
                        case "Mean", "MeanH" -> value = d -> d.value(d.hourlyStatistics().getMean(), true);
                        case "SD", "SDH" -> {
                            value = d -> d.value(d.hourlyStatistics().getSD(), false);
                            other = true;
                        }
                        case "Count", "CountH" -> {
                            value = d -> d.value(d.hourlyStatistics().getCount(), false);
                            other = true;
                        }
                        case "MinD" -> value = d -> d.value(d.dailyStatistics().getMin(), true);
                        case "MaxD" -> value = d -> d.value(d.dailyStatistics().getMax(), true);
                        case "MeanD" -> value = d -> d.value(d.dailyStatistics().getMean(), true);
                        case "SDD" -> {
                            value = d -> d.value(d.dailyStatistics().getSD(), false);
                            other = true;
                        }
                        case "CountD" -> {
                            value = d -> d.value(d.dailyStatistics().getCount(), false);
                            other = true;
                        }
                        case "MinW" -> value = d -> d.value(d.weeklyStatistics().getMin(), true);
                        case "MaxW" -> value = d -> d.value(d.weeklyStatistics().getMax(), true);
                        case "MeanW" -> value = d -> d.value(d.weeklyStatistics().getMean(), true);
                        case "SDW" -> {
                            value = d -> d.value(d.weeklyStatistics().getSD(), false);
                            other = true;
                        }
                        case "CountW" -> {
                            value = d -> d.value(d.weeklyStatistics().getCount(), false);
                            other = true;
                        }
                        case "MinM" -> value = d -> d.value(d.monthlyStatistics().getMin(), true);
                        case "MaxM" -> value = d -> d.value(d.monthlyStatistics().getMax(), true);
                        case "MeanM" -> value = d -> d.value(d.monthlyStatistics().getMean(), true);
                        case "SDM" -> {
                            value = d -> d.value(d.monthlyStatistics().getSD(), false);
                            other = true;
                        }
                        case "CountYM" -> {
                            value = d -> d.value(d.yearlyStatistics().getMin(), true);
                            other = true;
                        }
                        case "MinY" -> value = d -> d.value(d.yearlyStatistics().getMin(), true);
                        case "MaxY" -> value = d -> d.value(d.yearlyStatistics().getMax(), true);
                        case "MeanY" -> value = d -> d.value(d.yearlyStatistics().getMean(), true);
                        case "SDY" -> {
                            value = d -> d.value(d.yearlyStatistics().getSD(), false);
                            other = true;
                        }
                        case "CountY" -> {
                            value = d -> d.value(d.yearlyStatistics().getCount(), false);
                            other = true;
                        }
                        default -> {
                            return prefix + "?";
                        }
                    }
                    name = name.substring(p1 + 1);
                } else {
                    p1 = name.indexOf('.');
                    if(p1 > 0) {
                        String attribute = name.substring(p1 + 1);
                        name = name.substring(0, p1);
                        value = d -> d.value(attribute);
                    } else {
                        value = DataSet.DataStatus::value;
                    }
                }
            }
            DataSet.DataStatus<?> ds = searchTree(name, ordinality, unitName);
            if(ds == null) {
                return name + "=N/A";
            }
            BlockContent.Value v;
            if(label) {
                values.add(v = new BlockContent.Value(ds, DataSet.DataStatus::label, 0));
                return "<span id=\"so-sv-label_" + v.id + "\">*</span>";
            } else if(caption) {
                values.add(v = new BlockContent.Value(ds, DataSet.DataStatus::caption, 0));
                return "<span id=\"so-sv-caption_" + v.id + "\">*</span>";
            } else if(other) {
                values.add(v = new BlockContent.Value(ds, value, 2));
                return "<span id=\"so-sv-other_" + v.id + "\">*</span>";
            } else {
                values.add(v = new BlockContent.Value(ds, value, 1));
                return "<span id=\"so-sv-value_" + v.id + "\">*</span>";
            }
        }

        private DataSet.DataStatus<?> searchTree(String name, int ordinality, String unitName) {
            DataSet.SiteData siteData =
            DataSet.getSites().stream().filter(s -> s.getSite().getId().equals(GUI.site().getId())).findAny()
                    .orElse(null);
            return siteData == null ? null : searchBranches(name, ordinality, unitName, siteData);
        }

        private DataSet.DataStatus<?> searchBranches(String name, int ordinality, String unitName,
                                                     DataSet.AbstractData parent) {
            for(DataSet.AbstractData row: parent.children()) {
                if(row instanceof DataSet.UnitData ud) {
                    if(gui.units.contains(ud.getUnit())) {
                        for(DataSet.DataStatus<?> ds: ud.getDataStatus()) {
                            if(ds.ordinality() == ordinality && ds.getValueDefinition().getName().equals(name)) {
                                if(unitName != null) {
                                    String ucName = ds.getUnit().getClass().getName();
                                    ucName = ucName.substring(ucName.lastIndexOf('.') + 1);
                                    if(!ucName.equals(unitName)) {
                                        continue;
                                    }
                                }
                                return ds;
                            }
                        }
                    }
                }
                DataSet.DataStatus<?> ds = searchBranches(name, ordinality, unitName, row);
                if(ds != null) {
                    return ds;
                }
            }
            return null;
        }
    }
}

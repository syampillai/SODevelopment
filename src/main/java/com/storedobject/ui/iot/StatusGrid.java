package com.storedobject.ui.iot;

import com.storedobject.common.StringList;
import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.iot.*;
import com.storedobject.ui.DataGrid;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.HTMLText;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.HTMLGenerator;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class StatusGrid extends DataGrid<DataSet.DataStatus> {

    private final ELabel lastUpdate = new ELabel();
    private Consumer<Id> refresher;
    private final BlockComboField blockField = new BlockComboField();
    private final Checkbox allBlocks = new Checkbox("All Blocks"),
            allAttributes = new Checkbox("All Attributes"),
            commandsOnly = new Checkbox("Controls Only");
    private final GUI gui;
    private volatile boolean updating = false;

    public StatusGrid(GUI gui) {
        super(DataSet.DataStatus.class, StringList.create("Block", "Unit", "Attribute", "Value", "Status", "Message"));
        this.gui = gui;
        setCaption("Status");
        blockField.addValueChangeListener(e -> loadStatus());
        allBlocks.addValueChangeListener(e -> loadStatus());
        allAttributes.addValueChangeListener(e -> loadStatus());
        commandsOnly.addValueChangeListener(e -> loadStatus());
        addConstructedListener(l -> {
            addComponentColumn(this::send).setFlexGrow(0).setWidth("120px");
            Site site = gui.getSite();
            if(site != null) {
                site = StoredObject.get(Site.class, "Active");
                gui.setSite(site);
            }
            blockField.setSite(site);
            loadStatus();
        });
    }

    @Override
    public boolean isColumnSortable(String columnName) {
        return false;
    }

    @Override
    public String getColumnCaption(String columnName) {
        return "Block".equals(columnName) ? "Building/Block" : super.getColumnCaption(columnName);
    }

    public String getBlock(DataSet.DataStatus ds) {
        return ds.getUnit().getBlock().getName();
    }

    public String getUnit(DataSet.DataStatus ds) {
        return ds.getUnit().getName();
    }

    public String getMessage(DataSet.DataStatus ds) {
        return ds.getValueDefinition().getAlertMessage(ds.alarm());
    }

    @Override
    public Component createHeader() {
        return new ButtonLayout(
                allBlocks,
                new ELabel("|", "green"),
                allAttributes,
                new ELabel("|", "green"),
                commandsOnly,
                new ELabel("|", "green"),
                new ELabel("Block:"),
                blockField,
                lastUpdate,
                //new Button("Dashboard", VaadinIcon.DASHBOARD, e -> gui.showDashboard()),
                new Button("Value Charts", "chart", e -> gui.showChart()),
                new Button(gui.getSiteViewLabel(), VaadinIcon.FACTORY, e -> gui.showSiteView()),
                new Button("Send Control Command", VaadinIcon.PAPERPLANE_O, e -> gui.sendCommand()),
                gui.consumptionButton(),
                gui.dataButton(),
                new Button("Exit", e -> close())
        );
    }

    private void refreshStatus(Id blockId) {
        gui.application.access(() -> {
            if(blockId != null && blockId.equals(gui.blockId()) && !updating && !allBlocks.getValue()) {
                loadStatus();
            }
            gui.lastUpdate(lastUpdate);
        });
    }

    @Override
    public void execute(View lock) {
        super.execute(lock);
        if(refresher == null) {
            loadStatus();
            gui.lastUpdate(lastUpdate);
            gui.application.setPollInterval(this, 30000);
            refresher = this::refreshStatus;
            DataSet.register(refresher);
        }
    }

    @Override
    public void clean() {
        updating = false;
        if(refresher != null) {
            DataSet.unregister(refresher);
            refresher = null;
        }
        gui.application.stopPolling(this);
        super.clean();
    }

    private void loadStatus() {
        updating = true;
        clear();
        buildTree();
        getColumn("Block").setVisible(allBlocks.getValue());
        updating = false;
    }

    private void buildTree() {
        Block b = blockField.getValue();
        if(b == null) {
            return;
        }
        gui.setBlock(b);
        Site site = b.getSite();
        Id blockId = allBlocks.getValue() ? null : Id.ZERO;
        if(Id.ZERO.equals(blockId)) {
            Block block = gui.getBlock();
            if(block != null) {
                blockId = block.getId();
            }
        }
        Id finalBlockId = blockId;
        DataSet.getSites().stream().filter(s -> s.getSite().getId().equals(site.getId()))
                .forEach(s -> buildBranches(s, finalBlockId, allAttributes.getValue(), commandsOnly.getValue()));
    }

    private void buildBranches(DataSet.AbstractData parent, Id blockId, boolean all, boolean onlyCommands) {
        parent.children().forEach(row -> {
            if(row instanceof DataSet.UnitData ud) {
                if (blockId == null || ud.getUnit().getBlockId().equals(blockId)) {
                    if (all && !onlyCommands) {
                        this.addAll(ud.getDataStatus());
                    } else {
                        ud.getDataStatus().forEach(ds -> {
                            if (onlyCommands && !ds.getValueDefinition().getCommand()) {
                                return;
                            }
                            if (all || ds.alarm() != 0) {
                                add(ds);
                            }
                        });
                    }
                }
            }
            buildBranches(row, blockId, all, onlyCommands);
        });
    }

    public String getAttribute(DataSet.DataStatus ds) {
        return ds.label();
    }

    public String getValue(DataSet.DataStatus ds) {
        return ds.value();
    }

    public HTMLGenerator getStatus(DataSet.DataStatus ds) {
        HTMLText s = new HTMLText();
        append(s, ds);
        return s;
    }

    private void append(HTMLText h, DataSet.DataStatus ds) {
        String value;
        if(ds instanceof DataSet.AlarmStatus) {
            value = ds.value();
        } else {
            value = switch (ds.alarm()) {
                case 3 -> "Highest";
                case 2 -> "Higher";
                case 1 -> "High";
                case 0 -> "Normal";
                case -1 -> "Low";
                case -2 -> "Lower";
                case -3 -> "Lowest";
                default -> "";
            };
        }
        h.append(" " + value + " ",
                ValueStyle.styles(ds, false,
                        "display:inline-block;padding:5px 10px;margin:5px;border-radius:10px"));
    }

    private Component send(DataSet.DataStatus ds) {
        ValueDefinition vd = ds.getValueDefinition();
        if(vd.getCommand()) {
            return new Button("Send Control", (String)null, e -> new SendCommand(null, ds.getUnit(), vd)
                    .execute());
        }
        return new Span();
    }

    public void setBlock(Block block) {
        blockField.setValue(block);
    }
}

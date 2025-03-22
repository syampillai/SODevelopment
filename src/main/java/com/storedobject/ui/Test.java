package com.storedobject.ui;

// Start of Notes
/*

 */
// End of Notes
import com.storedobject.core.MediaFile;
import com.storedobject.core.StringUtility;
import com.storedobject.iot.AlarmSwitch;
import com.storedobject.iot.Block;
import com.storedobject.iot.DataSet;
import com.storedobject.iot.Unit;
import com.storedobject.ui.iot.BlockView;
import com.storedobject.ui.iot.GUI;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.template.Id;

import java.util.List;

@SuppressWarnings("unused")
public class Test extends BlockView {
    private static final String VIEW_ACCESS = "0";
    private static final int DOWNLOAD_ACCESS = 1;
    private static final int CONTROL_COMMAND_ACCESS = 2;
    private static final int DOWNLOAD_DATA_AND_SEND_COMMAND = 3;

    @Id private UnorderedList dataList;
    @Id private com.vaadin.flow.component.button.Button unitsButton;
    @Id private Button notificationButton;
    @Id private Div notificationList;
    @Id private UnorderedList unitList;
    @Id private Image unitImage;
    @Id private Button alarmButton;
    @Id private Button chartButton;
    @Id private Button rooftopButton;
    @Id private Button commandButton;
    private Unit unit;
    private final GUI gui = new GUI();
    private final int accessSpecifier;

    public Test() {
        this(VIEW_ACCESS);
    }

    public Test(String accessSpecifier) {
        super();
        setCaption("Dashboard");
        gui.setBlockView(this);
        gui.setSiteViewLabel("Rooftop View");
        this.accessSpecifier = parseAccessSpecifier(accessSpecifier);
        log(accessSpecifier);
        gui.setAllowDownload((this.accessSpecifier & DOWNLOAD_ACCESS) == 1);
        gui.setAllowCommand((this.accessSpecifier & CONTROL_COMMAND_ACCESS) == 2);
    }

    private int parseAccessSpecifier(String accessSpecifier) {
        if (StringUtility.isWhite(accessSpecifier) || !StringUtility.isDigit(accessSpecifier)) {
            return 0;
        }
        return Integer.parseInt(accessSpecifier);
    }

    @Override
    public void viewConstructed(View view) {
        super.viewConstructed(view);
        Block block = getBlock();
        if (block != null) {
            paint(block);
        }
        commandButton.setVisible((accessSpecifier & CONTROL_COMMAND_ACCESS) == 2);
    }

    @Override
    protected void paint(Block block) {
        resetUI();
        paintUnitList();
        super.paint(block);
        notificationList.setVisible(false);
        unitList.setVisible(true);
    }

    private void resetUI() {
        if (dataList != null) {
            dataList.removeAll();
        }
        if (unitList != null) {
            unitList.removeAll();
        }
        if (unitImage != null) {
            unitImage.clear();
        }
    }

    private void paintUnitList() {
        List<Unit> units = getBlock().listUnits().toList();
        for (Unit unit : units) {
            Button button =
                    new Button(
                            unit.getName(),
                            e -> {
                                this.unit = unit;
                                reload();
                            });
            button.setClassName("unit-button");
            unitList.add(button);
        }
    }

    @Override
    protected void paint(DataSet.LimitStatus limitStatus) {
        super.paint(limitStatus);
        if (unit == null
                || !com.storedobject.core.Id.equals(unit.getId(), limitStatus.getUnit().getId())) {
            return;
        }
        //Thread.dumpStack();
        ListItem listItem = new ListItem();
        listItem.add(new Paragraph(limitStatus.getValueDefinition().getCaption(limitStatus.getUnit())));
        listItem.add(new Paragraph(limitStatus.value()));
        dataList.add(listItem);
    }

    @Override
    protected void paint(DataSet.AlarmStatus alarmStatus) {
        if (unit == null
                || !com.storedobject.core.Id.equals(unit.getId(), alarmStatus.getUnit().getId())) {
            return;
        }

        String caption = alarmStatus.getValueDefinition().getCaption();

        int value = alarmStatus.getValue() ? 1 : 0;
        if (value == alarmStatus.getValueDefinition().getAlarmWhen()) {
            ListItem listItem = new ListItem();
            listItem.add(new Paragraph(caption));
            listItem.add(new Paragraph(alarmStatus.getValueDefinition().getAlarmWhenValue()));
            notificationList.add(listItem);
        }
        setUnitImage(alarmStatus);
        super.paint(alarmStatus);
    }

    private void setUnitImage(DataSet.AlarmStatus alarmStatus) {
        AlarmSwitch valueDefinition = alarmStatus.getValueDefinition();
        String imageName = StringUtility.toString(valueDefinition.getName()) + "-";
        imageName += alarmStatus.alarm() == 0 ? "GREEN" : "RED";
        MediaFile image = MediaFile.get(imageName);
        if (image == null) {
            return;
        }
        unitImage.setSource(image);
    }

    @Override
    protected Component createComponentForId(String id) {
        return switch (id) {
            case "dataList", "unitList" -> new UnorderedList();
            case "unitsButton" ->
                    new Button(
                            "Units",
                            e -> {
                                notificationList.setVisible(false);
                                unitList.setVisible(true);
                                unitsButton.addClassName("active");
                                notificationButton.removeClassName("active");
                            });
            case "notificationButton" ->
                    new Button(
                            "Notifications",
                            e -> {
                                notificationList.setVisible(true);
                                unitList.setVisible(false);
                                unitsButton.removeClassName("active");
                                notificationButton.addClassName("active");
                            });
            case "unitImage" -> new Image();
            case "alarmButton" ->
                    new Button(
                            "Show Alarm",
                            e -> {
                                // gui.setSite(getSite());
                                gui.setBlock(getBlock());
                                gui.showStatusGrid();
                            });
            case "rooftopButton" ->
                    new Button(
                            "Rooftop Layout",
                            e -> {
                                gui.setBlock(getBlock());
                                gui.showSiteView();
                            });
            case "chartButton" ->
                    new Button(
                            "Show Graph",
                            e -> {
                                gui.setBlock(getBlock());
                                gui.showChart();
                            });
            case "commandButton" ->
                    new Button(
                            "Execute Command",
                            e -> {
                                gui.setBlock(getBlock());
                                gui.sendCommand();
                            });
            default -> super.createComponentForId(id);
        };
    }

    @Override
    protected void reloading() {
        if (dataList != null) {
            dataList.removeAll();
        }
        if (notificationList != null) {
            notificationList.removeAll();
        }
        if (unitImage != null) {
            unitImage.clear();
        }
    }

    @Override
    protected void clicked(DataSet.AlarmStatus alarmStatus) {
        unit = alarmStatus.getUnit();
        execute();
        reload();
        setUnitImage(alarmStatus);
        super.clicked(alarmStatus);
    }

    @Override
    protected void clicked(DataSet.LimitStatus limitStatus) {
        execute();
        super.clicked(limitStatus);
    }
}

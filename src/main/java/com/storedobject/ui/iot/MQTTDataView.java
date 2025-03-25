package com.storedobject.ui.iot;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringUtility;
import com.storedobject.iot.Block;
import com.storedobject.iot.Data;
import com.storedobject.iot.MQTT;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.ObjectViewer;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ComboField;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.icon.VaadinIcon;

public class MQTTDataView extends BlockSelector {

    private final ComboField<Class<?>> classField;

    public MQTTDataView() {
        super("MQTT Data View");
        MQTT mqtt = MQTT.get();
        if(mqtt == null) {
            throw new SORuntimeException("MQTT processor is not running");
        }
        classField = new ComboField<>("Data Type", MQTT.getDataClasses());
        classField.setItemLabelGenerator(com.storedobject.core.StringUtility::makeLabel);
        addField(classField);
        setRequired(classField);
        addField(new ELabelField("Messages in the Queue", StringUtility.format(MQTT.getPendingMessageCount(), true)));
        long totalMessageCount = MQTT.getMessageCount();
        addField(new ELabelField("Messages Processed", StringUtility.format(totalMessageCount, true)));
        addField(new ELabelField("Messages with Errors", StringUtility.format(MQTT.getErrorMessageCount(), true)));
        addField(new ELabelField("Messages Purged", StringUtility.format(MQTT.getPurgedMessageCount(), true)));
        addField(new ELabelField("Messages Ignored", StringUtility.format(MQTT.getIgnoredMessageCount(), true)));
        if(totalMessageCount > 0) {
            addField(new ELabelField("Minimum Processing Time",
                    StringUtility.format(MQTT.getMinProcessingTime(), true) + " ms"));
            addField(new ELabelField("Maximum Processing Time",
                    StringUtility.format(MQTT.getMaxProcessingTime(), true) + " ms"));
            addField(new ELabelField("Processing Time of Last Message",
                    StringUtility.format(MQTT.getLastProcessingTime(), true) + " ms"));
            ELabelField a = new ELabelField("Average Processing Time (Per Message)");
            long time = MQTT.getTotalProcessingTime();
            long avg = time / totalMessageCount;
            if(avg > 0) {
                a.append(StringUtility.format(avg, true) + " ms");
            } else {
                a.append(StringUtility.format((double)time / (double) totalMessageCount, true) + " ms");
            }
            a.update();
            addField(a);
        }
        add(new Html("<hr/>"));
        setColumns(2);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        buttonPanel.add(new Button("Refresh", e -> again()));
        buttonPanel.add(new Button("Report Duplicate Errors", VaadinIcon.BUG, e -> {
            MQTT.reportDuplicateErrors();
            message("New duplicate errors will be reported");
            again();
        }));
    }

    private void again() {
        close();
        new MQTTDataView().execute();
    }

    @Override
    protected boolean accept(Block block) throws Exception {
        Data data = MQTT.getData(block, classField.getValue());
        if(data == null) {
            message("No data found");
            return false;
        }
        close();
        view(new ObjectViewer(Application.get()), data);
        return true;
    }

    private static void view(ObjectViewer ov, Data data) {
        ov.view(data, "Refresh", o -> view(ov, data));
    }
}

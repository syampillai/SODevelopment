package com.storedobject.ui.iot;

import com.storedobject.core.DateUtility;
import com.storedobject.iot.*;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.vaadin.*;

public class ViewConsumption extends AbstractConsumptionSelector {

    final ChoiceField periodicityField = new ChoiceField("Periodicity", ConsumptionDashboard.periodicity);
    private final IntegerField yearFromField;
    private final IntegerField yearToField;

    public ViewConsumption() {
        this(null, null);
    }

    public ViewConsumption(Resource resource, Block block) {
        super("View Consumption", resource, block);
        int year = DateUtility.getYear();
        yearFromField = new IntegerField(year, 4);
        yearFromField.setWidth("4em");
        yearToField = new IntegerField(year, 4);
        yearToField.setWidth("4em");
        addField(periodicityField, new CompoundField("Year Range", yearFromField, new ELabel("to"), yearToField));
    }

    @Override
    protected void accept(Resource resource, Block block) throws Exception {
        new ConsumptionList(resource, block, periodicityField.getValue(), yearFromField.getValue(),
                yearToField.getValue()).view();
    }
}

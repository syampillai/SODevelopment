package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.IntegerField;

public class Test extends DataForm {

    //  public ComputedInteger dayInteger;
    public ComputedIntegerField dayIntegerField, fhField, flField;

    public Test() {
        super("Test");
    }

    @Override
    protected void buildFields() {
        ComputedDate date = ComputedDate.create(DateUtility.today());
        ComputedDateField dateField = new ComputedDateField("Date ", date);

        addField(dateField);

        // ComputedInteger Field
        ComputedInteger dayInteger = new ComputedInteger(30, true);
        dayIntegerField = new ComputedIntegerField("Show for next ", dayInteger);
        IntegerField x = new IntegerField("Integer", 0, 8, true, true);
        addField(x);
        addField(dayIntegerField);
        //dayIntegerField.setValue(dayInteger);

        dayInteger = new ComputedInteger(50);
        dayInteger.consider(true);
        fhField = new ComputedIntegerField(" FH Integer ", dayInteger);
        addField(fhField);

        flField = new ComputedIntegerField(" FL Integer ");
        flField.setValue(dayInteger);
        addField(flField);

        // ComputedDouble Field
        ComputedDoubleField fcField = new ComputedDoubleField("FC Double ");
        fcField.setValue(6.0d);
        addField(fcField);

        ComputedDouble cd = new ComputedDouble(10.0d);
        ComputedDoubleField totalFC = new ComputedDoubleField("Computed Double with value change listener");
        totalFC.setValue(cd);
        error(totalFC.getValue());
        totalFC.addValueChangeListener(e -> error(e.getValue()));
        addField(totalFC);

        // ComputedLong Field
        ComputedLongField totalFH = new ComputedLongField("Total FH Long");
        ComputedLong cl = new ComputedLong(100);
        totalFH.setValue(cl);
        addField(totalFH);

        // ComputedMinutes Field
        ComputedMinute minute = new ComputedMinute(100, true);
        ComputedMinutesField minutesField = new ComputedMinutesField("Aircraft FH");
        minutesField.setValue(minute);
        addField(minutesField);

        // Phone
        PhoneField phoneField = new PhoneField("Phone");
        addField(phoneField);

        super.buildFields();
    }

    @Override
    protected boolean process() {
        return false;
    }
}

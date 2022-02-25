package com.storedobject.ui;

import com.storedobject.common.Country;
import com.storedobject.core.*;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;
import com.storedobject.vaadin.TextField;
import com.storedobject.vaadin.TokensField;
import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.List;

public class Test extends DataForm {

    private DateField dateField;
    private TimestampField tsField;
    private MoneyField mField;
    private DatePicker vdate;

    public Test() {
        super("TestTokenField");
    }

    @Override
    protected boolean process() {
        message("V Date: " + vdate.getValue());
        message("Date: " + dateField.getValue());
        message("Time: " + tsField.getValue());
        message("Amount: " + mField.getValue());
        return false;
    }

    @Override
    protected void buildFields() {
        super.buildFields();
        add(vdate = new DatePicker("VDate"));
        setRequired(vdate);
        QuantityField qField = new QuantityField("Qty");
        qField.setRequired(true);
        ObjectField<Person> pField = new ObjectField<>("Person", Person.class);
        pField.setFilter(p -> p.getFirstName().startsWith("S"));
        List<Country> countries = Country.list();
        TokensField<Country> tokensField = new TokensField<>("Country", countries);
        tokensField.setItemLabelGenerator(Country::getName);
        addField(pField, qField, dateField = new DateField("Date"), tsField = new TimestampField("Time"),
                mField = new MoneyField("Amount"),
                tokensField);
        mField.setRequired(true);
        TextField tf = new TextField("Text");
        addField(tf);
        tf.setRequired(true);
        setRequired(dateField);
    }
}

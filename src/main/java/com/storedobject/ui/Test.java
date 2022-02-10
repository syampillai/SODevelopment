package com.storedobject.ui;

import com.storedobject.common.Country;
import com.storedobject.common.StringList;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
import com.storedobject.core.Person;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test extends DataForm {

    private DateField dateField;

    public Test() {
        super("TestTokenField");
    }

    @Override
    protected boolean process() {
        message("Date: " + dateField.getValue());
        return false;
    }

    @Override
    protected void buildFields() {
        super.buildFields();
        List<Country> countries = Country.list();
        TokensField<Country> tokensField = new TokensField<>("Country", countries);
        tokensField.setItemLabelGenerator(Country::getName);
        addField(dateField = new DateField("Date"), tokensField);
    }
}

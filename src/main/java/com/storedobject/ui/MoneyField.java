package com.storedobject.ui;

import com.storedobject.core.Money;
import com.storedobject.vaadin.Clickable;
import com.storedobject.vaadin.CustomTextField;
import com.storedobject.vaadin.RequiredField;
import com.storedobject.vaadin.util.HasTextValue;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;

import java.math.BigDecimal;
import java.util.*;

public class MoneyField extends CustomTextField<Money> implements RequiredField {

    private Span symbol;
    private List<Currency> allowedCurrencies = null;
    private boolean required = false;
    private TextField textField;

    public MoneyField() {
        this((String)null);
    }

    public MoneyField(int width) {
        this(width, (Currency) null);
    }

    public MoneyField(int width, Currency currency) {
        this(null, width, currency);
    }

    public MoneyField(int width, String currency) {
        this(null, width, currency);
    }

    public MoneyField(String label) {
        this(label, 0, (Currency) null);
    }

    public MoneyField(String label, int width) {
        this(label, width, (Currency)null);
    }

    public MoneyField(String label, int width, String currency) {
        this(label, width, Money.getCurrency(currency));
    }

    public MoneyField(String label, int width, Currency currency) {
        this(currency == null ? new Money() : new Money(currency));
        if(width < 4 || width > 22) {
            width = 22;
        }
        getField().setMaxLength(width);
        Money v = getEmptyValue();
        setValue(v);
        setPresentationValue(v);
        setLabel(label);
    }

    private MoneyField(Money defaultValue) {
        super(defaultValue);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Application a = Application.get();
        if(a != null) {
            ((TextField) getField()).setAutoselect(!a.getWebBrowser().isAndroid());
        }
    }

    @Override
    protected void customizeTextField(HasTextValue textField) {
        if(symbol == null) {
            symbol = new Span();
            new Clickable<>(symbol, c -> changeCurrency());
        }
        this.textField = (TextField) textField;
        this.textField.setPrefixComponent(symbol);
        this.textField.setRequired(required);
    }

    @Override
    public void setValue(Money value) {
        if(value == null) {
            value = new Money();
        }
        if(allowedCurrencies != null && !allowedCurrencies.contains(value.getCurrency())) {
            value = new Money(value.getValue(), allowedCurrencies.get(0));
        }
        super.setValue(value);
    }

    @Override
    protected Money getModelValue(String string) {
        Money m;
        string = string.trim().replace(",", "");
        try {
            Double.parseDouble(string);
            m = new Money(new BigDecimal(string), getCurrency());
        } catch(Throwable notNumber) {
            m = Money.create(string);
            if(allowedCurrencies != null && !allowedCurrencies.contains(m.getCurrency())) {
                m = new Money(m.getValue(), allowedCurrencies.get(0));
                focus();
            }
        }
        if(m.wasRounded()) {
            focus();
        }
        setPresentationValue(m);
        return m;
    }

    @Override
    protected String format(Money value) {
        return required && value.isZero() ? "" : value.toString(false);
    }

    @Override
    protected void setPresentationValue(Money value) {
        getField().setValue(format(value));
        symbol.setText(value.getCurrency().getCurrencyCode());
    }

    public Currency getCurrency() {
        return Money.getCurrency(symbol.getText());
    }

    public void setAllowedCurrencies(Collection<Currency> allowedCurrencies) {
        if(allowedCurrencies == null || allowedCurrencies.isEmpty()) {
            this.allowedCurrencies = null;
        } else {
            this.allowedCurrencies = new ArrayList<>(allowedCurrencies);
            checkCurrency();
        }
    }

    public void setAllowedCurrencies(Currency... allowedCurrencies) {
        if(allowedCurrencies == null || allowedCurrencies.length == 0) {
            this.allowedCurrencies = null;
        } else {
            this.allowedCurrencies = Arrays.asList(allowedCurrencies);
            checkCurrency();
        }
    }

    public void setAllowedCurrencies(String... allowedCurrencies) {
        if(allowedCurrencies == null || allowedCurrencies.length == 0) {
            this.allowedCurrencies = null;
        } else {
            List<Currency> currencies = new ArrayList<>();
            Currency currency;
            for(String c: allowedCurrencies) {
                currency = Currency.getInstance(c);
                if(currency != null) {
                    currencies.add(currency);
                }
            }
            setAllowedCurrencies(currencies);
        }
    }

    private void checkCurrency() {
        if(allowedCurrencies != null) {
            Money m = getValue();
            if(!allowedCurrencies.contains(m.getCurrency())) {
                setValue(new Money(m.getValue(), allowedCurrencies.get(0)));
            }
        }
    }

    private void changeCurrency() {
        if(allowedCurrencies != null && allowedCurrencies.size() > 1 && !isReadOnly()) {
            Money m = getValue();
            int i = allowedCurrencies.indexOf(m.getCurrency()) + 1;
            if(i == allowedCurrencies.size()) {
                i = 0;
            }
            setValue(new Money(m.getValue(), allowedCurrencies.get(i)));
        }
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
        this.textField.setRequired(required);
        setPresentationValue(getValue());
    }
}
package com.storedobject.ui;

import com.storedobject.core.Money;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.util.ItemContextMenu;
import com.storedobject.vaadin.CustomTextField;
import com.storedobject.vaadin.RequiredField;
import com.storedobject.vaadin.util.HasTextValue;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

import java.math.BigDecimal;
import java.util.*;

/**
 * Field to accept monetary values ({@link Money}).
 *
 * @author Syam
 */
public class MoneyField extends CustomTextField<Money> implements RequiredField {

    private static final String HELP = "Click on the currency to change it";
    private Span symbol;
    private List<Currency> allowedCurrencies = null;
    private ItemContextMenu<Currency> popup;
    private boolean required = false;
    private TextField textField;
    private boolean tagDebit = true;
    private boolean localCurrency = false;
    private SystemUser forUser;

    /**
     * Constructor.
     */
    public MoneyField() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param width Width of the field (number of characters).
     */
    public MoneyField(int width) {
        this(width, (Currency) null);
    }

    /**
     * Constructor.
     *
     * @param width Width of the field (number of characters).
     * @param currency Initial currency to use.
     */
    public MoneyField(int width, Currency currency) {
        this(null, width, currency);
    }

    /**
     * Constructor.
     *
     * @param width Width of the field (number of characters).
     * @param currency Initial currency to use.
     */
    public MoneyField(int width, String currency) {
        this(null, width, currency);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     */
    public MoneyField(String label) {
        this(label, 0, (Currency) null);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param width Width of the field (number of characters).
     */
    public MoneyField(String label, int width) {
        this(label, width, (Currency)null);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param width Width of the field (number of characters).
     * @param currency Initial currency to use.
     */
    public MoneyField(String label, int width, String currency) {
        this(label, width, Money.getCurrency(currency));
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param width Width of the field (number of characters).
     * @param currency Initial currency to use.
     */
    public MoneyField(String label, int width, Currency currency) {
        super(currency == null ? new Money() : new Money(currency));
        findUser();
        if(width < 4 || width > 22) {
            width = 22;
        }
        getField().setMaxLength(width);
        Money v = getEmptyValue();
        setValue(v);
        setPresentationValue(v);
        setLabel(label);
        ((TextField)getField()).addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        symbol(false);
    }

    private void findUser() {
        if(forUser == null) {
            Application a = Application.get();
            if(a != null) {
                forUser = a.getTransactionManager().getUser();
                ((TextField) getField()).setAutoselect(!a.getWebBrowser().isAndroid());
            }
        }
    }

    /**
     * Tag negative values with "DB".
     *
     * @param tagDebit To tag or not.
     */
    public void setTagDebit(boolean tagDebit) {
        this.tagDebit = tagDebit;
        setPresentationValue(getValue());
    }

    /**
     * Set local currency mode. In local currency mode, no currency symbol/name is displayed.
     *
     * @param localCurrency True/false.
     */
    public void setLocalCurrency(boolean localCurrency) {
        this.localCurrency = localCurrency;
        if(localCurrency) {
            setAllowedCurrencies(Application.getDefaultCurrency());
        } else {
            allowedCurrencies = null;
        }
        setPresentationValue(getValue());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        findUser();
    }

    @Override
    protected void customizeTextField(HasTextValue textField) {
        if(symbol == null) {
            symbol = new Span();
        }
        this.textField = (TextField) textField;
        this.textField.setSuffixComponent(symbol);
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
        if(string == null || string.isEmpty()) {
            string = "0";
        }
        Money m;
        string = string.trim().replace(",", "");
        try {
            Double.parseDouble(string);
            m = new Money(new BigDecimal(string), getCurrency());
        } catch(Throwable notNumber) {
            m = Money.create(string);
            if(m == null) {
                m = getEmptyValue();
            }
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
        return required && value.isZero() ? "" : value.toString(false, tagDebit, forUser);
    }

    @Override
    protected void setPresentationValue(Money value) {
        getField().setValue(format(value));
        symbol.setText(localCurrency ? "" : value.getCurrency().getCurrencyCode());
    }

    /**
     * Get the currency of the current value.
     *
     * @return Currency.
     */
    public Currency getCurrency() {
        return Money.getCurrency(symbol.getText());
    }

    /**
     * Restrict allowed currencies to a given set of values.
     *
     * @param allowedCurrencies Allowed currencies.
     */
    public void setAllowedCurrencies(Collection<Currency> allowedCurrencies) {
        if(allowedCurrencies == null || allowedCurrencies.isEmpty()) {
            this.allowedCurrencies = null;
        } else {
            this.allowedCurrencies = new ArrayList<>(allowedCurrencies);
        }
        checkCurrency();
    }

    /**
     * Restrict allowed currencies to a given set of values.
     *
     * @param allowedCurrencies Allowed currencies.
     */
    public void setAllowedCurrencies(Currency... allowedCurrencies) {
        if(allowedCurrencies == null || allowedCurrencies.length == 0) {
            this.allowedCurrencies = null;
        } else {
            this.allowedCurrencies = Arrays.asList(allowedCurrencies);
        }
        checkCurrency();
    }

    /**
     * Restrict allowed currencies to a given set of values.
     *
     * @param allowedCurrencies Allowed currencies.
     */
    public void setAllowedCurrencies(String... allowedCurrencies) {
        if(allowedCurrencies == null || allowedCurrencies.length == 0) {
            this.allowedCurrencies = null;
            checkCurrency();
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
        setHelperText(null);
        if(allowedCurrencies == null || allowedCurrencies.size() == 1) {
            if(popup != null) {
                popup.setTarget(null);
                popup = null;
            }
            if(allowedCurrencies == null) {
                symbol(isReadOnly());
                return;
            }
        }
        Money m = getValue();
        if(!allowedCurrencies.contains(m.getCurrency())) {
            setValue(new Money(m.getValue(), allowedCurrencies.get(0)));
        }
        if(allowedCurrencies.size() > 1) {
            if(popup == null) {
                popup = new ItemContextMenu<>(symbol, this::changeCurrency);
                popup.setItemLabelGenerator(c -> c.getCurrencyCode() + " " + c.getDisplayName());
            }
            popup.setItems(allowedCurrencies);
            if(isReadOnly()) {
                setHelperText(null);
                popup.setTarget(null);
            } else {
                setHelperText(HELP);
            }
        }
        symbol(isReadOnly());
    }

    private void changeCurrency(Currency currency) {
        if(isReadOnly()) {
            return;
        }
        Money m = getValue();
        if(m.getCurrency() != currency) {
            setValue(new Money(m.getValue(), currency));
        }
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
        this.textField.setRequired(required);
        setPresentationValue(getValue());
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        symbol(readOnly);
        setHelperText(allowedCurrencies != null && allowedCurrencies.size() > 1 && !readOnly ? HELP : null);
        if(popup != null) {
            if(readOnly) {
                popup.setTarget(null);
            } else {
                popup.setTarget(symbol);
            }
        }
    }

    private void symbol(boolean readOnly) {
        symbol.getStyle().set("cursor", "pointer");
        symbol.getElement().
                setProperty("title",
                        allowedCurrencies != null && allowedCurrencies.size() > 1 && !readOnly ?
                                "Click to change" : "Currency");
    }
}
package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.vaadin.TextField;
import org.vaadin.textfieldformatter.CustomStringBlockFormatter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A field to accept a valid currency code.
 *
 * @author Syam
 */
public class CurrencyField extends TextField {

    private List<Currency> allowedCurrencies;

    /**
     * Constructor.
     */
    public CurrencyField() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param label Label to set.
     */
    public CurrencyField(String label) {
        new CustomStringBlockFormatter(new int[] { 3 }, new String[] { }, CustomStringBlockFormatter.ForceCase.UPPER, null, false).
                extend(this);
        setLabel(label);
        setValue(Application.getDefaultCurrency().getCurrencyCode());
        addValueChangeListener(e -> {
           if(e.isFromClient()) {
               String c = e.getValue();
               Currency curr = curr(c);
               if(curr == null) {
                   setHelperText(c.trim().toUpperCase() + " is not a currency");
                   focus();
               } else if(!isAllowed(curr)) {
                   StringBuilder m = new StringBuilder(c);
                   m.append(" - Not allowed. Try others like - ");
                   m.append(allowedCurrencies.stream().limit(4).map(Currency::getCurrencyCode).
                           collect(Collectors.joining(", ")));
                   if(allowedCurrencies.size() > 4) {
                       m.append("...");
                   }
                   setHelperText(m.toString());
                   focus();
               } else {
                   setHelperText(null);
               }
           }
        });
    }

    private static Currency curr(String value) {
        try {
            return Currency.getInstance(value.trim().toUpperCase());
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Get the currency that currently set.
     * @return Currency (could be null).
     */
    public Currency getCurrency() {
        return curr(getValue());
    }

    /**
     * Set a currency as the currency value.
     *
     * @param currency Currency to set.
     */
    public void setCurrency(Currency currency) {
        setValue(currency == null ? "" : currency.getCurrencyCode());
    }

    @Override
    public boolean isInvalid() {
        return !isAllowed(curr(getValue()));
    }

    /**
     * Is the given currency is allowed or not.
     *
     * @param currency Currency to check.
     * @return True or false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isAllowed(Currency currency) {
        return currency != null && (allowedCurrencies == null || allowedCurrencies.contains(currency));
    }

    /**
     * Set a set of allowed currencies.
     *
     * @param currencies Currencies to be allowed.
     */
    public void setAllowedCurrencies(Currency... currencies) {
        if(currencies == null || currencies.length == 0) {
            allowedCurrencies = null;
            return;
        }
        allowedCurr(Arrays.stream(currencies));
    }

    /**
     * Set a set of allowed currencies.
     *
     * @param currencies Currencies to be allowed.
     */
    public void setAllowedCurrencies(String... currencies) {
        if(currencies == null || currencies.length == 0) {
            allowedCurrencies = null;
            return;
        }
        if(currencies.length == 1 && currencies[0].contains(",")) {
            setAllowedCurrencies(StringList.create(currencies[0]).stream());
        } else {
            setAllowedCurrencies(Arrays.stream(currencies));
        }
    }

    /**
     * Set a set of allowed currencies.
     *
     * @param currencies Currencies to be allowed.
     */
    public void setAllowedCurrencies(Stream<String> currencies) {
        if(currencies == null) {
            allowedCurrencies = null;
            return;
        }
        allowedCurr(currencies.map(CurrencyField::curr));
    }

    private void allowedCurr(Stream<Currency> currencies) {
        if(currencies == null) {
            allowedCurrencies = null;
            return;
        }
        if(allowedCurrencies == null) {
            allowedCurrencies = new ArrayList<>();
        } else {
            allowedCurrencies.clear();
        }
        currencies.filter(Objects::nonNull).forEach(c -> allowedCurrencies.add(c));
        if(allowedCurrencies.isEmpty()) {
            allowedCurrencies = null;
        } else {
            if(!isAllowed(getCurrency())) {
                setCurrency(allowedCurrencies.get(0));
            }
        }
    }
}
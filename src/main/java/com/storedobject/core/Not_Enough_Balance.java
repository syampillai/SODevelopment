package com.storedobject.core;

import java.util.Currency;

public class Not_Enough_Balance extends Database_Id_Message {

    public Not_Enough_Balance(String id) {
        super(id);
    }

    public Account getAccount() {
        return StoredObject.get(Account.class, getId(), true);
    }

    @Override
    protected String getCustomMessage() {
        Account a = getAccount();
        Currency currency = a.getCurrency();
        String tail = getTail();
        System.err.println(tail);
        int comma;
        comma = tail.indexOf(',');
        Money balance = new Money(tail.substring(0, comma).trim(), currency);
        tail = tail.substring(comma + 1);
        System.err.println(tail);
        comma = tail.indexOf(',');
        Money tran = new Money(tail.substring(0, comma).trim(), currency);
        tail = tail.substring(comma + 1);
        System.err.println(tail);
        comma = tail.indexOf(',');
        Money limit = new Money(tail.substring(0, comma).trim(), currency);
        tail = tail.substring(comma + 1);
        System.err.println(tail);
        Money excess = new Money(tail.trim(), currency);
        Money available = balance.add(limit);
        String s = "Account: " + a.toDisplay() + ", Balance: " + balance;
        if(!limit.isZero()) {
            if(limit.isPositive()) {
                s += ", limit: " + limit;
            } else {
                s += ", Lien: " + limit.negate();
            }
            s += ", Available: " + available;
        }
        s += ", Transaction: " + tran + ", Shortage: " + tran.add(available).negate();
        if(!excess.isZero()) {
            s += ", (Excess Allowed: " + excess + ")";
        }
        return s;
    }
}
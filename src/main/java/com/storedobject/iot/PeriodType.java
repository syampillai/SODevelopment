package com.storedobject.iot;

public enum PeriodType {

    HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY;

    public long time(long from, int period) {
        return from + period * switch (this) {
            case HOURLY -> 3600000L;
            case DAILY -> 86400000L;
            case WEEKLY -> 604800000L;
            case MONTHLY -> 2592000000L;
            case YEARLY -> 31536000000L;
        };
    }
}

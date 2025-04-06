package com.storedobject.iot;

import com.storedobject.core.DateUtility;
import com.storedobject.core.StoredObject;

import java.util.List;
import java.util.function.BiFunction;

/**
 * The AQI class represents the Air Quality Index, a measure used to communicate how polluted the air currently is or how polluted it is forecast to become.
 * The AQI is used for assessing and reporting daily air quality levels and understanding their potential impact on health.
 * It typically takes into account various air pollutants such as ground-level ozone, particulate matter, carbon monoxide, sulfur dioxide, and nitrogen dioxide.
 * The class includes methods to set and retrieve these pollutant levels, calculate the overall AQI value, and interpret the results in terms of health advisories.
 * <pre>
 *     Assumptions about data:
 *     (1) Temperature is in degree celsius. (Used in the conversion of PPM to µg/m³).
 *     (2) PM 10 and PM 2.5 are in µg/m³.
 *     (3) O₃, CO, SO2, NO2 are in PPM.
 * </pre>
 *
 * @author Syam
 */
public class AQI extends Index {

    private static final int[][] breakpoints = new int[][] { // All values except the sub-index values are x10. -1 means N/A.
            { -1, -1, 0, 1000, 0, 54, 0, 920, -1, -1, 0, 1000, 0, 750, 0, 504, 0, 50 },
            { -1, -1, 1010, 1200, 55, 104, 930, 3500, -1, -1, 1010, 4000, 760, 1500, 505, 604, 51, 100 },
            { 2000, 3220, 1210, 1670, 105, 144, 3510, 4850, -1, -1, 4010, 6770, 1510, 2500, 605, 754, 101, 150 },
            { 3230, 4000, 1680, 2060, 145, 179, 4860, 7970, -1, -1, 6780, 12210, 2510, 3500, 755, 1504, 151, 200 },
            { 4010, 7920, 2070, 3920, 180, 354, -1, -1, 7980, 15830, 12220, 23490, 3510, 4200, 1505, 2504, 201, 300 },
            { 7930, 11840, -1, 355, 584, -1, -1, 15840, 26310, 23500, 38530, 4210, 6000, 2505, 5004, 301, 500 }
    };
    private static final String[] names = new String[] {
            "O3", "CO", "SO2", "NO2", "PM10", "PM25"
    };
    private static final double[] molecularWeight = new double[] {
            48, 28.01, 64.07, 46.01, -1, -1
    };
    private int aqi, aqiCause = -1;
    private BiFunction<Integer, Integer, Double> temperature;
    private int o3, co, so2, no2, pm10, pm25;

    private AQI(Unit unit, int year, int hour) {
        super(unit, year, hour);
    }

    public static AQI create(Data data) {
        return create(AQI.class, data, AQI::new);
    }

    @Override
    public double getValue() {
        return aqi;
    }

    public int get() {
        return aqi;
    }

    @Override
    protected void compute() {
        aqi = 0;
        aqiCause = 8;
        for(String name: names) {
            switch (name) {
                case "O3" -> {
                    o3 = compute(name, 1, 0);
                    int v = compute(name, 8, 1);
                    if(v > o3) {
                        o3 = v;
                    }
                }
                case "CO" -> co = compute(name, 8, 2);
                case "SO2" -> {
                    so2 = compute(name, 1, 3);
                    int v = compute(name, 24, 4);
                    if(v > so2) {
                        so2 = v;
                    }
                }
                case "NO2" -> no2 = compute(name, 1, 5);
                case "PM10" -> pm10 = compute(name, 24, 6);
                case "PM25" -> pm25 = compute(name, 24, 7);
            }
        }
    }

    private int compute(String name, int hours, int cause) {
        int value;
        String condition = "Unit=" + unit.getId() + " AND Name='" + name + "' AND Year=";
        if(hours == 1) {
            HourlyStatistics hs = StoredObject.get(HourlyStatistics.class, condition + year
                    + " AND Hour=" + hour);
            if(hs == null) {
                return -1;
            }
            double reading = hs.getMean();
            if(reading <= 0) {
                return (int) (reading * 100);
            }
            value = value(reading, name);
        } else {
            List<HourlyStatistics> hss = StoredObject.list(HourlyStatistics.class, condition + year + " AND Hour<=" + hour,
                    "Hour DESC").limit(hours).toList();
            if (hour < hours) {
                StoredObject.list(HourlyStatistics.class, "Unit=" + unit.getId()
                                + " AND Name='" + name + "' AND Year=" + (year - 1)
                                + " AND Hour<" + Integer.MAX_VALUE, "Hour DESC").limit(hours - hour)
                        .forEach(hss::add);
            }
            hss.removeIf(hs -> !range(hs, hours));
            int n = hss.size();
            if(n == 0) {
                return -1;
            }
            double v;
            if((hours == 8 && n < 6) || (hours == 24 && n < 18)) {
                v = -1;
                for(HourlyStatistics hs: hss) {
                    if(hs.getMean() > v) {
                        v = hs.getMean();
                    }
                }
                value = value(v, name);
            } else {
                v = 0;
                for(HourlyStatistics hs: hss) {
                    v += hs.getMean();
                }
                value = value(v / n, name);
            }
        }
        int index;
        switch (name) {
            case "O3" -> index = hours == 1 ? 0 : 2;
            case "CO" -> index = 4;
            case "SO2" -> index = hours == 1 ? 6 : 8;
            case "NO2" -> index = 10;
            case "PM10" -> index = 12;
            case "PM25" -> index = 14;
            default -> {
                return -1;
            }
        }
        int vindex = -1;
        for(int i = 0; i < 5; i++) {
            if(value >= breakpoints[i][index] && value <= breakpoints[i][index + 1]) {
                vindex = i;
                break;
            }
        }
        if(vindex == -1) {
            return -1;
        }
        int qi = (breakpoints[vindex][17] - breakpoints[vindex][16]) * (value - breakpoints[vindex][index]);
        qi = (int) (((double)qi / (double) (breakpoints[vindex][index + 1] - breakpoints[vindex][index])) + 0.5);
        qi += breakpoints[vindex][16];
        if(qi <= aqi) {
            return value;
        }
        aqi = qi;
        aqiCause = cause;
        return value;
    }

    private boolean range(HourlyStatistics hs, int hours) {
        if(hs.getYear() == year) {
            return hs.getHour() <= hour;
        }
        if(hs.getYear() != (year - 1)) {
            return false;
        }
        int leastHour = DateUtility.getHourOfYear(
                DateUtility.endTime(DateUtility.create(year - 1, 12, 31)))
                - (hours - hour);
        return hs.getHour() > leastHour;
    }

    private int value(double value, String name) {
        double mw = mw(name);
        if(mw > 0) {
            // Concentration (mg/m³) = (Concentration (ppm) * Molecular Weight * Pressure) / (22.4 * (273 + Temperature))
            // Pressure is 1 atm
            value *= (mw * 1000) / (22.41 * (temperature() + 273)); // In µg/m³ by multiplying by 1000
            if ("CO".equals(name)) { // For CO, it should be in mg/m³
                value /= 1000.0;
            }
        }
        switch (name) {
            // Needs rounding
            case "CO", "PM25" -> value += 0.05;
        }
        return (int)(value * 10); // Make it x10 to compare with breakpoints
    }

    private double mw(String name) {
        return switch (name) {
            case "O3" -> molecularWeight[0];
            case "CO" -> molecularWeight[1];
            case "SO2" -> molecularWeight[2];
            case "NO2" -> molecularWeight[3];
            case "PM10" -> molecularWeight[4];
            case "PM25" -> molecularWeight[5];
            default -> 0;
        };
    }

    public int getCause() {
        return aqiCause;
    }

    public String getCauseValue() {
        get();
        return getCauseValue(aqiCause);
    }

    public static String getCauseValue(int aqiCause) {
        return switch (aqiCause) {
            case 0, 1 -> "O₃";
            case 2 -> "CO";
            case 3, 4 -> "SO₂";
            case 5 -> "NO₂";
            case 6 -> "PM10";
            case 7 -> "PM2.5";
            case 8 -> "None";
            default -> "Unknown";
        };
    }

    private double temperature() {
        return temperature == null ? 25 : temperature.apply(year, hour);
    }

    public void setTemperature(BiFunction<Integer, Integer, Double> temperature) {
        this.temperature = temperature;
    }

    public int getYear() {
        return year;
    }

    public int getHour() {
        return hour;
    }

    public Unit getUnit() {
        return unit;
    }

    public double getO3() {
        return o3 / 100.0;
    }

    public double getCO() {
        return co / 1000.0;
    }

    public double getSO2() {
        return so2 / 100.0;
    }

    public double getNO2() {
        return no2 / 100.0;
    }

    public double getPM10() {
        return pm10 / 100.0;
    }

    public double getPM25() {
        return pm25 / 100.0;
    }

    @Override
    public String toString() {
        String casue = getCauseValue();
        if(!casue.isEmpty()) {
            casue = " (" + casue + ")";
        }
        return aqi + casue;
    }
}

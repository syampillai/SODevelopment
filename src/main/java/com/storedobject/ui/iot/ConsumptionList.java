package com.storedobject.ui.iot;

import com.storedobject.chart.DataMatrix;
import com.storedobject.core.StringUtility;
import com.storedobject.iot.*;
import com.storedobject.office.ExcelReport;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ListGrid;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;

import java.util.ArrayList;
import java.util.List;

public class ConsumptionList extends ArrayList<ConsumptionList.Entry> {

    private final Resource resource;
    private final Block block;
    private final int periodicity, yearFrom, yearTo;
    private final List<Consumption4Unit<?>> units = new ArrayList<>();

    public ConsumptionList(Resource resource, Block block, int periodicity, int yearFrom, int yearTo) {
        this(resource, block, periodicity, yearFrom, yearTo, 0);
    }

    public ConsumptionList(Resource resource, Block block, int periodicity, int yearFrom, int yearTo, int limit) {
        this.resource = resource;
        this.block = block;
        this.periodicity = periodicity;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
        Consumption4Unit.process(resource, block, periodicity, yearFrom, yearTo, units::add);
        for(int i = 0; i < units.size(); i++) {
            int finalI = i;
            units.get(i).load(limit).forEach(c -> createEntry(c, units.size(), finalI));
        }
    }

    private void createEntry(Consumption<?> c, int size, int index) {
        String p = c.getPeriodDetail();
        Entry entry = stream().filter(e -> e.period.equals(p))
                .findAny().orElse(null);
        if(entry == null) {
            double[] v = new double[size];
            entry = new Entry(p, v);
            add(entry);
        }
        entry.consumption[index] = Math.round(c.getConsumption() * 100) / 100.0;
    }

    public Resource getResource() {
        return resource;
    }

    public Block getBlock() {
        return block;
    }

    public record Entry(String period, double[] consumption) {
    }

    public String getCaption() {
        return ConsumptionDashboard.periodicity.get(periodicity) + " " + resource.getName()
                + " Consumption (" + yearFrom + (yearFrom == yearTo ? "" : ("-" + yearTo)) + ")";
    }

    public String getDescription() {
        return getCaption() + " in " + resource.getMeasurementUnit();
    }

    public void view() {
        new Grid().execute();
    }

    private class Grid extends ListGrid<Entry> {

        @SuppressWarnings("unchecked")
        public Grid() {
            super(Entry.class, ConsumptionList.this);
            setCaption(ConsumptionList.this.getCaption());
            createColumn("Period", e -> e.period);
            Consumption4Unit<?> c;
            for(int i = 0; i < units.size(); i++) {
                c = units.get(i);
                int finalI = i;
                createColumn(c.unit().getName(), e -> StringUtility.format(e.consumption[finalI], 2, true));
            }
        }

        @Override
        public int getColumnOrder(String columnName) {
            if("Period".equals(columnName)) {
                return 1;
            }
            for(int i = 0; i < units.size(); i++) {
                if(units.get(i).unit().getName().equals(columnName)) {
                    return i + 2;
                }
            }
            return super.getColumnOrder(columnName);
        }

        @Override
        public ColumnTextAlign getTextAlign(String columnName) {
            return ColumnTextAlign.END;
        }

        @Override
        public boolean isColumnSortable(String columnName) {
            return false;
        }

        @Override
        public boolean isColumnFrozen(String columnName) {
            return "Period".equals(columnName);
        }

        @Override
        public Component createHeader() {
            //noinspection resource
            return new ButtonLayout(
                    new ELabel(ConsumptionList.this.getDescription()),
                    new Button("Report", e -> new Report().execute()),
                    new Button("Download", e -> new Download().execute()),
                    new Button("View Another", e -> {
                        close();
                        new ViewConsumption(resource, block).execute();
                    }),
                    new Button("Exit", e -> close())
            );
        }

        @Override
        public void execute(View lock) {
            if(units.isEmpty()) {
                return;
            }
            super.execute(lock);
        }
    }

    private class Download extends ExcelReport {

        Download() {
            super(Application.get());
        }

        @Override
        public String getFileName() {
            return ConsumptionList.this.getCaption();
        }

        @Override
        public void generateContent() {
            int row = 0;
            goToCell(0, row);
            setCellValue(ConsumptionList.this.getCaption() + " in " + resource.getMeasurementUnit());
            goToCell(0, ++row);
            setCellValue("Period");
            units.forEach(u -> {
                moveRight();
                setCellValue(u.unit().getName());
            });
            for(Entry e: ConsumptionList.this) {
                goToCell(0, ++row);
                setCellValue(e.period);
                for (double v: e.consumption) {
                    moveRight();
                    setCellValue(Math.round(v * 100)/100.0);
                }
            }
        }
    }

    private class Report extends PDFReport {

        public Report() {
            super(Application.get());
        }

        @Override
        public Object getTitle() {
            return units.getFirst().unit().getSite().getName() + "\n" + ConsumptionList.this.getCaption()
                    + " in " + resource.getMeasurementUnit();
        }

        @Override
        public void generateContent() {
            int[] w = new int[units.size() + 1];
            w[0] = periodicity == 2 ? 20 : 10;
            for(int i = 1; i < w.length; i++) {
                w[i] = 10;
            }
            PDFTable table = createTable(w);
            table.addCell(createCell(createTitleText("Period")));
            units.forEach(u -> table.addCell(createCell(createTitleText(u.unit().getName()))));
            table.setHeaderRows(1);
            int rows = 0;
            for(Entry e: ConsumptionList.this) {
                table.addCell(createCell(e.period));
                for (double v: e.consumption) {
                    table.addCell(createCell(v, true));
                }
                if((++rows % 80) == 0) {
                    add(table);
                }
            }
            add(table);
        }

        @Override
        public int getPageOrientation() {
            return units.size() > 5 ? ORIENTATION_LANDSCAPE : ORIENTATION_PORTRAIT;
        }
    }

    public DataMatrix getDataMatrix() {
        DataMatrix dm = new DataMatrix(getDescription());
        dm.setColumnDataName("Units");
        String[] s = new String[units.size()];
        int i;
        for(i = 0; i < units.size(); i++) {
            s[i] = units.get(i).unit().getName();
        }
        dm.setColumnNames(s);
        int n = size();
        s = new String[n];
        i = n - 1;
        for(Entry e: this) {
            s[i--] = e.period;
        }
        dm.setRowNames(s);
        for(Entry e: this) {
            dm.insertRow(0, e.consumption);
        }
        return dm;
    }

    public AbstractUnit getUnit(int index) {
        return units.get(index).unit();
    }

    public int getUnitsCount() {
        return units.size();
    }
}
